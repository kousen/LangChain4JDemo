package com.kousenit;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AnthropicTest {
    private final ChatLanguageModel model = AiModels.CLAUDE_SONNET;

    @Test
    void test() {
        System.out.println("Testing the new model: (should be claude-3-5-sonnet-20241022");
        ChatResponse response = model.chat(
                UserMessage.from("""
                Shall I compare thee to a summer's day?
                Thou art more lovely and more temperate:
                Rough winds do shake the darling buds of May,
                And summer's lease hath all too short a date:
                Sometime too hot the eye of heaven shines,
                And often is his gold complexion dimm'd;
                And every fair from fair sometime declines,
                By chance or nature's changing course untrimm'd;
                But thy eternal summer shall not fade
                Nor lose possession of that fair thou owest;
                Nor shall Death brag thou wanderest in his shade,
                When in eternal lines to time thou growest:
                So long as men can breathe or eyes can see,
                So long lives this, and this gives life to thee.
    }"""));
        System.out.println(response);
        assertThat(response).isNotNull();
    }
}
