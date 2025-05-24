# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build/Test Commands
- Build: `./gradlew build`
- Run tests: `./gradlew test`
- Run specific test: `./gradlew test --tests "com.kousenit.OpenAiTest"`
- Run specific test method: `./gradlew test --tests "com.kousenit.OpenAiTest.testGenerateWithMessages"`
- Run MYM application: `./gradlew runMYM`
- Run image demo: `./gradlew runImageDemo`

## LangChain4j Information
- Version: 1.0.0 (stable release)
- Key API classes: Use `ChatModel` (not `ChatLanguageModel`), `StreamingChatModel` (not `StreamingChatLanguageModel`)
- AiServices: Use `.chatModel()` method (not `.chatLanguageModel()`)
- Tokenizer classes: OpenAiTokenizer and HuggingFaceTokenizer were removed in 1.0.0
- Model instances: Available in `AiModels.java` (e.g., `AiModels.GPT_4_O`, `AiModels.CLAUDE_SONNET`)

## Code Style Guidelines
- Indentation: 4 spaces
- Imports: Organized by package, no wildcards
- Naming: CamelCase for classes/methods, UPPER_SNAKE_CASE for constants
- Types: Use records for DTOs, var for local variables with clear initialization
- Patterns: Builder pattern with fluent API, services as interfaces with annotations
- Error handling: RuntimeException for checked exceptions, try-with-resources
- String literals: Use text blocks (""") for multi-line strings
- Conditional expressions: Use modern switch expressions with pattern matching
- Testing: JUnit 5 with descriptive method names, AssertJ for assertions

## Project Management
- Create GitHub issues for new features, enhancements, and significant updates
- Use descriptive issue titles and include task checklists
- Reference related commits in issue descriptions
- Use appropriate labels (enhancement, bug, security, etc.)
- Assign issues to track ownership and progress