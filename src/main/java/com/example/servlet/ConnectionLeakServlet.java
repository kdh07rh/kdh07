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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/connection-leak-test")
public class ConnectionLeakServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<Connection> leakedConnections = new ArrayList<>();
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            this.dataSource = (DataSource) envContext.lookup("jdbc/shopping");
        } catch (NamingException e) {
            throw new ServletException("Cannot initialize ConnectionLeakServlet", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        long startTime = System.currentTimeMillis();

        if ("leak".equals(action)) {
            try {
                // Connection을 얻고 실제 DB 작업을 수행한 후 누수시킴
                Connection conn = dataSource.getConnection();
                
                // 실제 DB 작업으로 Connection을 활성 상태로 만듦
                performDatabaseWork(conn);
                
                // Connection을 누수 목록에 추가 (닫지 않음)
                leakedConnections.add(conn);
                
                System.out.println("ConnectionLeakServlet: Connection leaked. Total leaked: " + leakedConnections.size());
                
            } catch (SQLException e) {
                System.err.println("ConnectionLeakServlet: Failed to get database connection - " + e.getMessage());
                throw new ServletException("Failed to get database connection", e);
            }
        } else if ("reset".equals(action)) {
            int closedCount = 0;
            for (Connection conn : leakedConnections) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                        closedCount++;
                    }
                } catch (SQLException e) {
                    System.err.println("ConnectionLeakServlet: Error closing leaked connection - " + e.getMessage());
                    e.printStackTrace();
                }
            }
            leakedConnections.clear();
            System.out.println("ConnectionLeakServlet: Reset completed. Closed " + closedCount + " leaked connections.");
        } else if ("status".equals(action)) {
            // Connection Pool 상태 확인
            showConnectionPoolStatus(response);
            return;
        }

        long processingTime = System.currentTimeMillis() - startTime;
        request.setAttribute("leakSize", leakedConnections.size());
        request.setAttribute("processingTime", processingTime);
        request.getRequestDispatcher("connection-leak-test.jsp").forward(request, response);
    }
    
    private void performDatabaseWork(Connection conn) throws SQLException {
        // 실제 DB 쿼리를 실행하여 Connection을 활성 상태로 유지
        try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM products")) {
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int productCount = rs.getInt(1);
                System.out.println("ConnectionLeakServlet: DB work completed. Product count: " + productCount);
            }
        }
        
        // 추가적인 DB 작업으로 Connection Pool 압박 증가
        try (java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
            java.sql.ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userCount = rs.getInt(1);
                System.out.println("ConnectionLeakServlet: Additional DB work. User count: " + userCount);
            }
        }
        
        // Connection을 바쁜 상태로 만들기 위한 지연
        try {
            Thread.sleep(100 + (int)(Math.random() * 200)); // 100-300ms 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // CRITICAL: Connection을 진짜 ACTIVE 상태로 유지
        // 별도 스레드에서 무한 대기 쿼리 실행
        Thread leakThread = new Thread(() -> {
            try {
                // 진짜 장기간 실행되는 쿼리 (30초 대기)
                java.sql.PreparedStatement longRunningStmt = conn.prepareStatement(
                    "SELECT pg_sleep(30)"
                );
                
                System.out.println("ConnectionLeakServlet: Starting 30-second blocking query to keep connection ACTIVE");
                java.sql.ResultSet rs = longRunningStmt.executeQuery();
                
                // Statement와 ResultSet을 의도적으로 닫지 않음!
                System.out.println("ConnectionLeakServlet: Long-running query completed (should not reach here quickly)");
                
            } catch (SQLException e) {
                System.err.println("ConnectionLeakServlet: Long-running query failed: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("ConnectionLeakServlet: Thread interrupted: " + e.getMessage());
            }
        });
        
        // 데몬 스레드로 설정하여 서버 종료 시 자동 종료
        leakThread.setDaemon(true);
        leakThread.start();
        
        System.out.println("ConnectionLeakServlet: Started background thread to keep connection active");
    }
    
    private void showConnectionPoolStatus(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>Connection Pool Status</h2>");
        response.getWriter().println("<p><strong>Leaked Connections:</strong> " + leakedConnections.size() + "</p>");
        response.getWriter().println("<p><strong>Active Connections:</strong> " + countActiveConnections() + "</p>");
        response.getWriter().println("<hr>");
        response.getWriter().println("<p><a href='?action=leak'>Leak Connection</a> | ");
        response.getWriter().println("<a href='?action=reset'>Reset All</a> | ");
        response.getWriter().println("<a href='?action=status'>Refresh Status</a></p>");
        response.getWriter().println("</body></html>");
    }
    
    private int countActiveConnections() {
        int activeCount = 0;
        for (Connection conn : leakedConnections) {
            try {
                if (conn != null && !conn.isClosed()) {
                    activeCount++;
                }
            } catch (SQLException e) {
                // Connection이 이미 닫힌 경우
            }
        }
        return activeCount;
    }
}
