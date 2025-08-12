<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Lock Contention Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1 class="my-4">Lock Contention Simulation</h1>
        <div class="alert alert-info" role="alert">
            <b>How to test:</b> Open this page in two or more browser tabs and click the "Start Test" button in each tab at the same time. You will see that each request has to wait for the previous one to release the lock.
        </div>
        <div class="card">
            <div class="card-body">
                <a href="lock-contention-test" class="btn btn-warning">Start Test</a>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Back to Home</a>
    </div>
</body>
</html>
