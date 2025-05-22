package com.kousenit.tools;

import com.kousenit.AiModels;
import com.kousenit.services.Assistant;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

public class WeatherAiTest {
    private final Assistant assistant = AiServices.builder(Assistant.class)
            .chatModel(AiModels.GPT_4_O)
            .tools(new OpenWeatherMap())
            .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
            .build();

    @Test
    void testWeather() {
        String question = "What is the weather forecast for Bangalore, India?";
        String answer = assistant.chat(question);
        System.out.println(answer);
    }
}
