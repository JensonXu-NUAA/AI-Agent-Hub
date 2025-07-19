package cn.tycoding.langchat.server.controller;


import cn.tycoding.langchat.common.ai.dto.ChatReq;
import cn.tycoding.langchat.server.service.ChatService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/aigc/chat")
public class AigcChatController {

    private final ChatService chatService;

    /**
     * 处理流式聊天请求
     *
     * @param chatReq 包含聊天请求信息的对象，如会话 ID、消息内容等
     * @return 一个 Flux 流，包含格式化为 SSE 的聊天消息，最后以 [DONE] 标识结束
     */
    @PostMapping(value = "/streamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody ChatReq chatReq) {
        return Mono.fromCallable(() -> chatService.streamChat(chatReq))
                .flatMapMany(flux -> flux)
                .map(token -> "data: " + token + "\n\n")
                .subscribeOn(Schedulers.boundedElastic());  // 异步处理
    }
}
