package com.kousenit;

// Tokenizer classes have been removed or moved in LangChain4j 1.0.0
// This test is disabled until the new tokenizer API is available

/*
import dev.langchain4j.model.Tokenizer;
import dev.langchain4j.model.embedding.onnx.HuggingFaceTokenizer;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenizerTest {

    static Stream<Tokenizer> tokenizers() {
        return Stream.of(new OpenAiTokenizer(OpenAiChatModelName.GPT_4_O),
                         new HuggingFaceTokenizer());
    }

    @ParameterizedTest
    @MethodSource("tokenizers")
    void singleLine(Tokenizer tokenizer) {
        String text = "This is a test.";
        int wordCount = text.split("\\s+").length;
        int tokenCount = tokenizer.estimateTokenCountInText(text);
        assertThat(wordCount).isEqualTo(4);
        assertThat(tokenCount).isEqualTo(5);
    }

    @ParameterizedTest
    @MethodSource("tokenizers")
    void longerLine(Tokenizer tokenizer) {
        String text = "This is a much longer line of text to see what happens.";
        int wordCount = text.split("\\s+").length;
        int tokenCount = tokenizer.estimateTokenCountInText(text);
        assertThat(wordCount).isEqualTo(12);
        assertThat(tokenCount).isEqualTo(13);
    }

    @ParameterizedTest
    @MethodSource("tokenizers")
    void multilineTest(Tokenizer tokenizer) {
        String text = """
                This is a test of the emergency broadcast system.
                This is only a test.
                In the event of an actual emergency,
                you would be instructed where to tune
                in your area for news and official information.
                """;
        int wordCount = text.split("\\s+").length;
        int tokenCount = tokenizer.estimateTokenCountInText(text);
        assertThat(wordCount).isEqualTo(33);
        assertThat(tokenCount).isEqualTo(37);
    }
}
*/