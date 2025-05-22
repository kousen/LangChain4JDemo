package com.kousenit

import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.UserMessage
import dev.langchain4j.model.chat.ChatModel
import dev.langchain4j.model.ollama.OllamaChatModel
import dev.langchain4j.model.output.Response
import org.junit.jupiter.api.Test

class DeepSeekOllamaTest {
    ChatModel ollama = OllamaChatModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("deepseek-r1")
            .build()

    @Test
    void testChat() {
        def userMessage = UserMessage.from(
                '''How many r's are in the word "strawberry"?''')
        Response<AiMessage> aiMessage = ollama.generate(userMessage)
        println aiMessage
    }

}
