package com.example.userprofile.Service;

import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class ImgurService {
    private final String clientId = "ff869be3a0bad2d";
    RestTemplate restTemplate = new RestTemplate();

    public ImgurService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(MultipartFile file) throws Exception {
        String url = "https://apidocs.imgur.com/";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);

        // Create body for the request
        HttpEntity<MultipartFile> entity = new HttpEntity<>(file, headers);

        // Send POST request to Imgur
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // Check if the request was successful
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("data")) {
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                return (String) data.get("link");  // Return image URL
            }
        }

        throw new Exception("Image upload failed.");
    }


    public void deleteImage(String imageHash) {
        String url = "https://apidocs.imgur.com/" + imageHash;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Client-ID " + clientId);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        restTemplate.exchange(url, HttpMethod.DELETE, entity, String.class);
    }
}
