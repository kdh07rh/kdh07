package com.example.servlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet("/db-connection-fail")
public class DbConnectionFailServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String failType = request.getParameter("type");
        if (failType == null) {
            failType = "invalid_host";
        }
        
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            switch (failType) {
                case "invalid_host":
                    testInvalidHost(request, response);
                    break;
                case "wrong_credentials":
                    testWrongCredentials(request, response);
                    break;
                case "connection_exhaustion":
                    testConnectionExhaustion(request, response);
                    break;
                case "database_down":
                    testDatabaseDown(request, response);
                    break;
                case "network_timeout":
                    testNetworkTimeout(request, response);
                    break;
                default:
                    response.getWriter().println("<h2>Unknown fail type: " + failType + "</h2>");
            }
        } catch (Exception e) {
            response.getWriter().println("<h2>DB Connection Failure Test Result</h2>");
            response.getWriter().println("<p><strong>Expected Failure:</strong> " + failType + "</p>");
            response.getWriter().println("<p><strong>Error Message:</strong> " + e.getMessage() + "</p>");
            response.getWriter().println("<p><strong>Error Type:</strong> " + e.getClass().getSimpleName() + "</p>");
            response.getWriter().println("<p style='color: red;'>Connection failed as expected!</p>");
            response.getWriter().println("<a href='db-connection-fail-test.jsp'>Back to test page</a>");
            e.printStackTrace();
        }
    }
    
    private void testInvalidHost(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        String url = "jdbc:postgresql://invalid-host:5432/shopping_db";
        String username = "postgres";
        String password = "password";
        
        Connection conn = DriverManager.getConnection(url, username, password);
        response.getWriter().println("This should not happen - connection succeeded!");
    }
    
    private void testWrongCredentials(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        String url = "jdbc:postgresql://localhost:5432/shopping_db";
        String username = "wrong_user";
        String password = "wrong_password";
        
        Connection conn = DriverManager.getConnection(url, username, password);
        response.getWriter().println("This should not happen - connection succeeded!");
    }
    
    private void testConnectionExhaustion(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException, NamingException {
        Context initContext = new InitialContext();
        Context envContext = (Context) initContext.lookup("java:/comp/env");
        DataSource dataSource = (DataSource) envContext.lookup("jdbc/shopping");
        
        List<Connection> connections = new ArrayList<>();
        
        // PostgreSQL 기본 max_connections는 100개 정도이므로 200개를 시도
        for (int i = 0; i < 200; i++) {
            Connection conn = dataSource.getConnection();
            connections.add(conn);
            response.getWriter().println("Connection " + (i + 1) + " created<br>");
            response.getWriter().flush();
        }
        
        response.getWriter().println("All connections created successfully - this should not happen!");
    }
    
    private void testDatabaseDown(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        String url = "jdbc:postgresql://localhost:9999/shopping_db";
        String username = "postgres";
        String password = "password";
        
        Connection conn = DriverManager.getConnection(url, username, password);
        response.getWriter().println("This should not happen - connection succeeded!");
    }
    
    private void testNetworkTimeout(HttpServletRequest request, HttpServletResponse response) 
            throws SQLException, IOException {
        String url = "jdbc:postgresql://10.255.255.1:5432/shopping_db?connectTimeout=3&socketTimeout=3";
        String username = "postgres";
        String password = "password";
        
        Connection conn = DriverManager.getConnection(url, username, password);
        response.getWriter().println("This should not happen - connection succeeded!");
    }
}