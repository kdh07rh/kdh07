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
import java.util.concurrent.atomic.AtomicInteger;

@WebServlet("/infinite-loop-test")
public class InfiniteLoopServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final AtomicInteger activeLoops = new AtomicInteger(0);

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String startTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
        String threadName = Thread.currentThread().getName();
        long threadId = Thread.currentThread().getId();
        
        // 즉시 응답 전송 (클라이언트가 상태를 알 수 있도록)
        out.println("<html><body>");
        out.println("<h2>🔥 Infinite Loop Test - STARTING NOW!</h2>");
        out.println("<p><strong>시작 시간:</strong> " + startTime + "</p>");
        out.println("<p><strong>스레드:</strong> " + threadName + " (ID: " + threadId + ")</p>");
        out.println("<p><strong>활성 루프:</strong> " + activeLoops.incrementAndGet() + "</p>");
        out.println("<p style='color: red; font-size: 20px;'><b>무한루프 시작! CPU 100% 사용 예정!</b></p>");
        out.flush(); // 즉시 클라이언트로 전송
        
        // 강력한 로깅
        System.out.println("=".repeat(80));
        System.out.println(String.format(
            "[%s] 🚨 INFINITE LOOP START 🚨", startTime
        ));
        System.out.println(String.format(
            "Thread: %s (ID: %d)", threadName, threadId
        ));
        System.out.println(String.format(
            "Active Loops: %d", activeLoops.get()
        ));
        System.out.println("=".repeat(80));

        // 여러 종류의 CPU 집약적 무한루프
        String loopType = request.getParameter("type");
        
        try {
            if ("regex".equals(loopType)) {
                intensiveCpuLoop("REGEX");
            } else if ("recursive".equals(loopType)) {
                intensiveCpuLoop("RECURSIVE");  
            } else {
                intensiveCpuLoop("BASIC");
            }
        } finally {
            // 이 코드는 절대 실행되지 않음
            activeLoops.decrementAndGet();
        }
    }
    
    private void intensiveCpuLoop(String type) {
        System.out.println(String.format(
            "[%s] 🔥 %s 무한루프 시작 - Thread: %s", 
            LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
            type,
            Thread.currentThread().getName()
        ));
        
        long counter = 0;
        double result = 0;
        
        // 매우 CPU 집약적인 무한루프
        while (true) {
            // 복잡한 수학 연산으로 CPU 사용률 극대화
            for (int i = 0; i < 10000; i++) {
                result += Math.sqrt(counter + i) * Math.sin(counter + i) * Math.cos(counter + i);
                result += Math.pow(counter % 100, 2);
                result += Math.log(Math.abs(counter) + 1);
            }
            
            // 메모리 접근 패턴으로 캐시 미스 유발
            String dummy = "infinite_loop_" + counter + "_" + System.nanoTime();
            dummy.hashCode(); // CPU 사용
            
            counter++;
            
            // 주기적 로깅 (1억번마다)
            if (counter % 100000000L == 0) {
                System.out.println(String.format(
                    "[%s] 🔥 %s Loop Counter: %d, Result: %.2f - Thread: %s", 
                    LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")),
                    type,
                    counter, 
                    result,
                    Thread.currentThread().getName()
                ));
            }
            
            // 컴파일러 최적화 방지
            if (result > Double.MAX_VALUE - 1000) {
                System.out.println("Preventing optimization: " + result);
                result = 0;
            }
        }
    }
    
    // 브라우저 직접 테스트용 간단한 페이지도 추가
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
