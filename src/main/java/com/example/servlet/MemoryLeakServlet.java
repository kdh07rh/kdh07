package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet("/memory-leak-test")
public class MemoryLeakServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<byte[]> memoryHog = new ArrayList<>();
    private static final Random random = new Random();
    
    // 개선: 누수 제한을 크게 늘려서 더 심각한 누수 시나리오 구현
    private static final int MAX_MEMORY_MB = 800; // 800MB까지 누수 허용 (기존 120MB에서 증가)
    private static long requestCount = 0;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        // 개선: 수동 정리 기능 추가
        if ("cleanup".equals(action)) {
            performManualCleanup();
            response.getWriter().println("Manual cleanup performed. Remaining leaks: " + memoryHog.size() + " MB");
            return;
        }
        
        // 개선: 상태 확인 기능 추가
        if ("status".equals(action)) {
            showMemoryStatus(response);
            return;
        }
        
        long startTime = System.currentTimeMillis();
        
        if ("leak".equals(action)) {
            requestCount++;
            
            // 현재 메모리 상태 확인
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            System.out.println(String.format(
                "[%s] MemoryLeakServlet: Used=%dMB, Max=%dMB, Usage=%.1f%%, LeakedArrays=%d, RequestCount=%d", 
                new java.util.Date(), usedMemory, maxMemory, memoryUsagePercent, memoryHog.size(), requestCount
            ));
            
            // 개선: 더 공격적인 누수 패턴으로 변경
            if (memoryUsagePercent > 85) {
                // 85% 초과시: 최소한의 정리만 + 지연
                performMinimalCleanup();
                performMaintenanceWork();
                System.out.println("MemoryLeakServlet: Critical memory usage - minimal cleanup only");
            } else if (memoryUsagePercent > 65) {
                // 65-85%: 강력한 누수 + CPU 부하
                performAggressiveMemoryLeak();
                performHeavyMemoryPressureWork();
                performExtendedCPUWork();
                System.out.println("MemoryLeakServlet: High memory usage - aggressive leak mode");
            } else if (memoryHog.size() < MAX_MEMORY_MB) {
                // 65% 미만: 매우 공격적 누수
                performVeryAggressiveMemoryLeak();
                performHeavyMemoryPressureWork();
                performExtendedCPUWork();
                System.out.println("MemoryLeakServlet: Low memory usage - very aggressive leak mode");
            } else {
                // 최대 누수량 도달: 현재 상태 유지
                performHighIntensityMaintenance();
                System.out.println("MemoryLeakServlet: Max leak capacity reached - maintenance mode");
            }
            
            // 개선: 주기적 정리를 더 보수적으로 (50 -> 80, 정리량 40% -> 15%)
            if (requestCount % 80 == 0) {
                performConservativeCleanup();
                System.out.println("MemoryLeakServlet: Conservative cleanup performed (every 80 requests)");
            }
        }
        
        long processingTime = System.currentTimeMillis() - startTime;
        System.out.println("MemoryLeakServlet: Processing took " + processingTime + "ms");
        
        // 현재 상태 정보
        Runtime runtime = Runtime.getRuntime();
        long currentUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        double currentUsagePercent = (double) currentUsed / (runtime.maxMemory() / (1024 * 1024)) * 100;
        
        request.setAttribute("leakSize", memoryHog.size());
        request.setAttribute("processingTime", processingTime);
        request.setAttribute("currentMemoryUsage", currentUsed);
        request.setAttribute("memoryUsagePercent", String.format("%.1f", currentUsagePercent));
        request.setAttribute("requestCount", requestCount);
        request.setAttribute("maxLeakCapacity", MAX_MEMORY_MB);
        
        request.getRequestDispatcher("memory-leak-test.jsp").forward(request, response);
    }
    
    // 개선: 더 공격적인 누수 메서드들
    private void performVeryAggressiveMemoryLeak() {
        // 50-80MB 메모리 누수 (기존 30-50MB에서 증가)
        int leakSizeMB = 50 + random.nextInt(31);
        for (int i = 0; i < leakSizeMB; i++) {
            byte[] leak = new byte[1024 * 1024];
            // 실제 데이터로 채워서 GC 최적화 방지
            for (int j = 0; j < leak.length; j += 1024) {
                leak[j] = (byte) random.nextInt(256);
            }
            memoryHog.add(leak);
        }
        System.out.println("MemoryLeakServlet: Very aggressive leak - added " + leakSizeMB + " MB");
    }
    
    private void performAggressiveMemoryLeak() {
        // 30-50MB 메모리 누수
        int leakSizeMB = 30 + random.nextInt(21);
        for (int i = 0; i < leakSizeMB; i++) {
            byte[] leak = new byte[1024 * 1024];
            // 실제 데이터로 채워서 GC 최적화 방지
            for (int j = 0; j < leak.length; j += 1024) {
                leak[j] = (byte) random.nextInt(256);
            }
            memoryHog.add(leak);
        }
        System.out.println("MemoryLeakServlet: Aggressive leak - added " + leakSizeMB + " MB");
    }
    
    private void performHeavyMemoryPressureWork() {
        // 임시 메모리 압박 (200-300MB)
        List<byte[]> tempMemory = new ArrayList<>();
        
        try {
            int tempSize = 200 + random.nextInt(101);
            for (int i = 0; i < tempSize; i++) {
                tempMemory.add(new byte[1024 * 1024]);
                
                // CPU 작업과 지연
                if (i % 3 == 0) {
                    performCPUWork();
                    Thread.yield();
                    
                    try {
                        Thread.sleep(8); // 8ms 지연
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            // 메모리 작업
            for (int i = 0; i < Math.min(100, tempMemory.size()); i++) {
                byte[] memory = tempMemory.get(i);
                for (int j = 0; j < 8000; j += 40) {
                    if (j < memory.length) {
                        memory[j] = (byte) random.nextInt(256);
                    }
                }
            }
            
        } finally {
            tempMemory.clear();
        }
    }
    
    private void performHighIntensityMaintenance() {
        // 고강도 유지 모드
        try {
            Thread.sleep(2000 + random.nextInt(3000)); // 2-5초 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        performExtendedCPUWork();
        performCPUWork();
    }
    
    private void performExtendedCPUWork() {
        long iterations = 800000 + random.nextInt(700000);
        double result = 0;
        for (long i = 0; i < iterations; i++) {
            result += Math.sqrt(i) * Math.sin(i % 1000) * Math.cos(i % 500);
            
            if (i % 15000 == 0) {
                System.currentTimeMillis();
            }
        }
        
        if (result > Double.MAX_VALUE - 1000) {
            System.out.println("Extended CPU work result: " + result);
        }
    }
    
    private void performMaintenanceWork() {
        try {
            Thread.sleep(1000 + random.nextInt(2000)); // 1-3초 지연
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        performCPUWork();
    }
    
    // 개선: 최소한의 정리만 수행 (5% 정도)
    private void performMinimalCleanup() {
        int cleanupCount = Math.max(1, memoryHog.size() / 20); // 5% 정리
        for (int i = 0; i < cleanupCount && !memoryHog.isEmpty(); i++) {
            memoryHog.remove(memoryHog.size() - 1);
        }
        
        System.gc();
        System.out.println("MemoryLeakServlet: Minimal cleanup - removed " + cleanupCount + " MB");
        
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // 개선: 보수적 정리 (15% 정도만 정리)
    private void performConservativeCleanup() {
        int cleanupCount = (memoryHog.size() * 15) / 100; // 15% 정리 (기존 40%에서 감소)
        for (int i = 0; i < cleanupCount; i++) {
            if (!memoryHog.isEmpty()) {
                memoryHog.remove(0);
            }
        }
        
        System.gc();
        System.out.println("MemoryLeakServlet: Conservative cleanup - removed " + cleanupCount + " MB, remaining: " + memoryHog.size() + " MB");
    }
    
    // 개선: 수동 정리 기능
    private void performManualCleanup() {
        int beforeSize = memoryHog.size();
        memoryHog.clear();
        System.gc();
        System.out.println("MemoryLeakServlet: Manual cleanup completed - removed " + beforeSize + " MB");
        
        try {
            Thread.sleep(200); // GC 시간 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    // 개선: 메모리 상태 확인 기능
    private void showMemoryStatus(HttpServletResponse response) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>Memory Leak Servlet Status</h2>");
        response.getWriter().println("<p><strong>Current Memory Usage:</strong> " + usedMemory + " MB / " + maxMemory + " MB (" + String.format("%.1f", memoryUsagePercent) + "%)</p>");
        response.getWriter().println("<p><strong>Leaked Arrays:</strong> " + memoryHog.size() + " MB</p>");
        response.getWriter().println("<p><strong>Max Leak Capacity:</strong> " + MAX_MEMORY_MB + " MB</p>");
        response.getWriter().println("<p><strong>Request Count:</strong> " + requestCount + "</p>");
        response.getWriter().println("<p><strong>Leak Utilization:</strong> " + String.format("%.1f", (double) memoryHog.size() / MAX_MEMORY_MB * 100) + "%</p>");
        response.getWriter().println("<hr>");
        response.getWriter().println("<p><a href='?action=leak'>Continue Leaking</a> | ");
        response.getWriter().println("<a href='?action=cleanup'>Manual Cleanup</a> | ");
        response.getWriter().println("<a href='?action=status'>Refresh Status</a></p>");
        response.getWriter().println("</body></html>");
    }
    
    private void performCPUWork() {
        long iterations = 300000 + random.nextInt(400000);
        double result = 0;
        for (long i = 0; i < iterations; i++) {
            result += Math.sqrt(i) * Math.sin(i % 1000);
        }
        
        if (result > Double.MAX_VALUE - 1000) {
            System.out.println("CPU work result: " + result);
        }
    }
}
