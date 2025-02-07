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
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.List;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@EnabledIfEnvironmentVariable(named = "OPENAI_API_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "ANTHROPIC_API_KEY", matches = ".*")
@EnabledIfEnvironmentVariable(named = "GOOGLEAI_API_KEY", matches = ".*")
public class FeudTest {

    public static final String WIKIPEDIA_FEUD_ARTICLE =
            "https://en.wikipedia.org/wiki/Drake%E2%80%93Kendrick_Lamar_feud";

    private final static int GPT4O_MAX_TOKENS = 128 * 1024;
    private final static int CLAUDE_MAX_TOKENS = 200 * 1024;
    private final static int GEMINI_MAX_TOKENS = 1024 * 1024;

    private final ChatLanguageModel gpt4o = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName(OpenAiChatModelName.GPT_4_O)
            .maxRetries(1)
            .build();

    private final ChatLanguageModel claude = AnthropicChatModel.builder()
            .apiKey(System.getenv("ANTHROPIC_API_KEY"))
            .modelName(AnthropicChatModelName.CLAUDE_3_5_SONNET_20240620)
            .maxRetries(1)
            .build();

    private final ChatLanguageModel gemini = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GOOGLEAI_API_KEY"))
            .modelName("gemini-2.0-flash-001")
            .maxRetries(1)
            .build();

    private final ChatLanguageModel mistral = MistralAiChatModel.builder()
            .apiKey(System.getenv("MISTRAL_API_KEY"))
            .modelName(MistralAiChatModelName.MISTRAL_LARGE_LATEST)
            .maxRetries(1)
            .build();

    private final List<String> prompts = List.of(
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

    public interface Assistant {
        String answer(@UserMessage String prompt);

        @SystemMessage("Given {{information}}, answer the {{prompt}}")
        String answerWithData(@UserMessage @V("prompt") String prompt,
                              @V("information") String information);
    }

    @Test
    void feud_without_any_extra_info() {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(gpt4o)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        prompts.forEach(prompt -> {
            System.out.println("## " + prompt);
            String response = assistant.answer(prompt);
            System.out.println(response + "\n");
        });
    }

    @Test
    void prompt_stuffing_url_document_loader_gpt4o() {
        Document feudDoc = UrlDocumentLoader.load(
                WIKIPEDIA_FEUD_ARTICLE, new TextDocumentParser());

        boolean okay = sizeOkay(GPT4O_MAX_TOKENS, feudDoc.text());
        if (!okay) {
            System.out.println("Document too large");
            return;
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(gpt4o)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        answerQuestions(assistant, feudDoc.text());
    }

    private void answerQuestions(Assistant assistant, String text) {
        prompts.forEach(prompt -> {
            System.out.println("## " + prompt);
            try {
                String response = assistant.answerWithData(prompt, text);
                System.out.println(response + "\n");
            } catch (Exception e) {
                System.err.println("Exception: " + e.getMessage());
            }
        });
    }

    @Test
    void prompt_stuffing_url_document_loader_claude() {
        Document feudDoc = UrlDocumentLoader.load(
                WIKIPEDIA_FEUD_ARTICLE, new TextDocumentParser());

        if (!sizeOkay(CLAUDE_MAX_TOKENS, feudDoc.text())) {
            System.out.println("Document too large");
            return;
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(claude)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        answerQuestions(assistant, feudDoc.text());
    }

    @Test
    void prompt_stuffing_url_document_loader_gemini() {
        Document feudDoc = UrlDocumentLoader.load(
                WIKIPEDIA_FEUD_ARTICLE, new TextDocumentParser());

        if (!sizeOkay(GEMINI_MAX_TOKENS, feudDoc.text())) {
            System.out.println("Document too large");
            return;
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(gemini)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        answerQuestions(assistant, feudDoc.text());
    }

    @Test
    void prompt_stuffing_jsoup() throws Exception {
        String wikiText = Jsoup.connect(WIKIPEDIA_FEUD_ARTICLE).get().text();

        if (!sizeOkay(GPT4O_MAX_TOKENS, wikiText)) {
            System.out.println("Document too large");
            return;
        }

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(gpt4o)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        answerQuestions(assistant, wikiText);
    }

    @Test
    void rag() {
        // Load the document
        Document document = UrlDocumentLoader.load(
                WIKIPEDIA_FEUD_ARTICLE, new TextDocumentParser());

        // Split the document
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 0);
        List<TextSegment> segments = splitter.split(document);
        System.out.println("Number of segments: " + segments.size());

        // Embed the segments
        EmbeddingModel embeddingModel = new BgeSmallEnEmbeddingModel();
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

        // Store the embeddings
        EmbeddingStore<TextSegment> embeddingStore = new InMemoryEmbeddingStore<>();
        embeddingStore.addAll(embeddings, segments);

        // Create the content retriever
        ContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .maxResults(4) // at most 4 relevant segments
                .minScore(0.5) // at least somewhat similar to user query
                .build();

        // Create the assistant with the content retriever
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(mistral)
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

    private boolean sizeOkay(int maxTokens, String documentText) {
        var openAiTokenizer = new OpenAiTokenizer(OpenAiChatModelName.GPT_4_O);
        return prompts.stream()
                .map(prompt -> openAiTokenizer.estimateTokenCountInText(
                        "Given %s, answer the %s".formatted(documentText, prompt)))
                .peek(tokens -> System.out.printf("OpenAI: %d tokens%n", tokens))
                .noneMatch(tokens -> tokens > maxTokens);
    }
}
