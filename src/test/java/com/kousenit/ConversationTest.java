package com.kousenit;

import org.junit.jupiter.api.Test;

class ConversationTest {
    private final Conversation conversation = new Conversation();

    @Test
    void testTalkToEachOther() {
        conversation.talkToEachOther();
    }

    @Test
    void anotherTry() {
        conversation.anotherTry("""
                Tell me a story about a soprano, a tenor, and a baritone.
                If one is already in progress, please continue.
                """);
    }

}