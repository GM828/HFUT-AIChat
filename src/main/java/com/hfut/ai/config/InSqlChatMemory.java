package com.hfut.ai.config;

import com.hfut.ai.entity.inSqlChat.ChatMessage;
import com.hfut.ai.mapper.ChatHistoryMapper;
import com.hfut.ai.mapper.ChatMessageMapper;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义使用数据库存储会话历史
 * @author GM
 */
@Component
public class InSqlChatMemory implements ChatMemory {

    @Autowired
    private ChatMessageMapper chatMessageMapper;


    @Override
    public void add(String conversationId, List<Message> messages) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty");// 判断conversationId是否为空
        Assert.notNull(messages, "messages cannot be null");// 判断messages是否为空
        Assert.noNullElements(messages, "messages cannot contain null elements");// 确保messages中不包含null元素

        for (Message message : messages) {
            String role = "";
            switch (message.getMessageType()) {
                case USER:
                    role = "user";
                    break;
                case ASSISTANT:
                    role = "assistant";
                    break;
                default:
                    role = "unknown";
                    break;
            }

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setConversationId(conversationId);
            chatMessage.setRole(role);
            chatMessage.setContent(message.getText());

            // 插入到数据库
            chatMessageMapper.save(chatMessage);
        }
    }

    // 从数据库中加载会话
    @Override
    public List<Message> get(String conversationId) {
        Assert.hasText(conversationId, "conversationId cannot be null or empty"); // 判断conversationId是否为空
        //System.out.println("🔍 正在从数据库加载会话: " + conversationId);
        List<ChatMessage> chatMessages = chatMessageMapper.findByConversationId(conversationId);  // 需要定义该方法
        //System.out.println("📊 查询结果数量: " + (chatMessages != null ? chatMessages.size() : 0));

        List<Message> messages = new ArrayList<>();

        for (ChatMessage chatMessage : chatMessages) {
            switch (chatMessage.getRole()) {
                case "user":
                    messages.add(new UserMessage(chatMessage.getContent()));
                    break;
                case "assistant":
                    messages.add(new AssistantMessage(chatMessage.getContent()));
                    break;
                default:
                    throw new IllegalArgumentException("Unknown role: " + chatMessage.getRole());
            }
        }
        //System.out.println("message 查询结果数量: " + (messages != null ? messages.size() : 0));
        return messages;
    }

    @Override
    public void clear(String conversationId) {
        chatMessageMapper.deleteByConversationId(conversationId);
    }
}
