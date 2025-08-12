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
            This page simulates a blocking call to a slow external API.
        </div>
        <div class="card">
            <div class="card-header">
                Test Details
            </div>
            <div class="card-body">
                <p>The external API call took approximately <strong>${duration}</strong> seconds to respond.</p>
                <hr>
                <h5>API Response:</h5>
                <pre><code>${response}</code></pre>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Back to Home</a>
    </div>
</body>
</html>
