package com.example.servlet;

import com.example.dao.ProductDAO;
import com.example.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet("/reviews")
public class ProductReviewsServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private Random random = new Random();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String productIdParam = request.getParameter("productId");
        String action = request.getParameter("action");
        
        try {
            if (productIdParam != null) {
                // 특정 상품의 리뷰 조회
                int productId = Integer.parseInt(productIdParam);
                Product product = productDAO.getProductById(productId);
                
                if (product != null) {
                    List<ProductReview> reviews = getProductReviews(productId);
                    ReviewStats stats = getReviewStats(productId);
                    
                    request.setAttribute("product", product);
                    request.setAttribute("reviews", reviews);
                    request.setAttribute("reviewStats", stats);
                    request.getRequestDispatcher("product-reviews.jsp").forward(request, response);
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Product not found");
                }
            } else if ("popular".equals(action)) {
                // 인기 리뷰 상품들
                List<ProductReviewSummary> popularReviews = getPopularReviewedProducts();
                request.setAttribute("popularReviews", popularReviews);
                request.setAttribute("pageTitle", "인기 리뷰 상품");
                request.getRequestDispatcher("reviews-popular.jsp").forward(request, response);
            } else {
                // 전체 리뷰 목록 (최신순)
                List<ProductReviewSummary> allReviews = getAllProductReviews();
                request.setAttribute("allReviews", allReviews);
                request.setAttribute("pageTitle", "전체 상품 리뷰");
                request.getRequestDispatcher("reviews-all.jsp").forward(request, response);
            }
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product ID");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error retrieving reviews", e);
        }
    }
    
    private List<ProductReview> getProductReviews(int productId) {
        List<ProductReview> reviews = new ArrayList<>();
        Random productRandom = new Random(productId * 1000);
        
        // 리뷰 개수 (5-20개)
        int reviewCount = 5 + productRandom.nextInt(16);
        
        String[] reviewers = {
            "구매고객A", "쇼핑매니아", "리뷰어123", "만족고객", "추천해요",
            "좋아요사용자", "실사용후기", "정직한리뷰", "추천드려요", "만족구매자",
            "검증된구매자", "실제사용자", "추천고객", "만족사용자", "좋은선택"
        };
        
        String[][] reviewTexts = {
            // 5점 리뷰
            {
                "정말 만족스러운 제품입니다! 품질도 좋고 배송도 빨라서 재구매 의사 있어요.",
                "기대했던 것보다 훨씬 좋네요. 주변에도 추천하고 있습니다.",
                "가격 대비 성능이 훌륭합니다. 다음에도 이 브랜드 제품 구매할 예정이에요.",
                "포장도 꼼꼼하고 제품 상태도 완벽했습니다. 강력 추천!",
                "오랫동안 사용해봤는데 내구성도 좋고 디자인도 마음에 들어요."
            },
            // 4점 리뷰  
            {
                "전반적으로 만족하지만 배송이 조금 늦었어요. 그래도 제품은 좋습니다.",
                "품질은 좋은데 가격이 조금 아쉽네요. 할인할 때 사시길 추천합니다.",
                "사용하기 편하고 기능도 좋아요. 색상 선택폭이 더 넓었으면 좋겠어요.",
                "기대했던 만큼 좋네요. 약간의 아쉬운 점은 있지만 전체적으로 만족해요.",
                "실용적이고 좋습니다. 다만 설명서가 조금 부족한 것 같아요."
            },
            // 3점 리뷰
            {
                "보통 수준이에요. 가격 생각하면 이 정도면 괜찮은 것 같습니다.",
                "기본 기능은 하는데 특별한 점은 없어요. 무난한 선택이에요.",
                "나쁘지 않지만 기대했던 것만큼은 아니에요. 그래도 쓸만해요.",
                "평범한 제품입니다. 특별히 좋지도 나쁘지도 않네요.",
                "가격대비 적당한 것 같아요. 크게 불만은 없습니다."
            },
            // 2점 리뷰
            {
                "생각보다 품질이 아쉬워요. 가격을 생각하면 더 좋을 줄 알았는데...",
                "배송은 빨랐는데 제품이 기대에 못 미쳐요. 조금 실망스럽네요.",
                "사용하다 보니 불편한 점들이 있어요. 개선이 필요할 것 같습니다.",
                "품질에 비해 가격이 비싸다고 생각해요. 다른 제품도 고려해보세요.",
                "AS가 잘 안 되는 것 같아요. 구매 전에 잘 알아보고 사세요."
            },
            // 1점 리뷰
            {
                "완전 실망했어요. 사진과 실물이 너무 달라요. 환불 고려중입니다.",
                "품질이 너무 안 좋아서 바로 교환 요청했어요. 추천하지 않습니다.",
                "배송도 늦고 제품도 불량이었어요. 최악의 쇼핑 경험이었습니다.",
                "가격만 비싸고 품질은 최악이에요. 절대 사지 마세요.",
                "고객 서비스도 불친절하고 제품도 문제가 많아요. 돈 아까워요."
            }
        };
        
        for (int i = 0; i < reviewCount; i++) {
            ProductReview review = new ProductReview();
            
            // 리뷰 ID
            review.reviewId = "REV" + productId + String.format("%03d", i + 1);
            
            // 평점 (1-5점, 높은 평점에 가중치)
            int[] ratingWeights = {5, 10, 15, 35, 35}; // 1점=5%, 2점=10%, 3점=15%, 4점=35%, 5점=35%
            int randomValue = productRandom.nextInt(100);
            int cumulativeWeight = 0;
            review.rating = 1;
            
            for (int j = 0; j < ratingWeights.length; j++) {
                cumulativeWeight += ratingWeights[j];
                if (randomValue < cumulativeWeight) {
                    review.rating = j + 1;
                    break;
                }
            }
            
            // 리뷰어 이름
            review.reviewerName = reviewers[productRandom.nextInt(reviewers.length)];
            
            // 리뷰 내용
            String[] ratingReviews = reviewTexts[review.rating - 1];
            review.content = ratingReviews[productRandom.nextInt(ratingReviews.length)];
            
            // 리뷰 날짜 (최근 180일 내)
            long now = System.currentTimeMillis();
            long reviewTime = now - (productRandom.nextLong(180L * 24 * 60 * 60 * 1000) % (180L * 24 * 60 * 60 * 1000));
            review.reviewDate = new java.util.Date(reviewTime);
            
            // 도움이 됨 수
            review.helpfulCount = productRandom.nextInt(20);
            
            reviews.add(review);
        }
        
        // 최신 리뷰부터 정렬
        reviews.sort((a, b) -> b.reviewDate.compareTo(a.reviewDate));
        
        return reviews;
    }
    
    private ReviewStats getReviewStats(int productId) {
        List<ProductReview> reviews = getProductReviews(productId);
        
        ReviewStats stats = new ReviewStats();
        stats.totalReviews = reviews.size();
        
        // 평점별 카운트
        stats.ratingCounts = new int[5];
        int totalScore = 0;
        
        for (ProductReview review : reviews) {
            stats.ratingCounts[review.rating - 1]++;
            totalScore += review.rating;
        }
        
        // 평균 평점
        stats.averageRating = stats.totalReviews > 0 ? (double) totalScore / stats.totalReviews : 0.0;
        
        return stats;
    }
    
    private List<ProductReviewSummary> getPopularReviewedProducts() throws SQLException {
        List<Product> products = productDAO.getAllProducts();
        List<ProductReviewSummary> summaries = new ArrayList<>();
        
        for (Product product : products) {
            ReviewStats stats = getReviewStats(product.getId());
            if (stats.totalReviews >= 10) { // 리뷰 10개 이상인 상품만
                ProductReviewSummary summary = new ProductReviewSummary();
                summary.product = product;
                summary.reviewStats = stats;
                summaries.add(summary);
            }
        }
        
        // 리뷰 수와 평점 기준으로 정렬 (인기순)
        summaries.sort((a, b) -> {
            double scoreA = a.reviewStats.averageRating * Math.log(a.reviewStats.totalReviews + 1);
            double scoreB = b.reviewStats.averageRating * Math.log(b.reviewStats.totalReviews + 1);
            return Double.compare(scoreB, scoreA);
        });
        
        return summaries.subList(0, Math.min(12, summaries.size()));
    }
    
    private List<ProductReviewSummary> getAllProductReviews() throws SQLException {
        List<Product> products = productDAO.getAllProducts();
        List<ProductReviewSummary> summaries = new ArrayList<>();
        
        for (Product product : products) {
            ReviewStats stats = getReviewStats(product.getId());
            ProductReviewSummary summary = new ProductReviewSummary();
            summary.product = product;
            summary.reviewStats = stats;
            summaries.add(summary);
        }
        
        // 평균 평점 기준으로 정렬
        summaries.sort((a, b) -> Double.compare(b.reviewStats.averageRating, a.reviewStats.averageRating));
        
        return summaries;
    }
    
    // 내부 클래스들
    public static class ProductReview {
        public String reviewId;
        public String reviewerName;
        public int rating;
        public String content;
        public java.util.Date reviewDate;
        public int helpfulCount;
    }
    
    public static class ReviewStats {
        public int totalReviews;
        public double averageRating;
        public int[] ratingCounts; // 1점~5점별 카운트
    }
    
    public static class ProductReviewSummary {
        public Product product;
        public ReviewStats reviewStats;
    }
}