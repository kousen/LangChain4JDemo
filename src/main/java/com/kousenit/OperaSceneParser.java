package com.kousenit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperaSceneParser {

    record Scene(int number, String title, String content) {}

    private static final String RESOURCE_PATH = "src/main/resources";

    public static void main(String[] args) {
        try {
            Path inputPath = Paths.get(RESOURCE_PATH, "libretto.md");
            String markdownContent = Files.readString(inputPath);
            List<Scene> scenes = parseScenes(markdownContent);
            printScenes(scenes);
            saveScenesToFiles(scenes);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }

    private static List<Scene> parseScenes(String markdownContent) {
        List<Scene> parsedScenes = new ArrayList<>();
        Pattern scenePattern = Pattern.compile("### Scene (\\d+): (.*)");
        String[] rawScenes = markdownContent.split("(?=### Scene \\d+:)");

        System.out.println("Number of raw scenes: " + rawScenes.length);

        for (int i = 0; i < rawScenes.length; i++) {
            String rawScene = rawScenes[i];
            Matcher matcher = scenePattern.matcher(rawScene);

            System.out.println("Processing raw scene " + (i + 1) + ":");
            System.out.println(rawScene.substring(0, Math.min(100, rawScene.length())) + "...");

            if (matcher.find()) {
                int sceneNumber = Integer.parseInt(matcher.group(1));
                String title = matcher.group(2).strip();
                String content = cleanContent(rawScene.substring(matcher.end()));
                parsedScenes.add(new Scene(sceneNumber, title, content));
                System.out.println("Parsed scene " + sceneNumber + ": " + title);
            } else {
                System.out.println("Failed to match scene pattern for scene " + (i + 1));
            }
            System.out.println("---");
        }

        return parsedScenes;
    }

    private static String cleanContent(String content) {
        return content
                .replaceAll("--------- .*? ---------", "")
                .strip();
    }

    private static void printScenes(List<Scene> scenes) {
        for (Scene scene : scenes) {
            System.out.printf("Scene %d: %s%n", scene.number(), scene.title());
            System.out.println(scene.content().substring(0, Math.min(100, scene.content().length())) + "...");
            System.out.println("=".repeat(50) + "\n");
        }
    }

    private static void saveScenesToFiles(List<Scene> scenes) {
        for (Scene scene : scenes) {
            String filename = String.format("scene_%d_%s.txt",
                    scene.number(),
                    scene.title().replaceAll("\\s+", "_").toLowerCase());
            Path outputPath = Paths.get(RESOURCE_PATH, filename);
            try {
                String content = String.format("""
                    Scene %d: %s

                    %s
                    """, scene.number(), scene.title(), scene.content());
                Files.writeString(outputPath, content);
                System.out.println("Saved " + outputPath);
            } catch (IOException e) {
                System.err.println("Error writing to file " + filename + ": " + e.getMessage());
            }
        }
    }
}