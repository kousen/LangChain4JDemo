package com.kousenit;

import com.kousenit.services.Assistant;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ConversationTest {
    private final Conversation conversation = new Conversation();

    public final ChatModel gpt4o = AiModels.GPT_4_O;

    public final ChatModel gemini = AiModels.GEMINI_FLASH;

    @Test
    void statelessDemo() {
        String firstAnswer = gpt4o.chat("My name is Inigo Montoya.");
        String secondAnswer = gpt4o.chat("What's my name?");
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
        AiMessage firstResponse = gpt4o.chat(memory.messages()).aiMessage();
        memory.add(firstResponse);
        String firstAnswer = firstResponse.text();

        memory.add(UserMessage.from("What's my name?"));
        AiMessage secondResponse = gpt4o.chat(memory.messages()).aiMessage();
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

    private Assistant createAssistant(ChatModel model) {
        return AiServices.builder(Assistant.class)
                .chatModel(model)
                .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                .build();
    }

    @Test
    void statefulGPT4oWithAssistant() {
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

    @Test
    void critique() {
        Document libretto = FileSystemDocumentLoader.loadDocument(
                "src/main/resources/libretto.md");
        String query = """
                Write a detailed literary critique of the following
                libretto, from an opera entitled
                "Whispers of the Lost Hartford,"
                suitable for a newspaper review or academic journal:
                %s
                """.formatted(libretto.text());
        ChatResponse response = gemini.chat(
                List.of(SystemMessage.from("""
                        You are an experienced music critic
                        with a deep knowledge of opera.
                        """),
                        UserMessage.from(query)));
        System.out.println(response.aiMessage().text());
        System.out.println(response.tokenUsage());
    }

}