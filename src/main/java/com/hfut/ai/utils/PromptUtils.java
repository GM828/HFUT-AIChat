package com.hfut.ai.utils;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PromptUtils {

    @Autowired
    private VectorStore vectorStore;

    // 定义问答模板
    private final String PROMPT_BLUEPRINT = """
      严格参考提供的上下文回答查询：
      {context}
      Query:
      {query}
      如果您从提供的上下文中没有任何答案，只需说：
      很抱歉，我没有您要找的信息。
    """;

    // 获取上下文
    private List<Document> getContext(String query, String fileName) {
        // 根据用户输入的查询和文件名获取上下文
        SearchRequest request = SearchRequest.builder()
                .query(query) // 查询
                .topK(2)  // 返回的相似文档数量
                .similarityThreshold(0.6) // 相似度阈值
                .filterExpression("file_name == '"+fileName+"'")  // 过滤条件
                .build();
        // 通过向量数据库搜索获取上下文
        List<Document> docs = vectorStore.similaritySearch(request);
        return docs;
    }


    public String getPrompt(String query, String fileName) {

        // 获取上下文
        List<Document> docs = getContext(query, fileName);
        PromptTemplate promptTemplate = new PromptTemplate(PROMPT_BLUEPRINT);
        promptTemplate.add("context", docs);
        promptTemplate.add("query", query);
        return promptTemplate.render();
    }

}



