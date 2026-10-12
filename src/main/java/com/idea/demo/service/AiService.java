package com.idea.demo.service;

import com.idea.demo.dto.AiIdeaResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private final RestClient restClient = RestClient.create();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    @SuppressWarnings("unchecked")
    public AiIdeaResponse improve(String roughText) {

        String prompt = "A student wants to post a project idea on a platform where other students can join the team. "
                + "Their rough idea is: \"" + roughText + "\"\n\n"
                + "Improve it. Reply in EXACTLY this format, with no extra text, no markdown, and each item on one line:\n"
                + "TITLE: a short catchy project title\n"
                + "DESCRIPTION: a clear description of 3 to 4 sentences covering the problem and what the project does\n"
                + "TAGS: 3 to 5 comma separated tags\n"
                + "SKILLS: 3 to 5 comma separated skills needed to build it";

        Map<String, Object> body = Map.of(
                "contents", List.of(Map.of("parts", List.of(Map.of("text", prompt))))
        );

        Map<String, Object> response;
        try {
            response = restClient.post()
                    .uri("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent")
                    .header("x-goog-api-key", apiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(Map.class);
        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "AI service error. Check the API key and model name. " + e.getMessage());
        }

        String text;
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            text = (String) parts.get(0).get("text");
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI returned an unexpected reply");
        }

        String title = "";
        String description = "";
        String tags = "";
        String skills = "";

        for (String line : text.replace("**", "").split("\\r?\\n")) {
            String l = line.trim();
            String upper = l.toUpperCase();
            if (upper.startsWith("TITLE:")) {
                title = l.substring(6).trim();
            } else if (upper.startsWith("DESCRIPTION:")) {
                description = l.substring(12).trim();
            } else if (upper.startsWith("TAGS:")) {
                tags = l.substring(5).trim();
            } else if (upper.startsWith("SKILLS:")) {
                skills = l.substring(7).trim();
            }
        }

        if (title.isEmpty() && description.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "AI reply could not be read. Try again.");
        }

        return new AiIdeaResponse(title, description, tags, skills);
    }
}