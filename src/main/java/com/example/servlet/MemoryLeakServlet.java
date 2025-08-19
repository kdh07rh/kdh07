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
import java.util.concurrent.ThreadLocalRandom;

@WebServlet("/memory-leak-test")
public class MemoryLeakServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<byte[]> memoryHog = new ArrayList<>();
    private static final Random random = new Random();
    
    // 안전 장치: 최대 메모리 사용량 제한 (더 공격적으로)
    private static final int MAX_MEMORY_MB = 120; // 최대 120MB까지만 누수 (감소)
    private static final long MEMORY_CHECK_INTERVAL = 10;
    private static long requestCount = 0;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        long startTime = System.currentTimeMillis();
        
        if ("leak".equals(action)) {
            requestCount++;
            
            // 현재 메모리 상태 확인
            Runtime runtime = Runtime.getRuntime();
            long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
            long maxMemory = runtime.maxMemory() / (1024 * 1024);
            double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
            
            System.out.println(String.format(
                "MemoryServlet: Used=%dMB, Max=%dMB, Usage=%.1f%%, LeakedArrays=%d", 
                usedMemory, maxMemory, memoryUsagePercent, memoryHog.size()
            ));
            
            // 메모리 사용률에 따른 동적 제어 (더 공격적)
            if (memoryUsagePercent > 80) {
                // 80% 초과시: 메모리 정리 + 가벼운 부하만
                performMemoryCleanup();
                performLightMemoryWork();
                System.out.println("MemoryServlet: High memory usage - cleanup performed");
            } else if (memoryUsagePercent > 55) { // 70% → 55%로 낮춤
                // 55-80%: 강한 메모리 누수 + 압박 (더 이른 시점부터)
                performAggressiveMemoryLeak(); // 보통 → 공격적으로 변경
                performHeavyMemoryPressureWork(); // 더 강한 압박
                performExtendedCPUWork(); // CPU 부하 추가
                System.out.println("MemoryServlet: Early aggressive memory pressure applied");
            } else if (memoryHog.size() < MAX_MEMORY_MB) {
                // 55% 미만: 매우 적극적 메모리 누수
                performVeryAggressiveMemoryLeak(); // 새로운 단계 추가
                performHeavyMemoryPressureWork();
                performExtendedCPUWork();
                System.out.println("MemoryServlet: Very aggressive memory leak applied");
            } else {
                // 최대 누수량 도달: 고강도 유지 모드
                performHighIntensityMaintenance();
                System.out.println("MemoryServlet: High intensity maintenance mode");
            }
            
            // 더 자주 정리 (WAS 생존 보장하면서 압박 유지)
            if (requestCount % 30 == 0) { // 50 → 30으로 단축
                performPeriodicCleanup();
                System.out.println("MemoryServlet: Periodic cleanup performed (every 30 requests)");
            }
        }
        
        long processingTime = System.currentTimeMillis() - startTime;
        System.out.println("MemoryServlet: Processing took " + processingTime + "ms");
        
        // 현재 상태 정보
        Runtime runtime = Runtime.getRuntime();
        long currentUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        double currentUsagePercent = (double) currentUsed / (runtime.maxMemory() / (1024 * 1024)) * 100;
        
        request.setAttribute("leakSize", memoryHog.size());
        request.setAttribute("processingTime", processingTime);
        request.setAttribute("currentMemoryUsage", currentUsed);
        request.setAttribute("memoryUsagePercent", String.format("%.1f", currentUsagePercent));
        request.setAttribute("requestCount", requestCount);
        
        request.getRequestDispatcher("memory-leak-test.jsp").forward(request, response);
    }
    
    private void performVeryAggressiveMemoryLeak() {
        // 30-50MB 메모리 누수 (더 공격적)
        int leakSizeMB = 30 + random.nextInt(21);
        for (int i = 0; i < leakSizeMB; i++) {
            memoryHog.add(new byte[1024 * 1024]);
        }
    }
    
    private void performAggressiveMemoryLeak() {
        // 20-35MB 메모리 누수 (기존보다 증가)
        int leakSizeMB = 20 + random.nextInt(16);
        for (int i = 0; i < leakSizeMB; i++) {
            memoryHog.add(new byte[1024 * 1024]);
        }
    }
    
    private void performModerateMemoryLeak() {
        // 5-15MB 메모리 누수
        int leakSizeMB = 5 + random.nextInt(11);
        for (int i = 0; i < leakSizeMB; i++) {
            memoryHog.add(new byte[1024 * 1024]);
        }
    }
    
    private void performHeavyMemoryPressureWork() {
        // 더 강한 임시 메모리 압박 (150-250MB)
        List<byte[]> tempMemory = new ArrayList<>();
        
        try {
            // 150-250MB 임시 할당 (기존 50-100MB에서 증가)
            int tempSize = 150 + random.nextInt(101);
            for (int i = 0; i < tempSize; i++) {
                tempMemory.add(new byte[1024 * 1024]);
                
                // 더 자주 지연과 CPU 작업 (5번에 한 번)
                if (i % 5 == 0) {
                    performCPUWork();
                    Thread.yield();
                    
                    // 추가 의도적 지연
                    try {
                        Thread.sleep(5); // 5ms 추가 지연
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
            
            // 더 집약적인 메모리 작업
            for (int i = 0; i < Math.min(50, tempMemory.size()); i++) { // 10 → 50으로 증가
                byte[] memory = tempMemory.get(i);
                for (int j = 0; j < 5000; j += 50) { // 더 많은 메모리 작업
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
        // 고강도 유지 모드 (더 긴 지연)
        try {
            // 의도적 지연 (1-4초로 증가)
            Thread.sleep(1000 + random.nextInt(3000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 연속적인 CPU 작업
        performExtendedCPUWork();
        performCPUWork();
    }
    
    private void performExtendedCPUWork() {
        // 확장된 CPU 작업 (0.5-1초)
        long iterations = 500000 + random.nextInt(500000);
        double result = 0;
        for (long i = 0; i < iterations; i++) {
            result += Math.sqrt(i) * Math.sin(i % 1000) * Math.cos(i % 500);
            
            // 중간중간 메모리 접근 (캐시 미스 유발)
            if (i % 10000 == 0) {
                System.currentTimeMillis(); // 시스템 호출
            }
        }
        
        // 컴파일러 최적화 방지
        if (result > Double.MAX_VALUE - 1000) {
            System.out.println("Extended CPU work result: " + result);
        }
    }
    
    private void performLightMemoryWork() {
        // 가벼운 메모리 작업만 (WAS 보호)
        List<String> tempStrings = new ArrayList<>();
        
        try {
            for (int i = 0; i < 1000; i++) {
                tempStrings.add("Light work " + i + "_" + System.currentTimeMillis());
            }
            
            // 가벼운 CPU 작업
            performLightCPUWork();
            
        } finally {
            tempStrings.clear();
        }
    }
    
    private void performMaintenanceWork() {
        // 현재 상태 유지하면서 응답시간만 지연
        try {
            // 의도적 지연 (0.5-2초)
            Thread.sleep(500 + random.nextInt(1500));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 가벼운 CPU 작업
        performCPUWork();
    }
    
    private void performMemoryCleanup() {
        // 메모리 정리 (20-30% 정도)
        int cleanupCount = Math.max(1, memoryHog.size() / 4); // 25% 정리
        for (int i = 0; i < cleanupCount && !memoryHog.isEmpty(); i++) {
            memoryHog.remove(memoryHog.size() - 1);
        }
        
        // 강제 GC 제안
        System.gc();
        
        try {
            Thread.sleep(100); // GC 시간 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    private void performPeriodicCleanup() {
        // 30번째 요청마다 40% 정리 (50% → 40%로 감소하여 더 많은 메모리 유지)
        int cleanupCount = (memoryHog.size() * 2) / 5; // 40% 정리
        for (int i = 0; i < cleanupCount; i++) {
            if (!memoryHog.isEmpty()) {
                memoryHog.remove(0); // 오래된 것부터 제거
            }
        }
        
        System.gc();
        System.out.println("MemoryServlet: Cleaned up " + cleanupCount + " MB, remaining: " + memoryHog.size() + " MB");
    }
    
    private void performCPUWork() {
        // 중간 수준의 CPU 작업 (0.2-0.5초)
        long iterations = 200000 + random.nextInt(300000);
        double result = 0;
        for (long i = 0; i < iterations; i++) {
            result += Math.sqrt(i) * Math.sin(i % 1000);
        }
        
        // 컴파일러 최적화 방지
        if (result > Double.MAX_VALUE - 1000) {
            System.out.println("CPU work result: " + result);
        }
    }
    
    private void performLightCPUWork() {
        // 가벼운 CPU 작업 (0.1초 미만)
        long iterations = 50000;
        double result = 0;
        for (long i = 0; i < iterations; i++) {
            result += Math.sqrt(i);
        }
    }
}
