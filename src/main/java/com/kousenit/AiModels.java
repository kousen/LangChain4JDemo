package com.kousenit;

import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

@SuppressWarnings("unused")
public class AiModels {
    public static final String PERPLEXITY_SONAR_SMALL = "llama-3.1-sonar-small-128k-online";
    public static final String PERPLEXITY_SONAR_LARGE = "llama-3.1-sonar-large-128k-online";
    public static final String PERPLEXITY_SONAR_HUGE = "llama-3.1-sonar-huge-128k-online";
    public static final String PERPLEXITY_CHAT_SMALL = "llama-3.1-sonar-small-128k-chat";
    public static final String PERPLEXITY_CHAT_LARGE = "llama-3.1-sonar-large-128k-chat";

    public static final String PERPLEXITY_BASE_URL = "https://api.perplexity.ai";

    public static final ChatLanguageModel GPT_4_O = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .modelName(OpenAiChatModelName.GPT_4_O)
            .build();

    public final static ChatLanguageModel MISTRAL_SMALL_MODEL = MistralAiChatModel.builder()
            .apiKey(ApiKeys.MISTRAL_API_KEY)
            .modelName(String.valueOf(MistralAiChatModelName.MISTRAL_SMALL_LATEST))
            .logRequests(true)
            .logResponses(true)
            .build();

    public final static ChatLanguageModel MISTRAL_LARGE_MODEL = MistralAiChatModel.builder()
            .apiKey(ApiKeys.MISTRAL_API_KEY)
            .modelName(String.valueOf(MistralAiChatModelName.MISTRAL_LARGE_LATEST))
            .logRequests(true)
            .logResponses(true)
            .build();

    public static final ChatLanguageModel OLLAMA_CHAT_MODEL = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.2")
            .build();

    public static final ChatLanguageModel CLAUDE_HAIKU = AnthropicChatModel.builder()
            .apiKey(ApiKeys.ANTHROPIC_API_KEY)
            .modelName(AnthropicChatModelName.CLAUDE_3_HAIKU_20240307)
            .build();

    public static final ChatLanguageModel CLAUDE_SONNET = AnthropicChatModel.builder()
            .apiKey(ApiKeys.ANTHROPIC_API_KEY)
            .modelName(AnthropicChatModelName.CLAUDE_3_5_SONNET_20240620)
            .build();

    public static final ChatLanguageModel CLAUDE_OPUS = AnthropicChatModel.builder()
            .apiKey(ApiKeys.ANTHROPIC_API_KEY)
            .modelName(AnthropicChatModelName.CLAUDE_3_OPUS_20240229)
            .build();

    public static final ChatLanguageModel GEMINI_FLASH = GoogleAiGeminiChatModel.builder()
            .apiKey(ApiKeys.GOOGLEAI_API_KEY)
            .modelName("gemini-1.5-flash")
            .build();

    public static final ChatLanguageModel PERPLEXITY_LARGE_SONAR = OpenAiChatModel.builder()
            .apiKey(ApiKeys.PERPLEXITY_API_KEY)
            .baseUrl(PERPLEXITY_BASE_URL)
            .modelName(PERPLEXITY_SONAR_LARGE)
            .build();

    public static final ChatLanguageModel PERPLEXITY_SMALL_SONAR = OpenAiChatModel.builder()
            .apiKey(ApiKeys.PERPLEXITY_API_KEY)
            .baseUrl(PERPLEXITY_BASE_URL)
            .modelName(PERPLEXITY_SONAR_SMALL)
            .logRequests(true)
            .logResponses(true)
            .build();
}
