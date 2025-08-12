package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@WebServlet("/disk-io-test")
public class DiskIoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger logger = Logger.getLogger(DiskIoServlet.class.getName());

    @Override
    public void init() throws ServletException {
        try {
            // Configure logger to write to a separate file to simulate disk I/O
            String logFilePath = getServletContext().getRealPath("/") + "/../logs/disk-io-test.log";
            FileHandler fileHandler = new FileHandler(logFilePath, true); // true for append
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setUseParentHandlers(false); // Do not log to console
        } catch (IOException e) {
            throw new ServletException("Failed to configure logger for DiskIoServlet", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String logCountStr = request.getParameter("logCount");
        int logCount = 10000; // Default value
        if (logCountStr != null && !logCountStr.isEmpty()) {
            try {
                logCount = Integer.parseInt(logCountStr);
            } catch (NumberFormatException e) {
                // Ignore and use default
            }
        }

        long startTime = System.currentTimeMillis();

        // Simulate heavy synchronous logging
        for (int i = 0; i < logCount; i++) {
            logger.info("This is a test log message line " + (i + 1) + ". Writing to disk synchronously.");
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        String message = String.format("Successfully generated %d log messages in %d ms.", logCount, duration);
        request.setAttribute("message", message);
        request.getRequestDispatcher("disk-io-test.jsp").forward(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("disk-io-test.jsp").forward(request, response);
    }
}
