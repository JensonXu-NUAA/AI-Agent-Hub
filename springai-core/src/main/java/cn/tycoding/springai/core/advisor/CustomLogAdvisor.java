package cn.tycoding.springai.core.advisor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Slf4j
@Component
public class CustomLogAdvisor implements CallAdvisor, StreamAdvisor {

    @NotNull
    @Override
    public ChatClientResponse adviseCall(@NotNull ChatClientRequest chatClientRequest, @NotNull CallAdvisorChain callAdvisorChain) {
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    @NotNull
    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        // 从context中获取用户信息
        String userId = (String) request.context().get("userId");
        String chatId = (String) request.context().get("chatId");
        String conversationId = (String) request.context().get("conversationId");

        StringBuilder aggregatedContent = new StringBuilder();

        return chain.nextStream(request)
                .doOnSubscribe(subscription -> log.info("开始订阅流式响应, user id: {}, conversation id: {}, chat id: {}", userId, conversationId, chatId))
                .doOnNext(response -> {
                    assert response.chatResponse() != null;
                    String token = response.chatResponse().getResult().getOutput().getText();
                    if (StringUtils.isNotBlank(token)) {
                        aggregatedContent.append(token);
                        log.info("接收到流式token: {}", token);
                    }
                })
                .doOnComplete(() -> log.info("流式响应完成, 结果: {}", aggregatedContent))
                .doOnError(error -> log.error("流式响应发生错误, user id: {}, conversation id: {}, chat id: {}", userId, conversationId, chatId, error))
                .doOnCancel(() -> log.info("流式响应被取消, user id: {}, conversation id: {}, chat id: {}",  userId, conversationId, chatId));
    }
}
