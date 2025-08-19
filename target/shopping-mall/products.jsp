<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="header.jspf" %>

<div class="row">
    <div class="col-lg-12">
        <h2 class="my-4">Our Products</h2>

        <!-- Search Form -->
        <div class="row mb-4">
            <div class="col-md-6">
                <form action="products" method="get" class="form-inline">
                    <div class="input-group">
                        <input type="text" name="search" class="form-control" placeholder="Search for products..." value="<c:out value='${searchTerm}'/>">
                        <div class="input-group-append">
                            <button type="submit" class="btn btn-primary">Search</button>
                        </div>
                    </div>
                </form>
            </div>
        </div>

        <table class="table table-hover">
            <thead class="table-dark">
                <tr>
                    <th>Name</th>
                    <th>Price</th>
                    <th class="text-center">Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="product" items="${productList}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/products?id=${product.id}"><c:out value="${product.name}" /></a></td>
                        <td><fmt:formatNumber value="${product.price}" type="currency" currencySymbol="₩" /></td>
                        <td class="text-center">
                            <c:if test="${sessionScope.user != null}">
                                <form action="cart" method="post" style="display:inline;">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <button type="submit" class="btn btn-primary btn-sm">Add to Cart</button>
                                </form>
                            </c:if>
                            <c:if test="${sessionScope.user == null}">
                                <a href="login.jsp" class="btn btn-secondary btn-sm">Login to Buy</a>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<div class="row my-5">
    <div class="col-lg-12">
        <h3 class="my-4">Test Scenarios</h3>
        <div class="list-group">
            <a href="${pageContext.request.contextPath}/slow-query" class="list-group-item list-group-item-action"><b>Slow Query Test</b> &rarr; DB 쿼리를 30초간 지연시켜 스레드 블록 상황을 재현합니다.</a>
            <a href="${pageContext.request.contextPath}/external-api-test" class="list-group-item list-group-item-action"><b>External API Test</b> &rarr; 외부 API 호출을 15초간 지연시켜 동기 블로킹 상황을 재현합니다.</a>
            <a href="${pageContext.request.contextPath}/memory-leak-test" class="list-group-item list-group-item-action list-group-item-danger"><b>Memory Leak Test</b> &rarr; 버튼 클릭 시마다 10MB의 메모리를 누수시켜 OutOfMemoryError를 유발합니다.</a>
            <a href="${pageContext.request.contextPath}/lock-contention-test.jsp" class="list-group-item list-group-item-action"><b>Lock Contention Test</b> &rarr; 여러 스레드가 동시에 락을 획득하려는 경합 상황을 재현합니다.</a>
            <a href="${pageContext.request.contextPath}/connection-leak-test" class="list-group-item list-group-item-action list-group-item-danger"><b>Connection Leak Test</b> &rarr; DB 커넥션을 반납하지 않아 커넥션 풀 고갈 상황을 재현합니다.</a>
            <a href="${pageContext.request.contextPath}/disk-io-test" class="list-group-item list-group-item-action"><b>Disk I/O Load Test</b> &rarr; 과도한 동기 로깅으로 디스크 병목 상황을 재현합니다.</a>
            <a href="${pageContext.request.contextPath}/infinite-loop-test.jsp" class="list-group-item list-group-item-action list-group-item-danger"><b>Infinite Loop Test</b> &rarr; 무한 루프를 실행하여 CPU 사용량을 100%로 만듭니다. (서버 재시작 필요)</a>
        </div>
    </div>
</div>

<%@ include file="footer.jspf" %>