package cn.tycoding.springai.core.service.impl;

import cn.tycoding.springai.data.dto.CustomChatDTO;
import cn.tycoding.springai.core.service.SpringAIChatService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.Map;
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

        Map<String, Object> userMessageMetadata = new HashMap<>();
        userMessageMetadata.put("userId", customChatDTO.getUserId());
        userMessageMetadata.put("chatId", customChatDTO.getChatId());
        userMessageMetadata.put("conversationId", conversationId);
        userMessageMetadata.put("modelName", customChatDTO.getModelName());

        // 使用Spring AI标准的UserMessage
        UserMessage userMessage = UserMessage.builder()
                .text(customChatDTO.getMessage())
                .metadata(userMessageMetadata)
                .build();

        return chatClient.prompt()
                .messages(userMessage)
                .advisors(advisor -> advisor
                        .param(ChatMemory.CONVERSATION_ID, conversationId)
                        .param("userId", customChatDTO.getUserId())
                        .param("chatId", customChatDTO.getChatId())
                        .param("conversationId", conversationId))
                .stream()
                .content();
    }
}
