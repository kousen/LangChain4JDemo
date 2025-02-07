package com.kousenit;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallen.BgeSmallEnEmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "GOOGLEAI_API_KEY", matches = ".*")
public class FeudWithSharedEmbeddingStoreTest {

    public static final String WIKIPEDIA_FEUD_ARTICLE =
            "https://en.wikipedia.org/wiki/Drake%E2%80%93Kendrick_Lamar_feud";

    private static EmbeddingModel embeddingModel;
    private static EmbeddingStore<TextSegment> embeddingStore;

    private static final List<String> prompts = List.of(
            "Tell me about the beef about between Drake and Kendrick Lamar",
            "How did it escalate in 2024?",
            "Who won?",
            "Why was 'Not Like Us' so controversial?",
            """
                    What are the chances Kendrick will perform
                    "Not Like Us" during the 2025 Super Bowl
                    halftime show?
                    """
    );

    private static final ChatLanguageModel gpt4o = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(OpenAiChatModelName.GPT_4_O)
            .maxRetries(1)
            .build();

    private static final ChatLanguageModel claude = AnthropicChatModel.builder()
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(AnthropicChatModelName.CLAUDE_3_5_SONNET_20240620)
            .maxRetries(1)
            .build();

    private static final ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GOOGLEAI_API_KEY"))
            .modelName("gemini-2.0-flash-001")
            .maxRetries(1)
            .build();

    private static final ChatLanguageModel mistral = MistralAiChatModel.builder()
            .apiKey(System.getenv("MISTRAL_API_KEY"))
            .modelName(MistralAiChatModelName.MISTRAL_LARGE_LATEST)
            .maxRetries(1)
            .build();

    public interface Assistant {
        String answer(@UserMessage String prompt);
    }

    /**
     * Prepares shared resources before running the parameterized tests.
     */
    @BeforeAll
    static void setupEmbeddingStore() {
        // Load the document
        Document document = UrlDocumentLoader.load(WIKIPEDIA_FEUD_ARTICLE, new TextDocumentParser());

        // Split the document
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);
        System.out.println("Number of segments: " + segments.size());

        // Embed the segments
        embeddingModel = new BgeSmallEnEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // Store the embeddings
        embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);
    }

    /**
     * Provides the `ChatLanguageModel` instances for the parameterized test.
     */
    private static Stream<ChatLanguageModel> chatLanguageModels() {
        //return Stream.of(gpt4o, claude, gemini, mistral);
        return Stream.of(claude, gemini, mistral);
    }

    /**
     * Parameterized test for running RAG over different ChatLanguageModels.
     */
    @ParameterizedTest(name = "RAG for {0}")
    @MethodSource("chatLanguageModels")
    void rag_for_all_models(ChatLanguageModel model) {
        // Create the content retriever
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(4) // at most 4 relevant segments
                .minScore(0.5) // at least somewhat similar to user query
                .build();

        // Build the assistant with the shared content retriever
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .contentRetriever(contentRetriever)
                .build();

        // Ask the questions
        prompts.forEach(prompt -> {
            System.out.println("## " + prompt);
            String response = assistant.answer(prompt);
            System.out.println(response + "\n");
        });
    }
}