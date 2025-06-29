package com.hfut.ai;

import com.hfut.ai.utils.PromptUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.util.List;



@Slf4j
@SpringBootTest
public class VectorStoreTests {

    @Autowired
    private VectorStore vectorStore;

    /**
     * VectorStore向量库测试
     */
    @Test
    public void testVectorStore(){
        Resource resource = new FileSystemResource("中二知识笔记.pdf");
        // 1.创建PDF的读取器
        PagePdfDocumentReader reader = new PagePdfDocumentReader(
                resource, // 文件源
                PdfDocumentReaderConfig.builder()
                        .withPageExtractedTextFormatter(ExtractedTextFormatter.defaults())
                        .withPagesPerDocument(1) // 每1页PDF作为一个Document
                        .build()
        );
        // 2.读取PDF文档，拆分为Document
        List<Document> documents = reader.read();
        // 3.写入向量库
        vectorStore.add(documents);
    }

    @Test
    public void testRedisVectorStore(){
        // List<Document> docs = vectorStore.similaritySearch("论语中教育的目的是什么");
        SearchRequest request = SearchRequest.builder()
                .query("论语中教育的目的是什么") // 查询
                .topK(5)  // 返回的相似文档数量
                .similarityThreshold(0.6) // 相似度阈值
                //.filterExpression("file_name == '中二知识笔记.pdf'")  // 过滤条件
                .build();
        List<Document> docs = vectorStore.similaritySearch(request);
        if (docs == null) {
            System.out.println("没有搜索到任何内容");
            return;
        }
        for (Document doc : docs) {
            // log.info("搜索到内容:", doc.getId(), doc.getScore(), doc.getText());
            System.out.println(doc.getId());
            System.out.println(doc.getScore());
            System.out.println(doc.getText());
        }
    }

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
                .filterExpression("file_name == \"" + fileName + "\"")  // 过滤条件
                .build();
        // 通过向量数据库搜索获取上下文
        List<Document> docs = vectorStore.similaritySearch(request);
        return docs;
    }

    public String getPrompt(String query, String fileName) {

        // 获取上下文
        List<Document> docs = getContext(query, fileName);
        if (docs == null || docs.isEmpty())
            return "很抱歉，没有找到任何内容。";
        PromptTemplate promptTemplate = new PromptTemplate(PROMPT_BLUEPRINT);
        String context = "";
        for (Document doc : docs) {
            context += doc.getText();
        }
        promptTemplate.add("context", context);
        promptTemplate.add("query", query);
        return promptTemplate.render();
    }

    @Test
    public void testPromptUtils(){
        PromptUtils promptUtils = new PromptUtils();
        String prompt = this.getPrompt("论语中教育的目的是什么","Test.pdf");
        System.out.println(prompt);
    }
}

