package cn.tycoding.springai.core.service;

import cn.tycoding.springai.core.data.dto.CustomChatDTO;
import reactor.core.publisher.Flux;

public interface SpringAIChatService {
    Flux<String> streamChat(CustomChatDTO customChatDTO);
}
