package com.kousenit

import org.junit.jupiter.api.Test

class DeepSeekDirectTest {

    @SuppressWarnings('GroovyAssignabilityCheck')
    @Test
    void deepSeekDirectLink() {
        def responseMap = DeepSeekDirect.chat(
                "How many r's are in the word 'strawberry'?",
                "deepseek-reasoner")

        println responseMap.usage
        def message = responseMap.choices[0].message
        println "Content: ${message.content}\n\n"
        println "Reasoning: ${message.reasoning_content ?: 'None'}"
    }

}
