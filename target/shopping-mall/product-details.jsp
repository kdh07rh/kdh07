<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="header.jspf" %>

<div class="container mt-5">
    <div class="row">
        <div class="col-md-6">
            <!-- Assuming a placeholder image. You can later add a dynamic image URL from the product model. -->
            <img src="https://via.placeholder.com/500" class="img-fluid" alt="Product Image">
        </div>
        <div class="col-md-6">
            <h2><c:out value="${product.name}" /></h2>
            <p class="lead"><c:out value="${product.description}" /></p>
            <hr>
            <h4>Price: <fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₩" /></h4>
            <hr>
            <c:if test="${sessionScope.user != null}">
                <form action="cart" method="post" class="form-inline">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="productId" value="${product.id}">
                    <div class="form-group">
                        <label for="quantity" class="mr-2">Quantity:</label>
                        <input type="number" name="quantity" id="quantity" class="form-control mr-2" value="1" min="1" style="width: 80px;">
                    </div>
                    <button type="submit" class="btn btn-primary">Add to Cart</button>
                </form>
            </c:if>
            <c:if test="${sessionScope.user == null}">
                <a href="login.jsp" class="btn btn-secondary">Login to Buy</a>
            </c:if>
            <br>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-secondary mt-3">Back to Products</a>
        </div>
    </div>
</div>

<%@ include file="footer.jspf" %>
