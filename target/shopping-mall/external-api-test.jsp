<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>External API Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1 class="my-4">External API Test Results</h1>
        <div class="alert alert-warning" role="alert">
            This page simulates a blocking call to a slow external API with variable delay times (3-10 seconds).
        </div>
        
        <div class="card">
            <div class="card-header">
                <h5>Test Details</h5>
            </div>
            <div class="card-body">
                <div class="row">
                    <div class="col-md-6">
                        <h6>Timing Information:</h6>
                        <ul class="list-group list-group-flush">
                            <li class="list-group-item">
                                <strong>Planned Delay:</strong> 
                                <span class="badge bg-primary">${plannedDelay} seconds</span>
                            </li>
                            <li class="list-group-item">
                                <strong>Actual Duration:</strong> 
                                <span class="badge bg-success">${actualDuration} seconds</span>
                            </li>
                            <li class="list-group-item">
                                <strong>Status:</strong> 
                                <% if (request.getAttribute("errorMessage") != null) { %>
                                    <span class="badge bg-warning">Simulated (Network Issue)</span>
                                <% } else { %>
                                    <span class="badge bg-success">External API Success</span>
                                <% } %>
                            </li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h6>Performance Impact:</h6>
                        <div class="progress mb-2">
                            <div class="progress-bar bg-warning" role="progressbar" 
                                 style="width: ${(actualDuration * 10)}%" 
                                 aria-valuenow="${actualDuration}" aria-valuemin="0" aria-valuemax="10">
                                ${actualDuration}s / 10s max
                            </div>
                        </div>
                        <small class="text-muted">
                            This delay blocks a server thread for the entire duration.
                        </small>
                    </div>
                </div>
                
                <% if (request.getAttribute("errorMessage") != null) { %>
                    <div class="alert alert-info mt-3" role="alert">
                        <strong>Note:</strong> ${errorMessage}
                        <br><small>This fallback ensures consistent testing even when external services are unavailable.</small>
                    </div>
                <% } %>
                
                <hr>
                <h6>API Response:</h6>
                <div class="card bg-light">
                    <div class="card-body">
                        <pre><code>${response}</code></pre>
                    </div>
                </div>
            </div>
        </div>
        
        <div class="row mt-4">
            <div class="col-md-6">
                <a href="${pageContext.request.contextPath}/external-api-test" class="btn btn-primary">
                    🔄 Test Again (Random Delay)
                </a>
            </div>
            <div class="col-md-6 text-end">
                <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary">
                    🏠 Back to Home
                </a>
            </div>
        </div>
        
        <div class="alert alert-info mt-4" role="alert">
            <h6>💡 Monitoring Tips:</h6>
            <ul class="mb-0">
                <li><strong>Response Time:</strong> Look for variable spikes (3-10 seconds) in your APM</li>
                <li><strong>Thread Analysis:</strong> Check for blocked threads during API calls</li>
                <li><strong>Pattern Recognition:</strong> Notice how random delays affect overall system performance</li>
                <li><strong>Resource Usage:</strong> Monitor thread pool utilization during concurrent calls</li>
            </ul>
        </div>
    </div>
</body>
</html>
