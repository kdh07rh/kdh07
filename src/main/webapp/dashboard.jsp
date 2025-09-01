<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.servlet.UserDashboardServlet.*" %>
<%@ page import="com.example.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>내 대시보드 - Shopping Mall</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .dashboard-card {
            border-radius: 15px;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
            transition: transform 0.2s;
        }
        .dashboard-card:hover {
            transform: translateY(-2px);
        }
        .grade-badge {
            font-size: 1.1em;
            padding: 8px 16px;
        }
        .stats-icon {
            font-size: 2.5rem;
            opacity: 0.7;
        }
        .product-card {
            transition: transform 0.2s;
        }
        .product-card:hover {
            transform: scale(1.02);
        }
    </style>
</head>
<body>
    <div class="container">
        <%@ include file="header.jspf" %>
        
        <%
        User user = (User) request.getAttribute("user");
        DashboardData dashboardData = (DashboardData) request.getAttribute("dashboardData");
        %>
        
        <div class="row mb-4">
            <div class="col-md-12">
                <h1 class="display-5">안녕하세요, <%= user.getName() %>님! 👋</h1>
                <p class="lead text-muted">오늘도 즐거운 쇼핑 되세요</p>
            </div>
        </div>
        
        <!-- 회원 등급 및 포인트 -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card dashboard-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white;">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <h4>회원 등급: <span class="grade-badge badge bg-light text-dark"><%= dashboardData.memberGrade %></span></h4>
                                <% if (dashboardData.nextGradeRequiredAmount > 0) { %>
                                    <p class="mb-0">다음 등급까지 <strong>₩<%= String.format("%,d", dashboardData.nextGradeRequiredAmount) %></strong> 더 구매하세요!</p>
                                <% } else { %>
                                    <p class="mb-0">최고 등급을 달성하셨습니다! 🎉</p>
                                <% } %>
                            </div>
                            <div class="col-md-4 text-end">
                                <div class="row">
                                    <div class="col-6">
                                        <h3><%= String.format("%,d", dashboardData.availablePoints) %></h3>
                                        <small>보유 포인트</small>
                                    </div>
                                    <div class="col-6">
                                        <h3><%= dashboardData.availableCoupons %></h3>
                                        <small>사용가능 쿠폰</small>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 주요 통계 -->
        <div class="row mb-4">
            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card text-center">
                    <div class="card-body">
                        <div class="stats-icon text-primary">📦</div>
                        <h3><%= dashboardData.totalOrders %></h3>
                        <p class="text-muted mb-0">총 주문수</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card text-center">
                    <div class="card-body">
                        <div class="stats-icon text-success">💰</div>
                        <h3>₩<%= String.format("%,d", dashboardData.totalSpent) %></h3>
                        <p class="text-muted mb-0">총 구매금액</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card text-center">
                    <div class="card-body">
                        <div class="stats-icon text-warning">❤️</div>
                        <h3><%= dashboardData.wishlistCount %></h3>
                        <p class="text-muted mb-0">위시리스트</p>
                    </div>
                </div>
            </div>
            <div class="col-md-3 col-sm-6 mb-3">
                <div class="card dashboard-card text-center">
                    <div class="card-body">
                        <div class="stats-icon text-info">🚚</div>
                        <h3><%= dashboardData.pendingOrders %></h3>
                        <p class="text-muted mb-0">배송중 주문</p>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 빠른 메뉴 -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <h5 class="card-title">빠른 메뉴</h5>
                        <div class="row">
                            <div class="col-md-2 col-4 mb-3">
                                <a href="order-history" class="btn btn-outline-primary btn-lg w-100">
                                    <div>📦</div>
                                    <small>주문내역</small>
                                </a>
                            </div>
                            <div class="col-md-2 col-4 mb-3">
                                <a href="wishlist" class="btn btn-outline-danger btn-lg w-100">
                                    <div>❤️</div>
                                    <small>위시리스트</small>
                                </a>
                            </div>
                            <div class="col-md-2 col-4 mb-3">
                                <a href="reviews" class="btn btn-outline-warning btn-lg w-100">
                                    <div>⭐</div>
                                    <small>리뷰관리</small>
                                </a>
                            </div>
                            <div class="col-md-2 col-4 mb-3">
                                <a href="profile" class="btn btn-outline-info btn-lg w-100">
                                    <div>👤</div>
                                    <small>내정보</small>
                                </a>
                            </div>
                            <div class="col-md-2 col-4 mb-3">
                                <a href="coupons" class="btn btn-outline-success btn-lg w-100">
                                    <div>🎫</div>
                                    <small>쿠폰함</small>
                                </a>
                            </div>
                            <div class="col-md-2 col-4 mb-3">
                                <a href="points" class="btn btn-outline-secondary btn-lg w-100">
                                    <div>💎</div>
                                    <small>포인트</small>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 추천 상품 -->
        <div class="row mb-4">
            <div class="col-md-12">
                <h4>🎯 <%= user.getName() %>님을 위한 추천상품</h4>
                <div class="row">
                    <%
                    List<Product> recommendedProducts = (List<Product>) request.getAttribute("recommendedProducts");
                    if (recommendedProducts != null) {
                        for (Product product : recommendedProducts) {
                    %>
                        <div class="col-md-3 col-sm-6 mb-3">
                            <div class="card product-card">
                                <div class="card-body">
                                    <h6 class="card-title"><%= product.getName() %></h6>
                                    <p class="card-text text-primary">₩<%= String.format("%,d", product.getPrice()) %></p>
                                    <a href="products?id=<%= product.getId() %>" class="btn btn-sm btn-primary">상세보기</a>
                                </div>
                            </div>
                        </div>
                    <%
                        }
                    }
                    %>
                </div>
            </div>
        </div>
        
        <!-- 위시리스트 -->
        <div class="row mb-4">
            <div class="col-md-6">
                <h5>❤️ 내 위시리스트</h5>
                <%
                List<Product> wishlistProducts = (List<Product>) request.getAttribute("wishlistProducts");
                if (wishlistProducts != null && !wishlistProducts.isEmpty()) {
                    for (Product product : wishlistProducts.subList(0, Math.min(3, wishlistProducts.size()))) {
                %>
                    <div class="d-flex align-items-center mb-2 p-2 border rounded">
                        <div class="flex-grow-1">
                            <strong><%= product.getName() %></strong><br>
                            <span class="text-primary">₩<%= String.format("%,d", product.getPrice()) %></span>
                        </div>
                        <div>
                            <a href="products?id=<%= product.getId() %>" class="btn btn-sm btn-outline-primary">보기</a>
                        </div>
                    </div>
                <%
                    }
                %>
                    <div class="text-center">
                        <a href="wishlist" class="btn btn-link">전체 위시리스트 보기 →</a>
                    </div>
                <%
                } else {
                %>
                    <p class="text-muted">위시리스트가 비어있습니다.</p>
                <%
                }
                %>
            </div>
            
            <!-- 최근 본 상품 -->
            <div class="col-md-6">
                <h5>👀 최근 본 상품</h5>
                <%
                List<Product> recentlyViewed = (List<Product>) request.getAttribute("recentlyViewedProducts");
                if (recentlyViewed != null && !recentlyViewed.isEmpty()) {
                    for (Product product : recentlyViewed.subList(0, Math.min(3, recentlyViewed.size()))) {
                %>
                    <div class="d-flex align-items-center mb-2 p-2 border rounded">
                        <div class="flex-grow-1">
                            <strong><%= product.getName() %></strong><br>
                            <span class="text-primary">₩<%= String.format("%,d", product.getPrice()) %></span>
                        </div>
                        <div>
                            <a href="products?id=<%= product.getId() %>" class="btn btn-sm btn-outline-primary">다시보기</a>
                        </div>
                    </div>
                <%
                    }
                %>
                <%
                } else {
                %>
                    <p class="text-muted">최근 본 상품이 없습니다.</p>
                <%
                }
                %>
            </div>
        </div>
        
        <!-- 활동 통계 -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card dashboard-card">
                    <div class="card-body">
                        <h5 class="card-title">📊 이번 달 활동</h5>
                        <div class="row">
                            <div class="col-md-4 text-center">
                                <h4><%= dashboardData.monthlyVisits %></h4>
                                <small class="text-muted">방문 횟수</small>
                            </div>
                            <div class="col-md-4 text-center">
                                <h4><%= dashboardData.recentLoginDays %></h4>
                                <small class="text-muted">일 전 마지막 로그인</small>
                            </div>
                            <div class="col-md-4 text-center">
                                <h4><%= dashboardData.wishlistCount %></h4>
                                <small class="text-muted">찜한 상품</small>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <%@ include file="footer.jspf" %>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>