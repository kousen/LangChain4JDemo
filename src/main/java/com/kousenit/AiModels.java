package com.kousenit;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModelName;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class AiModels {
    private static final String OLLAMA_MODEL_NAME = "llama3";  // try "mistral", "llama3", "codellama", "phi" or "tinyllama"

    public static final ChatLanguageModel OPENAI_CHAT_MODEL = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            // .modelName(OpenAiChatModelName.GPT_4_TURBO_PREVIEW)
//            .logRequests(true)
//            .logResponses(true)
            //.responseFormat("json_object")
            .build();

    public final static ChatLanguageModel MISTRAL_SMALL_MODEL = MistralAiChatModel.builder()
            .apiKey(ApiKeys.MISTRAL_API_KEY)
            .modelName(String.valueOf(MistralAiChatModelName.MISTRAL_SMALL_LATEST))
            .logRequests(true)
            .logResponses(true)
            .build();

    public static final ChatLanguageModel OLLAMA_CHAT_MODEL = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName(OLLAMA_MODEL_NAME)
            .format("json")
            .build();
}
