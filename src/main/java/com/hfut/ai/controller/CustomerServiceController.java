package com.hfut.ai.controller;

import com.hfut.ai.enums.ChatType;
import com.hfut.ai.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RequestMapping("/ai")
@RestController
@RequiredArgsConstructor
public class CustomerServiceController {

    private final ChatClient serviceChatClient;//  客服模型

    @Autowired
    @Qualifier("inSqlChatHistoryRepository") //  使用数据库存储会话id
    private ChatHistoryRepository chatHistoryRepository;

    @RequestMapping(value = "/service", produces = "text/html;charset=utf-8")
    // @CrossOrigin("http://localhost:5173")
    public Flux<String> chat(@RequestParam("prompt") String prompt, @RequestParam("chatId") String chatId) {
        // 保存会话ID
        chatHistoryRepository.save(ChatType.SERVICE.getValue(), chatId);
        // 请求模型
        return serviceChatClient.prompt()
                .user(prompt)// 设置用户输入
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID,chatId))// 设置会话ID
                .stream()// 开启流式对话
                .content();// 获取对话内容
    }
}
