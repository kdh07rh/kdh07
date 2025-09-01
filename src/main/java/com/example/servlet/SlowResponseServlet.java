package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/slow-response")
public class SlowResponseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger requestCounter = new AtomicInteger(0);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String threadName = Thread.currentThread().getName();
        int requestId = requestCounter.incrementAndGet();
        
        // 지연 시간 파라미터 (기본 5초)
        String delayParam = request.getParameter("delay");
        int delaySeconds = (delayParam != null) ? Integer.parseInt(delayParam) : 5;
        
        out.println("<html><body>");
        out.println("<h2>🐌 Slow Response Test</h2>");
        out.println("<p><strong>Request ID:</strong> " + requestId + "</p>");
        out.println("<p><strong>시작 시간:</strong> " + startTime + "</p>");
        out.println("<p><strong>스레드:</strong> " + threadName + "</p>");
        out.println("<p><strong>지연 시간:</strong> " + delaySeconds + "초</p>");
        out.println("<p style='color: orange; font-size: 16px;'><b>" + delaySeconds + "초 지연 처리 중...</b></p>");
        out.flush();
        
        System.out.println(String.format(
            "[%s] 🐌 Slow-Response START - ID: %d, Delay: %ds, Thread: %s", 
            startTime, requestId, delaySeconds, threadName
        ));
        
        try {
            // 설정된 시간만큼 지연 (Tomcat connectionTimeout=3초보다 길게)
            Thread.sleep(delaySeconds * 1000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Slow-Response interrupted: " + e.getMessage());
            
            out.println("<p style='color: red;'><b>요청이 중단되었습니다! (Connection Timeout)</b></p>");
            out.println("</body></html>");
            return;
        }
        
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        out.println("<p><strong>완료 시간:</strong> " + endTime + "</p>");
        out.println("<p style='color: green; font-size: 16px;'><b>" + delaySeconds + "초 지연 완료!</b></p>");
        
        // Connection이 아직 살아있다면 응답 완료
        out.println("<div style='background: #f0f0f0; padding: 10px; margin: 10px 0;'>");
        out.println("<h3>응답 데이터</h3>");
        out.println("<p>이 응답이 보인다면 Connection Timeout이 발생하지 않았습니다.</p>");
        out.println("<p>Tomcat connectionTimeout: 3000ms</p>");
        out.println("<p>실제 처리시간: " + delaySeconds + "000ms</p>");
        out.println("</div>");
        out.println("</body></html>");
        
        System.out.println(String.format(
            "[%s] ✅ Slow-Response END - ID: %d, Thread: %s", 
            endTime, requestId, threadName
        ));
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}