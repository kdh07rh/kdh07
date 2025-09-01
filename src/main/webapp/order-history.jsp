<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.servlet.OrderHistoryServlet.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>주문 내역 - Shopping Mall</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .order-card { 
            border-left: 4px solid #007bff; 
            margin-bottom: 20px;
        }
        .status-badge {
            font-size: 0.9em;
        }
        .stats-card {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
    </style>
</head>
<body>
    <div class="container">
        <%@ include file="header.jspf" %>
        
        <h1 class="my-4">📦 주문 내역</h1>
        
        <!-- 주문 통계 -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card stats-card">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-3 text-center">
                                <h4>${orderStats.totalOrders}</h4>
                                <small>총 주문수</small>
                            </div>
                            <div class="col-md-3 text-center">
                                <h4>₩${String.format("%,d", orderStats.totalSpent)}</h4>
                                <small>총 구매금액</small>
                            </div>
                            <div class="col-md-3 text-center">
                                <h4>₩${String.format("%,d", orderStats.averageOrderAmount)}</h4>
                                <small>평균 주문금액</small>
                            </div>
                            <div class="col-md-3 text-center">
                                <h4>${orderStats.recentOrdersThisMonth}</h4>
                                <small>이번 달 주문</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 주문 내역 리스트 -->
        <div class="row">
            <div class="col-md-12">
                <div class="d-flex justify-content-between align-items-center mb-3">
                    <h4>전체 주문 (${totalOrders}개)</h4>
                    <div>
                        <span class="text-muted">페이지 ${currentPage} / ${totalPages}</span>
                    </div>
                </div>
                
                <%
                List<OrderInfo> orderHistory = (List<OrderInfo>) request.getAttribute("orderHistory");
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                
                if (orderHistory != null && !orderHistory.isEmpty()) {
                    for (OrderInfo order : orderHistory) {
                %>
                    <div class="card order-card">
                        <div class="card-body">
                            <div class="row">
                                <div class="col-md-8">
                                    <div class="d-flex justify-content-between align-items-start mb-2">
                                        <div>
                                            <h6 class="card-title mb-1">주문번호: <%= order.orderId %></h6>
                                            <small class="text-muted">주문일시: <%= sdf.format(order.orderDate) %></small>
                                        </div>
                                        <span class="badge status-badge <%= 
                                            "배송완료".equals(order.status) ? "bg-success" :
                                            "배송중".equals(order.status) ? "bg-primary" :
                                            "취소".equals(order.status) ? "bg-danger" : "bg-warning" 
                                        %>"><%= order.status %></span>
                                    </div>
                                    
                                    <!-- 주문 상품 목록 -->
                                    <div class="order-items">
                                        <% for (OrderItem item : order.items) { %>
                                            <div class="d-flex justify-content-between py-1">
                                                <span><%= item.productName %> × <%= item.quantity %></span>
                                                <span class="text-muted">₩<%= String.format("%,d", item.amount) %></span>
                                            </div>
                                        <% } %>
                                    </div>
                                </div>
                                
                                <div class="col-md-4 text-end">
                                    <div class="mb-2">
                                        <strong>총 금액</strong><br>
                                        <h5 class="text-primary mb-0">₩<%= String.format("%,d", order.totalAmount) %></h5>
                                    </div>
                                    <div class="btn-group" role="group">
                                        <button type="button" class="btn btn-outline-primary btn-sm">상세보기</button>
                                        <% if (!"취소".equals(order.status) && !"배송완료".equals(order.status)) { %>
                                            <button type="button" class="btn btn-outline-secondary btn-sm">주문취소</button>
                                        <% } %>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                <%
                    }
                } else {
                %>
                    <div class="text-center py-5">
                        <h5 class="text-muted">주문 내역이 없습니다.</h5>
                        <a href="products" class="btn btn-primary mt-3">쇼핑하러 가기</a>
                    </div>
                <%
                }
                %>
            </div>
        </div>
        
        <!-- 페이지네이션 -->
        <% if (totalPages > 1) { %>
            <nav aria-label="주문 내역 페이지네이션" class="mt-4">
                <ul class="pagination justify-content-center">
                    <% if (currentPage > 1) { %>
                        <li class="page-item">
                            <a class="page-link" href="order-history?page=<%= currentPage - 1 %>">이전</a>
                        </li>
                    <% } %>
                    
                    <% 
                    int startPage = Math.max(1, currentPage - 2);
                    int endPage = Math.min(totalPages, currentPage + 2);
                    
                    for (int i = startPage; i <= endPage; i++) { 
                    %>
                        <li class="page-item <%= (i == currentPage) ? "active" : "" %>">
                            <a class="page-link" href="order-history?page=<%= i %>"><%= i %></a>
                        </li>
                    <% } %>
                    
                    <% if (currentPage < totalPages) { %>
                        <li class="page-item">
                            <a class="page-link" href="order-history?page=<%= currentPage + 1 %>">다음</a>
                        </li>
                    <% } %>
                </ul>
            </nav>
        <% } %>
        
        <%@ include file="footer.jspf" %>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>