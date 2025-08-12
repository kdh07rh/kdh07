<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Disk I/O Load Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1 class="my-4">Disk I/O Load Simulation</h1>
        <div class="alert alert-info" role="alert">
            <b>How to Monitor:</b> This action writes a large number of logs to a file synchronously, causing high Disk I/O. In your APM, observe the transaction for <code>/disk-io-test</code>. You should see a long response time, and the thread profile should indicate significant time spent on file I/O operations.
        </div>
        <div class="card">
            <div class="card-body">
                <form action="disk-io-test" method="post">
                    <div class="mb-3">
                        <label for="logCount" class="form-label">Number of Log Messages to Generate:</label>
                        <input type="number" class="form-control" id="logCount" name="logCount" value="10000">
                    </div>
                    <button type="submit" class="btn btn-primary">Generate Logs</button>
                </form>
                <c:if test="${not empty message}">
                    <div class="alert alert-success mt-3" role="alert">
                        ${message}
                    </div>
                </c:if>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-secondary mt-3">Back to Home</a>
    </div>
</body>
</html>
