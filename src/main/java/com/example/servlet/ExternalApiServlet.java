package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@WebServlet("/external-api-test")
public class ExternalApiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Simulate calling a slow external API
        URL url = new URL("http://httpbin.org/delay/15");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        long startTime = System.currentTimeMillis();
        
        StringBuilder content;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
        } finally {
            con.disconnect();
        }

        long endTime = System.currentTimeMillis();
        long duration = (endTime - startTime) / 1000;

        request.setAttribute("duration", duration);
        request.setAttribute("response", content.toString());
        request.getRequestDispatcher("external-api-test.jsp").forward(request, response);
    }
}
