# Structured Data Extraction Examples

This package contains classes demonstrating how to use LangChain4j for structured data extraction from unstructured text.

## Classes

### PersonExtraction

This class provides methods to extract structured `Person` records from text. It demonstrates:

- Creating a `PersonExtractor` service using `AiServices.builder`
- Extracting a `Person` record with nested `Address` from unstructured text
- Formatting the extracted information for display

The class is tested by `PersonExtractionTest` which verifies the extraction functionality with different text examples.

### DateTimeExtraction

This class provides methods to extract date and time information from text. It demonstrates:

- Creating a `DateTimeExtractor` service using `AiServices.builder`
- Extracting `LocalDate`, `LocalTime`, and `LocalDateTime` objects from text
- Formatting the extracted information using customizable formatters
- Handling different date and time formats in natural language

The class is tested by `DateTimeExtractionTest` which verifies the extraction and formatting functionality.

## How It Works

LangChain4j uses the LLM's ability to understand natural language to extract structured data. The process works as follows:

1. Define an interface with methods that return the desired structured data types
2. Annotate the methods with `@UserMessage` to provide prompt templates
3. Create an implementation of the interface using `AiServices.builder`
4. Call the methods with text containing the information to extract

The LLM will parse the text and return the structured data in the format specified by the return type.

## Usage Tips

- Set `temperature` to 0.0 for more deterministic extraction results
- Use try-catch blocks to handle cases where the information cannot be extracted
- Provide clear examples in your prompts to guide the extraction process
