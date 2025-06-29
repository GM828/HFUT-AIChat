package com.hfut.ai.config;

import com.hfut.ai.constants.SystemConstants;
import com.hfut.ai.tools.ElectiveCourseTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class CommonConfiguration {
    /**
     * AI对话用ChatClient对象，用于处理用户输入的文本，并返回处理结果
     * @param model 使用本地的模型
     * @param inSqlChatMemory 通过数据库进行会话历史存储
     * @return
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel model, InSqlChatMemory inSqlChatMemory) {
        return ChatClient
                .builder(model)// 选择模型
                .defaultOptions(ChatOptions.builder().model("qwen-omni-turbo").build())// 单独设置模型为 qwen-omni-turbo 多模态模型
                .defaultSystem("你是合肥工业大学的一名资深老学长，十分熟悉校园，请以该身份的语气和性格回答问题")// 系统设置
                .defaultAdvisors(new SimpleLoggerAdvisor())// 添加日志记录
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(inSqlChatMemory).build())// 添加会话记忆功能
                .build();
    }

    /**
     * 哄哄模拟器游戏用ChatClient对象，用于模拟女友进行游戏
     * @param model 使用OpenAI的模型
     * @param chatMemory 通过内存进行会话历史存储
     * @return
     */
    @Bean
    public ChatClient gameChatClient(OpenAiChatModel model, ChatMemory chatMemory) {
        return ChatClient
                .builder(model)// 选择模型
                .defaultSystem(SystemConstants.GAME_SYSTEM_PROMPT)// 系统设置
                .defaultAdvisors(new SimpleLoggerAdvisor())// 添加日志记录
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())// 添加会话记忆功能
                .build();
    }

    /**
     * 客服用ChatClient对象，用于模拟选修课程推荐客服
     * @param model 使用OpenAI的模型
     * @param inSqlChatMemory 通过数据库进行会话历史存储
     * @return
     */
    @Bean
    public ChatClient serviceChatClient(OpenAiChatModel model, InSqlChatMemory inSqlChatMemory, ElectiveCourseTools electiveCourseTools) {
        return ChatClient
                .builder(model)// 选择模型
                .defaultSystem(SystemConstants.SERVICE_SYSTEM_PROMPT)// 系统设置
                .defaultAdvisors(new SimpleLoggerAdvisor())// 添加日志记录
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(inSqlChatMemory).build())// 添加会话记忆功能
                .defaultTools(electiveCourseTools)// 添加工具
                .build();
    }

    /**
     * 配置pdf文件对话用ChatClient对象，用于处理用户输入的pdf文件，并返回处理结果
     * @param model 使用OpenAI的模型
     * @param inSqlChatMemory 通过数据库进行会话历史存储
     * @param vectorStore 通过向量库进行相似度搜索
     * @return
     */
    @Bean
    public ChatClient pdfChatClient(OpenAiChatModel model, InSqlChatMemory inSqlChatMemory,  VectorStore vectorStore) {
        return ChatClient
                .builder(model)// 选择模型
                .defaultSystem(SystemConstants.PDF_SYSTEM_PROMPT)// 系统设置
                .defaultAdvisors(new SimpleLoggerAdvisor())// 添加日志记录
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(inSqlChatMemory).build())// 添加会话记忆功能
                .defaultAdvisors(QuestionAnswerAdvisor.builder(vectorStore).// 添加向量库
                        searchRequest(SearchRequest.builder().similarityThreshold(0.6d).// 设置相似度阈值
                                topK(2).// 设置返回文档数量
                                build()).build())
                .build();
    }


    /**
     * 配置会话历史存储
     * @return
     */
    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().build(); // 使用 MessageWindowChatMemory 作为会话历史存储策略，默认使用内存存储，窗口大小20
        // return new InSqlChatMemory(); // 使用自定义的 InSqlChatMemory 作为会话历史存储策略，使用数据库存储
    }

    /**
     * 配置SimpleVectorStore向量库
     * @param embeddingModel
     * @return
     */
    /*@Bean
    public VectorStore vectorStore(OpenAiEmbeddingModel embeddingModel) {
        return SimpleVectorStore.builder(embeddingModel).build();
    }*/

}













