<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.servlet.ProductCategoriesServlet.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>상품 카테고리 - Shopping Mall</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .category-card {
            transition: all 0.3s ease;
            border-radius: 15px;
            border: none;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .category-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 25px rgba(0,0,0,0.15);
        }
        .category-icon {
            font-size: 3rem;
            margin-bottom: 1rem;
        }
        .category-stats {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 10px;
            color: white;
            padding: 1rem;
            margin-bottom: 2rem;
        }
        .hero-section {
            background: linear-gradient(135deg, #74b9ff 0%, #0984e3 100%);
            color: white;
            padding: 4rem 0 2rem 0;
            margin-bottom: 3rem;
            border-radius: 20px;
        }
    </style>
</head>
<body>
    <div class="container">
        <%@ include file="header.jspf" %>
        
        <!-- 히어로 섹션 -->
        <div class="hero-section text-center">
            <h1 class="display-4 mb-3">🛍️ 상품 카테고리</h1>
            <p class="lead">원하는 카테고리를 선택하여 다양한 상품들을 둘러보세요</p>
        </div>
        
        <!-- 카테고리 통계 -->
        <div class="category-stats text-center">
            <div class="row">
                <div class="col-md-4">
                    <h3>
                        <%
                        List<CategoryInfo> categories = (List<CategoryInfo>) request.getAttribute("categories");
                        int totalProducts = categories.stream().mapToInt(c -> c.productCount).sum();
                        %>
                        <%= totalProducts %>
                    </h3>
                    <small>전체 상품 수</small>
                </div>
                <div class="col-md-4">
                    <h3><%= categories.size() %></h3>
                    <small>카테고리 수</small>
                </div>
                <div class="col-md-4">
                    <h3>
                        <%
                        if (!categories.isEmpty()) {
                            double avgPrice = categories.stream().mapToInt(c -> c.averagePrice).average().orElse(0);
                        %>
                            ₩<%= String.format("%,d", (int)avgPrice) %>
                        <% } else { %>
                            ₩0
                        <% } %>
                    </h3>
                    <small>평균 상품 가격</small>
                </div>
            </div>
        </div>
        
        <!-- 카테고리 그리드 -->
        <div class="row">
            <%
            if (categories != null && !categories.isEmpty()) {
                for (CategoryInfo category : categories) {
            %>
                <div class="col-lg-4 col-md-6 mb-4">
                    <div class="card category-card h-100">
                        <div class="card-body text-center">
                            <div class="category-icon">
                                <%= category.icon %>
                            </div>
                            <h5 class="card-title"><%= category.name %></h5>
                            <p class="card-text text-muted"><%= category.description %></p>
                            
                            <!-- 카테고리 정보 -->
                            <div class="row text-center mb-3">
                                <div class="col-6">
                                    <strong class="text-primary"><%= category.productCount %></strong><br>
                                    <small class="text-muted">상품 수</small>
                                </div>
                                <div class="col-6">
                                    <strong class="text-success">₩<%= String.format("%,d", category.averagePrice) %></strong><br>
                                    <small class="text-muted">평균 가격</small>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <small class="text-muted">가격대: <%= category.priceRange %></small>
                            </div>
                            
                            <a href="categories?category=<%= java.net.URLEncoder.encode(category.name, "UTF-8") %>" 
                               class="btn btn-primary btn-lg w-100">
                                상품 보기 →
                            </a>
                        </div>
                    </div>
                </div>
            <%
                }
            } else {
            %>
                <div class="col-12 text-center py-5">
                    <h4 class="text-muted">등록된 카테고리가 없습니다.</h4>
                    <a href="products" class="btn btn-primary mt-3">전체 상품 보기</a>
                </div>
            <%
            }
            %>
        </div>
        
        <!-- 추가 정보 섹션 -->
        <div class="row mt-5">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">🎯 카테고리별 특징</h5>
                        <div class="row">
                            <div class="col-md-6">
                                <h6 class="text-primary">인기 카테고리</h6>
                                <ul class="list-unstyled">
                                    <%
                                    if (categories.size() > 0) {
                                        for (int i = 0; i < Math.min(3, categories.size()); i++) {
                                            CategoryInfo topCategory = categories.get(i);
                                    %>
                                        <li class="mb-2">
                                            <%= topCategory.icon %> <strong><%= topCategory.name %></strong> 
                                            - <%= topCategory.productCount %>개 상품
                                        </li>
                                    <%
                                        }
                                    }
                                    %>
                                </ul>
                            </div>
                            <div class="col-md-6">
                                <h6 class="text-success">쇼핑 팁</h6>
                                <ul class="list-unstyled">
                                    <li class="mb-2">📱 모바일에서도 편리하게 쇼핑하세요</li>
                                    <li class="mb-2">⭐ 리뷰를 참고하여 현명한 선택을 하세요</li>
                                    <li class="mb-2">🔍 검색 필터를 활용해 원하는 상품을 찾으세요</li>
                                    <li class="mb-2">💰 가격 비교를 통해 최적의 가격을 찾으세요</li>
                                </ul>
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