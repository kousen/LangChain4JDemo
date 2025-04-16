# Streaming Chat Examples

This package contains examples demonstrating how to use LangChain4j for streaming chat responses from LLMs.

## Examples

### StreamingChatExample

This example shows how to create an interactive chat application that streams responses in real-time. It demonstrates:

- Creating a `StreamingChatLanguageModel` using `OpenAiStreamingChatModel.builder`
- Implementing a `StreamingChatResponseHandler` to process streaming responses
- Displaying partial responses as they are generated
- Tracking token usage for the complete response

## How It Works

LangChain4j provides streaming capabilities that allow you to receive and process responses from LLMs in real-time. The process works as follows:

1. Create a `StreamingChatLanguageModel` instance
2. Implement a `StreamingChatResponseHandler` with three key methods:
   - `onPartialResponse`: Called for each chunk of the response as it's generated
   - `onError`: Called if there's an error during streaming
   - `onCompleteResponse`: Called when the complete response has been received
3. Send a message to the model using the `chat` method, passing your handler
4. Process the streaming response in your handler methods

## Benefits of Streaming

- **Improved User Experience**: Users see responses as they're generated, rather than waiting for the complete response
- **Faster Perceived Response Time**: The first parts of the response appear quickly
- **Ability to Cancel Long Responses**: You can implement cancellation if the response is taking too long
- **Progress Indication**: Users can see that the system is actively working on their request

## Usage Tips

- Use a `CountDownLatch` to coordinate waiting for the complete response
- Consider implementing a timeout mechanism for responses that take too long
- For web applications, you can use Server-Sent Events (SSE) or WebSockets to stream responses to the client
- Remember to handle errors gracefully in the `onError` method