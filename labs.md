# Labs for LangChain4j

## Lab 1: Create a new project

- At the [OpenAI developer site](https://platform.openai.com/docs/overview), sign up for an account and create an API key. The link to your API keys is at https://platform.openai.com/api-keys . OpenAI now recommends project API keys, but you don't have to use them if you prefer a single key for everything. See [this page](https://help.openai.com/en/articles/9186755-managing-your-work-in-the-api-platform-with-projects) for more information.
- Add the API key to an environment variable called `OPENAI_API_KEY`.
- Create a new Java project called `langchain4jdemos` in your favorite IDE with either Gradle or Maven as your build tool.
- Check the [Get Started](https://docs.langchain4j.dev/get-started) page for LangChain4j, and add either the Gradle or Maven dependencies shown there.
- Create a new class called `ChatDemo` in a package of your choosing.
- Add a private, static, final attribute called `OPENAI_API_KEY` of type `String` that reads the API key from the environment variable.

```java
private static final String OPENAI_API_KEY = System.getenv("OPENAI_API_KEY");
```

- Add a `main` method to the `ChatDemo` class.
- Inside the `main` method, create a new `ChatLanguageModel` reference by instantiating the `OpenAiChatModel` class with the API key.

```java
public static void main(String[] args) {
    ChatLanguageModel model = OpenAiChatModel.withApiKey(apiKey);
}
```

- Call the `generate` method on the `model` object with any string request.
- Print the result to the console.

```java
public static void main(String[] args) {
    ChatLanguageModel model = OpenAiChatModel.withApiKey(apiKey);
    String answer = model.generate("Say 'Hello World'");
    System.out.println(answer); // Hello World
}
```

- Run the `ChatDemo` class.

## Lab 2: Switch to test cases

- While the outputs from AI models have a built-in measure of randomness, you can still test the model with a known input and output. Create a new test class called `OpenAiChatModelTest` in the same package as the `ChatDemo` class (under `src/test/java`, of course).
- Add an attribute to the class of type `ChatLanguageModel` and instantiate it in a `@BeforeAll` method.

```java
private ChatLanguageModel model;

@BeforeAll
public static void setUp() {
    model = OpenAiChatModel.withApiKey(OPENAI_API_KEY);
}
```

- Add a test method called `testGenerate` that calls the `generate` method on the `model` object with a known input and checks the output.

```java
@Test
public void testGenerate() {
    String answer = model.generate("Say 'Hello World'");
    System.out.println(answer);
    assertTrue(answer.contains("Hello World"));
}
```

- Make sure the test passes.

- You can change the model in the `setUp` method:

```java
@BeforeAll
public static void setUp() {
    model = OpenAiChatModel.builder()
            .apiKey(OPENAI_API_KEY)
            .modelName(OpenAiChatModelName.GPT_4_O)
            .build();
}
```

- Run the test class and make sure the test passes.

## Lab 2: Use a UserMessage object

- There is another `generate` method in the `ChatLanguageModel` interface that takes a vararg list of `ChatMessage` objects. Create a new `UserMessage` object using its static factory method called `from` with a prompt, and pass it to the `generate` method. The result this time will be an `Response<AiMessage>` object. Print the `Response` object to the console.

```java
@Test
public void testGenerateWithMessages() {
    UserMessage userMessage = UserMessage.from("""
            Hello, my name is Inigo Montoya.
            """);
    Response<AiMessage> aiMessage = model.generate(userMessage);
    System.out.println(aiMessage);
}
```

- The result will print not only the response message, but also the usage tokens, all in JSON form.
- The methods in `Response` to retrieve the individual parts of the response are `content()`, `finishReason()`, and `tokenUsage()`. The result of `finishReason()` is an enum of type `FinishReason`, as you can show in the following code snippet:

```java
@Test
void testGenerateWithFinishReasons() {
    UserMessage userMessage = UserMessage.from("""
            Hello, my name is Inigo Montoya.
            """);
    Response<AiMessage> aiMessage = model.generate(userMessage);
    System.out.println(aiMessage);
    switch (aiMessage.finishReason()) {
        case STOP -> System.out.println(aiMessage.content().text());
        case LENGTH -> System.out.println("Token limit reached");
        case TOOL_EXECUTION -> System.out.println("Tool execution needed");
        case CONTENT_FILTER -> System.out.println("Content filtering required");
        case OTHER -> System.out.println("Call finished for some other reason");
    }
}
```

- The result in this case should be `STOP`, but this shows all the possible reasons for a call to finish.

- Another method in `Response` is `tokenUsage()`. The tokens are used to determine the cost of the call. The `tokenUsage()` method returns a `TokenUsage` object with the following methods: `inputTokenCount()`, `outputTokenCount()`, and `totalTokenCount()`. Input and output tokens are charged differently in AI models, with output tokens generally double or triple to cost of input tokens. You can limit both totals if you wish. Check the values for this simple request:

```java
@Test
void testGenerateWithTokenUsage() {
    UserMessage userMessage = UserMessage.from("""
            Hello, my name is Inigo Montoya.
            """);
    Response<AiMessage> aiMessage = model.generate(userMessage);
    TokenUsage tokenUsage = aiMessage.tokenUsage();
    System.out.println("Input tokens: " + tokenUsage.inputTokenCount());
    System.out.println("Output tokens: " + tokenUsage.outputTokenCount());
    System.out.println("Total tokens: " + tokenUsage.totalTokenCount());
}
```

- At the moment, this test only prints the token counts. You can add assertions that the counts are less than some maximum, like 100, if you like.

- As of this writing, For GPT-4o, 1K input tokens cost half a cent (\$0.005) and 1K output tokens cost 1.5 cents (\$0.015). The total number of tokens allowed in the _context window_ is 128K. The total number of tokens for the above example is almost certainly less than 100, so the cost for this demo is in hundredths of a cent, and this is with their flagship model. For current pricing information, see the [OpenAI pricing page](https://openai.com/api/pricing).

## Lab 3: Conversations

