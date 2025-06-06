plugins {
    id("java")
    id("groovy")
}

group = "com.kousenit"
version = "1.0-SNAPSHOT"

dependencies {
    // Groovy
    implementation(platform("org.apache.groovy:groovy-bom:4.0.27"))
    implementation("org.apache.groovy:groovy-json")
    implementation("org.apache.groovy:groovy-datetime")
    testImplementation("org.apache.groovy:groovy-test")

    // JsonPath
    implementation("com.jayway.jsonpath:json-path:2.9.0")

    // LangChain4J
    implementation(platform("dev.langchain4j:langchain4j-bom:1.0.1"))
    implementation("dev.langchain4j:langchain4j")
    implementation("dev.langchain4j:langchain4j-core")
    implementation("dev.langchain4j:langchain4j-open-ai")
    implementation("dev.langchain4j:langchain4j-mistral-ai")
    implementation("dev.langchain4j:langchain4j-google-ai-gemini")
    implementation("dev.langchain4j:langchain4j-ollama")
    implementation("dev.langchain4j:langchain4j-anthropic")
    implementation("dev.langchain4j:langchain4j-mcp")

    implementation("dev.langchain4j:langchain4j-chroma")

    // Tokenizer for OpenAI to estimate counts
    implementation("com.knuddels:jtokkit:1.1.0")

    // Embeddings and loaders
    implementation("dev.langchain4j:langchain4j-embeddings")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2")
    implementation("dev.langchain4j:langchain4j-embeddings-bge-small-en")
    implementation("dev.langchain4j:langchain4j-document-parser-apache-pdfbox")
    
    // Exclude all PDFBox dependencies from Tika to avoid version conflicts
    implementation("dev.langchain4j:langchain4j-document-parser-apache-tika") {
        exclude(group = "org.apache.pdfbox")
    }
    
    // Force PDFBox 2.0.32 for the entire project
    implementation("org.apache.pdfbox:pdfbox:2.0.32")
    implementation("org.apache.pdfbox:fontbox:2.0.32")
    
    implementation("dev.langchain4j:langchain4j-document-transformer-jsoup")

    // Logging
    implementation("org.slf4j:slf4j-api:2.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.18")
    implementation("org.apache.logging.log4j:log4j-core:2.24.3")

    // Make CVE warnings go away
    implementation("org.apache.poi:poi-ooxml:5.4.0")
    implementation("net.minidev:json-smart:2.5.2")

    // Testing
    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.assertj:assertj-core:3.26.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

// Define a task to run MYM
tasks.register<JavaExec>("runMYM") {
    group = "application"
    description = "Run MYM application"
    mainClass.set("com.kousenit.rag.MYM")
    classpath = sourceSets["main"].runtimeClasspath
}

// Define a task to run OpenAiImageModelDemo
tasks.register<JavaExec>("runImageDemo") {
    group = "application"
    description = "Run OpenAiImageModelDemo application"
    mainClass.set("com.kousenit.images.OpenAiImageModelDemo")
    classpath = sourceSets["main"].runtimeClasspath
}
