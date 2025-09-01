<%@ page import="java.io.*" %>
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<html>
<head>
    <title>Load Test Results</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
    <div class="container mt-5">
        <h1>Load Test Results</h1>
        <pre><%
            try {
                ProcessBuilder pb = new ProcessBuilder("k6", "run", "/WAS/tomcat/test_shop/test.js");
                pb.redirectErrorStream(true);
                Process process = pb.start();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }

                process.waitFor();
                reader.close();
            } catch (Exception e) {
                e.printStackTrace(new java.io.PrintWriter(out));
            }
        %></pre>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">Back to Home</a>
    </div>
</body>
</html>
