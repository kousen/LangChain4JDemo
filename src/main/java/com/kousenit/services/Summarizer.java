package com.kousenit.services;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface Summarizer {
    @SystemMessage("""
        Summarize every message from user in {{n}} bullet points.
        Provide only bullet points.""")
    List<String> summarize(@UserMessage String text, @V("n") int n);
}
