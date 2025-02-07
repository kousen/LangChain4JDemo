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
    implementation("dev.langchain4j:langchain4j:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-open-ai:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-mistral-ai:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-ollama:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-anthropic:${langchain4jVersion}")

    // Embeddings and loaders
    implementation("dev.langchain4j:langchain4j-embeddings:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-embeddings-bge-small-en-v15-q:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-pdfbox:${langchain4jVersion}")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-tika:${langchain4jVersion}")

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
