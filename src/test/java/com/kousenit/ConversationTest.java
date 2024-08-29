package com.kousenit;

import com.kousenit.services.Assistant;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationTest {
    private final Conversation conversation = new Conversation();

    public final ChatLanguageModel gpt4o = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .modelName(OpenAiChatModelName.GPT_4_O)
            .build();

    @Test
    void statelessDemo() {
        String firstAnswer = gpt4o.generate("My name is Inigo Montoya.");
        String secondAnswer = gpt4o.generate("What's my name?");
        List<String> response = List.of(firstAnswer, secondAnswer);
        System.out.println(response);
        assertThat(response)
                .hasSize(2)
                .satisfies(l -> {
                    assertThat(l.getFirst()).contains("Inigo Montoya");
                    assertThat(l.getLast()).doesNotContain("Inigo Montoya");
                });
    }

    @Test
    void statefulDemo() {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        memory.add(UserMessage.from("My name is Inigo Montoya."));
        AiMessage firstResponse = gpt4o.generate(memory.messages()).content();
        memory.add(firstResponse);
        String firstAnswer = firstResponse.text();

        memory.add(UserMessage.from("What's my name?"));
        AiMessage secondResponse = gpt4o.generate(memory.messages()).content();
        memory.add(secondResponse);
        String secondAnswer = secondResponse.text();
        List<String> response = List.of(firstAnswer, secondAnswer);

        System.out.println(response);
        assertThat(response)
                .hasSize(2)
                .satisfies(l -> {
                    assertThat(l.getFirst()).contains("Inigo Montoya");
                    assertThat(l.getLast()).contains("Inigo Montoya");
                });
    }

    private Assistant createAssistant(ChatLanguageModel model) {
        return AiServices.builder(Assistant.class)
                .chatLanguageModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Test
    void statefulGPTroWithAssistant() {
        Assistant assistant = createAssistant(gpt4o);
        String firstAnswer = assistant.chat("My name is Inigo Montoya.");
        String secondAnswer = assistant.chat("What's my name?");
        List<String> response = List.of(firstAnswer, secondAnswer);
        System.out.println(response);
        assertThat(response)
                .hasSize(2)
                .satisfies(l -> {
                    assertThat(l.getFirst()).contains("Inigo Montoya");
                    assertThat(l.getLast()).contains("Inigo Montoya");
                });
    }

    @Test
    void testTalkToEachOther() {
        conversation.talkToEachOther();
    }

    @Test
    void anotherTry() {
        conversation.anotherTry("""
                Tell me a story about a soprano, a tenor, and a baritone.
                If one is already in progress, please continue.
                """);
    }

}