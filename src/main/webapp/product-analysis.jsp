<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<%@ include file="header.jspf" %>

<div class="container">
    <h2 class="my-4">📊 상품 분석 결과</h2>
    
    <div class="alert alert-info" role="alert">
        <h5>분석 정보</h5>
        <p><strong>분석 유형:</strong> ${analysisType}</p>
        <p><strong>처리 시간:</strong> ${processingTime} ms</p>
        <p><strong>사용 메모리:</strong> <fmt:formatNumber value="${memoryUsed}" pattern="#,##0.00"/> MB</p>
        <p><strong>분석 결과 수:</strong> ${analysisResults.size()}개</p>
    </div>

    <!-- 메모리 사용량 시각화 -->
    <div class="row mb-4">
        <div class="col-md-6">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <h5>💾 메모리 사용량</h5>
                </div>
                <div class="card-body">
                    <%
                    Runtime runtime = Runtime.getRuntime();
                    long totalMemory = runtime.totalMemory();
                    long freeMemory = runtime.freeMemory();
                    long usedMemory = totalMemory - freeMemory;
                    long maxMemory = runtime.maxMemory();
                    
                    double usedPercent = (double) usedMemory / maxMemory * 100;
                    %>
                    <p><strong>사용 메모리:</strong> <%= String.format("%.2f MB", usedMemory / 1024.0 / 1024.0) %></p>
                    <p><strong>전체 메모리:</strong> <%= String.format("%.2f MB", totalMemory / 1024.0 / 1024.0) %></p>
                    <p><strong>최대 메모리:</strong> <%= String.format("%.2f MB", maxMemory / 1024.0 / 1024.0) %></p>
                    
                    <div class="progress" style="height: 25px;">
                        <div class="progress-bar <%= usedPercent > 80 ? "bg-danger" : usedPercent > 60 ? "bg-warning" : "bg-success" %>" 
                             role="progressbar" style="width: <%= usedPercent %>%">
                            <%= String.format("%.1f%%", usedPercent) %>
                        </div>
                    </div>
                    
                    <% if (usedPercent > 80) { %>
                        <div class="alert alert-danger mt-2">
                            ⚠️ 메모리 사용률이 80%를 초과했습니다!
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
        
        <div class="col-md-6">
            <div class="card">
                <div class="card-header bg-success text-white">
                    <h5>🔄 다른 분석 실행</h5>
                </div>
                <div class="card-body">
                    <div class="btn-group-vertical w-100" role="group">
                        <a href="product-analysis?type=basic" class="btn btn-outline-primary">
                            기본 분석 (~5MB)
                        </a>
                        <a href="product-analysis?type=detailed" class="btn btn-outline-warning">
                            상세 분석 (~20MB)
                        </a>
                        <a href="product-analysis?type=statistical" class="btn btn-outline-info">
                            통계 분석 (~15MB)
                        </a>
                        <a href="product-analysis?type=comprehensive" class="btn btn-outline-danger">
                            종합 분석 (~50MB+)
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- 분석 결과 테이블 -->
    <div class="card">
        <div class="card-header">
            <h5>📈 분석 결과 (${analysisType})</h5>
        </div>
        <div class="card-body">
            <c:if test="${not empty analysisResults}">
                <table class="table table-striped">
                    <thead>
                        <tr>
                            <th>상품 ID</th>
                            <th>상품명</th>
                            <th>가격</th>
                            <th>분석 데이터 크기</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="result" items="${analysisResults}" varStatus="status">
                            <c:if test="${status.index < 10}"> <!-- 처음 10개만 표시 -->
                                <tr>
                                    <td>${result.productId}</td>
                                    <td>${result.productName}</td>
                                    <td><fmt:formatNumber value="${result.price}" type="currency" currencySymbol="₩"/></td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${analysisType == 'basic'}">
                                                <span class="badge bg-success">소량</span>
                                            </c:when>
                                            <c:when test="${analysisType == 'detailed'}">
                                                <span class="badge bg-warning">대량</span>
                                            </c:when>
                                            <c:when test="${analysisType == 'statistical'}">
                                                <span class="badge bg-info">중량</span>
                                            </c:when>
                                            <c:when test="${analysisType == 'comprehensive'}">
                                                <span class="badge bg-danger">초대량</span>
                                            </c:when>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:if>
                        </c:forEach>
                    </tbody>
                </table>
                
                <c:if test="${analysisResults.size() > 10}">
                    <p class="text-muted">... 외 ${analysisResults.size() - 10}개 결과 (메모리 절약을 위해 처음 10개만 표시)</p>
                </c:if>
            </c:if>
            
            <c:if test="${empty analysisResults}">
                <div class="alert alert-warning">
                    분석 결과가 없습니다.
                </div>
            </c:if>
        </div>
    </div>

    <div class="mt-4">
        <a href="${pageContext.request.contextPath}/products" class="btn btn-primary">
            🏠 메인으로 돌아가기
        </a>
        <button onclick="location.reload()" class="btn btn-secondary">
            🔄 페이지 새로고침
        </button>
    </div>
</div>

<%@ include file="footer.jspf" %>
