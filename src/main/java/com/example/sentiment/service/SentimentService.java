//package com.example.sentiment.service;
//
//import com.example.sentiment.config.SentimentApiConfig;
//import com.example.sentiment.model.SentimentResponse;
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//public class SentimentService {
//
//    private final RestTemplate restTemplate;
//    private final SentimentApiConfig config;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    public SentimentService(RestTemplateBuilder builder, SentimentApiConfig config) {
//        this.restTemplate = builder.build();
//        this.config = config;
//    }
//
//    public SentimentResponse analyze(String comment) {
//        try {
//            // Build request body
//            Map<String, Object> body = Map.of(
//                    "messages", List.of(
//                            Map.of(
//                                    "role", "user",
//                                    "content", "I hate this hat"
//                                    "content", "Analyze the sentiment of this text from -1 (very negative) to 1 (very positive).\n\n" +
//                                            "Text: \"" + comment + "\"\n\n" +
//                                            "Return ONLY raw JSON, no explanation, no code block. Fields:\n" +
//                                            "- score: number between -1 and 1\n" +
//                                            "- sentiment: one of Negative, Neutral, Positive\n\n" +
//                                            "Example:\n{\"score\": 0.7, \"sentiment\": \"Positive\"}"
//                            )
//                    ),
//                    "web_access", false
//            );
//
//            // Headers
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_JSON);
//            headers.set("x-rapidapi-key", config.getKey());
//            headers.set("x-rapidapi-host", config.getHost());
//
//            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//            // Call API
//            var response = restTemplate.exchange(
//                    config.getUrl(),
//                    HttpMethod.POST,
//                    entity,
//                    String.class
//            );
//
//            // Parse API response
//            ObjectMapper mapper = new ObjectMapper();
//            JsonNode root = mapper.readTree(response.getBody());
//
//            JsonNode sentimentJson = null;
//
//// Case 1: "result" is already JSON
//            if (root.has("result") && root.get("result").isObject()) {
//                sentimentJson = root.get("result");
//            }
//// Case 2: "result" is a JSON string
//            else if (root.has("result") && root.get("result").isTextual()) {
//                sentimentJson = mapper.readTree(root.get("result").asText());
//            }
//// Case 3: fallback to "output"
//            else if (root.has("output")) {
//                if (root.get("output").isObject()) {
//                    sentimentJson = root.get("output");
//                } else if (root.get("output").isTextual()) {
//                    sentimentJson = mapper.readTree(root.get("output").asText());
//                }
//            }
//
//// Build response
//            SentimentResponse sr = new SentimentResponse();
//            if (sentimentJson != null) {
//                sr.setScore(sentimentJson.path("score").asDouble());
//                sr.setSentiment(sentimentJson.path("sentiment").asText());
//            } else {
//                sr.setScore(0.0);
//                sr.setSentiment("Unknown");
//            }
//            return sr;
//
//
//        } catch (Exception e) {
//            SentimentResponse sr = new SentimentResponse();
//            sr.setScore(0.0);
//            sr.setSentiment("Unknown");
//            return sr;
//        }
//    }
//}


package com.example.sentiment.service;

import com.example.sentiment.config.SentimentApiConfig;
import com.example.sentiment.model.SentimentResponse;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
        import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class SentimentService {

    private final RestTemplate restTemplate;
    private final SentimentApiConfig config;
    private final ObjectMapper mapper = new ObjectMapper();

    public SentimentService(RestTemplateBuilder builder, SentimentApiConfig config) {
        this.restTemplate = builder.build();
        this.config = config;
    }

    public SentimentResponse analyze(String comment) {
        try {
            // ✅ Build request body with ObjectMapper
            Map<String, Object> body = Map.of(
                    "messages", List.of(
                            Map.of(
                                    "role", "user",
                                    "content",
                                    "Analyze the sentiment of this text. Return ONLY raw JSON, no explanation, no code block. Fields:\n" +
                                            "- score: number between -1 (very negative) and 1 (very positive)\n" +
                                            "- sentiment: one of Negative, Neutral, Positive\n" +
                                            "- emotion: main emotion (e.g. Happy, Angry, Sad, Excited, Frustrated)\n" +
                                            "- intent: what is the user trying to do (e.g. complain, praise, ask question, suggest improvement)\n" +
                                            "- broadcast: a short friendly version of the text suitable for broadcasting (1 sentence summary)\n\n" +
                                            "Example:\n" +
                                            "{\"score\": -0.8, \"sentiment\": \"Negative\", \"emotion\": \"Angry\", \"intent\": \"complain\", \"broadcast\": \"User is upset with the service.\"}\n\n" +
                                            "Text: \"" + comment + "\""
                            )
                    ),
                    "web_access", false
            );

            String jsonBody = mapper.writeValueAsString(body); // serialize to JSON string

            // ✅ Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-rapidapi-key", config.getKey());
            headers.set("x-rapidapi-host", config.getHost());

            HttpEntity<String> entity = new HttpEntity<>(jsonBody, headers);

            // ✅ Call API
            ResponseEntity<String> response = restTemplate.exchange(
                    config.getUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            // ✅ Parse API response
            JsonNode root = mapper.readTree(response.getBody());

            JsonNode sentimentJson = null;

            if (root.hasNonNull("result")) {
                JsonNode resultNode = root.get("result");

                if (resultNode.isObject()) {
                    sentimentJson = resultNode;
                } else if (resultNode.isTextual()) {
                    String raw = resultNode.asText();

                    // Remove ```json ... ``` wrappers if present
                    if (raw.startsWith("```")) {
                        raw = raw.replaceAll("```[a-zA-Z]*", "").trim();
                    }

                    try {
                        sentimentJson = mapper.readTree(raw);
                    } catch (Exception e) {
                        System.err.println("Failed to parse sentiment JSON: " + raw);
                    }
                }
            }

            SentimentResponse sr = new SentimentResponse();
            if (sentimentJson != null) {
                sr.setScore(sentimentJson.path("score").asDouble(0.0));
                sr.setSentiment(sentimentJson.path("sentiment").asText("Unknown"));
                sr.setEmotion(sentimentJson.path("emotion").asText(null));
                sr.setIntent(sentimentJson.path("intent").asText(null));
                sr.setBroadcast(sentimentJson.path("broadcast").asText(null));
            } else {
                sr.setScore(0.0);
                sr.setSentiment("Unknown");
                sr.setEmotion(null);
                sr.setIntent(null);
                sr.setBroadcast(null);
            }
            return sr;

        } catch (Exception e) {
            SentimentResponse sr = new SentimentResponse();
            sr.setScore(0.0);
            sr.setSentiment("Unknown");
            sr.setEmotion(null);
            sr.setIntent(null);
            sr.setBroadcast(null);
            return sr;
        }
    }

}
