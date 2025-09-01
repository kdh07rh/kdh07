package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@WebServlet("/external-api-test")
public class ExternalApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 3~15초 사이의 랜덤한 지연을 가진 외부 API 시뮬레이션
        int delaySeconds = ThreadLocalRandom.current().nextInt(3, 16);
        
        System.out.println("ExternalApiServlet: Making real external HTTP call with " + delaySeconds + "s delay");
        
        long startTime = System.currentTimeMillis();
        String apiResponse = null;
        String errorMessage = null;
        boolean isRealCall = false;
        
        try {
            // 실제 외부 HTTP 호출: httpbin.org의 delay 엔드포인트 사용
            apiResponse = callExternalApiWithDelay(delaySeconds);
            isRealCall = true;
        } catch (Exception e) {
            // 외부 API 호출 실패 시 fallback으로 Thread.sleep 사용
            System.out.println("External API call failed, using fallback: " + e.getMessage());
            errorMessage = "External API unavailable, using simulated delay: " + e.getMessage();
            try {
                Thread.sleep(delaySeconds * 1000L);
                apiResponse = generateFallbackResponse(delaySeconds);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new ServletException("Fallback simulation interrupted", ie);
            }
        }
        
        long endTime = System.currentTimeMillis();
        double actualDuration = (endTime - startTime) / 1000.0;
        
        // 결과를 request에 설정
        request.setAttribute("plannedDelay", delaySeconds);
        request.setAttribute("actualDuration", String.format("%.2f", actualDuration));
        request.setAttribute("response", apiResponse);
        request.setAttribute("delayCategory", getDelayCategory(delaySeconds));
        request.setAttribute("isRealCall", isRealCall);
        if (errorMessage != null) {
            request.setAttribute("errorMessage", errorMessage);
        }
        
        // JSP로 포워드
        request.getRequestDispatcher("external-api-test.jsp").forward(request, response);
    }
    
    private String callExternalApiWithDelay(int delaySeconds) throws IOException {
        // httpbin.org의 delay 엔드포인트를 사용하여 실제 HTTP 지연 구현
        String urlString = "https://httpbin.org/delay/" + delaySeconds;
        URL url = new URL(urlString);
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000); // 5초 연결 타임아웃
        connection.setReadTimeout((delaySeconds + 5) * 1000); // 응답 타임아웃
        connection.setRequestProperty("User-Agent", "Shopping-Mall-External-API-Test");
        connection.setRequestProperty("Accept", "application/json");
        
        int responseCode = connection.getResponseCode();
        
        StringBuilder apiResponse = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                responseCode >= 200 && responseCode < 300 ? 
                connection.getInputStream() : connection.getErrorStream()))) {
            
            String line;
            while ((line = reader.readLine()) != null) {
                apiResponse.append(line).append("\n");
            }
        }
        
        // 실제 API 응답을 래핑하여 정보 추가
        return String.format(
            "{\n" +
            "  \"external_api_call\": {\n" +
            "    \"url\": \"%s\",\n" +
            "    \"method\": \"GET\",\n" +
            "    \"response_code\": %d,\n" +
            "    \"planned_delay_seconds\": %d,\n" +
            "    \"timestamp\": %d,\n" +
            "    \"response_data\": %s\n" +
            "  }\n" +
            "}",
            urlString,
            responseCode, 
            delaySeconds,
            System.currentTimeMillis(),
            apiResponse.toString().trim()
        );
    }
    
    private String generateFallbackResponse(int delaySeconds) {
        return String.format(
            "{\n" +
            "  \"fallback_simulation\": {\n" +
            "    \"method\": \"Thread.sleep\",\n" +
            "    \"delay_seconds\": %d,\n" +
            "    \"timestamp\": %d,\n" +
            "    \"message\": \"External API unavailable - used local delay simulation\",\n" +
            "    \"note\": \"This fallback ensures consistent testing even when external services are down\"\n" +
            "  }\n" +
            "}",
            delaySeconds,
            System.currentTimeMillis()
        );
    }
    
    private String generateRandomApiResponse(int delaySeconds) {
        String[] apiTypes = {"Payment", "User Service", "Inventory", "Notification", "Analytics"};
        String[] statuses = {"success", "partial", "delayed"};
        
        String apiType = apiTypes[ThreadLocalRandom.current().nextInt(apiTypes.length)];
        String status = statuses[ThreadLocalRandom.current().nextInt(statuses.length)];
        
        return String.format(
            "{\n" +
            "  \"api_type\": \"%s\",\n" +
            "  \"status\": \"%s\",\n" +
            "  \"delay_seconds\": %d,\n" +
            "  \"timestamp\": %d,\n" +
            "  \"message\": \"Simulated external API response\",\n" +
            "  \"data\": {\n" +
            "    \"processed\": true,\n" +
            "    \"request_id\": \"req_%d\",\n" +
            "    \"server_load\": \"%s\"\n" +
            "  }\n" +
            "}",
            apiType, 
            status, 
            delaySeconds,
            System.currentTimeMillis(),
            ThreadLocalRandom.current().nextInt(10000, 99999),
            getServerLoadStatus(delaySeconds)
        );
    }
    
    private String getDelayCategory(int delaySeconds) {
        if (delaySeconds <= 5) {
            return "Fast";
        } else if (delaySeconds <= 10) {
            return "Medium";
        } else {
            return "Slow";
        }
    }
    
    private String getServerLoadStatus(int delaySeconds) {
        if (delaySeconds <= 4) {
            return "Low";
        } else if (delaySeconds <= 7) {
            return "Medium";
        } else {
            return "High";
        }
    }
}
