package cn.tycoding.springai.core.data.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;

@Data
public class CustomChatDTO {
    private String userId;
    private String message;
    private String chatId;
    private String role;
    private String conversationId;


    public String getConversationId() {
        if (StringUtils.isAllBlank(conversationId)) {
            this.conversationId = UUID.randomUUID().toString();
        }
        return conversationId;
    }
}
