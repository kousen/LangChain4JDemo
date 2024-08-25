package com.kousenit.services;

import com.kousenit.ApiKeys;
import com.kousenit.tools.Calculator;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.vertexai.VertexAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AssistantTest {
    private static final Logger logger = LoggerFactory.getLogger(AssistantTest.class);

    private final String question = """
            What is the square root of the sum of the numbers of letters
            in the words "hello" and "world"?
            Show all your steps.
            """;

    @Test
    void conversation() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName("gpt-4o")
                .build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();

        String answer = assistant.chat("What are the FAANG companies?");
        System.out.println(answer + "\n-------\n");

        answer = assistant.chat("""
                Are they called that because
                they are run by vampires?
                """);
        System.out.println(answer + "\n-------\n");

        answer = assistant.chat("""
                Do any of them have offices in Transylvania?
                """);
        System.out.println(answer + "\n-------\n");

        answer = assistant.chat("""
                Have any of them worked with Tom Cruise?
                Everybody knows he's a member of the undead.
                """);
        System.out.println(answer + "\n-------\n");

        answer = assistant.chat("""
                Let's focus on Amazon. Give me ten potential names
                for an Amazon division, based in Transylvania,
                that is run by vampires.
                """);
        System.out.println(answer + "\n-------\n");
    }

    @Test
    void sqrtSumLetters_openAi_gpt35() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
                .build();
        String answer = model.generate(question);
        logger.info("\n%s: %s".formatted(OpenAiChatModelName.GPT_3_5_TURBO, answer));
        assertThat(answer).contains("3.16");
    }

    @Test
    void sqrtSumLetters_openAi_with_tools() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_3_5_TURBO)
                .build();
        Assistant openAiAssistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(new Calculator()) // Woot! Function calling FTW
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
        String answer = openAiAssistant.chat(question);
        logger.info("\ngpt-3.5-turbo with tools: %s".formatted(answer));
        assertThat(answer).contains("3.16");
    }

    @Test
    void sqrtSumLetters_openAi_gpt4() {
        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_TURBO_PREVIEW)
                .build();
        String answer = model.generate(question);
        logger.info("\n%s: %s".formatted(OpenAiChatModelName.GPT_4_TURBO_PREVIEW, answer));
        assertThat(answer).contains("3.16");
    }

    @Test
    void sqrtSumLetters_mistral_small() {
        ChatLanguageModel model = MistralAiChatModel.builder()
                .apiKey(ApiKeys.MISTRAL_API_KEY)
                .modelName(MistralAiChatModelName.MISTRAL_SMALL_LATEST.toString())
                .build();
        String answer = model.generate(question);
        logger.info("\n%s: %s".formatted(MistralAiChatModelName.MISTRAL_SMALL_LATEST, answer));
        assertThat(answer).contains("3.16");
    }

    @Test
    void sqrtSumLetters_claude_haiku() {
        ChatLanguageModel model = AnthropicChatModel.builder()
                .apiKey(ApiKeys.ANTHROPIC_API_KEY)
                .modelName("claude-3-haiku-20240307")
                .build();
        String answer = model.generate(question);
        logger.info("\n%s: %s".formatted("claude-3-haiku", answer));
        assertThat(answer).contains("3.16");
    }

    @Test
    void sqrtSumLetters_vertex_gemini_pro() {
        // Gemini 1.0 pro model (1.5 not yet available)
        ChatLanguageModel model = VertexAiGeminiChatModel.builder()
                .project("gemini-demos-415920")
                .location("us-central1")
                .modelName("gemini-pro")
                .build();
        String answer = model.generate(question);
        logger.info("\nGemini-pro: %s".formatted(answer));
        assertThat(answer).contains("3.16");
    }

    @ParameterizedTest(name = "{0} (via Ollama)")
    @ValueSource(strings = {
            "orca-mini", "llama3", "codellama", "phi3", "tinyllama",
            "gemma:2b", "gemma", "vicuna", "neural-chat", "mixtral",
            "starcoder2", "starcoder2:7b", "wizard-math", "llama2:70b"})
    void sqrtSumLetters_with_model(String modelName) {
        try {
            ChatLanguageModel model = OllamaChatModel.builder()
                    .baseUrl("http://localhost:11434")
                    .modelName(modelName)
                    .build();
            String answer = model.generate(question);
            logger.info("\n\n%s: %s".formatted(modelName, answer));
            assertThat(answer).contains("3.16");
        } catch (Exception e) {
            System.err.printf("%s threw a RuntimeException: %s%n", modelName, e.getMessage());
        }
    }

    private final String lengthQuestion = """
            What is the sum of the lengths of the words in the sentence
            "The quick brown fox jumped over the lazy dog"?
            """;

    private final int correctAnswer = Arrays.stream(
            lengthQuestion.split(" "))
            .mapToInt(String::length)
            .sum();

    @ParameterizedTest(name = "checking {0}")
    @ValueSource(strings = {
            "orca-mini", "llama3", "codellama", "phi3", "tinyllama",
            "gemma:2b", "gemma", "vicuna", "neural-chat", "mixtral",
            "starcoder2", "starcoder2:7b", "wizard-math"})
    void sum_of_word_lengths_with_model(String modelName) {
        ChatLanguageModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName(modelName)
                .build();
        try {
            String answer = model.generate(lengthQuestion);
            logger.info("\n\n%s (via ollama): %s".formatted(modelName, answer));
            assertThat(answer).contains(String.valueOf(correctAnswer));
        } catch (Exception e) {
            System.err.printf("%s threw a RuntimeException: %s%n", modelName, e.getMessage());
        }
    }
}