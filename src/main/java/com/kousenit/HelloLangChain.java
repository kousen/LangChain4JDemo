package com.kousenit;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;

// Based on LangChain4J home page example at
// https://github.com/langchain4j/langchain4j
public class HelloLangChain {
    public static void main(String[] args) {

        // ChatLanguageModel model = OpenAiChatModel.withApiKey(ApiKeys.OPENAI_API_KEY);

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

//                """));

        /*
        String answer = model.generate("""
                What are the FAANG companies?
                """);

        System.out.println(answer + "\n");

        Response<AiMessage> response = model.generate(
                UserMessage.from("""
                        What are they called again?
                        """));

        System.out.println(response);

         */

        UserMessage userMessage = UserMessage.from("""
                Hello, my name is Inigo Montoya.
                """);
        Response<AiMessage> aiMessage = model.generate(userMessage);
        //System.out.println(aiMessage);
        switch (aiMessage.finishReason()) {
            case STOP -> System.out.println(aiMessage.content().text());
            case LENGTH -> System.out.println("Token length reached");
            case TOOL_EXECUTION -> System.out.println("Tool execution needed");
            case CONTENT_FILTER -> System.out.println("Content filtering required");
            case OTHER -> System.out.println("Call finished for some other reason");
        }

        TokenUsage tokenUsage = aiMessage.tokenUsage();
        System.out.println("Input tokens: " + tokenUsage.inputTokenCount());
        System.out.println("Output tokens: " + tokenUsage.outputTokenCount());
        System.out.println("Total tokens: " + tokenUsage.totalTokenCount());
    }
}
