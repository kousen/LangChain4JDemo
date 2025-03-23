package com.kousenit.images;

import com.kousenit.ApiKeys;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModelName;
import dev.langchain4j.model.output.Response;

import java.nio.file.Path;

/**
 * Demonstrates how to use the OpenAIImageModel in LangChain4j beta2+
 * with alternatives to the removed persistTo functionality
 */
public class OpenAiImageModelDemo {
    public static void main(String[] args) {
        // Create the image model
        var model = OpenAiImageModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiImageModelName.DALL_E_3)
                // The persistTo method was removed in LangChain4j 1.0.0-beta2
                // .persistTo(Paths.get("src/main/resources"))  
                .build();
        
        // Generate an image
        Response<Image> imageResponse = model.generate("""
                A warrior cat with a sword
                riding into battle on the
                back of a dragon
                """);
        
        // Print image details
        System.out.println("Image URL: " + imageResponse.content().url());
        System.out.println("Revised prompt: " + imageResponse.content().revisedPrompt());
        System.out.println("Tokens: " + imageResponse.tokenUsage());
        
        // Use our custom ImageSaver utility to save the image
        // This replaces the persistTo functionality
        Path savedImagePath = ImageSaver.saveImage(imageResponse, "src/main/resources");
        
        if (savedImagePath != null) {
            System.out.println("Successfully saved image to: " + savedImagePath);
        } else {
            System.err.println("Failed to save image");
        }
    }
}
