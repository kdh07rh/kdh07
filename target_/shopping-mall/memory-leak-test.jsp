<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Memory Leak Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1 class="my-4">Memory Leak Simulation</h1>
        <div class="alert alert-danger" role="alert">
            <b>Warning:</b> Clicking the button below will add 10MB of data to a static list that is never cleared. Repeated clicks will likely cause an <code>OutOfMemoryError</code> and crash the server.
        </div>
        <div class="card">
            <div class="card-body">
                <p>Current leaked memory size: <strong>${leakSize} MB</strong></p>
                <form action="memory-leak-test" method="get">
                    <input type="hidden" name="action" value="leak">
                    <button type="submit" class="btn btn-danger">Add 10MB to Leak</button>
                </form>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Back to Home</a>
    </div>
</body>
</html>
