package com.kousenit;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenizerTest {

    private final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private final Encoding encoding = registry.getEncodingForModel(ModelType.GPT_4O);

    @Test
    void singleLine() {
        String text = "This is a test.";
        int wordCount = text.split("\\s+").length;
        int tokenCount = encoding.countTokens(text);
        assertThat(wordCount).isEqualTo(4);
        assertThat(tokenCount).isEqualTo(5);
    }

    @Test
    void longerLine() {
        String text = "This is a much longer line of text to see what happens.";
        int wordCount = text.split("\\s+").length;
        int tokenCount = encoding.countTokens(text);
        assertThat(wordCount).isEqualTo(12);
        assertThat(tokenCount).isEqualTo(13);
    }

    @Test
    void multilineTest() {
        String text = """
                This is a test of the emergency broadcast system.
                This is only a test.
                In the event of an actual emergency,
                you would be instructed where to tune
                in your area for news and official information.
                """;
        int wordCount = text.split("\\s+").length;
        int tokenCount = encoding.countTokens(text);

        var softly = new SoftAssertions();
        softly.assertThat(wordCount).isEqualTo(36);
        softly.assertThat(tokenCount).isEqualTo(41);
        softly.assertAll();
    }
}