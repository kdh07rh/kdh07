<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Infinite Loop Test</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container">
        <h1 class="my-4">Infinite Loop Simulation</h1>
        <div class="alert alert-danger" role="alert">
            <b>Warning:</b> Clicking the button below will start a process with an infinite loop. This will cause one of your CPU cores to run at 100% and the request will never complete. You will likely need to restart the server to recover.
        </div>
        <div class="card">
            <div class="card-body">
                <p>Click the button to start the test. The page will hang.</p>
                <a href="infinite-loop-test" class="btn btn-danger" target="_blank">Start Infinite Loop Test</a>
            </div>
        </div>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary mt-3">Back to Home</a>
    </div>
</body>
</html>
