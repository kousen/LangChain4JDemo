package com.kousenit;

import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenizerTest {
    private final Tokenizer tokenizer = new OpenAiTokenizer();

    @Test
    void singleLine() {
        String text = "This is a test";
        int count = tokenizer.estimateTokenCountInText(text);
        assertThat(count).isEqualTo(4);
    }

    @Test
    void longerLine() {
        String text = "This is a test of the emergency broadcast system";
        int count = tokenizer.estimateTokenCountInText(text);
        assertThat(count).isEqualTo(9);
    }

    @Test
    void multilineTest() {
        String text = """
                This is a test of the emergency broadcast system.
                This is only a test.
                If this had been an actual emergency,
                you would have been instructed where to go and what to do.
                """;
        int wordCount = text.split("\\s+").length;
        int tokenCount = tokenizer.estimateTokenCountInText(text);
        assertThat(wordCount).isEqualTo(33);
        assertThat(tokenCount).isEqualTo(37);
    }
}
