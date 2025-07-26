package cn.tycoding.springai.core.service.impl;

import cn.tycoding.springai.core.advisor.CustomLogAdvisor;
import cn.tycoding.springai.core.data.dto.CustomChatDTO;
import cn.tycoding.springai.core.service.SpringAIChatService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import java.util.UUID;

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
        customChatDTO.setChatId(UUID.randomUUID().toString());
        String conversationId = customChatDTO.getConversationId();

        return chatClient.prompt()
                .user(customChatDTO.getMessage())
                .advisors(advisor -> advisor
                        .param(ChatMemory.CONVERSATION_ID, conversationId)
                        // 传递额外的上下文信息给advisor
                        .param("userId", customChatDTO.getUserId())
                        .param("chatId", customChatDTO.getChatId())
                        .param("conversationId", conversationId))
                .stream()
                .content();
    }
}
