package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/thread-occupier-4")
public class ThreadOccupier4Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String startTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String threadName = Thread.currentThread().getName();
        
        out.println("<html><body>");
        out.println("<h2>🔒 Thread Occupier 4</h2>");
        out.println("<p><strong>시작 시간:</strong> " + startTime + "</p>");
        out.println("<p><strong>스레드:</strong> " + threadName + "</p>");
        out.println("<p style='color: orange;'><b>10초 Thread 점유 시작!</b></p>");
        out.flush();
        
        System.out.println(String.format(
            "[%s] 🟡 Thread-Occupier-4 START - Thread: %s", 
            startTime, threadName
        ));
        
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        String endTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        out.println("<p><strong>완료 시간:</strong> " + endTime + "</p>");
        out.println("<p style='color: green;'><b>Thread 점유 완료!</b></p>");
        out.println("</body></html>");
        
        System.out.println(String.format(
            "[%s] ✅ Thread-Occupier-4 END - Thread: %s", 
            endTime, threadName
        ));
    }
}