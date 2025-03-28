package com.kousenit.rag;

import com.kousenit.ApiKeys;
import com.kousenit.services.Assistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

import java.nio.file.Path;
import java.util.List;

public class LoadMyDocuments {

    // Easy RAG example from https://docs.langchain4j.dev/tutorials/rag
    public static void main(String[] args) {
        // Load Medium posts from the filesystem
        List<Document> documents = FileSystemDocumentLoader.loadDocuments(
                Path.of("src/main/resources/mymMediumPosts"));

        ChatLanguageModel gpt4o = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        // Create an assistant that can chat about the documents
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(gpt4o)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(createContentRetriever(documents))
                .build();

        // Start chatting with the assistant
        String answer = assistant.chat("What messages should you be giving your manager?");
        System.out.println(answer);
    }

    private static ContentRetriever createContentRetriever(List<Document> documents) {

        // Here, we create and empty in-memory store for our documents and their embeddings.
        InMemoryEmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();

        // Here, we are ingesting our documents into the store.
        // Under the hood, a lot of "magic" is happening, but we can ignore it for now.
        EmbeddingStoreIngestor.ingest(documents, embeddingStore);

        // Lastly, let's create a content retriever from an embedding store.
        return EmbeddingStoreContentRetriever.from(embeddingStore);
    }

}
