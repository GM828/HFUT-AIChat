package com.hfut.ai.entity.inSqlChat;

import lombok.Data;

/**
 * 记录chatId的实体类
 * @author GM
 */
@Data
public class ChatHistory {
    private String id;
    private String type;
    private String chatId;
}
