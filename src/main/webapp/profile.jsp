<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%@ include file="header.jspf" %>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-8 offset-md-2">
            <div class="card">
                <div class="card-header">
                    <h2>User Profile</h2>
                </div>
                <div class="card-body">
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label"><strong>Username:</strong></label>
                        <div class="col-sm-9">
                            <p class="form-control-plaintext"><c:out value="${userProfile.username}" /></p>
                        </div>
                    </div>
                    <div class="form-group row">
                        <label class="col-sm-3 col-form-label"><strong>User ID:</strong></label>
                        <div class="col-sm-9">
                            <p class="form-control-plaintext"><c:out value="${userProfile.id}" /></p>
                        </div>
                    </div>
                    <!-- Add other user details here as needed -->
                    <hr>
                    <a href="#" class="btn btn-primary">Edit Profile</a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="footer.jspf" %>
