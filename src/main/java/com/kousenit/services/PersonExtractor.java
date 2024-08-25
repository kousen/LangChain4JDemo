package com.kousenit.services;

import dev.langchain4j.service.UserMessage;

public interface PersonExtractor {
//    @SystemMessage("""
//            Extract information about a person, using the fields
//            contained in the Person record in the classpath.
//            """)
    @UserMessage("Extract information from {{it}} into a Person record.")
    Person extractPersonFrom(String text);
}
