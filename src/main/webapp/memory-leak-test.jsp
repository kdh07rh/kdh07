<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Controlled Memory Leak Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .memory-status {
            font-family: 'Courier New', monospace;
        }
        .progress-high { background-color: #dc3545 !important; }
        .progress-medium { background-color: #ffc107 !important; }
        .progress-low { background-color: #28a745 !important; }
    </style>
</head>
<body>
    <div class="container">
        <h1 class="my-4">🧠 Controlled Memory Leak Test</h1>
        
        <div class="alert alert-info" role="alert">
            <h5>🎯 Sustainable Memory Pressure</h5>
            <p class="mb-0">This test creates <strong>controlled memory pressure</strong> with built-in safety mechanisms to prevent WAS crashes while maintaining dramatic performance degradation.</p>
        </div>

        <div class="row">
            <!-- Memory Status Card -->
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5>💾 Memory Status</h5>
                    </div>
                    <div class="card-body memory-status">
                        <div class="row mb-3">
                            <div class="col-6"><strong>Leaked Memory:</strong></div>
                            <div class="col-6"><span class="badge bg-danger fs-6">${leakSize} MB</span></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-6"><strong>Current Usage:</strong></div>
                            <div class="col-6"><span class="badge bg-warning fs-6">${currentMemoryUsage} MB</span></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-6"><strong>Usage Percent:</strong></div>
                            <div class="col-6">
                                <% 
                                double usagePercent = Double.parseDouble((String)request.getAttribute("memoryUsagePercent"));
                                String badgeClass = usagePercent > 80 ? "bg-danger" : usagePercent > 60 ? "bg-warning" : "bg-success";
                                %>
                                <span class="badge <%= badgeClass %> fs-6">${memoryUsagePercent}%</span>
                            </div>
                        </div>
                        
                        <!-- Memory Usage Progress Bar -->
                        <div class="mb-3">
                            <label class="form-label"><strong>Memory Pressure Level:</strong></label>
                            <div class="progress" style="height: 25px;">
                                <%
                                String progressClass = usagePercent > 80 ? "progress-high" : usagePercent > 60 ? "progress-medium" : "progress-low";
                                %>
                                <div class="progress-bar <%= progressClass %>" role="progressbar" 
                                     style="width: ${memoryUsagePercent}%" 
                                     aria-valuenow="${memoryUsagePercent}" aria-valuemin="0" aria-valuemax="100">
                                    ${memoryUsagePercent}%
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Control Panel -->
        <div class="card mb-4">
            <div class="card-header bg-dark text-white">
                <h5>🎮 Control Panel</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-8">
                        <form action="memory-leak-test" method="get" class="d-inline me-2">
                            <input type="hidden" name="action" value="leak">
                            <button type="submit" class="btn btn-warning btn-lg">
                                🧠 Add Memory Pressure
                            </button>
                        </form>
                        
                        <a href="memory-leak-test" class="btn btn-info btn-lg me-2">
                            📊 Check Status
                        </a>
                        
                        <a href="memory-leak-test?action=reset" class="btn btn-secondary btn-lg">
                            🔄 Reset (if available)
                        </a>
                    </div>
                    <div class="col-md-4 text-end">
                        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
                            🏠 Back to Home
                        </a>
                    </div>
                </div>
            </div>
        </div>

        <!-- Safety Features Info -->
        <div class="alert alert-success" role="alert">
            <h5>🛡️ Built-in Safety Features</h5>
            <ul class="mb-0">
                <li><strong>Memory Limit:</strong> Maximum 120MB leak prevention</li>
                <li><strong>Dynamic Control:</strong> Automatic cleanup at 80% memory usage</li>
                <li><strong>Periodic Cleanup:</strong> Every 30 requests removes 40% old memory</li>
                <li><strong>Gradual Pressure:</strong> Controlled memory allocation based on current usage</li>
                <li><strong>WAS Protection:</strong> Prevents OutOfMemoryError crashes</li>
            </ul>
        </div>

        <!-- Monitoring Guide -->
        <div class="alert alert-info" role="alert">
            <h5>📈 JENNIFER APM Monitoring Points</h5>
            <div class="row">
                <div class="col-md-6">
                    <h6>Memory Metrics:</h6>
                    <ul class="small">
                        <li>Heap Memory Usage (should increase steadily)</li>
                        <li>GC Frequency (should increase over time)</li>
                        <li>GC Duration (longer Full GC cycles)</li>
                        <li>Memory Pool utilization</li>
                    </ul>
                </div>
                <div class="col-md-6">
                    <h6>Performance Metrics:</h6>
                    <ul class="small">
                        <li>Response Time (gradual increase)</li>
                        <li>Throughput (gradual decrease)</li>
                        <li>Thread Pool utilization</li>
                        <li>CPU usage during GC</li>
                    </ul>
                </div>
            </div>
        </div>

        <!-- Real-time Status -->
        <div class="card">
            <div class="card-header bg-secondary text-white">
                <h5>🔍 Technical Details</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6>Current Memory Strategy:</h6>
                        <%
                        if (usagePercent > 85) {
                        %>
                            <div class="alert alert-danger p-2">
                                <strong>🚨 Cleanup Mode:</strong> High memory usage detected. 
                                System is performing automatic cleanup and applying light memory work only.
                            </div>
                        <%
                        } else if (usagePercent > 70) {
                        %>
                            <div class="alert alert-warning p-2">
                                <strong>⚠️ Moderate Pressure:</strong> Applying 5-15MB memory leaks 
                                with temporary memory pressure work.
                            </div>
                        <%
                        } else {
                        %>
                            <div class="alert alert-info p-2">
                                <strong>🚀 Building Pressure:</strong> Applying aggressive 15-30MB 
                                memory leaks with full memory pressure work.
                            </div>
                        <%
                        }
                        %>
                    </div>
                    <div class="col-md-6">
                        <h6>Next Cleanup:</h6>
                        <%
                        long reqCount = (Long)request.getAttribute("requestCount");
                        long nextCleanup = 30 - (reqCount % 30); // 50 → 30으로 변경
                        %>
                        <div class="alert alert-info p-2">
                            <strong>🔄 Automatic cleanup in:</strong> <%= nextCleanup %> requests<br>
                            <small>Periodic cleanup removes 40% of leaked memory every 30 requests</small>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Auto-refresh script -->
        <script>
            // Auto-refresh every 10 seconds when actively testing
            if (window.location.search.includes('action=leak')) {
                setTimeout(function() {
                    window.location.href = 'memory-leak-test';
                }, 10000);
            }
        </script>
    </div>
</body>
</html>>
            </div>

            <!-- Performance Status Card -->
            <div class="col-md-6">
                <div class="card mb-4">
                    <div class="card-header bg-success text-white">
                        <h5>⚡ Performance Status</h5>
                    </div>
                    <div class="card-body memory-status">
                        <div class="row mb-3">
                            <div class="col-6"><strong>Processing Time:</strong></div>
                            <div class="col-6">
                                <%
                                long procTime = (Long)request.getAttribute("processingTime");
                                String timeClass = procTime > 2000 ? "bg-danger" : procTime > 1000 ? "bg-warning" : "bg-success";
                                %>
                                <span class="badge <%= timeClass %> fs-6">${processingTime} ms</span>
                            </div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-6"><strong>Request Count:</strong></div>
                            <div class="col-6"><span class="badge bg-info fs-6">#${requestCount}</span></div>
                        </div>
                        <div class="row mb-3">
                            <div class="col-6"><strong>System Status:</strong></div>
                            <div class="col-6">
                                <%
                                if (usagePercent > 85) {
                                %>
                                    <span class="badge bg-danger fs-6">🚨 High Pressure</span>
                                <%
                                } else if (usagePercent > 70) {
                                %>
                                    <span class="badge bg-warning fs-6">⚠️ Moderate Pressure</span>
                                <%
                                } else {
                                %>
                                    <span class="badge bg-success fs-6">✅ Building Pressure</span>
                                <%
                                }
                                %>
                            </div>
                        </div>
                        
                        <!-- Performance Impact Indicator -->
                        <div class="mb-3">
                            <label class="form-label"><strong>Performance Impact:</strong></label>
                            <div class="progress" style="height: 25px;">
                                <%
                                double impactPercent = Math.min(100, (procTime / 50.0)); // 5초 = 100%
                                String impactClass = impactPercent > 60 ? "bg-danger" : impactPercent > 30 ? "bg-warning" : "bg-success";
                                %>
                                <div class="progress-bar <%= impactClass %>" role="progressbar" 
                                     style="width: <%= impactPercent %>%" 
                                     aria-valuenow="<%= impactPercent %>" aria-valuemin="0" aria-valuemax="100">
                                    <%= String.format("%.0f", impactPercent) %>%
                                </div>
                            </div>
                        </div>
                    </div>
		</div>
