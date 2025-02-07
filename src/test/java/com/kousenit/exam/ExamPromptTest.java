package com.kousenit.exam;

import com.kousenit.ApiKeys;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;

import static java.time.Duration.ofSeconds;

class ExamPromptTest {

    private final ChatLanguageModel model = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .timeout(ofSeconds(60))
            .logRequests(true)
            .logResponses(true)
            .build();

    @Test
    void javaAndTopic() {
        ExamPrompt record = new ExamPrompt("Java", "Streams");

        Prompt prompt = StructuredPromptProcessor.toPrompt(record);
        String exam = model.chat(prompt.text());
        System.out.println(exam);
    }

}