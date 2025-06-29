package com.hfut.ai.controller;

import com.hfut.ai.enums.ChatType;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/ai")
@RestController
@RequiredArgsConstructor
public class GameController {

    private final ChatClient gameChatClient;// 游戏聊天客户端

    @RequestMapping(value = "/game", produces = "text/html;charset=utf-8")
    // @CrossOrigin("http://localhost:5173")
    public Flux<String> chat(@RequestParam("prompt") String prompt, @RequestParam("chatId") String chatId) {
        // 请求模型
        return gameChatClient.prompt()
                .user(prompt)// 设置用户输入
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))// 设置会话ID
                .stream()// 开启流式对话
                .content();// 获取对话内容
    }
}
