<%@ page contentType="text/html; charset=UTF-8" language="java" %>

<%@ include file="header.jspf" %>

<div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
        <div class="card">
            <div class="card-header text-center">
                <h3>Register</h3>
            </div>
            <div class="card-body">
                <form action="register" method="post">
                    <div class="mb-3">
                        <label for="username" class="form-label">Username</label>
                        <input type="text" class="form-control" id="username" name="username" required>
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">Password</label>
                        <input type="password" class="form-control" id="password" name="password" required>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary">Register</button>
                    </div>
                </form>
                <div class="text-center mt-3">
                     <p>Already have an account? <a href="login.jsp">Login here</a></p>
                </div>
                 <% if ("true".equals(request.getParameter("error"))) { %>
                    <div class="alert alert-danger mt-3" role="alert">
                        Registration failed. Username might already exist.
                    </div>
                <% } %>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jspf" %>