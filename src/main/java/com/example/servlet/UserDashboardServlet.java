package com.example.servlet;

import com.example.dao.ProductDAO;
import com.example.dao.UserDAO;
import com.example.model.Product;
import com.example.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet("/dashboard")
public class UserDashboardServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private UserDAO userDAO = new UserDAO();
    private Random random = new Random();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        
        // 로그인하지 않은 경우 로그인 페이지로 리다이렉트
        if (username == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try {
            // 사용자 정보 조회
            User user = userDAO.getUserByUsername(username);
            if (user == null) {
                // 사용자 정보가 없으면 기본 정보 생성
                user = createDefaultUser(username);
            }
            
            // 대시보드 데이터 생성
            DashboardData dashboardData = generateDashboardData(username);
            
            // 최근 주문 상품 (추천 상품)
            List<Product> recommendedProducts = getRecommendedProducts(username);
            
            // 위시리스트 상품
            List<Product> wishlistProducts = getWishlistProducts(username);
            
            // 최근 본 상품
            List<Product> recentlyViewedProducts = getRecentlyViewedProducts(username);
            
            request.setAttribute("user", user);
            request.setAttribute("dashboardData", dashboardData);
            request.setAttribute("recommendedProducts", recommendedProducts);
            request.setAttribute("wishlistProducts", wishlistProducts);
            request.setAttribute("recentlyViewedProducts", recentlyViewedProducts);
            
            request.getRequestDispatcher("dashboard.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error loading dashboard", e);
        }
    }
    
    private User createDefaultUser(String username) {
        User user = new User();
        user.setUsername(username);
        // User 모델에는 email과 name 필드가 없으므로 username만 설정
        
        return user;
    }
    
    private DashboardData generateDashboardData(String username) throws SQLException {
        Random userRandom = new Random(username.hashCode());
        
        DashboardData data = new DashboardData();
        
        // 주문 관련 통계
        data.totalOrders = 15 + userRandom.nextInt(36); // 15-50개
        data.pendingOrders = userRandom.nextInt(3); // 0-2개
        data.totalSpent = 50000 + userRandom.nextInt(500000); // 5만원-55만원
        
        // 포인트 및 쿠폰
        data.availablePoints = userRandom.nextInt(10000); // 0-10,000 포인트
        data.availableCoupons = userRandom.nextInt(5); // 0-4개 쿠폰
        
        // 위시리스트
        data.wishlistCount = 3 + userRandom.nextInt(8); // 3-10개
        
        // 최근 활동
        data.recentLoginDays = 1 + userRandom.nextInt(30); // 1-30일 전
        data.monthlyVisits = 5 + userRandom.nextInt(20); // 5-24회
        
        // 회원 등급
        String[] grades = {"브론즈", "실버", "골드", "플래티넘", "다이아몬드"};
        data.memberGrade = grades[Math.min(grades.length - 1, data.totalSpent / 100000)];
        
        // 다음 등급까지 필요한 금액
        int currentGradeIndex = java.util.Arrays.asList(grades).indexOf(data.memberGrade);
        if (currentGradeIndex < grades.length - 1) {
            data.nextGradeRequiredAmount = ((currentGradeIndex + 1) * 100000) - data.totalSpent;
        } else {
            data.nextGradeRequiredAmount = 0; // 최고 등급
        }
        
        return data;
    }
    
    private List<Product> getRecommendedProducts(String username) throws SQLException {
        List<Product> allProducts = productDAO.getAllProducts();
        List<Product> recommended = new ArrayList<>();
        
        // 사용자별 고정된 추천 상품 선택
        Random userRandom = new Random(username.hashCode() + 1);
        
        // 4개의 추천 상품 선택
        for (int i = 0; i < Math.min(4, allProducts.size()); i++) {
            Product product = allProducts.get(userRandom.nextInt(allProducts.size()));
            if (!recommended.contains(product)) {
                recommended.add(product);
            }
        }
        
        return recommended;
    }
    
    private List<Product> getWishlistProducts(String username) throws SQLException {
        List<Product> allProducts = productDAO.getAllProducts();
        List<Product> wishlist = new ArrayList<>();
        
        // 사용자별 고정된 위시리스트 생성
        Random userRandom = new Random(username.hashCode() + 2);
        
        int wishlistSize = 3 + userRandom.nextInt(4); // 3-6개
        for (int i = 0; i < Math.min(wishlistSize, allProducts.size()); i++) {
            Product product = allProducts.get(userRandom.nextInt(allProducts.size()));
            if (!wishlist.contains(product)) {
                wishlist.add(product);
            }
        }
        
        return wishlist;
    }
    
    private List<Product> getRecentlyViewedProducts(String username) throws SQLException {
        List<Product> allProducts = productDAO.getAllProducts();
        List<Product> recentlyViewed = new ArrayList<>();
        
        // 사용자별 고정된 최근 본 상품 생성
        Random userRandom = new Random(username.hashCode() + 3);
        
        // 5개의 최근 본 상품 선택
        for (int i = 0; i < Math.min(5, allProducts.size()); i++) {
            Product product = allProducts.get(userRandom.nextInt(allProducts.size()));
            if (!recentlyViewed.contains(product)) {
                recentlyViewed.add(product);
            }
        }
        
        return recentlyViewed;
    }
    
    // 대시보드 데이터 클래스
    public static class DashboardData {
        public int totalOrders;
        public int pendingOrders;
        public int totalSpent;
        public int availablePoints;
        public int availableCoupons;
        public int wishlistCount;
        public int recentLoginDays;
        public int monthlyVisits;
        public String memberGrade;
        public int nextGradeRequiredAmount;
    }
}