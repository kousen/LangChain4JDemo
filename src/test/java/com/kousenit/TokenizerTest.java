package com.kousenit;

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

    @ParameterizedTest(name = "{0}")
    @MethodSource("tokenizers")
    void singleLine(Tokenizer tokenizer) {
        String text = "This is a test";
        int count = tokenizer.estimateTokenCountInText(text);
        assertThat(count).isEqualTo(4);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tokenizers")
    void longerLine(Tokenizer tokenizer) {
        String text = "This is a test of the emergency broadcast system";
        int count = tokenizer.estimateTokenCountInText(text);
        assertThat(count).isEqualTo(9);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("tokenizers")
    void multilineTest(Tokenizer tokenizer) {
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
