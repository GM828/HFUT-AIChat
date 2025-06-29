package com.hfut.ai.entity.inSqlChat;

import lombok.Data;

/**
 * 记录历史会话内容的实体类
 * @author GM
 */
@Data
public class ChatMessage {
    private Long id;
    private String conversationId;
    private String role; // "USER", "ASSISTANT"
    private String content;
}