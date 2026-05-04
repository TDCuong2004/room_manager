package com.example.server.services;

import com.example.server.dto.AIResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class AIService {

    @Value("${gemini.api.key:}")
    private String API_KEY;

    private final ObjectMapper mapper = new ObjectMapper();

    public AIResult checkContent(String text) {

        if (API_KEY == null || API_KEY.isBlank()) {
            return fallback(text);
        }

        try {
            String endpoint =
                    "https://generativelanguage.googleapis.com/v1/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

            URL url = new URL(endpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // ✅ Prompt
            String prompt = """
                    Bạn là hệ thống kiểm duyệt nội dung.

                    Hãy đánh giá nội dung sau:
                    - spam
                    - lừa đảo
                    - nội dung nhạy cảm

                    CHỈ trả về JSON hợp lệ, không thêm text:
                    {
                      "score": 0.0,
                      "reason": "..."
                    }

                    Nội dung:
                    """ + text;

            // ✅ Escape JSON chuẩn
            String safePrompt = mapper.writeValueAsString(prompt);

            String body = """
                    {
                      "contents": [
                        {
                          "parts": [
                            { "text": %s }
                          ]
                        }
                      ]
                    }
                    """.formatted(safePrompt);

            // gửi request
            try (OutputStream os = conn.getOutputStream()) {
                os.write(body.getBytes());
            }

            int status = conn.getResponseCode();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            (status >= 400) ? conn.getErrorStream() : conn.getInputStream()
                    )
            );

            String response = br.lines().reduce("", String::concat);

            System.out.println("GEMINI RAW: " + response);

            return parseAI(response);

        } catch (Exception e) {
            e.printStackTrace();
            return fallback(text);
        }
    }

    private AIResult parseAI(String res) {
        try {
            JsonNode root = mapper.readTree(res);

            // ❗ Nếu API trả lỗi
            if (root.has("error")) {
                String msg = root.path("error").path("message").asText();
                return new AIResult(0.2, "Gemini lỗi: " + msg);
            }

            JsonNode candidates = root.path("candidates");

            if (!candidates.isArray() || candidates.isEmpty()) {
                return fallback("no candidates");
            }

            JsonNode parts = candidates.get(0)
                    .path("content")
                    .path("parts");

            if (!parts.isArray() || parts.isEmpty()) {
                return fallback("no parts");
            }

            String text = parts.get(0).path("text").asText();

            System.out.println("GEMINI TEXT: " + text);

            // ✅ Parse JSON từ AI
            try {
                JsonNode json = mapper.readTree(text);

                double score = json.path("score").asDouble(0.1);
                String reason = json.path("reason").asText("Unknown");

                return new AIResult(score, reason);

            } catch (Exception e) {
                return new AIResult(0.3, "AI trả sai format: " + text);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new AIResult(0.1, "Parse lỗi");
        }
    }

    // ✅ Fallback local khi AI lỗi
    private AIResult fallback(String text) {

        if (text == null) return new AIResult(0.1, "Empty");

        String lower = text.toLowerCase();

        if (lower.contains("lừa đảo") || lower.contains("chuyển khoản trước")) {
            return new AIResult(0.9, "Nghi lừa đảo");
        }

        if (lower.contains("http") || lower.contains("www")) {
            return new AIResult(0.6, "Spam link");
        }

        if (lower.length() < 10) {
            return new AIResult(0.5, "Nội dung quá ngắn");
        }

        return new AIResult(0.1, "OK");
    }
}