package com.kousenit;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

// Based on LangChain4J home page example at
// https://github.com/langchain4j/langchain4j
public class HelloLangChain {
    public static void main(String[] args) {

        // ChatLanguageModel model = OpenAiChatModel.withApiKey(ApiKeys.OPENAI_API_KEY);

        ChatLanguageModel model = OpenAiChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        System.out.println(model.generate("""
                Write a cover letter for a Java developer
                applying for an AI job, but use pirate speak.
                """));

        System.out.println(model.generate("""
                I got the job. Now help me write a memo to
                my boss explaining why I need a medical exemption
                to work from home because I'm allergic to traffic.
                """));

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
    }
}
