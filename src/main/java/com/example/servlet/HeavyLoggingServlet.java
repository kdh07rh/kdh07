package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@WebServlet("/heavy-logging")
public class HeavyLoggingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // 다양한 로그 레벨과 메시지 템플릿
    private static final String[] LOG_LEVELS = {"INFO", "DEBUG", "WARN", "ERROR", "TRACE"};
    private static final String[] LOG_TEMPLATES = {
        "User %s performed action %s with result %s",
        "Database query executed: %s took %dms with %d rows affected",
        "External API call to %s returned status %d with response time %dms",
        "Cache operation %s for key %s completed successfully",
        "Security check for user %s on resource %s result: %s",
        "Memory usage: heap=%dMB, nonheap=%dMB, total=%dMB",
        "Thread pool status: active=%d, queue=%d, completed=%d",
        "Transaction %s started for user %s with timeout %ds"
    };
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 랜덤하게 로그 부하 강도 결정 (1-5단계)
        int logIntensity = ThreadLocalRandom.current().nextInt(1, 6);
        
        long startTime = System.currentTimeMillis();
        
        // 각 요청마다 다른 강도의 로깅 수행
        performHeavyLogging(logIntensity);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 정상적인 페이지 응답 (실제 업무 로직처럼 보이게)
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html><head><title>시스템 모니터링</title></head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<h3>시스템 상태 확인 완료</h3>");
        response.getWriter().println("<p>로그 처리 강도: " + logIntensity + "/5</p>");
        response.getWriter().println("<p>처리 시간: " + duration + "ms</p>");
        response.getWriter().println("<p>상태: 정상</p>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/products'>메인으로</a>");
        response.getWriter().println("</body></html>");
    }
    
    private void performHeavyLogging(int intensity) {
        try {
            // 로그 파일 경로 (실제 운영환경처럼)
            String logDir = System.getProperty("catalina.home", ".") + "/logs";
            File logDirFile = new File(logDir);
            if (!logDirFile.exists()) {
                logDirFile.mkdirs();
            }
            
            String logFileName = logDir + "/application-" + 
                               LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
            
            // 강도에 따른 로그 양 결정
            int baseLogCount = intensity * 500; // 500 ~ 2500개
            int variableLogCount = random.nextInt(intensity * 300); // 추가 랜덤
            int totalLogs = baseLogCount + variableLogCount;
            
            // 동기식 파일 로깅 (실제 문제가 되는 패턴)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFileName, true))) {
                for (int i = 0; i < totalLogs; i++) {
                    String logLevel = LOG_LEVELS[random.nextInt(LOG_LEVELS.length)];
                    String logMessage = generateLogMessage();
                    String timestamp = LocalDateTime.now().format(formatter);
                    
                    // 실제 로그 형태로 기록
                    writer.write(String.format("[%s] %s - %s%n", timestamp, logLevel, logMessage));
                    
                    // 매 50개마다 강제로 디스크에 쓰기 (flush)
                    if (i % 50 == 0) {
                        writer.flush();
                        
                        // 추가적인 디스크 I/O 부하 생성
                        if (random.nextInt(10) < 3) { // 30% 확률로
                            Thread.yield(); // 다른 스레드에게 CPU 양보
                            try {
                                Thread.sleep(1); // 1ms 지연
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                }
                writer.flush(); // 최종 flush
            }
            
        } catch (IOException e) {
            // 로깅 실패 시에도 애플리케이션은 계속 동작
            System.err.println("Heavy logging failed: " + e.getMessage());
        }
    }
    
    private String generateLogMessage() {
        String template = LOG_TEMPLATES[random.nextInt(LOG_TEMPLATES.length)];
        
        // 템플릿에 따라 랜덤 데이터 생성
        switch (template) {
            case "User %s performed action %s with result %s":
                return String.format(template, 
                    "user" + random.nextInt(1000),
                    "action" + random.nextInt(50),
                    random.nextBoolean() ? "SUCCESS" : "FAILURE");
                    
            case "Database query executed: %s took %dms with %d rows affected":
                return String.format(template,
                    "SELECT * FROM table" + random.nextInt(20),
                    random.nextInt(500) + 10,
                    random.nextInt(1000));
                    
            case "External API call to %s returned status %d with response time %dms":
                return String.format(template,
                    "api-service-" + random.nextInt(10),
                    200 + random.nextInt(100),
                    random.nextInt(3000) + 100);
                    
            case "Cache operation %s for key %s completed successfully":
                return String.format(template,
                    random.nextBoolean() ? "GET" : "PUT",
                    "cache-key-" + random.nextInt(10000));
                    
            case "Security check for user %s on resource %s result: %s":
                return String.format(template,
                    "user" + random.nextInt(500),
                    "/resource/" + random.nextInt(100),
                    random.nextBoolean() ? "ALLOWED" : "DENIED");
                    
            case "Memory usage: heap=%dMB, nonheap=%dMB, total=%dMB":
                int heap = random.nextInt(2048) + 512;
                int nonheap = random.nextInt(512) + 128;
                return String.format(template, heap, nonheap, heap + nonheap);
                
            case "Thread pool status: active=%d, queue=%d, completed=%d":
                return String.format(template,
                    random.nextInt(50) + 5,
                    random.nextInt(100),
                    random.nextInt(10000) + 1000);
                    
            case "Transaction %s started for user %s with timeout %ds":
                return String.format(template,
                    "tx-" + System.currentTimeMillis(),
                    "user" + random.nextInt(200),
                    random.nextInt(300) + 30);
                    
            default:
                return "System operation completed with random data: " + random.nextInt(100000);
        }
    }
}
