package cn.tycoding.langchat.server.controller;

import cn.tycoding.springai.core.data.dto.CustomChatDTO;
import cn.tycoding.springai.core.service.SpringAIChatService;
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

    private final SpringAIChatService springAIChatService;

    /**
     * 处理流式聊天请求
     *
     * @param chatReq 包含聊天请求信息的对象，如会话 ID、消息内容等
     * @return 一个 Flux 流，包含格式化为 SSE 的聊天消息，最后以 [DONE] 标识结束
     */
    @PostMapping(value = "/streamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@RequestBody CustomChatDTO chatReq) {
        return Mono.fromCallable(() -> springAIChatService.streamChat(chatReq))
                .flatMapMany(flux -> flux)
                .subscribeOn(Schedulers.boundedElastic());  // 异步处理
    }
}
