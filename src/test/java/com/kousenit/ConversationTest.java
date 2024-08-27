package com.kousenit;

import dev.langchain4j.model.chat.ChatLanguageModel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationTest {
    private final Conversation conversation = new Conversation();

    @Test
    void statelessDemo() {
        List<String> response = conversation.statelessDemo();
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
        List<String> response = conversation.statefulDemo();
        System.out.println(response);
        assertThat(response)
                .hasSize(2)
                .satisfies(l -> {
                    assertThat(l.getFirst()).contains("Inigo Montoya");
                    assertThat(l.getLast()).contains("Inigo Montoya");
                });
    }

    static Stream<ChatLanguageModel> provideLanguageModels() {
        Conversation conv = new Conversation();
        return Stream.of(conv.gpt4o, conv.claude);
    }

    @ParameterizedTest
    @MethodSource("provideLanguageModels")
    void statefulDemoWithAssistant(ChatLanguageModel model) {
        List<String> response = conversation.statefulDemoWithAssistant(model);
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