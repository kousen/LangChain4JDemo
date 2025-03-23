package com.kousenit.images;

import com.kousenit.ApiKeys;
import dev.langchain4j.data.image.Image;
import dev.langchain4j.model.openai.OpenAiImageModel;
import dev.langchain4j.model.openai.OpenAiImageModelName;
import dev.langchain4j.model.output.Response;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class OperaImageGenerator {

    private static final String RESOURCE_PATH = "src/main/resources";

    public static Map<String, String> readScenes() {
        Map<String, String> scenes = new HashMap<>();

        try (Stream<Path> paths = Files.walk(Paths.get(RESOURCE_PATH))) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".txt"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path);
                            String fileName = path.getFileName().toString();
                            scenes.put(fileName, content);
                            System.out.println("Read file: " + fileName);
                        } catch (IOException e) {
                            System.err.println("Error reading file: " + path + " - " + e.getMessage());
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error walking through files: " + e.getMessage());
        }

        return scenes;
    }

    public static void generateImages(Map<String, String> sceneContents) {
        var model = OpenAiImageModel.builder()
                .apiKey(ApiKeys.OPENAI_API_KEY)
                .modelName(OpenAiImageModelName.DALL_E_3)
                // The persistTo method was removed in LangChain4j 1.0.0-beta2
                // .persistTo(Paths.get(RESOURCE_PATH))
                .build();

        sceneContents.forEach((fileName, content) -> {
            String prompt = generatePrompt(fileName, content);
            String desiredFileName = generateImageFileName(fileName);

            try {
                // Generate the image
                Response<Image> imageResponse = model.generate(prompt);
                
                // Save the image using our utility class
                Path savedPath = ImageSaver.saveImage(imageResponse, RESOURCE_PATH);
                
                if (savedPath != null) {
                    // Rename the file to our desired filename
                    Path newPath = savedPath.resolveSibling(desiredFileName);
                    Files.move(savedPath, newPath, StandardCopyOption.REPLACE_EXISTING);
                    System.out.println("Generated and renamed image: " + newPath);
                } else {
                    System.err.println("Failed to save image for " + fileName);
                }
            } catch (IOException e) {
                System.err.println("Error generating or renaming image for " + fileName + ": " + e.getMessage());
            }
        });
    }

    private static String generatePrompt(String fileName, String content) {
        String[] parts = fileName.split("_", 3);
        int sceneNumber = Integer.parseInt(parts[1]);
        String sceneTitle = parts[2].replace(".txt", "").replace("_", " ");

        return "Create an illustration for Scene " + sceneNumber + ": " + sceneTitle +
               ". The scene description: " + content.substring(0, Math.min(500, content.length()));
    }

    private static String generateImageFileName(String sceneFileName) {
        String[] parts = sceneFileName.split("_", 3);
        return parts[0] + "_" + parts[1] + "_illustration.png";
    }

    public static void main(String[] args) {
        Map<String, String> sceneContents = readScenes();
        generateImages(sceneContents);
    }
}
