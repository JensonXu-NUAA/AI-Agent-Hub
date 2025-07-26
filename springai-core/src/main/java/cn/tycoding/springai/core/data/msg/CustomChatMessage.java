package cn.tycoding.springai.core.data.msg;

import cn.tycoding.springai.core.data.dto.CustomChatDTO;

import lombok.Data;

import org.jetbrains.annotations.NotNull;

import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;

import java.util.HashMap;
import java.util.Map;

@Data
public class CustomChatMessage implements Message {
    private String text;
    private Map<String, Object> properties = new HashMap<>();

    public CustomChatMessage(CustomChatDTO customChatDTO) {
        this.text = customChatDTO.getMessage();
        this.properties.put("role", customChatDTO.getRole());
        this.properties.put("userId", customChatDTO.getUserId());
        this.properties.put("chatId", customChatDTO.getChatId());
        this.properties.put("conversationId", customChatDTO.getConversationId());
    }

    @NotNull
    @Override
    public MessageType getMessageType() {
        return MessageType.fromValue(this.properties.get("role").toString());
    }

    @Override
    public String getText() {
        return this.text;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return this.properties;
    }
}
