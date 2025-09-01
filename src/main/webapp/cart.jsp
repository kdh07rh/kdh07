<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="header.jspf" %>

<h2 class="my-4">Shopping Cart</h2>

<c:if test="${empty cartItems}">
    <div class="alert alert-info" role="alert">
        Your cart is empty. <a href="${pageContext.request.contextPath}/products" class="alert-link">Continue shopping</a>.
    </div>
</c:if>

<c:if test="${not empty cartItems}">
    <table class="table table-hover">
        <thead class="table-dark">
            <tr>
                <th>Product Name</th>
                <th class="text-center">Quantity</th>
                <th class="text-end">Price</th>
                <th class="text-center">Action</th>
            </tr>
        </thead>
        <tbody>
            <c:set var="total" value="${0}" />
            <c:forEach var="item" items="${cartItems}">
                <tr>
                    <td><c:out value="${item.productName}" /></td>
                    <td class="text-center"><c:out value="${item.quantity}" /></td>
                    <td class="text-end"><fmt:formatNumber value="${item.productPrice}" type="currency" currencySymbol="₩" /></td>
                    <td class="text-center">
                        <form action="cart" method="post" style="display:inline;">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="cartItemId" value="${item.id}">
                            <button type="submit" class="btn btn-danger btn-sm">Remove</button>
                        </form>
                    </td>
                </tr>
                <c:set var="total" value="${total + item.productPrice * item.quantity}" />
            </c:forEach>
        </tbody>
        <tfoot>
            <tr>
                <td colspan="2" class="text-end"><strong>Total:</strong></td>
                <td class="text-end"><strong><fmt:formatNumber value="${total}" type="currency" currencySymbol="₩" /></strong></td>
                <td></td>
            </tr>
        </tfoot>
    </table>
    <div class="d-flex justify-content-between mt-4">
        <a href="products" class="btn btn-secondary">Continue Shopping</a>
        <a href="#" class="btn btn-success">Proceed to Checkout</a>
    </div>
</c:if>

<%@ include file="footer.jspf" %>