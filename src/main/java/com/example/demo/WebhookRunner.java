package com.example.demo;

import org.springframework.boot.CommandLineRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class WebhookRunner implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void run(String... args) {
        // Step 1: Generate Webhook
        String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
        Map<String, String> requestBody = Map.of(
                "name", "John Doe",
                "regNo", "REG12347",
                "email", "john@example.com"
        );

        ResponseEntity<WebhookResponse> resp = restTemplate.postForEntity(generateUrl, requestBody, WebhookResponse.class);

        if (!resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            System.err.println("Failed to get webhook: " + resp.getStatusCode());
            if (resp.getBody() != null) {
                System.err.println("Response body: " + resp.getBody().toString());
            }
            return;
        }

        String webhookUrl = resp.getBody().getWebhook();
        String accessToken = resp.getBody().getAccessToken();

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        // Step 3: Solve SQL problem (Question 1 for odd regNo)
        String finalSql = "SELECT " +
                "p.AMOUNT AS SALARY, " +
                "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "d.DEPARTMENT_NAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "WHERE EXTRACT(DAY FROM p.PAYMENT_TIME) != 1 " +
                "ORDER BY p.AMOUNT DESC " +
                "LIMIT 1";

        // Step 4: Submit solution
        submitSolution(webhookUrl, accessToken, finalSql);
    }

    private void submitSolution(String webhookUrl, String jwtToken, String query) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        Map<String, String> payload = Map.of("finalQuery", query);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> res = restTemplate.postForEntity(webhookUrl, entity, String.class);

            if (res.getStatusCode().is2xxSuccessful()) {
                System.out.println("Solution submitted successfully!");
                System.out.println("Server response: " + res.getBody());
            } else {
                System.err.println("Submission failed: " + res.getStatusCode());
                System.err.println("Response body: " + (res.getBody() != null ? res.getBody() : "[no body]"));
            }
        } catch (Exception e) {
            System.err.println("Error during submission: " + e.getMessage());
        }
    }
}

