<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html>
<head>
    <title>Thread Block 2</title>
    <meta charset="UTF-8">
</head>
<body>
    <h2>🔒 Thread Block 2 - 5분 대기</h2>
    
    <%
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        String threadName = Thread.currentThread().getName();
        
        out.println("<p><strong>시작 시간:</strong> " + startTime + "</p>");
        out.println("<p><strong>스레드:</strong> " + threadName + "</p>");
        out.println("<p style='color: red; font-size: 18px;'><b>5분간 Thread 점유 시작!</b></p>");
        out.flush();
        
        System.out.println(String.format(
            "[%s] 🟠 Thread-Block-2 START - Thread: %s", 
            startTime, threadName
        ));
        
        try {
            Thread.sleep(300000); // 5분
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Thread-Block-2 interrupted: " + e.getMessage());
        }
        
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        out.println("<p><strong>완료 시간:</strong> " + endTime + "</p>");
        out.println("<p style='color: green; font-size: 18px;'><b>Thread 해제 완료!</b></p>");
        
        System.out.println(String.format(
            "[%s] ✅ Thread-Block-2 END - Thread: %s", 
            endTime, threadName
        ));
    %>
    
</body>
</html>