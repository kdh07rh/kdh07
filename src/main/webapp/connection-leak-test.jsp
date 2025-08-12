<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Connection Leak Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1 class="my-4">Connection Leak Simulation</h1>
        <div class="alert alert-danger" role="alert">
            <b>Warning:</b> Clicking the button below will acquire a database connection and intentionally not close it. Repeated clicks will exhaust the connection pool and cause the application to fail.
        </div>
        <div class="card">
            <div class="card-body">
                <p>Current number of leaked connections: <strong>${leakSize}</strong></p>
                <form action="connection-leak-test" method="get" class="d-inline">
                    <input type="hidden" name="action" value="leak">
                    <button type="submit" class="btn btn-danger">Leak a Connection</button>
                </form>
                <form action="connection-leak-test" method="get" class="d-inline">
                    <input type="hidden" name="action" value="reset">
                    <button type="submit" class="btn btn-warning">Reset Connections</button>
                </form>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Back to Home</a>
    </div>
</body>
</html>
