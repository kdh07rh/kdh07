<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>DB Connection Failure Test</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 800px; margin: 0 auto; }
        .test-button { 
            display: inline-block; 
            padding: 10px 20px; 
            margin: 10px; 
            background-color: #f44336; 
            color: white; 
            text-decoration: none; 
            border-radius: 5px; 
            border: none;
            cursor: pointer;
        }
        .test-button:hover { background-color: #da190b; }
        .description { 
            margin: 10px 0; 
            padding: 10px; 
            background-color: #f9f9f9; 
            border-left: 4px solid #ccc; 
        }
        .warning {
            background-color: #fff3cd;
            border-left: 4px solid #ffc107;
            padding: 10px;
            margin: 10px 0;
        }
        h1 { color: #333; }
        h2 { color: #666; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🚨 Database Connection Failure Test Suite</h1>
        
        <div class="warning">
            <strong>⚠️ Warning:</strong> These tests are designed to trigger database connection failures for testing purposes. 
            Use only in development/test environments!
        </div>
        
        <h2>Available DB Connection Failure Tests:</h2>
        
        <div class="description">
            <h3>1. Invalid Host Connection</h3>
            <p>Tests connection to a non-existent database server host.</p>
            <a href="db-connection-fail?type=invalid_host" class="test-button">Test Invalid Host</a>
        </div>
        
        <div class="description">
            <h3>2. Wrong Credentials</h3>
            <p>Tests connection with incorrect username/password.</p>
            <a href="db-connection-fail?type=wrong_credentials" class="test-button">Test Wrong Credentials</a>
        </div>
        
        <div class="description">
            <h3>3. Connection Pool Exhaustion</h3>
            <p>Attempts to create more connections than the pool allows.</p>
            <a href="db-connection-fail?type=connection_exhaustion" class="test-button">Test Connection Exhaustion</a>
        </div>
        
        <div class="description">
            <h3>4. Database Service Down</h3>
            <p>Tests connection to a port where no database service is running.</p>
            <a href="db-connection-fail?type=database_down" class="test-button">Test Database Down</a>
        </div>
        
        <div class="description">
            <h3>5. Network Timeout</h3>
            <p>Tests connection to an unreachable IP address with short timeout.</p>
            <a href="db-connection-fail?type=network_timeout" class="test-button">Test Network Timeout</a>
        </div>
        
        <div style="margin-top: 30px; padding: 20px; background-color: #e9ecef; border-radius: 5px;">
            <h3>📊 Expected Results:</h3>
            <ul>
                <li><strong>Connection timeout:</strong> java.net.ConnectException</li>
                <li><strong>Authentication failure:</strong> org.postgresql.util.PSQLException</li>
                <li><strong>Connection exhaustion:</strong> SQLException (connection pool exhausted)</li>
                <li><strong>Network timeout:</strong> java.net.SocketTimeoutException</li>
                <li><strong>Host not found:</strong> java.net.UnknownHostException</li>
            </ul>
        </div>
        
        <div style="margin-top: 20px;">
            <a href="index.jsp" style="color: #007bff;">← Back to Main Page</a>
        </div>
        
        <div style="margin-top: 30px; font-size: 12px; color: #666;">
            <p><strong>Current Database Status:</strong></p>
            <% 
                try {
                    java.sql.Connection testConn = null;
                    javax.naming.Context initContext = new javax.naming.InitialContext();
                    javax.naming.Context envContext = (javax.naming.Context) initContext.lookup("java:/comp/env");
                    javax.sql.DataSource dataSource = (javax.sql.DataSource) envContext.lookup("jdbc/shopping");
                    testConn = dataSource.getConnection();
                    if (testConn != null && !testConn.isClosed()) {
                        out.println("✅ PostgreSQL Connection: <span style='color: green;'>Active</span>");
                        testConn.close();
                    }
                } catch (Exception e) {
                    out.println("❌ PostgreSQL Connection: <span style='color: red;'>Failed (" + e.getMessage() + ")</span>");
                }
            %>
        </div>
    </div>
</body>
</html>