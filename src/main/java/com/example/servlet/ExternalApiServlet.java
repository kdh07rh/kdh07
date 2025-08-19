package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@WebServlet("/external-api-test")
public class ExternalApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 3~10초 사이의 랜덤한 지연 시간 선택
        int delaySeconds = ThreadLocalRandom.current().nextInt(3, 11); // 3~10초
        
        // 추가 변동성을 위한 밀리초 단위 랜덤 추가 (0~999ms)
        int delayMillis = ThreadLocalRandom.current().nextInt(0, 1000);
        
        long totalDelayMs = (delaySeconds * 1000L) + delayMillis;
        
        System.out.println("ExternalApiServlet: Simulating " + delaySeconds + "." + 
                          String.format("%03d", delayMillis) + " seconds delay");
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 실제 외부 API 호출을 시뮬레이션하는 지연
            Thread.sleep(totalDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException("External API simulation interrupted", e);
        }
        
        long endTime = System.currentTimeMillis();
        double actualDuration = (endTime - startTime) / 1000.0;
        
        // 다양한 응답 패턴 시뮬레이션
        String simulatedResponse = generateRandomApiResponse(delaySeconds);
        
        // 결과를 request에 설정
        request.setAttribute("plannedDelay", delaySeconds);
        request.setAttribute("actualDuration", String.format("%.2f", actualDuration));
        request.setAttribute("response", simulatedResponse);
        request.setAttribute("delayCategory", getDelayCategory(delaySeconds));
        
        // JSP로 포워드
        request.getRequestDispatcher("external-api-test.jsp").forward(request, response);
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
        if (delaySeconds <= 4) {
            return "Fast";
        } else if (delaySeconds <= 7) {
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
