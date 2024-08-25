package com.kousenit.exam;

import dev.langchain4j.model.input.structured.StructuredPrompt;

@StructuredPrompt({
        """
        Create a quiz about {{topic}} in the {{language}} programming language.
        Structure your answer in the following way:
        It should have three to five questions.
        The questions should be numbered and the answers should be multiple choice,
        where the choices are labeled A, B, C.
        There should only be one correct answer for each question, which should
        be labeled with an asterisk (*).
        
        The output response should be:
        Question 1: ...
        A: ...
        B: ...
        C: ...
        
        Question 2: ...
        A: ...
        B: ...
        C: ...
        """
})
public record ExamPrompt(String language, String topic) {
}
