/*
 * Copyright (c) 2024 LangChat. TyCoding All Rights Reserved.
 *
 * Licensed under the GNU Affero General Public License, Version 3 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gnu.org/licenses/agpl-3.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.tycoding.langchat.server.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;

import cn.tycoding.langchat.ai.biz.entity.AigcApp;
import cn.tycoding.langchat.ai.biz.entity.AigcMessage;
import cn.tycoding.langchat.ai.biz.entity.AigcOss;

import cn.tycoding.langchat.ai.biz.service.AigcMessageService;
import cn.tycoding.langchat.ai.core.provider.ModelProvider;
import cn.tycoding.langchat.ai.core.service.LangChatService;

import cn.tycoding.langchat.common.ai.dto.ChatReq;
import cn.tycoding.langchat.common.ai.dto.ChatRes;
import cn.tycoding.langchat.common.ai.dto.ImageR;

import cn.tycoding.langchat.common.ai.utils.StreamEmitter;
import cn.tycoding.langchat.common.core.constant.RoleEnum;
import cn.tycoding.langchat.common.core.utils.ServletUtil;

import cn.tycoding.langchat.common.repository.mysql.entity.AigcMessageDO;
import cn.tycoding.langchat.server.service.ChatService;
import cn.tycoding.langchat.server.store.AppStore;

import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.function.Consumer;

/**
 * @author tycoding
 * @since 2024/1/4
 */
@Slf4j
@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ModelProvider provider;
    private final LangChatService langChatService;
    private final AigcMessageService aigcMessageService;
    private final AppStore appStore;

    @Override
    public void chat(ChatReq req) {
        StreamEmitter emitter = req.getEmitter();
        long startTime = System.currentTimeMillis();
        StringBuilder text = new StringBuilder();

        if (StrUtil.isNotBlank(req.getAppId())) {
            AigcApp app = appStore.get(req.getAppId());
            if (app != null) {
                req.setModelId(app.getModelId());
                req.setPromptText(app.getPrompt());
                req.setKnowledgeIds(app.getKnowledgeIds());
            }
        }

        // save user message
        req.setRole(RoleEnum.USER.getName());
        saveMessage(req, 0, 0);

        try {
            langChatService
                    .chat(req)
                    .onNext(e -> {
                        text.append(e);
                        emitter.send(new ChatRes(e));
                    })
                    .onComplete((e) -> {
                        TokenUsage tokenUsage = e.tokenUsage();
                        ChatRes res = new ChatRes(tokenUsage.totalTokenCount(), startTime);
                        emitter.send(res);
                        emitter.complete();

                        // save assistant message
                        req.setMessage(text.toString());
                        req.setRole(RoleEnum.ASSISTANT.getName());
                        saveMessage(req, tokenUsage.inputTokenCount(), tokenUsage.outputTokenCount());
                    })
                    .onError((e) -> {
                        emitter.error(e.getMessage());
                        throw new RuntimeException(e.getMessage());
                    })
                    .start();
        } catch (Exception e) {
            log.error("启动聊天服务失败: {}", e.getMessage(), e);
            emitter.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 处理流式聊天请求，返回一个包含聊天响应 token 的 Flux 流。
     * 该方法会使用指定的模型进行流式聊天，若会话 ID 为空则生成一个新的会话 ID。
     * 在聊天过程中，会将接收到的 token 通过 Flux 流发送出去，聊天结束或出错时会进行相应处理。
     *
     * @param req 聊天请求对象，包含会话 ID、消息内容、模型 ID 等信息
     * @return 一个包含聊天响应 token 的 Flux 流
     */
    @Override
    public Flux<String> streamChat(ChatReq req) {
        StringBuilder text = new StringBuilder();
        StreamingChatLanguageModel chatModel = provider.stream(req.getModelId());
        if (StrUtil.isBlank(req.getConversationId())) {
            req.setConversationId(IdUtil.simpleUUID());
        }

        try {
            log.info("stream chat start, conversation id: {}", req.getConversationId());
            Consumer<FluxSink<String>> emitter = fluxSink -> {  // 定义一个消费者，用于处理 FluxSink，将流式聊天的响应发送到 Flux 流中
                StreamingChatResponseHandler handler = new StreamingChatResponseHandler() {  // 处理聊天过程中的不同状态
                    @Override
                    public void onPartialResponse(String token) {
                        text.append(token);
                        fluxSink.next(token);  // 将接收到的 token 发送到 Flux 流中
                    }

                    @Override
                    public void onCompleteResponse(ChatResponse chatResponse) {
                        TokenUsage tokenUsage = chatResponse.tokenUsage();
                        fluxSink.complete();
                        log.info("stream chat success, conversation id: {}", req.getConversationId());

                        req.setMessage(text.toString());
                        req.setRole(RoleEnum.ASSISTANT.getName());
                        saveMessage(req, tokenUsage);
                    }

                    @Override
                    public void onError(Throwable error) {
                        fluxSink.error(error);
                        log.error("stream chat error: {}, conversation id: {}", error.getMessage(), req.getConversationId());  // 记录聊天出错的日志，包含会话 ID
                    }
                };

                chatModel.chat(req.getMessage(), handler);// 调用 chat 方法开始流式聊天
            };

            return Flux.create(emitter);
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    private void saveMessage(ChatReq req, Integer inputToken, Integer outputToken) {
        if (req.getConversationId() != null) {
            AigcMessage message = new AigcMessage();
            BeanUtils.copyProperties(req, message);
            message.setIp(ServletUtil.getIpAddr());
            message.setPromptTokens(inputToken);
            message.setTokens(outputToken);
            aigcMessageService.addMessage(message);
        }
    }

    private void saveMessage(ChatReq req, TokenUsage tokenUsage) {
        if (StrUtil.isNotBlank(req.getConversationId())) {
            AigcMessageDO message = new AigcMessageDO();
            BeanUtils.copyProperties(req, message);

            message.setModel(req.getModelName());
            message.setIp(ServletUtil.getIpAddr());
            message.setPromptTokens(tokenUsage.inputTokenCount());
            message.setTokens(tokenUsage.outputTokenCount());
            aigcMessageService.addMessage(message);
            log.info("stream chat saved, conversation id: {}", req.getConversationId());
        }
    }

    @Override
    public String text(ChatReq req) {
        String text;
        try {
            text = langChatService.text(req);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
        return text;
    }

    @Override
    public AigcOss image(ImageR req) {
        Response<Image> res = langChatService.image(req);

        String path = res.content().url().toString();
        AigcOss oss = new AigcOss();
        oss.setUrl(path);
        return oss;
    }
}