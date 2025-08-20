<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.io.*" %>
<%@ page import="java.time.LocalDateTime" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.Random" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>시스템 모니터링 대시보드</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .metric-card {
            transition: all 0.3s ease;
        }
        .metric-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .status-indicator {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            display: inline-block;
            margin-right: 8px;
        }
        .status-normal { background-color: #28a745; }
        .status-warning { background-color: #ffc107; }
        .status-error { background-color: #dc3545; }
    </style>
</head>
<body>
    <%
        // 실제 업무 로직처럼 보이는 시스템 모니터링 데이터 생성
        Random random = new Random();
        long startTime = System.currentTimeMillis();
        
        // 여러 시스템 컴포넌트의 상태를 확인하는 것처럼 시뮬레이션
        // 이 과정에서 과도한 로깅이 발생한다고 가정
        
        String logDir = System.getProperty("catalina.home", ".") + "/logs";
        File logDirFile = new File(logDir);
        if (!logDirFile.exists()) {
            logDirFile.mkdirs();
        }
        
        // 실제 문제가 되는 패턴: 세밀한 디버깅 로그를 동기 방식으로 기록
        String auditLogFile = logDir + "/audit-" + 
                             LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
        String performanceLogFile = logDir + "/performance-" + 
                                   LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".log";
        
        // 시스템 메트릭 생성 (랜덤)
        int cpuUsage = random.nextInt(30) + 20; // 20-50%
        int memoryUsage = random.nextInt(40) + 30; // 30-70%
        int diskUsage = random.nextInt(25) + 15; // 15-40%
        int networkIO = random.nextInt(100) + 50; // 50-150 Mbps
        
        int activeUsers = random.nextInt(100) + 50;
        int activeSessions = random.nextInt(200) + 100;
        int dbConnections = random.nextInt(20) + 5;
        
        // 과도한 로깅 수행 (실제 문제 패턴)
        int logIntensity = random.nextInt(3) + 1; // 1-3 강도
        
        try (BufferedWriter auditWriter = new BufferedWriter(new FileWriter(auditLogFile, true));
             BufferedWriter perfWriter = new BufferedWriter(new FileWriter(performanceLogFile, true))) {
            
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            
            // 감사 로그 (보안 정책상 모든 접근을 기록해야 한다는 이유)
            for (int i = 0; i < logIntensity * 200; i++) {
                auditWriter.write(String.format("[%s] AUDIT - User access monitoring dashboard, IP=%s, Session=%s, Result=SUCCESS%n",
                    timestamp, 
                    "192.168.1." + (random.nextInt(254) + 1),
                    "SESS" + System.currentTimeMillis() + random.nextInt(1000)));
                    
                if (i % 50 == 0) {
                    auditWriter.flush(); // 강제 디스크 쓰기
                }
            }
            
            // 성능 로그 (디버깅용이라는 이유로 모든 메트릭을 상세히 기록)
            for (int i = 0; i < logIntensity * 300; i++) {
                perfWriter.write(String.format("[%s] PERF - Component=%s, Metric=%s, Value=%d, Threshold=%d, Status=%s%n",
                    timestamp,
                    "COMP-" + (i % 10),
                    "metric-" + random.nextInt(20),
                    random.nextInt(1000),
                    random.nextInt(800) + 200,
                    random.nextBoolean() ? "OK" : "WARN"));
                    
                if (i % 30 == 0) {
                    perfWriter.flush(); // 강제 디스크 쓰기
                    Thread.yield(); // CPU 양보
                }
            }
        } catch (IOException e) {
            // 로깅 실패도 로그로... (악순환의 시작)
            e.printStackTrace();
        }
        
        long processingTime = System.currentTimeMillis() - startTime;
        
        // 상태 결정
        String systemStatus = "정상";
        String statusClass = "status-normal";
        if (processingTime > 3000) {
            systemStatus = "주의";
            statusClass = "status-warning";
        }
        if (processingTime > 5000) {
            systemStatus = "위험";
            statusClass = "status-error";
        }
    %>

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h1 class="h3">
                <span class="<%= statusClass %> status-indicator"></span>
                시스템 모니터링 대시보드
            </h1>
            <div class="badge bg-<%= statusClass.contains("normal") ? "success" : statusClass.contains("warning") ? "warning" : "danger" %> fs-6">
                <%= systemStatus %>
            </div>
        </div>

        <!-- 시스템 메트릭 카드들 -->
        <div class="row mb-4">
            <div class="col-md-3">
                <div class="card metric-card">
                    <div class="card-body text-center">
                        <h5 class="card-title">CPU 사용률</h5>
                        <h2 class="text-primary"><%= cpuUsage %>%</h2>
                        <div class="progress">
                            <div class="progress-bar" style="width: <%= cpuUsage %>%"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card metric-card">
                    <div class="card-body text-center">
                        <h5 class="card-title">메모리 사용률</h5>
                        <h2 class="text-info"><%= memoryUsage %>%</h2>
                        <div class="progress">
                            <div class="progress-bar bg-info" style="width: <%= memoryUsage %>%"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card metric-card">
                    <div class="card-body text-center">
                        <h5 class="card-title">디스크 사용률</h5>
                        <h2 class="text-warning"><%= diskUsage %>%</h2>
                        <div class="progress">
                            <div class="progress-bar bg-warning" style="width: <%= diskUsage %>%"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card metric-card">
                    <div class="card-body text-center">
                        <h5 class="card-title">네트워크 I/O</h5>
                        <h2 class="text-success"><%= networkIO %> Mbps</h2>
                        <small class="text-muted">송수신 합계</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- 애플리케이션 상태 -->
        <div class="row mb-4">
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h6 class="mb-0">활성 사용자</h6>
                    </div>
                    <div class="card-body">
                        <h3><%= activeUsers %>명</h3>
                        <small class="text-muted">현재 접속 중</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h6 class="mb-0">활성 세션</h6>
                    </div>
                    <div class="card-body">
                        <h3><%= activeSessions %>개</h3>
                        <small class="text-muted">유효 세션 수</small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card">
                    <div class="card-header">
                        <h6 class="mb-0">DB 연결</h6>
                    </div>
                    <div class="card-body">
                        <h3><%= dbConnections %>개</h3>
                        <small class="text-muted">활성 커넥션</small>
                    </div>
                </div>
            </div>
        </div>

        <!-- 처리 시간 정보 -->
        <div class="alert alert-<%= statusClass.contains("normal") ? "success" : statusClass.contains("warning") ? "warning" : "danger" %>" role="alert">
            <h6 class="alert-heading">
                <i class="bi bi-clock"></i> 처리 시간 정보
            </h6>
            <p class="mb-1">
                <strong>데이터 수집 시간:</strong> <%= processingTime %>ms
                <% if (processingTime >
