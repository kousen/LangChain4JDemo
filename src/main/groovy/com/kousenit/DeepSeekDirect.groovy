package com.kousenit

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class DeepSeekDirect {
    static final String URL = "https://api.deepseek.com/chat/completions"
    static final String KEY = System.env['DEEPSEEK_API_KEY']
    static boolean debug = false

    static def chat(String question, String model) {
        JsonSlurper slurper = new JsonSlurper()
        def requestMap = [
            model   : model,
            messages: [
                [role: 'system', content: 'You are a helpful assistant.'],
                [role: 'user', content: question]
            ],
            stream  : false
        ]
        String requestBody = JsonOutput.toJson(requestMap)

        if (debug) {
            println 'Transmitting...'
            println JsonOutput.prettyPrint(requestBody)
        }

        HttpClient.Version version = HttpClient.Version.HTTP_2
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(URL))
            .header('Content-Type', 'application/json')
            .header('Accept', 'application/json')
            .header('Authorization', "Bearer $KEY")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody))
            .build()

        try (HttpClient client = HttpClient.newBuilder()
            .version(version)
            .build()) {
            HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString())

            if (debug) {
                println "Status code: ${response.statusCode()}"
                println 'Received...'
                println JsonOutput.prettyPrint(response.body())
            }
            return slurper.parseText(response.body())
        }
    }
}
