package cn.tycoding.springai.core.memory;

import cn.tycoding.springai.data.entity.CustomChatMessageDO;
import cn.tycoding.springai.repository.SpringAIChatMemoryRepository;

import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.NotNull;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.List;
import java.util.UUID;
import java.util.Date;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Component
public class InMysqlMemory implements ChatMemory {

    private final SpringAIChatMemoryRepository springAIChatMemoryRepository;

    @Autowired
    public InMysqlMemory(SpringAIChatMemoryRepository springAIChatMemoryRepository) {
        this.springAIChatMemoryRepository = springAIChatMemoryRepository;
    }

    @Override
    public void add(@NotNull String conversationId, @NotNull Message message) {
        Map<String, Object> properties = message.getMetadata();
        CustomChatMessageDO customChatMessageDO = new CustomChatMessageDO();

        customChatMessageDO.setId(null);
        customChatMessageDO.setConversationId(conversationId);
        customChatMessageDO.setType(message.getMessageType().getValue());
        customChatMessageDO.setChatId(UUID.randomUUID().toString());
        customChatMessageDO.setUserId(properties.get("userId").toString());
        customChatMessageDO.setModelName(properties.get("modelName").toString());
        customChatMessageDO.setMessage(message.getText());
        customChatMessageDO.setCreateTime(new Date(System.currentTimeMillis()));

        springAIChatMemoryRepository.insert(customChatMessageDO);
    }

    @Override
    public void add(@NotNull String conversationId, @NotNull List<Message> messages) {

    }

    @NotNull
    @Override
    public List<Message> get(@NotNull String conversationId) {
        List<CustomChatMessageDO> customChatMessageDOList = springAIChatMemoryRepository.selectByConversationId(conversationId);
        if (CollectionUtils.isNotEmpty(customChatMessageDOList)) {
            return customChatMessageDOList.stream()
                    .map(messageDO -> {
                        Map<String, Object> metadata = Map.of(
                                "userId", messageDO.getUserId(),
                                "chatId", messageDO.getChatId(),
                                "conversationId", messageDO.getConversationId(),
                                "modelName", messageDO.getModelName(),
                                "createTime", messageDO.getCreateTime(),
                                "id", messageDO.getId()
                        );

                        MessageType type = MessageType.fromValue(messageDO.getType());

                        // 根据类型返回相应的Spring AI标准消息类型
                        return switch (type) {
                            case USER -> UserMessage.builder()
                                    .text(messageDO.getMessage())
                                    .metadata(metadata)
                                    .build();
                            case ASSISTANT -> new AssistantMessage(messageDO.getMessage(), metadata);
                            case SYSTEM -> SystemMessage.builder()
                                    .text(messageDO.getMessage())
                                    .metadata(metadata)
                                    .build();
                            default -> throw new IllegalArgumentException("Unsupported message type: " + type);
                        };
                    })
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }

    @Override
    public void clear(@NotNull String conversationId) {

    }
}
