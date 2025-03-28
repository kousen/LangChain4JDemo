package com.kousenit.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;

import java.util.List;

/**
 * Manages Chroma collection operations for LangChain4j
 */
public class ChromaManager {
    private final String baseUrl;
    private final EmbeddingModel embeddingModel;

    public ChromaManager(String baseUrl, EmbeddingModel embeddingModel) {
        this.baseUrl = baseUrl;
        this.embeddingModel = embeddingModel;
    }

    /**
     * Gets or creates a collection, checking if it exists with data
     *
     * @param collectionName Name of the collection to get or create
     * @param segments Text segments to embed if collection needs to be populated
     * @return A configured embedding store with the collection
     */
    public EmbeddingStore<TextSegment> getOrCreateCollection(String collectionName, List<TextSegment> segments) {
        // First try to access existing collection
        ChromaEmbeddingStore store;
        boolean needsEmbedding;

        try {
            // Build the store with the requested collection name
            store = ChromaEmbeddingStore.builder()
                    .baseUrl(baseUrl)
                    .collectionName(collectionName)
                    .build();

            // Test if the collection exists and has data
            var testQuery = embeddingModel.embed("test").content();
            var result = store.search(
                    EmbeddingSearchRequest.builder()
                            .queryEmbedding(testQuery)
                            .maxResults(1)
                            .build());

            // If we have any results, collection exists with data
            if (!result.matches().isEmpty()) {
                System.out.println("Using existing collection with data: " + collectionName);
                needsEmbedding = false;
            } else {
                System.out.println("Collection exists but appears empty, will add embeddings");
                needsEmbedding = true;
            }
        } catch (Exception e) {
            // If we couldn't access the collection or it doesn't exist
            String message = e.getMessage();

            if (message != null && message.contains("already exists")) {
                System.out.println("Collection exists but couldn't connect properly. Using unique name.");
                String uniqueName = collectionName + "_" + System.currentTimeMillis();
                store = ChromaEmbeddingStore.builder()
                        .baseUrl(baseUrl)
                        .collectionName(uniqueName)
                        .build();
            } else {
                System.out.println("Creating new collection: " + collectionName);
                // Retry with original name - it likely doesn't exist
                store = ChromaEmbeddingStore.builder()
                        .baseUrl(baseUrl)
                        .collectionName(collectionName)
                        .build();
            }
            needsEmbedding = true;
        }

        // Add embeddings if needed
        if (needsEmbedding && segments != null && !segments.isEmpty()) {
            System.out.println("Adding embeddings to collection...");
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            store.addAll(embeddings, segments);
            System.out.println("Embeddings added successfully");
        }

        return store;
    }

    /**
     * Simpler version that doesn't embed - useful when you only need to retrieve
     */
    public EmbeddingStore<TextSegment> getCollection(String collectionName) {
        return getOrCreateCollection(collectionName, null);
    }

    /**
     * Force creation of a new collection with unique name
     */
    public EmbeddingStore<TextSegment> createNewCollection(String baseCollectionName, List<TextSegment> segments) {
        String uniqueName = baseCollectionName + "_" + System.currentTimeMillis();
        System.out.println("Creating new collection: " + uniqueName);

        ChromaEmbeddingStore store = ChromaEmbeddingStore.builder()
                .baseUrl(baseUrl)
                .collectionName(uniqueName)
                .build();

        if (segments != null && !segments.isEmpty()) {
            System.out.println("Adding embeddings to collection...");
            List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
            store.addAll(embeddings, segments);
            System.out.println("Embeddings added successfully");
        }

        return store;
    }
}