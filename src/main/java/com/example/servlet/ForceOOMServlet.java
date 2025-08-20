package com.example.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/force-oom")
public class ForceOOMServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // 개선: 정적 변수로 변경하여 GC 방지 (진짜 정상적인 힙 부족 시뮬레이션)
    private static final List<byte[]> NORMAL_MEMORY_USAGE = new ArrayList<>();
    
    // 개선: 진짜 메모리 누수용 정적 변수 (static 액션용)
    private static final List<byte[]> STATIC_MEMORY_LEAK = new ArrayList<>();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        String action = request.getParameter("action");
        if (action == null) action = "gradual";
        
        // 개선: 정리 기능 추가
        if ("cleanup".equals(action)) {
            performCleanup(request, response);
            return;
        }
        
        // 개선: 상태 확인 기능 추가
        if ("status".equals(action)) {
            showMemoryStatus(response);
            return;
        }
        
        Runtime runtime = Runtime.getRuntime();
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();
        
        System.out.printf("[%s] 🚨 강제 OOM 시작 - Action: %s, 시작 메모리: %.2f MB%n", 
            new java.util.Date(), action, beforeMemory / 1024.0 / 1024.0);
        
        try {
            switch (action) {
                case "instant":
                    forceInstantOOM(response);
                    break;
                case "gradual":
                    forceGradualOOM(response);
                    break;
                case "static":
                    forceStaticOOM(response);
                    break;
                case "infinite":
                    forceInfiniteOOM(response);
                    break;
                case "normal-usage":
                    simulateNormalHeavyUsage(response);
                    break;
                default:
                    forceGradualOOM(response);
            }
        } catch (OutOfMemoryError e) {
            System.err.printf("[%s] 🎯 OOM 발생! - %s%n", new java.util.Date(), e.getMessage());
            e.printStackTrace();
            
            try {
                response.getWriter().println("<html><body>");
                response.getWriter().println("<h1 style='color: red;'>🎯 OutOfMemoryError 발생!</h1>");
                response.getWriter().printf("<p>액션: %s</p>", action);
                response.getWriter().println("<p>메모리가 부족하여 요청을 처리할 수 없습니다.</p>");
                response.getWriter().println("<p><a href='?action=cleanup&type=normal'>Normal Memory Cleanup</a> | ");
                response.getWriter().println("<a href='?action=cleanup&type=static'>Static Memory Cleanup</a></p>");
                response.getWriter().println("</body></html>");
            } catch (Exception ignored) {
                // OOM 상황에서는 응답도 실패할 수 있음
            }
            
            throw new ServletException("Forced OutOfMemoryError", e);
        }
    }
    
    private void forceInstantOOM(HttpServletResponse response) throws IOException {
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>🚀 즉시 OOM 유발</h2>");
        response.getWriter().flush();
        
        System.out.println("즉시 OOM 시도 - 대용량 배열 할당");
        
        // 매우 큰 배열을 한 번에 할당하여 즉시 OOM 유발
        byte[] instantOOM = new byte[Integer.MAX_VALUE - 2]; // 약 2GB
        
        response.getWriter().println("<p>이 줄은 출력되지 않을 것입니다.</p>");
        response.getWriter().println("</body></html>");
    }
    
    // 개선: 정적 변수 사용으로 진짜 "정상적인 힙 부족" 시뮬레이션
    private void forceGradualOOM(HttpServletResponse response) throws IOException {
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>📈 점진적 OOM 유발 (정상적인 힙 부족 시뮬레이션)</h2>");
        response.getWriter().println("<p>정적 변수를 사용하여 정상적인 대용량 처리를 시뮬레이션합니다...</p>");
        response.getWriter().println("<p><strong>특징:</strong> FullGC 후 항상 비슷한 양의 메모리가 회수됩니다.</p>");
        response.getWriter().flush();
        
        int initialSize = NORMAL_MEMORY_USAGE.size();
        System.out.printf("점진적 OOM 시작 - 현재 정상 메모리 사용량: %d MB%n", initialSize);
        
        try {
            for (int i = 1; i <= 1000; i++) {
                // 각각 8MB씩 할당 (더 큰 단위로 빠른 OOM 유발)
                byte[] chunk = new byte[8 * 1024 * 1024];
                
                // 배열을 실제 데이터로 채워서 GC 최적화 방지
                for (int j = 0; j < chunk.length; j += 2048) {
                    chunk[j] = (byte) (i % 256);
                }
                
                // 개선: 정적 변수에 저장하여 GC 방지
                NORMAL_MEMORY_USAGE.add(chunk);
                
                // 현재 메모리 상태 출력
                if (i % 8 == 0) {
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                    double usedMB = usedMemory / 1024.0 / 1024.0;
                    double maxMB = runtime.maxMemory() / 1024.0 / 1024.0;
                    double usedPercent = (usedMemory * 100.0) / runtime.maxMemory();
                    
                    System.out.printf("[%s] 정상 메모리 사용 %d단계: %.1f/%.1f MB (%.1f%%), 정상 배열 수: %d%n", 
                        new java.util.Date(), i, usedMB, maxMB, usedPercent, NORMAL_MEMORY_USAGE.size());
                    
                    response.getWriter().printf("<p>%d단계: %.1f MB 사용 (%.1f%%), 정상 배열: %d개</p>", 
                        i, usedMB, usedPercent, NORMAL_MEMORY_USAGE.size());
                    response.getWriter().flush();
                    
                    // 메모리 사용률이 90% 이상이면 더 공격적으로
                    if (usedPercent > 90) {
                        System.out.println("메모리 90% 초과 - 공격적 모드");
                        // 더 큰 청크로 변경
                        byte[] bigChunk = new byte[15 * 1024 * 1024]; // 15MB
                        for (int k = 0; k < bigChunk.length; k += 4096) {
                            bigChunk[k] = (byte) (i % 256);
                        }
                        NORMAL_MEMORY_USAGE.add(bigChunk);
                    }
                }
                
                // 약간의 지연으로 관찰 가능하게
                Thread.sleep(50); // 더 빠른 진행
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        response.getWriter().println("<p>🎯 OOM이 발생하지 않았습니다. 힙 크기가 너무 클 수 있습니다.</p>");
        response.getWriter().printf("<p>현재 정상 메모리 사용량: %d MB</p>", NORMAL_MEMORY_USAGE.size());
        response.getWriter().println("<p><a href='?action=cleanup&type=normal'>정상 메모리 정리</a></p>");
        response.getWriter().println("</body></html>");
    }
    
    private void forceStaticOOM(HttpServletResponse response) throws IOException {
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>📌 정적 변수 OOM 유발 (메모리 누수 시뮬레이션)</h2>");
        response.getWriter().println("<p>정적 변수를 통한 실제 메모리 누수...</p>");
        response.getWriter().println("<p><strong>특징:</strong> FullGC 후에도 메모리가 회수되지 않습니다.</p>");
        response.getWriter().flush();
        
        System.out.println("정적 변수 메모리 누수 시작");
        
        for (int i = 1; i <= 500; i++) {
            // 누수용 정적 리스트에 계속 추가 (GC되지 않음)
            byte[] staticChunk = new byte[12 * 1024 * 1024]; // 12MB
            
            // 실제 데이터로 채움
            for (int j = 0; j < staticChunk.length; j += 3072) {
                staticChunk[j] = (byte) (i % 256);
            }
            
            STATIC_MEMORY_LEAK.add(staticChunk);
            
            if (i % 4 == 0) {
                Runtime runtime = Runtime.getRuntime();
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                double usedPercent = (usedMemory * 100.0) / runtime.maxMemory();
                
                System.out.printf("[%s] 정적 메모리 누수 %d단계: %.1f MB (%.1f%%), 누수 배열 수: %d%n", 
                    new java.util.Date(), i, usedMemory / 1024.0 / 1024.0, usedPercent, STATIC_MEMORY_LEAK.size());
                
                response.getWriter().printf("<p>정적 누수 %d단계: %.1f%% 사용, 누수 배열: %d개</p>", 
                    i, usedPercent, STATIC_MEMORY_LEAK.size());
                response.getWriter().flush();
            }
            
            try {
                Thread.sleep(30); // 빠른 진행
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        
        response.getWriter().println("<p>정적 변수 메모리 누수 완료</p>");
        response.getWriter().printf("<p>총 누수량: %d MB</p>", STATIC_MEMORY_LEAK.size());
        response.getWriter().println("<p><a href='?action=cleanup&type=static'>누수 메모리 정리</a></p>");
        response.getWriter().println("</body></html>");
    }
    
    // 개선: 새로운 정상적인 대용량 처리 시뮬레이션
    private void simulateNormalHeavyUsage(HttpServletResponse response) throws IOException {
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>💼 정상적인 대용량 데이터 처리 시뮬레이션</h2>");
        response.getWriter().println("<p>배치 처리, 대용량 파일 처리 등을 시뮬레이션합니다.</p>");
        response.getWriter().flush();
        
        for (int batch = 1; batch <= 10; batch++) {
            System.out.printf("배치 %d 시작%n", batch);
            response.getWriter().printf("<p>배치 %d 처리 중...</p>", batch);
            response.getWriter().flush();
            
            // 배치별로 대용량 데이터 처리
            List<byte[]> batchData = new ArrayList<>();
            
            try {
                // 배치당 200-300MB 처리
                int batchSize = 200 + (int)(Math.random() * 100);
                for (int i = 0; i < batchSize; i++) {
                    byte[] data = new byte[1024 * 1024]; // 1MB
                    
                    // 데이터 처리 시뮬레이션
                    for (int j = 0; j < data.length; j += 1024) {
                        data[j] = (byte) ((batch * i) % 256);
                    }
                    
                    batchData.add(data);
                    
                    // 중간 처리
                    if (i % 50 == 0) {
                        // 일부 데이터 정리 (정상적인 처리 과정)
                        if (batchData.size() > 100) {
                            for (int k = 0; k < 20; k++) {
                                batchData.remove(0);
                            }
                        }
                        
                        Runtime runtime = Runtime.getRuntime();
                        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                        double usedPercent = (usedMemory * 100.0) / runtime.maxMemory();
                        
                        if (i % 100 == 0) {
                            response.getWriter().printf("&nbsp;&nbsp;- 진행률: %d/%d (%.1f%% 메모리 사용)<br>", 
                                i, batchSize, usedPercent);
                            response.getWriter().flush();
                        }
                    }
                }
                
                // 배치 완료 후 일부만 유지 (정상적인 결과 캐싱)
                while (batchData.size() > 50) {
                    batchData.remove(0);
                }
                
                // 결과를 정상 메모리 사용량에 추가
                NORMAL_MEMORY_USAGE.addAll(batchData);
                
                System.out.printf("배치 %d 완료 - 유지된 데이터: %d MB%n", batch, batchData.size());
                response.getWriter().printf("<p>배치 %d 완료 - 결과 데이터: %d MB 유지</p>", batch, batchData.size());
                response.getWriter().flush();
                
                Thread.sleep(1000); // 배치 간 간격
                
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        response.getWriter().println("<p>✅ 정상적인 대용량 처리 완료</p>");
        response.getWriter().printf("<p>총 처리 결과: %d MB</p>", NORMAL_MEMORY_USAGE.size());
        response.getWriter().println("<p><a href='?action=cleanup&type=normal'>처리 결과 정리</a></p>");
        response.getWriter().println("</body></html>");
    }
    
    private void forceInfiniteOOM(HttpServletResponse response) throws IOException {
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>♾️ 무한 메모리 할당</h2>");
        response.getWriter().println("<p>OOM이 발생할 때까지 무한 할당...</p>");
        response.getWriter().flush();
        
        List<byte[]> infiniteMemory = new ArrayList<>();
        int count = 0;
        
        while (true) {
            count++;
            
            // 2MB씩 무한 할당 (더 빠른 OOM)
            byte[] chunk = new byte[2 * 1024 * 1024];
            
            // 실제 데이터로 채움
            for (int i = 0; i < chunk.length; i += 1024) {
                chunk[i] = (byte) (count % 256);
            }
            
            infiniteMemory.add(chunk);
            
            // 50개마다 상태 출력
            if (count % 50 == 0) {
                Runtime runtime = Runtime.getRuntime();
                long usedMemory = runtime.totalMemory() - runtime.freeMemory();
                double usedPercent = (usedMemory * 100.0) / runtime.maxMemory();
                
                System.out.printf("[%s] 무한 할당 %d: %.1f%% 사용%n", 
                    new java.util.Date(), count, usedPercent);
                
                if (count % 200 == 0) {
                    response.getWriter().printf("<p>무한 할당 %d번째: %.1f%% 사용</p>", count, usedPercent);
                    response.getWriter().flush();
                }
            }
            
            // 컴파일러 최적화 방지
            if (chunk[0] == (byte) 255) {
                System.out.println("최적화 방지");
            }
        }
    }
    
    // 개선: 정리 기능
    private void performCleanup(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String type = request.getParameter("type");
        
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>🧹 메모리 정리</h2>");
        
        if ("normal".equals(type)) {
            int beforeSize = NORMAL_MEMORY_USAGE.size();
            NORMAL_MEMORY_USAGE.clear();
            System.gc();
            
            System.out.printf("정상 메모리 사용량 정리 완료: %d MB 해제%n", beforeSize);
            response.getWriter().printf("<p>✅ 정상 메모리 사용량 정리 완료: %d MB 해제</p>", beforeSize);
            
        } else if ("static".equals(type)) {
            int beforeSize = STATIC_MEMORY_LEAK.size();
            STATIC_MEMORY_LEAK.clear();
            System.gc();
            
            System.out.printf("정적 메모리 누수 정리 완료: %d MB 해제%n", beforeSize);
            response.getWriter().printf("<p>✅ 정적 메모리 누수 정리 완료: %d MB 해제</p>", beforeSize);
            
        } else if ("all".equals(type)) {
            int normalSize = NORMAL_MEMORY_USAGE.size();
            int staticSize = STATIC_MEMORY_LEAK.size();
            
            NORMAL_MEMORY_USAGE.clear();
            STATIC_MEMORY_LEAK.clear();
            System.gc();
            
            System.out.printf("전체 메모리 정리 완료: 정상 %d MB + 누수 %d MB = 총 %d MB 해제%n", 
                normalSize, staticSize, normalSize + staticSize);
            response.getWriter().printf("<p>✅ 전체 메모리 정리 완료:</p>");
            response.getWriter().printf("<p>&nbsp;&nbsp;- 정상 메모리: %d MB 해제</p>", normalSize);
            response.getWriter().printf("<p>&nbsp;&nbsp;- 누수 메모리: %d MB 해제</p>", staticSize);
            response.getWriter().printf("<p>&nbsp;&nbsp;- 총합: %d MB 해제</p>", normalSize + staticSize);
            
        } else {
            response.getWriter().println("<p>❌ 잘못된 정리 타입입니다.</p>");
        }
        
        try {
            Thread.sleep(500); // GC 시간 대기
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 정리 후 현재 상태 표시
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        double usedPercent = (usedMemory * 100.0) / (runtime.maxMemory() / (1024 * 1024));
        
        response.getWriter().printf("<p><strong>정리 후 메모리 사용량:</strong> %d MB (%.1f%%)</p>", usedMemory, usedPercent);
        response.getWriter().println("<hr>");
        response.getWriter().println("<p><a href='?action=status'>메모리 상태 확인</a> | ");
        response.getWriter().println("<a href='?action=gradual'>점진적 OOM 테스트</a> | ");
        response.getWriter().println("<a href='?action=static'>누수 OOM 테스트</a></p>");
        response.getWriter().println("</body></html>");
    }
    
    // 개선: 메모리 상태 확인 기능
    private void showMemoryStatus(HttpServletResponse response) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long maxMemory = runtime.maxMemory() / (1024 * 1024);
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().println("<html><body>");
        response.getWriter().println("<h2>Force OOM Servlet Status</h2>");
        
        response.getWriter().println("<h3>💾 현재 메모리 상태</h3>");
        response.getWriter().println("<p><strong>전체 메모리 사용량:</strong> " + usedMemory + " MB / " + maxMemory + " MB (" + String.format("%.1f", memoryUsagePercent) + "%)</p>");
        
        response.getWriter().println("<h3>📊 메모리 사용 분류</h3>");
        response.getWriter().println("<p><strong>정상 메모리 사용량:</strong> " + NORMAL_MEMORY_USAGE.size() + " MB</p>");
        response.getWriter().println("<p><strong>정적 메모리 누수:</strong> " + STATIC_MEMORY_LEAK.size() + " MB</p>");
        response.getWriter().println("<p><strong>총 추적된 메모리:</strong> " + (NORMAL_MEMORY_USAGE.size() + STATIC_MEMORY_LEAK.size()) + " MB</p>");
        
        double normalPercent = NORMAL_MEMORY_USAGE.size() * 100.0 / maxMemory;
        double leakPercent = STATIC_MEMORY_LEAK.size() * 100.0 / maxMemory;
        
        response.getWriter().println("<h3>📈 메모리 사용률 분석</h3>");
        response.getWriter().println("<p><strong>정상 사용률:</strong> " + String.format("%.1f", normalPercent) + "%</p>");
        response.getWriter().println("<p><strong>누수 사용률:</strong> " + String.format("%.1f", leakPercent) + "%</p>");
        response.getWriter().println("<p><strong>기타 사용률:</strong> " + String.format("%.1f", memoryUsagePercent - normalPercent - leakPercent) + "%</p>");
        
        response.getWriter().println("<hr>");
        response.getWriter().println("<h3>🚀 테스트 액션</h3>");
        response.getWriter().println("<p><a href='?action=gradual'>점진적 OOM (정상 힙 부족)</a></p>");
        response.getWriter().println("<p><a href='?action=static'>정적 변수 OOM (메모리 누수)</a></p>");
        response.getWriter().println("<p><a href='?action=normal-usage'>정상 대용량 처리</a></p>");
        response.getWriter().println("<p><a href='?action=infinite'>무한 할당 OOM</a></p>");
        response.getWriter().println("<p><a href='?action=instant'>즉시 OOM</a></p>");
        
        response.getWriter().println("<h3>🧹 정리 액션</h3>");
        response.getWriter().println("<p><a href='?action=cleanup&type=normal'>정상 메모리 정리</a></p>");
        response.getWriter().println("<p><a href='?action=cleanup&type=static'>누수 메모리 정리</a></p>");
        response.getWriter().println("<p><a href='?action=cleanup&type=all'>전체 메모리 정리</a></p>");
        
        response.getWriter().println("<hr>");
        response.getWriter().println("<p><a href='?action=status'>상태 새로고침</a></p>");
        response.getWriter().println("</body></html>");
    }
}
