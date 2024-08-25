package com.kousenit.images;

import com.kousenit.ApiKeys;
import dev.langchain4j.model.openai.OpenAiImageModel;

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
                .persistTo(Paths.get(RESOURCE_PATH))
                .build();

        sceneContents.forEach((fileName, content) -> {
            String prompt = generatePrompt(fileName, content);
            String desiredFileName = generateImageFileName(fileName);

            try {
                model.generate(prompt);

                // The API generates a file with a random name. We need to find and rename it.
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(RESOURCE_PATH), "*.png")) {
                    for (Path entry : stream) {
                        if (Files.getLastModifiedTime(entry).toMillis() > System.currentTimeMillis() - 5000) {
                            // This file was likely just created by our API call
                            Path newPath = entry.resolveSibling(desiredFileName);
                            Files.move(entry, newPath, StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Generated and renamed image: " + newPath);
                            break;
                        }
                    }
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
