package com.hfut.ai.repository;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将chatId保存在内存中
 *  @author GM
 *   @date 2023/5/23
 */
@Repository
public class InMemoryChatHistoryRepository implements ChatHistoryRepository{

    private final Map<String, List<String>> chatHistory = new HashMap<>();

     /**
     * 实现保存聊天记录功能
     * @param type
     * @param chatId
     */
    @Override
    public void save(String type, String chatId) {
        /*if (!chatHistory.containsKey(type))
        {
            chatHistory.put(type, new ArrayList<>());
        }
        List<String> chatIds = chatHistory.get(type);
        以上代码可以简化为下面一行代码
        */

        List<String> chatIds = chatHistory.computeIfAbsent(type, k -> new ArrayList<>());

        if (chatIds.contains(chatId))
        {
            return;
        }
        chatIds.add(chatId);
    }

    /**
     * TODO 实现删除功能
     * @param type
     * @param chatId
     */
    @Override
    public void delete(String type, String chatId) {

    }

     /**
     * 实现获取聊天记录功能
     * @param type
     * @return
     */
    @Override
    public List<String> getChatIds(String type) {
        /*if (!chatHistory.containsKey(type))
        {
            return new ArrayList<>();
        }
        return chatHistory.get(type);
        简化为以下一行代码
        */
        return chatHistory.getOrDefault(type, new ArrayList<>());
    }
}
