package com.kousenit.rag;

import com.kousenit.ApiKeys;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStore;

import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

public class MYM {
    interface MYMAgent {
        String answer(String query);
    }

    public static void main(String[] args) {
        // Set up the language model
        ChatModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName("gpt-4o")
                .temperature(0.3)
                .build();

        // Set up embedding model
        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName("text-embedding-3-small")
                .build();

        // Load and split the document
        var segments = loadAndSplitDocument(
                Path.of("src/main/resources/help-your-boss-help-you_P1.0.pdf"));

        // Use ChromaManager to handle collection management
        ChromaManager chromaManager =
                new ChromaManager("http://localhost:8000", embeddingModel);
        EmbeddingStore<TextSegment> embeddingStore =
                chromaManager.getOrCreateCollection("mym_book", segments);

        // Create content retriever for RAG
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(4)
                .minScore(0.5)
                .build();

        // Set up memory and agent
        ChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);
        MYMAgent agent = AiServices.builder(MYMAgent.class)
                .chatModel(model)
                .contentRetriever(contentRetriever)
                .chatMemory(chatMemory)
                .build();

        try (var scanner = new Scanner(System.in)) {
            while (true) {
                System.out.print("Enter a question (or press Enter to quit): ");
                String query = scanner.nextLine();
                if (query.isBlank()) {
                    System.out.println("Goodbye!");
                    break;
                }
                String answer = agent.answer(query);
                System.out.println("Answer: " + answer);
            }
        }

    }

    /**
     * Load and split a PDF document
     */
    private static List<TextSegment> loadAndSplitDocument(Path documentPath) {
        try {
            // Use PDFBox directly for PDFs
            DocumentParser parser = new ApachePdfBoxDocumentParser();
            Document document = FileSystemDocumentLoader.loadDocument(documentPath, parser);

            // Split into manageable segments
            DocumentSplitter splitter =
                    DocumentSplitters.recursive(300, 0);
            return splitter.split(document);
        } catch (Exception e) {
            System.err.println("Error loading document: " + e.getMessage());
            throw new RuntimeException("Failed to load document", e);
        }
    }
}