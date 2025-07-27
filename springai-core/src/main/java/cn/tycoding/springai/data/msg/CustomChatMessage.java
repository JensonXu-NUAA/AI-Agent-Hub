package cn.tycoding.springai.data.msg;

import cn.tycoding.springai.data.dto.CustomChatDTO;

import lombok.Data;

import org.jetbrains.annotations.NotNull;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import java.util.HashMap;
import java.util.Map;

@Data
public class CustomChatMessage implements Message {
    private String text;
    private MessageType messageType;
    private Map<String, Object> metaData = new HashMap<>();

    public CustomChatMessage(CustomChatDTO customChatDTO) {
        this.text = customChatDTO.getMessage();
        this.messageType = MessageType.fromValue(customChatDTO.getRole());

        this.metaData.put("userId", customChatDTO.getUserId());
        this.metaData.put("chatId", customChatDTO.getChatId());
        this.metaData.put("conversationId", customChatDTO.getConversationId());
    }

    public CustomChatMessage(String text, MessageType messageType, Map<String, Object> metadata) {
        this.text = text;
        this.messageType = messageType;
        this.metaData = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return this.messageType;
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return this.metaData;
    }
}
