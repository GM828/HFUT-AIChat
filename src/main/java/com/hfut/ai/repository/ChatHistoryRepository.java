package com.hfut.ai.repository;

import java.util.List;

/**
 * chatId存储接口
 */
public interface ChatHistoryRepository {

    /**
     * 保存聊天记录
     * @param type 业务类型，如：chat，service，pdf
     * @param chatId 聊天会话ID
     */
    void save(String type, String chatId);

    /**
     * TODO 删除聊天记录
     * @param type
     * @param chatId
     */
    void delete(String type, String chatId);

    /**
     * 获取聊天记录
     * @param type 业务类型，如：chat，service，pdf
     * @return 会话ID列表
     */
    List<String> getChatIds(String type);
}
