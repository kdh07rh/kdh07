package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/memory-leak-test")
public class MemoryLeakServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<byte[]> memoryHog = new ArrayList<>();

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if ("leak".equals(action)) {
            // Add 10MB of data to the list on each request
            for (int i = 0; i < 10; i++) {
                memoryHog.add(new byte[1024 * 1024]); // 1MB byte array
            }
        }

        request.setAttribute("leakSize", memoryHog.size() * 1); // Each object is 1MB
        request.getRequestDispatcher("memory-leak-test.jsp").forward(request, response);
    }
}
