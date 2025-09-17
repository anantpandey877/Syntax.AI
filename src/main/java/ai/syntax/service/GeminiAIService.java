package ai.syntax.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

public class GeminiAIService {

    private static final String API_KEY = "AIzaSyAZPNFAnsL0BdzkmubVXRADwX7ZOzbdbwM"; 
    private static final String BASE_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/";
    private static final String DEFAULT_MODEL_NAME = "gemini-1.5-flash"; 

    private Gson gson = new Gson();

    public GeminiAIService() {
    }

    public String getGeminiAnalysis(String originalCode, String language, String analysisMode) throws IOException {
        String promptText = buildPrompt(originalCode, language, analysisMode);
        String fullApiUrl = String.format("%s%s:generateContent?key=%s", BASE_API_URL, DEFAULT_MODEL_NAME, API_KEY);

        Map<String, Object> requestBodyMap = new HashMap<>();
        Map<String, Object> partMap = new HashMap<>();
        partMap.put("text", promptText);
        
        List<Map<String, Object>> partsList = List.of(partMap);
        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("parts", partsList);
        
        List<Map<String, Object>> contentsList = List.of(contentMap);
        requestBodyMap.put("contents", contentsList);

        String jsonRequestBody = gson.toJson(requestBodyMap);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fullApiUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonRequestBody))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println("Gemini Raw Response: " + responseBody);

            Map<String, Object> parsedResponse = gson.fromJson(responseBody, Map.class);

            if (parsedResponse != null && parsedResponse.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) parsedResponse.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> firstCandidate = candidates.get(0);
                    Map<String, Object> content = (Map<String, Object>) firstCandidate.get("content");
                    if (content != null && content.containsKey("parts")) {
                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (!parts.isEmpty()) {
                            Map<String, Object> firstPart = parts.get(0);
                            return (String) firstPart.get("text");
                        }
                    }
                }
            }
            System.err.println("Failed to parse Gemini response or no content found: " + responseBody);
            return "AI did not return a valid response. Please try again.";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Gemini API call interrupted: " + e.getMessage());
            throw new IOException("Gemini API call interrupted.", e);
        } catch (Exception e) {
            System.err.println("Error during Gemini API call or response processing: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Error processing Gemini API call.", e);
        }
    }

    private String buildPrompt(String originalCode, String language, String analysisMode) {
        String promptText = "";
        String lang = (language != null && !language.isEmpty() ? language : "programming");

        switch (analysisMode) {
            case "bug_fix":
                promptText = "You are a highly experienced software engineer and a strict code reviewer. Your task is to identify and fix all bugs, errors, and common anti-patterns in the following. if the given code is not a valid code just simply say this is not a code and explain what is this"
                             + lang + " code. Provide ONLY the corrected code, wrapped in a markdown code block. If no bugs are found, state 'No bugs found.' and provide the original code again.\n\nCode:\n"
                             + originalCode;
                break;
            case "explain_code":
                promptText = "You are an expert programming instructor. Explain the following "
                             + lang + " code clearly and concisely, focusing on its purpose, functionality, and key concepts involved. Use simple language suitable for a developer who might be new to this specific code.\n\nCode:\n"
                             + originalCode;
                break;
            case "refactor_performance":
                promptText = "You are a performance optimization expert. Refactor the following "
                             + lang + " code to improve its performance and efficiency, while maintaining its original functionality. Provide ONLY the refactored code, wrapped in a markdown code block. Briefly explain the performance improvements in comments within the code if possible.\n\nCode:\n"
                             + originalCode;
                break;
            case "add_comments":
                promptText = "You are a meticulous code documenter. Add comprehensive and clear comments to the following "
                             + lang + " code. Focus on explaining complex logic, purpose of functions/methods, and important variables. Provide ONLY the commented code, wrapped in a markdown code block.\n\nCode:\n"
                             + originalCode;
                break;
            case "convert_language":
                String targetLanguage = "Python";
                if (language != null && language.equalsIgnoreCase("python")) {
                    targetLanguage = "Java";
                } else if (language != null && language.equalsIgnoreCase("java")) {
                    targetLanguage = "Python";
                } else {
                    targetLanguage = "Python";
                }

                promptText = "You are a programming language translator. Convert the following "
                             + (language != null && !language.isEmpty() ? language : "unspecified") + " code into "
                             + targetLanguage + ". Ensure the converted code is functionally equivalent and idiomatic for " + targetLanguage + ". Provide ONLY the converted code, wrapped in a markdown code block.\n\nCode:\n"
                             + originalCode;
                break;
            default:
                promptText = "Please analyze the following " + lang + " code. Provide a general analysis and suggest improvements:\n\nCode:\n" + originalCode;
                break;
        }
        return promptText;
    }

    private String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }
}