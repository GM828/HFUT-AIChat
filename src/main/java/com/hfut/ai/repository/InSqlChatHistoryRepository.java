package com.hfut.ai.repository;

import com.hfut.ai.entity.inSqlChat.ChatHistory;
import com.hfut.ai.mapper.ChatHistoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 将chatId保存到数据库中
 */
@Component()
public class InSqlChatHistoryRepository implements ChatHistoryRepository{

    @Autowired
    private ChatHistoryMapper chatHistoryMapper;

    /**
     * 保存chatId到数据库
     * @param type 业务类型，如：chat，service，pdf
     * @param chatId 聊天会话ID
     */
    @Override
    public void save(String type, String chatId) {
        // 先查询是否已存在
        if (exists(type, chatId)) return;

        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setType(type);
        chatHistory.setChatId(chatId);
        chatHistoryMapper.insert(chatHistory);
    }

    // 判断 chatId 是否已存在
    private boolean exists(String type, String chatId) {
        List<String> chatIds = chatHistoryMapper.selectChatIdsByType(type);
        return chatIds.contains(chatId);
    }

    /**
     * TODO 删除
     * @param type
     * @param chatId
     */
    @Override
    public void delete(String type, String chatId) {

    }

    /**
     * 根据类型获取聊天记录
     * @param type
     * @return
     */
    @Override
    public List<String> getChatIds(String type) {
        return chatHistoryMapper.selectChatIdsByType(type);
    }
}
