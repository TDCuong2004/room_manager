package com.example.server.services;

import com.example.server.utils.MultipartInputStreamFileResource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class OcrService {

    private static final String API_URL = "https://api.fpt.ai/vision/idr/vnm";
    private static final String API_KEY = "0PPOqE6P2wBwXNKgGbn0eLqiB0u8CLxo";

    public Map<String, String> scanCCCD(MultipartFile file) {

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("api-key", API_KEY);
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image",
                    new MultipartInputStreamFileResource(
                            file.getInputStream(),
                            file.getOriginalFilename()
                    )
            );

            HttpEntity<MultiValueMap<String, Object>> request =
                    new HttpEntity<>(body, headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(
                    API_URL,
                    request,
                    String.class
            );

            System.out.println("FPT RAW: " + response.getBody());

            return parseFptResponse(response.getBody());

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("OCR FAILED: " + e.getMessage());
        }
    }

    private Map<String, String> parseFptResponse(String json) {

        Map<String, String> result = new HashMap<>();

        try {
            JSONObject root = new JSONObject(json);

            if (!root.has("data")) return result;

            JSONArray dataArr = root.getJSONArray("data");

            if (dataArr.length() > 0) {
                JSONObject obj = dataArr.getJSONObject(0);

                result.put("name", obj.optString("name", ""));
                result.put("id", obj.optString("id", ""));
                result.put("dob", obj.optString("dob", ""));
                result.put("address", obj.optString("address", ""));
            }

        } catch (Exception e) {
            throw new RuntimeException("Parse OCR failed");
        }

        return result;
    }
}