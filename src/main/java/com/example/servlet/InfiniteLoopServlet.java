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

@WebServlet("/infinite-loop-test")
public class InfiniteLoopServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String startTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        out.println("<html><body>");
        out.println("<h2>Infinite Loop Test</h2>");
        out.println("<p>Request received at: " + startTime + "</p>");
        out.println("<p><b>Starting infinite loop... This will hang the request.</b></p>");
        out.flush();

        // Infinite loop to simulate high CPU usage
        while (true) {
            // This loop will run indefinitely, consuming one CPU core.
        }
    }
}
