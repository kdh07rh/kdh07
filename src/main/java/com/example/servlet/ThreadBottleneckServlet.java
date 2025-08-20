package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/thread-bottleneck-test")
public class ThreadBottleneckServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger requestCounter = new AtomicInteger(0);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 요청 시작 시간 기록
        long startTime = System.currentTimeMillis();
        int requestId = requestCounter.incrementAndGet();
        String currentThread = Thread.currentThread().getName();
        
        // 현재 스레드 상태 정보 수집
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        int activeThreadCount = threadBean.getThreadCount();
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        try {
            // HTML 응답 시작
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<meta charset='UTF-8'>");
            out.println("<title>Thread Pool Bottleneck Test</title>");
            out.println("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css' rel='stylesheet'>");
            out.println("<meta http-equiv='refresh' content='2'>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div class='container mt-4'>");
            
            // 요청 정보 출력
            out.println("<div class='alert alert-info'>");
            out.println("<h4>🔄 Request #" + requestId + " Processing...</h4>");
            out.println("<p><strong>Thread:</strong> " + currentThread + "</p>");
            out.println("<p><strong>Start Time:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "</p>");
            out.println("<p><strong>Active Threads:</strong> " + activeThreadCount + "</p>");
            out.println("</div>");
            
            out.flush(); // 즉시 클라이언트로 전송
            
            // 처리 시간 시뮬레이션 (3-8초 랜덤)
            int processingTime = 3000 + (int)(Math.random() * 5000);
            
            out.println("<div class='card'>");
            out.println("<div class='card-header bg-warning'>");
            out.println("<h5>⏳ Simulating Work (" + (processingTime/1000) + " seconds)</h5>");
            out.println("</div>");
            out.println("<div class='card-body'>");
            
            // 진행률 표시하면서 작업 시뮬레이션
            int steps = 10;
            int stepTime = processingTime / steps;
            
            for (int i = 1; i <= steps; i++) {
                Thread.sleep(stepTime);
                
                int progress = (i * 100) / steps;
                out.println("<div class='progress mb-2'>");
                out.println("<div class='progress-bar progress-bar-striped progress-bar-animated' role='progressbar'");
                out.println("     style='width: " + progress + "%' aria-valuenow='" + progress + "' aria-valuemin='0' aria-valuemax='100'>");
                out.println(progress + "%");
                out.println("</div>");
                out.println("</div>");
                out.flush();
                
                // CPU 작업 시뮬레이션 (가벼운 연산)
                double result = 0;
                for (int j = 0; j < 100000; j++) {
                    result += Math.sqrt(j);
                }
            }
            
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            
            out.println("</div>");
            out.println("</div>");
            
            // 완료 정보
            out.println("<div class='alert alert-success mt-3'>");
            out.println("<h4>✅ Request #" + requestId + " Completed</h4>");
            out.println("<p><strong>Thread:</strong> " + currentThread + "</p>");
            out.println("<p><strong>Total Processing Time:</strong> " + totalTime + " ms</p>");
            out.println("<p><strong>End Time:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")) + "</p>");
            out.println("</div>");
            
            // 컨트롤 버튼들
            out.println("<div class='row mt-4'>");
            out.println("<div class='col-md-6'>");
            out.println("<a href='/shopping-mall/thread-bottleneck-test' class='btn btn-primary btn-lg'>🔄 New Request</a>");
            out.println("</div>");
            out.println("<div class='col-md-6 text-end'>");
            out.println("<a href='/shopping-mall/products' class='btn btn-secondary'>🏠 Back to Home</a>");
            out.println("</div>");
            out.println("</div>");
            
            // 테스트 안내
            out.println("<div class='alert alert-warning mt-4'>");
            out.println("<h5>🧪 Thread Pool Bottleneck Test</h5>");
            out.println("<ul>");
            out.println("<li><strong>Purpose:</strong> Thread pool exhaustion simulation</li>");
            out.println("<li><strong>Current Config:</strong> maxThreads=5 (very limited)</li>");
            out.println("<li><strong>Processing Time:</strong> 3-8 seconds per request</li>");
            out.println("<li><strong>How to Test:</strong> Open multiple browser tabs and refresh simultaneously</li>");
            out.println("</ul>");
            out.println("</div>");
            
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServletException("Request processing interrupted", e);
        }
    }
}
