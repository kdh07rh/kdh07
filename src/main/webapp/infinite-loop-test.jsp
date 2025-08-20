<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Infinite Loop Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .warning-box {
            border: 3px solid #dc3545;
            background-color: #f8d7da;
            animation: pulse 2s infinite;
        }
        @keyframes pulse {
            0% { border-color: #dc3545; }
            50% { border-color: #ffc107; }
            100% { border-color: #dc3545; }
        }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="my-4">🔥 Infinite Loop Simulation</h1>
        
        <div class="alert alert-danger warning-box" role="alert">
            <h4>⚠️ 매우 위험한 테스트</h4>
            <ul class="mb-0">
                <li><strong>CPU 코어 1개가 100% 사용됩니다</strong></li>
                <li><strong>해당 스레드는 영구적으로 블로킹됩니다</strong></li>
                <li><strong>여러 번 실행하면 스레드 풀이 고갈될 수 있습니다</strong></li>
                <li><strong>서버 재시작이 필요할 수 있습니다</strong></li>
            </ul>
        </div>

        <div class="row">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-danger text-white">
                        <h5>🎯 테스트 시나리오</h5>
                    </div>
                    <div class="card-body">
                        <p><strong>기본 무한루프:</strong> 단순한 while(true) 루프</p>
                        <a href="infinite-loop-test" class="btn btn-danger w-100 mb-2" target="_blank">
                            🔥 기본 무한루프 시작
                        </a>
                        
                        <p class="mt-3"><strong>정규식 시뮬레이션:</strong> 백트래킹 패턴 시뮬레이션</p>
                        <a href="infinite-loop-test?type=regex" class="btn btn-warning w-100 mb-2" target="_blank">
                            📝 정규식 무한루프 시작
                        </a>
                        
                        <p class="mt-3"><strong>재귀 호출:</strong> 스택 오버플로우 유발</p>
                        <a href="infinite-loop-test?type=recursive" class="btn btn-dark w-100" target="_blank">
                            🔄 재귀 무한루프 시작
                        </a>
                    </div>
                </div>
            </div>
            
            <div class="col-md-6">
                <div class="card">
                    <div class="card-header bg-info text-white">
                        <h5>📊 JENNIFER APM 모니터링 포인트</h5>
                    </div>
                    <div class="card-body">
                        <h6>CPU 메트릭:</h6>
                        <ul class="small">
                            <li>특정 CPU 코어 100% 사용률</li>
                            <li>시스템 전체 CPU 부하 증가</li>
                            <li>User Time vs System Time 비율</li>
                        </ul>
                        
                        <h6>스레드 메트릭:</h6>
                        <ul class="small">
                            <li>응답하지 않는 스레드 식별</li>
                            <li>스레드 덤프에서 RUNNABLE 상태 확인</li>
                            <li>스레드 풀 사용률 증가</li>
                        </ul>
                        
                        <h6>성능 메트릭:</h6>
                        <ul class="small">
                            <li>해당 요청의 무한 응답시간</li>
                            <li>다른 요청들의 성능 저하</li>
                            <li>큐잉 지연 시간 증가</li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="alert alert-warning mt-4" role="alert">
            <h5>🚨 주의사항</h5>
            <div class="row">
                <div class="col-md-6">
                    <h6>테스트 전:</h6>
                    <ul class="small">
                        <li>JENNIFER APM 모니터링 준비</li>
                        <li>서버 리소스 상태 확인</li>
                        <li>다른 중요한 작업이 없는지 확인</li>
                    </ul>
                </div>
                <div class="col-md-6">
                    <h6>테스트 후:</h6>
                    <ul class="small">
                        <li>스레드 덤프 분석</li>
                        <li>CPU 사용률 패턴 분석</li>
                        <li>필요시 서버 재시작</li>
                    </ul>
                </div>
            </div>
        </div>

        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/products" class="btn btn-primary btn-lg">
                🏠 안전한 홈으로 돌아가기
            </a>
        </div>
        
        <div class="alert alert-secondary mt-4" role="alert">
            <small>
                <strong>WAS 엔지니어 팁:</strong> 이런 상황이 운영에서 발생하면 
                <code>jstack</code> 명령어나 JENNIFER의 스레드 덤프 기능을 사용하여 
                블로킹된 스레드를 식별하고, 해당 코드를 수정해야 합니다.
            </small>
        </div>
    </div>
</body>
</html>
