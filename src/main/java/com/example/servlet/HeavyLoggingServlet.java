package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@WebServlet("/heavy-logging")
public class HeavyLoggingServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Random random = new Random();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    
    // 다양한 로그 레벨과 메시지 템플릿
    private static final String[] LOG_LEVELS = {"INFO", "DEBUG", "WARN", "ERROR", "TRACE", "FATAL"};
    private static final String[] LOG_TEMPLATES = {
        "User %s performed action %s with result %s at IP %s with session %s and execution time %dms",
        "Database query executed: %s took %dms with %d rows affected, connection pool usage: %d/%d",
        "External API call to %s returned status %d with response time %dms, retry count: %d, payload size: %d bytes",
        "Cache operation %s for key %s completed successfully, hit ratio: %.2f%%, memory usage: %d MB",
        "Security check for user %s on resource %s result: %s, threat level: %s, geo location: %s",
        "Memory usage: heap=%dMB, nonheap=%dMB, total=%dMB, gc_count=%d, gc_time=%dms",
        "Thread pool status: active=%d, queue=%d, completed=%d, rejected=%d, peak=%d",
        "Transaction %s started for user %s with timeout %ds, isolation level: %s, lock count: %d",
        "File system operation: %s on path %s completed in %dms, size: %d bytes, permissions: %s",
        "Network event: %s from %s:%d to %s:%d, bytes transferred: %d, protocol: %s, latency: %dms"
    };
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 강도 설정 - 파라미터나 랜덤으로 결정
        String intensityParam = request.getParameter("intensity");
        int logIntensity = 3; // 기본값 증가
        if (intensityParam != null) {
            try {
                logIntensity = Math.max(1, Math.min(5, Integer.parseInt(intensityParam)));
            } catch (NumberFormatException e) {
                // 무시하고 기본값 사용
            }
        } else {
            logIntensity = ThreadLocalRandom.current().nextInt(2, 6); // 2-5 강도
        }
        
        long startTime = System.currentTimeMillis();
        
        // 여러 종류의 디스크 I/O 부하 생성
        performIntensiveLogging(logIntensity);
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // 정상적인 페이지 응답 (실제 업무 로직처럼 보이게)
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<!DOCTYPE html>");
        response.getWriter().println("<html><head><title>시스템 모니터링</title>");
        response.getWriter().println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
        response.getWriter().println("</head>");
        response.getWriter().println("<body>");
        response.getWriter().println("<div class='container mt-4'>");
        response.getWriter().println("<h3>시스템 상태 확인 완료</h3>");
        response.getWriter().println("<div class='alert alert-info'>");
        response.getWriter().println("<p><strong>로그 처리 강도:</strong> " + logIntensity + "/5</p>");
        response.getWriter().println("<p><strong>처리 시간:</strong> " + duration + "ms</p>");
        response.getWriter().println("<p><strong>상태:</strong> " + (duration > 3000 ? "⚠️ 지연 발생" : "✅ 정상") + "</p>");
        response.getWriter().println("</div>");
        response.getWriter().println("<a href='" + request.getContextPath() + "/products' class='btn btn-primary'>메인으로</a>");
        response.getWriter().println("</div></body></html>");
    }
    
    private void performIntensiveLogging(int intensity) {
        try {
            // 로그 파일 경로 (실제 운영환경처럼)
            String logDir = System.getProperty("catalina.home", ".") + "/logs";
            File logDirFile = new File(logDir);
            if (!logDirFile.exists()) {
                logDirFile.mkdirs();
            }
            
            // 다중 로그 파일 생성으로 I/O 부하 증가
            List<String> logFiles = new ArrayList<>();
            logFiles.add(logDir + "/application-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log");
            logFiles.add(logDir + "/audit-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log");
            logFiles.add(logDir + "/performance-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log");
            logFiles.add(logDir + "/security-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log");
            logFiles.add(logDir + "/debug-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log");
            
            // 강도에 따른 로그 양 대폭 증가
            int baseLogCount = intensity * 2000; // 2000 ~ 10000개
            int variableLogCount = random.nextInt(intensity * 1500); // 추가 랜덤
            int totalLogs = baseLogCount + variableLogCount;
            
            System.out.printf("[%s] 강력한 디스크 I/O 부하 시작 - 강도: %d, 총 로그: %d개, 파일: %d개%n", 
                LocalDateTime.now().format(formatter), intensity, totalLogs, logFiles.size());
            
            // 각 파일에 동시에 쓰기 (더 많은 I/O 부하)
            for (String logFile : logFiles) {
                performFileLogging(logFile, totalLogs / logFiles.size(), intensity);
            }
            
            // 추가: 큰 파일 단일 쓰기 (순차 I/O 부하)
            if (intensity >= 3) {
                performBulkLogging(logDir + "/bulk-" + System.currentTimeMillis() + ".log", intensity);
            }
            
            // 추가: 작은 파일 다중 쓰기 (랜덤 I/O 부하)
            if (intensity >= 4) {
                performFragmentedLogging(logDir, intensity);
            }
            
        } catch (IOException e) {
            // 로깅 실패 시에도 애플리케이션은 계속 동작
            System.err.println("Heavy logging failed: " + e.getMessage());
        }
    }
    
    private void performFileLogging(String logFileName, int logCount, int intensity) throws IOException {
        // FileWriter 직접 사용으로 버퍼링 최소화
        try (FileWriter writer = new FileWriter(logFileName, true)) {
            
            for (int i = 0; i < logCount; i++) {
                String logLevel = LOG_LEVELS[random.nextInt(LOG_LEVELS.length)];
                String logMessage = generateDetailedLogMessage();
                String timestamp = LocalDateTime.now().format(formatter);
                
                // 실제 로그 형태로 기록 (더 긴 메시지)
                writer.write(String.format("[%s] %s - %s%n", timestamp, logLevel, logMessage));
                
                // 강제 디스크 쓰기 빈도 증가
                if (i % (50 - intensity * 8) == 0) { // intensity가 높을수록 자주 flush
                    writer.flush();
                    
                    // 추가적인 디스크 I/O 부하 생성
                    if (random.nextInt(10) < intensity * 2) { // intensity에 비례한 확률
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
            
            // 최종 강제 디스크 동기화
            writer.flush();
        }
    }
    
    private void performBulkLogging(String fileName, int intensity) throws IOException {
        // 대용량 단일 쓰기 (순차 I/O 부하)
        StringBuilder bulkData = new StringBuilder();
        int bulkSize = intensity * 5000; // 5000 ~ 25000 라인
        
        for (int i = 0; i < bulkSize; i++) {
            bulkData.append(String.format("[%s] BULK - %s%n", 
                LocalDateTime.now().format(formatter), 
                generateDetailedLogMessage()));
        }
        
        // 한 번에 대용량 쓰기
        Files.write(Paths.get(fileName), bulkData.toString().getBytes(), 
            StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
    
    private void performFragmentedLogging(String logDir, int intensity) throws IOException {
        // 작은 파일 다중 생성 (파일시스템 메타데이터 I/O 부하)
        int fileCount = intensity * 20; // 20 ~ 100개 파일
        
        for (int i = 0; i < fileCount; i++) {
            String fragmentFile = logDir + "/fragment-" + System.currentTimeMillis() + "-" + i + ".log";
            
            try (FileWriter writer = new FileWriter(fragmentFile)) {
                // 각 파일에 소량 데이터 쓰기
                for (int j = 0; j < 10; j++) {
                    writer.write(String.format("[%s] FRAG-%d - %s%n", 
                        LocalDateTime.now().format(formatter), i, generateDetailedLogMessage()));
                }
                writer.flush();
            }
            
            // 파일 생성 간격
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private String generateDetailedLogMessage() {
        String template = LOG_TEMPLATES[random.nextInt(LOG_TEMPLATES.length)];
        
        // 더 상세하고 긴 로그 메시지 생성
        switch (template) {
            case "User %s performed action %s with result %s at IP %s with session %s and execution time %dms":
                return String.format(template, 
                    "user" + random.nextInt(1000),
                    "action" + random.nextInt(50),
                    random.nextBoolean() ? "SUCCESS" : "FAILURE",
                    "192.168." + random.nextInt(255) + "." + random.nextInt(255),
                    "SESS-" + System.currentTimeMillis() + "-" + random.nextInt(10000),
                    random.nextInt(5000) + 10);
                    
            case "Database query executed: %s took %dms with %d rows affected, connection pool usage: %d/%d":
                return String.format(template,
                    "SELECT * FROM table" + random.nextInt(20) + " WHERE column LIKE '%pattern%' ORDER BY id LIMIT 1000",
                    random.nextInt(500) + 10,
                    random.nextInt(1000),
                    random.nextInt(50) + 5,
                    50);
                    
            case "External API call to %s returned status %d with response time %dms, retry count: %d, payload size: %d bytes":
                return String.format(template,
                    "https://api-service-" + random.nextInt(10) + ".example.com/v1/data",
                    200 + random.nextInt(100),
                    random.nextInt(3000) + 100,
                    random.nextInt(3),
                    random.nextInt(50000) + 1000);
                    
            // 다른 케이스들도 유사하게 확장...
            default:
                return "System operation completed with detailed context: " + 
                       "user_id=" + random.nextInt(100000) + 
                       ", session_duration=" + random.nextInt(3600) + "s" +
                       ", memory_usage=" + random.nextInt(2048) + "MB" +
                       ", cpu_time=" + random.nextInt(1000) + "ms" +
                       ", network_bytes=" + random.nextInt(1000000) +
                       ", timestamp=" + System.currentTimeMillis();
        }
    }
}
