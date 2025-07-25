package cn.tycoding.springai.core.service.impl;

import cn.tycoding.springai.core.data.dto.CustomChatDTO;
import cn.tycoding.springai.core.service.SpringAIChatService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

@Slf4j
@Service
public class SpringAIChatServiceImpl implements SpringAIChatService {

    private final ChatClient chatClient;

    @Autowired
    public SpringAIChatServiceImpl(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public Flux<String> streamChat(CustomChatDTO customChatDTO) {
        return chatClient.prompt(customChatDTO.getMessage())
                .stream()
                .content()
                .doOnSubscribe(subscription -> log.info("客户端开始订阅流式响应"))
                .doOnNext(chunk -> log.info("接收到流式数据块: {}", chunk))
                .doOnComplete(() -> log.info("流式响应完成"))
                .doOnError(error -> log.error("流式响应发生错误: ", error))
                .doOnCancel(() -> log.info("流式响应被取消"));
    }
}
