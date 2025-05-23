package com.kousenit;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModelName;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

public class Conversation {

    public final ChatModel gpt4o = OpenAiChatModel.builder()
            .apiKey(ApiKeys.OPENAI_API_KEY)
            .modelName(OpenAiChatModelName.GPT_4_O)
            .build();

    public final ChatModel claude = AnthropicChatModel.builder()
            .apiKey(ApiKeys.ANTHROPIC_API_KEY)
            .modelName(AnthropicChatModelName.CLAUDE_3_5_SONNET_20240620)
            .build();

    public void talkToEachOther() {
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        memory.add(SystemMessage.from("""
                They say that all operas are about a soprano
                who wants to sleep with the tenor, but the
                baritone won't let her. See, for example, La Traviata,
                Rigoletto, or Carmen.
                
                You are composing the libretto for such an opera.
                
                The setting is the wild jungles of Connecticut,
                in the not-so-distant future after global warming has
                reclaimed the land. The soprano is an intrepid
                explorer searching for the lost city of Hartford.
                The tenor is a native poet who has been living in
                the jungle for years, writing sonnets to the trees and
                composing symphonies for the monkeys.
                
                The baritone is a government agent who has been sent
                to stop the soprano from finding the lost city. He
                has a secret weapon: a giant robot that can sing
                Verdi arias in three different languages.
                
                The soprano and the tenor meet in the jungle and
                fall in love. They decide to join forces and find
                the lost city together. But the baritone is always
                one step behind them, and his giant robot is getting
                closer and closer.
                """));

        UserMessage userMessage = UserMessage.from("""
                Please write the next scene.
                """);
        memory.add(userMessage);

        ChatModel model;
        for (int i = 0; i < 5; i++) {
            model = i % 2 == 0 ? gpt4o : claude;
            ChatResponse response = model.chat(memory.messages());
            System.out.printf("--------- %s ---------%n", model.getClass().getSimpleName());
            System.out.println(response.aiMessage().text());
            memory.add(response.aiMessage());
            memory.add(userMessage);
        }
    }

    public void anotherTry(String initialPromptText) {
        // Initialize memory with a window of 10 messages
        ChatMemory memory = MessageWindowChatMemory.withMaxMessages(10);

        // Define the initial user prompt
        UserMessage initialPrompt = new UserMessage(initialPromptText);
        memory.add(initialPrompt);

        // Define the models to interact with (e.g., gpt4o and Claude)
        ChatModel[] models = {gpt4o, claude};

        // Loop through each model, generating a conversation
        for (ChatModel model : models) {
            // Generate the model's response
            ChatResponse response = model.chat(memory.messages());
            memory.add(response.aiMessage()); // Add the response to the memory

            // Print the conversation so far
            System.out.println(model.getClass().getSimpleName() + ": " + response.aiMessage().text());

            // Prepare a follow-up prompt for the next model based on the previous model's response
            UserMessage followUpPrompt = new UserMessage("What do you think about the previous response?");
            memory.add(followUpPrompt); // Add follow-up prompt to memory
        }
    }

}
