package OpenAI;

import okhttp3.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAiService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = HiddenKeys.APIKeyOpenAI_Epsilon();
    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public OpenAiService() {
        this.client = new OkHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public String questionAsync(String content) throws IOException {
        System.out.println("Summarizing Question...");
        return summarizeTextAsync(content);
    }

    private String summarizeTextAsync(String content) throws IOException {
        System.out.println("Starting text summarization...");
        int inputTokens = countTokens(content);
        System.out.println("Estimated input tokens: " + inputTokens);
        long initialMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        System.out.println("Initial memory usage: " + initialMemory + " bytes");

        if (content == null || content.isEmpty()) {
            return "Content is null or empty.";
        }

        Map<String, Object> data = new HashMap<>();
        data.put("model", HiddenKeys.ModelKeyGPT3Tuned());
        data.put("messages", List.of(
                Map.of("role", "system", "content", "You are a helpful assistant."),
                Map.of("role", "user", "content", content)
        ));
        data.put("temperature", 1);
        data.put("max_tokens", 150);
        data.put("top_p", 1);
        data.put("frequency_penalty", 0);
        data.put("presence_penalty", 0);

        String jsonData = objectMapper.writeValueAsString(data);
        RequestBody body = RequestBody.create(jsonData, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseString = response.body().string();
                Map<String, Object> responseObject = objectMapper.readValue(responseString, Map.class);
                String messageContent = ((Map<String, String>) ((List<Map<String, Object>>) responseObject.get("choices")).get(0).get("message")).get("content");
                System.out.println("Response received successfully from OpenAI.");
                int outputTokens = countTokens(messageContent);
                System.out.println("Estimated output tokens: " + outputTokens);
                long finalMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
                System.out.println("Final memory usage: " + finalMemory + " bytes");
                System.out.println("Memory used: " + (finalMemory - initialMemory) + " bytes");

                System.out.println("Output: " + messageContent.trim());
                return messageContent.trim();
            } else {
                String errorResponse = response.body().string();
                System.out.println("Failed to call OpenAI: " + response.code() + " - " + errorResponse);
                return "Failed to summarize.";
            }
        }
    }

    private int countTokens(String text) {
        // Simple placeholder for token counting
        return text.length() / 4; // Approximation of token count based on average character count
    }
}
