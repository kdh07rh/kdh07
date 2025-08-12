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

@WebServlet("/lock-contention-test")
public class LockContentionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Object lock = new Object();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String startTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        out.println("<html><body>");
        out.println("<h2>Lock Contention Test</h2>");
        out.println("<p>Request received at: " + startTime + "</p>");
        out.println("<p>Attempting to acquire lock...</p>");
        out.flush();

        synchronized (lock) {
            String lockAcquiredTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            out.println("<p><b>Lock acquired at: " + lockAcquiredTime + "</b></p>");
            out.println("<p>Holding lock for 10 seconds...</p>");
            out.flush();
            
            try {
                Thread.sleep(10000); // 10 seconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            String lockReleasedTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            out.println("<p><b>Lock released at: " + lockReleasedTime + "</b></p>");
        }
        
        out.println("<a href='lock-contention-test'>Run again</a><br>");
        out.println("<a href='" + request.getContextPath() + "/products'>Back to Home</a>");
        out.println("</body></html>");
    }
}
