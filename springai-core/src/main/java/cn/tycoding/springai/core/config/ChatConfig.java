package cn.tycoding.springai.core.config;

import cn.tycoding.springai.core.advisor.ChatMemoryAdvisor;
import cn.tycoding.springai.core.advisor.CustomLogAdvisor;
import cn.tycoding.springai.core.advisor.PersistenceMemoryAdvisor;
import cn.tycoding.springai.core.memory.InMysqlMemory;
import cn.tycoding.springai.repository.SpringAIChatMemoryRepository;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    private final SpringAIChatMemoryRepository springAIChatMemoryRepository;
    private static final String DEFAULT_PROMPT = "你是一个博学的智能聊天助手，请根据用户提问回答！";

    @Autowired
    public ChatConfig(SpringAIChatMemoryRepository springAIChatMemoryRepository) {
        this.springAIChatMemoryRepository = springAIChatMemoryRepository;
    }

    @Bean
    public ChatMemory chatMemory() {
        return new InMysqlMemory(springAIChatMemoryRepository);
    }

    @Bean
        public ChatClient chatClient(ChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultSystem(DEFAULT_PROMPT)
                .defaultAdvisors(
                        new ChatMemoryAdvisor(chatMemory), // 负责多轮对话
                        new CustomLogAdvisor(),  // 日志
                        new PersistenceMemoryAdvisor(chatMemory)  // 负责存储
                )
                .defaultOptions(
                        DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                .build();

    }
}
