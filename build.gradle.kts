plugins {
    id("java")
    id("groovy")
}

group = "com.kousenit"
version = "1.0-SNAPSHOT"

val langchain4jVersion by extra("1.0.0-beta1")

dependencies {
    // Groovy
    implementation("org.apache.groovy:groovy:4.0.25")
    implementation("org.apache.groovy:groovy-json:4.0.25")
    implementation("org.apache.groovy:groovy-datetime:4.0.25")
    testImplementation("org.apache.groovy:groovy-test:4.0.25")

    // JsonPath
    implementation("com.jayway.jsonpath:json-path:2.9.0")

    // LangChain4J
    implementation(platform("dev.langchain4j:langchain4j-bom:1.0.0-beta1"))
    implementation("dev.langchain4j:langchain4j")
    implementation("dev.langchain4j:langchain4j-open-ai")
    implementation("dev.langchain4j:langchain4j-mistral-ai")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini")
    implementation("dev.langchain4j:langchain4j-ollama")
    implementation("dev.langchain4j:langchain4j-anthropic")

    // Embeddings and loaders
    implementation("dev.langchain4j:langchain4j-embeddings")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2")
    implementation("dev.langchain4j:langchain4j-embeddings-bge-small-en")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-pdfbox")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-tika")

    // Jsoup
    implementation("org.jsoup:jsoup:1.18.1")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("ch.qos.logback:logback-classic:1.5.3")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
