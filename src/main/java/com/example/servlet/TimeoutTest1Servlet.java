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

@WebServlet("/timeout-test-1")
public class TimeoutTest1Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String threadName = Thread.currentThread().getName();
        
        out.println("<html><body>");
        out.println("<h2>⏱️ Timeout Test Page 1</h2>");
        out.println("<p><strong>시작 시간:</strong> " + startTime + "</p>");
        out.println("<p><strong>스레드:</strong> " + threadName + "</p>");
        out.println("<p style='color: orange; font-size: 16px;'><b>5초 딜레이 처리 중... (1/10)</b></p>");
        out.flush();
        
        System.out.println(String.format(
            "[%s] ⏱️ Timeout-Test-1 START - Thread: %s", 
            startTime, threadName
        ));
        
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            out.println("<p style='color: red;'><b>요청이 중단되었습니다! (Connection Timeout)</b></p>");
            out.println("</body></html>");
            return;
        }
        
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        out.println("<p><strong>완료 시간:</strong> " + endTime + "</p>");
        out.println("<p style='color: green; font-size: 16px;'><b>Timeout Test 1 완료!</b></p>");
        out.println("</body></html>");
        
        System.out.println(String.format(
            "[%s] ✅ Timeout-Test-1 END - Thread: %s", 
            endTime, threadName
        ));
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}