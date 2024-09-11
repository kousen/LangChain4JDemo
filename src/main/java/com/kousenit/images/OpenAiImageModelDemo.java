package com.kousenit.images;

import com.kousenit.ApiKeys;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.output.Response;

import java.nio.file.Paths;

public class OpenAiImageModelDemo {
    public static void main(String[] args) {
//        var model = OpenAiImageModel.withApiKey(ApiKeys.OPENAI_API_KEY);
//        Response<Image> response = model.generate("""
//                A photo-realistic image of a
//                chicken crossing a road
//                """);
//        System.out.println(response.content().url());

        var model = OpenAiImageModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .persistTo(Paths.get("src/main/resources"))
                .build();
        Response<Image> imageResponse = model.generate("""
                A warrior cat with a sword
                riding into battle on the
                back of a dragon
                """);
        System.out.println(imageResponse.content().url());
        System.out.println("Revised prompt: " + imageResponse.content().revisedPrompt());
        System.out.println("Tokens: " + imageResponse.tokenUsage());
    }
}
