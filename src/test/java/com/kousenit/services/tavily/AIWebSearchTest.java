package com.kousenit.services.tavily;

import com.kousenit.services.Assistant;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

public class AIWebSearchTest {

    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .modelName(OpenAiChatModelName.GPT_4_O_MINI)
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();

    private final Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(model)
            .tools(new TavilyService())
            .build();

    @Test
    void search() {
        System.out.println(assistant.chat("What is the weather in Boston, MA?"));
    }
}
