<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.servlet.ProductReviewsServlet.*" %>
<%@ page import="com.example.model.*" %>
<%@ page import="java.util.*" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${pageTitle} - Shopping Mall</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .rating-stars {
            color: #ffc107;
        }
        .product-card {
            transition: transform 0.2s;
            border-radius: 10px;
        }
        .product-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .rating-bar {
            height: 8px;
            border-radius: 4px;
        }
    </style>
</head>
<body>
    <div class="container">
        <%@ include file="header.jspf" %>
        
        <div class="row mb-4">
            <div class="col-md-12">
                <h1>⭐ ${pageTitle}</h1>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="products">상품</a></li>
                        <li class="breadcrumb-item active">리뷰</li>
                    </ol>
                </nav>
            </div>
        </div>
        
        <!-- 필터 및 정렬 -->
        <div class="row mb-4">
            <div class="col-md-12">
                <div class="card">
                    <div class="card-body">
                        <div class="row align-items-center">
                            <div class="col-md-8">
                                <div class="btn-group" role="group">
                                    <a href="reviews" class="btn btn-primary">전체 리뷰</a>
                                    <a href="reviews?action=popular" class="btn btn-outline-primary">인기 리뷰</a>
                                </div>
                            </div>
                            <div class="col-md-4 text-end">
                                <select class="form-select" onchange="sortProducts(this.value)">
                                    <option value="rating">평점 높은 순</option>
                                    <option value="reviews">리뷰 많은 순</option>
                                    <option value="name">상품명 순</option>
                                </select>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- 상품 리뷰 목록 -->
        <div class="row" id="product-list">
            <%
            List<ProductReviewSummary> allReviews = (List<ProductReviewSummary>) request.getAttribute("allReviews");
            if (allReviews != null && !allReviews.isEmpty()) {
                for (ProductReviewSummary summary : allReviews) {
                    Product product = summary.product;
                    ReviewStats stats = summary.reviewStats;
            %>
                <div class="col-md-6 col-lg-4 mb-4 product-item" 
                     data-rating="<%= String.format("%.1f", stats.averageRating) %>"
                     data-reviews="<%= stats.totalReviews %>"
                     data-name="<%= product.getName() %>">
                    <div class="card product-card h-100">
                        <div class="card-body">
                            <h5 class="card-title"><%= product.getName() %></h5>
                            <p class="text-primary mb-2">₩<%= String.format("%,d", product.getPrice()) %></p>
                            
                            <!-- 평점 정보 -->
                            <div class="mb-3">
                                <div class="d-flex align-items-center mb-2">
                                    <div class="rating-stars me-2">
                                        <%
                                        double rating = stats.averageRating;
                                        for (int i = 1; i <= 5; i++) {
                                            if (i <= rating) {
                                        %>
                                                ⭐
                                        <%
                                            } else if (i - 0.5 <= rating) {
                                        %>
                                                ⭐
                                        <%
                                            } else {
                                        %>
                                                ☆
                                        <%
                                            }
                                        }
                                        %>
                                    </div>
                                    <strong><%= String.format("%.1f", stats.averageRating) %></strong>
                                    <span class="text-muted ms-2">(<%= stats.totalReviews %>개 리뷰)</span>
                                </div>
                                
                                <!-- 평점 분포 미니 차트 -->
                                <div class="small">
                                    <%
                                    for (int i = 4; i >= 0; i--) {
                                        int count = stats.ratingCounts[i];
                                        double percentage = stats.totalReviews > 0 ? (double) count / stats.totalReviews * 100 : 0;
                                    %>
                                        <div class="d-flex align-items-center mb-1">
                                            <span class="me-2"><%= (i + 1) %>점</span>
                                            <div class="progress flex-grow-1 me-2" style="height: 6px;">
                                                <div class="progress-bar bg-warning" style="width: <%= percentage %>%"></div>
                                            </div>
                                            <span class="text-muted" style="min-width: 30px;"><%= count %></span>
                                        </div>
                                    <%
                                    }
                                    %>
                                </div>
                            </div>
                            
                            <div class="d-grid gap-2">
                                <a href="reviews?productId=<%= product.getId() %>" class="btn btn-outline-primary">리뷰 보기</a>
                                <a href="products?id=<%= product.getId() %>" class="btn btn-primary">상품 보기</a>
                            </div>
                        </div>
                    </div>
                </div>
            <%
                }
            } else {
            %>
                <div class="col-md-12 text-center py-5">
                    <h4 class="text-muted">리뷰가 있는 상품이 없습니다.</h4>
                    <a href="products" class="btn btn-primary mt-3">상품 둘러보기</a>
                </div>
            <%
            }
            %>
        </div>
        
        <%@ include file="footer.jspf" %>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function sortProducts(criteria) {
            const productList = document.getElementById('product-list');
            const products = Array.from(productList.getElementsByClassName('product-item'));
            
            products.sort((a, b) => {
                if (criteria === 'rating') {
                    const ratingA = parseFloat(a.getAttribute('data-rating'));
                    const ratingB = parseFloat(b.getAttribute('data-rating'));
                    return ratingB - ratingA;
                } else if (criteria === 'reviews') {
                    const reviewsA = parseInt(a.getAttribute('data-reviews'));
                    const reviewsB = parseInt(b.getAttribute('data-reviews'));
                    return reviewsB - reviewsA;
                } else if (criteria === 'name') {
                    const nameA = a.getAttribute('data-name');
                    const nameB = b.getAttribute('data-name');
                    return nameA.localeCompare(nameB);
                }
                return 0;
            });
            
            // 정렬된 순서로 다시 추가
            products.forEach(product => {
                productList.appendChild(product);
            });
        }
    </script>
</body>
</html>