package com.kousenit.services;

import com.kousenit.AiModels;
import com.kousenit.ApiKeys;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static java.time.Duration.ofSeconds;
import static org.assertj.core.api.Assertions.assertThat;

class TranslatorTest {
    private final ChatModel model = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .timeout(ofSeconds(60))
            .build();

    @Test
    void translateUsingService() {
        Translator translator = AiServices.create(Translator.class,
                AiModels.GPT_4_O);

        String italian = translator.translate("Hello, how are you?", "Italian");
        assertThat(italian).isEqualTo("Ciao, come stai?");
    }

    @Test
    void usePromptTemplateInstead() {
        PromptTemplate promptTemplate = PromptTemplate.from("Say '{{text}}' in {{language}}.");
        Prompt prompt = promptTemplate.apply(
                Map.ofEntries(
                        Map.entry("text", "Hello, how are you?"),
                        Map.entry("language", "Italian")));

        String italian = model.chat(prompt.text());
        System.out.println(italian);
        assertThat(italian).contains("Ciao");
    }
}