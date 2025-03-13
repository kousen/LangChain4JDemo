package com.kousenit.services.tavily;

import com.kousenit.services.Assistant;
import com.kousenit.services.openai.OpenAiResponsesService;
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

    @Test
    void searchWithOpenAi() {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(new OpenAiResponsesService())
                .build();

        System.out.println(assistant.chat("""
                        What is the price of a Macbook Air
                        in Bangalore?
                        """
        ));
    }

    @Test
    void searchWithTavily() {
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .tools(new TavilyService())
                .build();

        System.out.println(assistant.chat("""
                        What is the price of a Macbook Air
                        in Bangalore?
                        """
        ));
    }
}
