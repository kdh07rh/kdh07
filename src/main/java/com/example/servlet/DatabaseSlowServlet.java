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

@WebServlet("/db-slow-query")
public class DatabaseSlowServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger queryCounter = new AtomicInteger(0);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String threadName = Thread.currentThread().getName();
        int queryId = queryCounter.incrementAndGet();
        
        out.println("<html><body>");
        out.println("<h2>🗄️ Database Slow Query Test</h2>");
        out.println("<p><strong>Query ID:</strong> " + queryId + "</p>");
        out.println("<p><strong>시작 시간:</strong> " + startTime + "</p>");
        out.println("<p><strong>스레드:</strong> " + threadName + "</p>");
        out.println("<p style='color: blue; font-size: 16px;'><b>복잡한 DB 쿼리 실행 중...</b></p>");
        out.flush();
        
        System.out.println(String.format(
            "[%s] 🗄️ DB-Slow-Query START - ID: %d, Thread: %s", 
            startTime, queryId, threadName
        ));
        
        try {
            // DB Slow Query 시뮬레이션 (4초 - connectionTimeout보다 길게)
            simulateSlowDatabaseQuery();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("DB-Slow-Query interrupted: " + e.getMessage());
            
            out.println("<p style='color: red;'><b>DB 쿼리가 중단되었습니다! (Connection Timeout)</b></p>");
            out.println("</body></html>");
            return;
        }
        
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        out.println("<p><strong>완료 시간:</strong> " + endTime + "</p>");
        out.println("<p style='color: green; font-size: 16px;'><b>DB 쿼리 완료!</b></p>");
        
        // 가상의 DB 결과 출력
        out.println("<div style='background: #e8f4fd; padding: 10px; margin: 10px 0;'>");
        out.println("<h3>DB 쿼리 결과</h3>");
        out.println("<table border='1' style='border-collapse: collapse;'>");
        out.println("<tr><th>ID</th><th>Product</th><th>Price</th><th>Stock</th></tr>");
        for (int i = 1; i <= 5; i++) {
            out.println("<tr>");
            out.println("<td>" + i + "</td>");
            out.println("<td>Product " + i + "</td>");
            out.println("<td>$" + (100 + i * 50) + "</td>");
            out.println("<td>" + (50 - i * 5) + "</td>");
            out.println("</tr>");
        }
        out.println("</table>");
        out.println("<p><small>이 결과가 보인다면 Connection Timeout이 발생하지 않았습니다.</small></p>");
        out.println("</div>");
        out.println("</body></html>");
        
        System.out.println(String.format(
            "[%s] ✅ DB-Slow-Query END - ID: %d, Thread: %s", 
            endTime, queryId, threadName
        ));
    }
    
    private void simulateSlowDatabaseQuery() throws InterruptedException {
        // 복잡한 쿼리 시뮬레이션 단계별 처리
        System.out.println("  📊 Phase 1: 테이블 스캔 중...");
        Thread.sleep(1000);
        
        System.out.println("  📊 Phase 2: 인덱스 조회 중...");
        Thread.sleep(1000);
        
        System.out.println("  📊 Phase 3: 조인 연산 중...");
        Thread.sleep(1000);
        
        System.out.println("  📊 Phase 4: 집계 연산 중...");
        Thread.sleep(1000);
        
        System.out.println("  📊 Phase 5: 결과 정렬 중...");
        Thread.sleep(500);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}