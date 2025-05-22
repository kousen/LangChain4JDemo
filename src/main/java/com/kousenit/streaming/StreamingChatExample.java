package com.kousenit.streaming;

import com.kousenit.ApiKeys;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Example demonstrating streaming chat responses from LLMs using LangChain4j
 * This allows for real-time display of responses as they are generated
 */
public class StreamingChatExample {

    public static void main(String[] args) {
        // Create a streaming chat model
        StreamingChatModel model = OpenAiStreamingChatModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiChatModelName.GPT_4_O)
                .build();

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Welcome to the Streaming Chat Example!");
            System.out.println("Type 'exit' to quit.");
            
            while (true) {
                System.out.print("\nEnter your message: ");
                String input = scanner.nextLine();
                
                if ("exit".equalsIgnoreCase(input.trim())) {
                    System.out.println("Goodbye!");
                    break;
                }
                
                // Create a latch to wait for the response to complete
                CountDownLatch latch = new CountDownLatch(1);
                
                System.out.println("\nResponse:");

                StringBuilder fullResponse = new StringBuilder();

                // Send the message and handle the streaming response
                model.chat(input, new StreamingChatResponseHandler() {

                    @Override
                    public void onPartialResponse(String partialResponse) {
                        // Print the partial response without a newline to simulate typing
                        System.out.print(partialResponse);
                        fullResponse.append(partialResponse);
                    }
                    
                    @Override
                    public void onError(Throwable error) {
                        System.err.println("\nError: " + error.getMessage());
                        latch.countDown();
                    }
                    
                    @Override
                    public void onCompleteResponse(ChatResponse response) {
                        System.out.println("\n\nResponse complete. Token usage: " + 
                                response.tokenUsage().totalTokenCount() + " tokens");
                        latch.countDown();
                    }
                });

                System.out.println(fullResponse.length() + " characters received so far.");
                
                try {
                    // Wait for the response to complete
                    boolean competed = latch.await(60, TimeUnit.SECONDS);
                    if (!competed) {
                        System.err.println("Response timed out after 60 seconds.");
                    }
                } catch (InterruptedException e) {
                    System.err.println("Interrupted while waiting for response: " + e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}