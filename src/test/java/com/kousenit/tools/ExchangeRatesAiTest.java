package com.kousenit.tools;

import com.kousenit.AiModels;
import com.kousenit.services.Assistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

public class ExchangeRatesAiTest {
    private final Assistant assistant = AiServices.builder(Assistant.class)
            .chatLanguageModel(AiModels.GPT_4_O)
            //.tools(new OpenExchangeRates())
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build();

    @Test
    void bestDealMacbookAir() {
        String question = """
                At the Amazon.com website in various countries, a Macbook Air costs
                718.25 GPB, 83,900 INR, 749.99 USD, and 177,980 JPY.
                Which is the best deal?
                """;
        String answer = assistant.chat(question);
        System.out.println(answer);
    }
}
