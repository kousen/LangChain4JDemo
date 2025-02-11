package com.kousenit.documents;

import com.kousenit.ApiKeys;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;
import java.util.List;

public class MYM {
    interface MYMAgent {
        String answer(String query);
    }

    public static void main(String[] args) {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName("gpt-4o")
                .temperature(0.3)
                //.logRequests(true)
                //.logResponses(true)
                .build();

        // Load the document
        Path book = Path.of("src/main/resources/help-your-boss-help-you_P1.0.pdf");
        DocumentParser parser = new ApachePdfBoxDocumentParser();
        Document document = FileSystemDocumentLoader.loadDocument(book, parser);

        // Split the document into segments
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);

        // Embed the segments
        // EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName("text-embedding-3-small")
                .build();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // Store the embeddings
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        // Retrieve relevant content
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(4) // on each interaction we will retrieve the 4 most relevant segments
                .minScore(0.5) // we want to retrieve segments at least somewhat similar to user query
                .build();

        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        MYMAgent agent = AiServices.builder(MYMAgent.class)
                .chatLanguageModel(model)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .build();

        String answer = agent.answer("""
                How does the book define
                the phrase "working professional"?
                """);
        System.out.println(answer);
    }
}
