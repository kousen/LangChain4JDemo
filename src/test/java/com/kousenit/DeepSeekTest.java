package com.kousenit;

import com.kousenit.services.Assistant;
import com.kousenit.tools.Calculator;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.service.AiServices;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeepSeekTest {
    private ChatLanguageModel model;

    static List<ChatLanguageModel> getModels() {
        return List.of(AiModels.DEEPSEEK_CHAT, AiModels.DEEPSEEK_R1);
    }

    @Nested
    class ChatModels {

        @Test
        public void useDeepSeekChat() {
            model = AiModels.DEEPSEEK_CHAT;

            UserMessage userMessage = UserMessage.from("""
                    How many r's are in the word "strawberry"?
                    """);
            ChatResponse response = model.chat(userMessage);
            System.out.println(response);
        }

        @Test
        public void useDeepSeekR1() {
            model = AiModels.DEEPSEEK_R1;

            UserMessage userMessage = UserMessage.from("""
                    Given our concerns about personal information
                    both with technology companies and foreign countries,
                    should we be using AI tools at all? If so, should
                    we favor those developed in the US? Why or why not?
                    """);
            ChatResponse response = model.chat(userMessage);
            System.out.println(response);
        }

        @Test
        void streamingDeepSeekChat() throws InterruptedException {
            StreamingChatLanguageModel chatModel = OpenAiStreamingChatModel.builder()
                    .baseUrl("https://api.deepseek.com")
                    .apiKey(ApiKeys.DEEPSEEK_API_KEY)
                    .modelName("deepseek-reasoner")
                    .build();

            var latch = new CountDownLatch(1);
            chatModel.chat("Tell me a joke about AI.",
                    new StreamingChatResponseHandler() {
                        @Override
                        public void onPartialResponse(String partialResponse) {
                            System.out.println("onPartialResponse(): " + partialResponse);
                        }

                        @Override
                        public void onError(Throwable error) {
                            System.err.println("onError(): " + error.getMessage());
                        }

                        @Override
                        public void onCompleteResponse(ChatResponse response) {
                            System.out.println("complete");
                            latch.countDown();
                        }
                    });

            boolean completed = latch.await(30, TimeUnit.SECONDS);
            assertTrue(completed, "Async call did not complete in time");
        }
    }

    @Nested
    class ConversationalState {
        @Test
        void statelessConversation() {
            model = AiModels.DEEPSEEK_CHAT;
            String response1 = model.chat("Hello, my name is Inigo Montoya.");
            System.out.println("Response 1:\n" + response1);
            String response2 = model.chat("What's my name?");
            System.out.println("Response 2:\n" + response2);
            assertFalse(response2.contains("Inigo Montoya"));
        }

        @Test
        void statefulConversation() {
            model = AiModels.DEEPSEEK_CHAT;

            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .chatMemory(MessageWindowChatMemory.withMaxMessages(10))
                    .build();

            String response1 = assistant.chat("Hello, my name is Inigo Montoya.");
            System.out.println("Response 1:\n" + response1);
            String response2 = assistant.chat("What's my name?");
            System.out.println("Response 2:\n" + response2);
            assertTrue(response2.contains("Inigo Montoya"));
        }
    }

    @Nested
    @Disabled("Vision models not yet supported")
    class VisionModels {

        @ParameterizedTest(name = "{index}: Test with model = {0}")
        @MethodSource("com.kousenit.DeepSeekTest#getModels")
        void vision_from_localFile(ChatLanguageModel deepSeekModel) throws IOException {
            byte[] fileBytes;
            try (InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream("skynet.jpg")) {
                if (inputStream == null) {
                    throw new FileNotFoundException("File skynet.jpg not found in resources");
                }
                fileBytes = inputStream.readAllBytes();
            }

            String base64Data = Base64.getEncoder().encodeToString(fileBytes);

            UserMessage userMessage = UserMessage.from(
                    TextContent.from("""
                            My boss wants me to embed
                            an AI model into this robot,
                            which my company (identified
                            by the logo in the picture)
                            is planning to build.
                            
                            What could go wrong?
                            """),
                    new ImageContent(base64Data, "image/jpg")
            );

            ChatResponse response = deepSeekModel.chat(userMessage);
            System.out.println(response.aiMessage().text());
            System.out.println(response.tokenUsage());
        }

        @ParameterizedTest(name = "model = {0}")
        @MethodSource("com.kousenit.DeepSeekTest#getModels")
        void vision_from_publicURL(ChatLanguageModel deepSeekModel) {
            String imageUrl =
                    "https://upload.wikimedia.org/wikipedia/commons/a/a0/Hello_Kitty_coffee.jpg";
            UserMessage userMessage = UserMessage.from(
                    TextContent.from("What character is shown in this image?"),
                    ImageContent.from(imageUrl)
            );
            ChatResponse response = deepSeekModel.chat(userMessage);
            System.out.println(response.aiMessage().text());
            System.out.println(response.tokenUsage());
        }
    }

    @Nested
    class FunctionCalling {
        @Test
        void testFromLangChain4JExampleProject() {
            model = AiModels.DEEPSEEK_CHAT;

            Assistant assistant = AiServices.builder(Assistant.class)
                    .chatLanguageModel(model)
                    .tools(new Calculator())
                    .build();

            String question = """
                    What is the square root of the sum of the numbers of letters
                    in the words "hello" and "world"?
                    """;
            String answer = assistant.chat(question);
            System.out.println("Answer: " + answer);
            assertTrue(answer.contains("3.162"));
        }
    }

    @Nested
    class RecordExtractor {
        record Person(
                String first,
                String last,
                LocalDate dateOfBirth
        ) {
        }

        interface PersonExtractor {
            Person extractPerson(String text);
        }

        @Test
        void extractPerson() {
            model = AiModels.DEEPSEEK_CHAT;

            var personExtractor = AiServices.builder(PersonExtractor.class)
                    .chatLanguageModel(model)
                    .build();

            String message = """
                    Captain Picard was born in La Barre, France on Earth
                    on the 13th of juillet, in the year 2305.
                    His given name, Jean-Luc, is of French origin.
                    He and his brother Robert were raised on the family
                    vineyard, Chateau Picard, located in Saint-Estephe,
                    in Burgundy. A running gag among Star Trek fans is
                    that the wine from Chateau Picard is awful.
                    """.stripIndent();

            Person jeanLuc = personExtractor.extractPerson(message);
            System.out.println(jeanLuc);
        }
    }

}
