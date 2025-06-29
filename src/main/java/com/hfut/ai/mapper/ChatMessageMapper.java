package com.hfut.ai.mapper;

import com.hfut.ai.entity.inSqlChat.ChatMessage;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 历史会话内容持久化接口
 */
@Mapper
public interface ChatMessageMapper {

    /**
     * 保存会话内容
     * @param message
     */
    @Insert("INSERT INTO chat_message (conversation_id, role, content) VALUES (#{conversationId}, #{role}, #{content})")
    void save(ChatMessage message);

    /**
     * 根据会话ID查询会话内容
     * @param conversationId
     * @return
     */
    @Select("SELECT * FROM chat_message WHERE conversation_id = #{conversationId} ORDER BY id ASC")
    List<ChatMessage> findByConversationId(String conversationId);

    /**
     * 根据会话ID删除会话内容
     * @param conversationId
     */
    @Delete("DELETE FROM chat_message WHERE conversation_id = #{conversationId}")
    void deleteByConversationId(String conversationId);
}
