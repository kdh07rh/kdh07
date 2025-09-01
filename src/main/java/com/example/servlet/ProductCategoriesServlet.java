package com.example.servlet;

import com.example.dao.ProductDAO;
import com.example.model.Product;
import java.math.BigDecimal;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/categories")
public class ProductCategoriesServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
    private Random random = new Random();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String categoryName = request.getParameter("category");
        String sortBy = request.getParameter("sort");
        String minPriceParam = request.getParameter("minPrice");
        String maxPriceParam = request.getParameter("maxPrice");
        
        try {
            if (categoryName != null) {
                // 특정 카테고리 상품 조회
                List<Product> categoryProducts = getCategoryProducts(categoryName);
                
                // 가격 필터링
                if (minPriceParam != null || maxPriceParam != null) {
                    categoryProducts = filterByPrice(categoryProducts, minPriceParam, maxPriceParam);
                }
                
                // 정렬
                if (sortBy != null) {
                    categoryProducts = sortProducts(categoryProducts, sortBy);
                }
                
                CategoryInfo categoryInfo = getCategoryInfo(categoryName);
                List<CategoryInfo> allCategories = getAllCategories();
                PriceRange priceRange = getCategoryPriceRange(categoryName);
                
                request.setAttribute("categoryName", categoryName);
                request.setAttribute("categoryProducts", categoryProducts);
                request.setAttribute("categoryInfo", categoryInfo);
                request.setAttribute("allCategories", allCategories);
                request.setAttribute("priceRange", priceRange);
                request.setAttribute("selectedSort", sortBy);
                request.setAttribute("selectedMinPrice", minPriceParam);
                request.setAttribute("selectedMaxPrice", maxPriceParam);
                
                request.getRequestDispatcher("category-products.jsp").forward(request, response);
            } else {
                // 전체 카테고리 목록
                List<CategoryInfo> categories = getAllCategories();
                
                request.setAttribute("categories", categories);
                request.getRequestDispatcher("categories.jsp").forward(request, response);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error retrieving category data", e);
        }
    }
    
    private List<Product> getCategoryProducts(String categoryName) throws SQLException {
        List<Product> allProducts = productDAO.getAllProducts();
        Map<String, List<Product>> categoryMap = categorizeProducts(allProducts);
        
        return categoryMap.getOrDefault(categoryName, new ArrayList<>());
    }
    
    private Map<String, List<Product>> categorizeProducts(List<Product> products) {
        Map<String, List<Product>> categoryMap = new HashMap<>();
        
        // 제품명 기반으로 카테고리 분류
        for (Product product : products) {
            String name = product.getName().toLowerCase();
            String category = determineCategory(name);
            
            categoryMap.computeIfAbsent(category, k -> new ArrayList<>()).add(product);
        }
        
        return categoryMap;
    }
    
    private String determineCategory(String productName) {
        // 제품명 기반 카테고리 분류 로직
        if (productName.contains("laptop") || productName.contains("computer") || 
            productName.contains("노트북") || productName.contains("컴퓨터")) {
            return "컴퓨터/노트북";
        } else if (productName.contains("phone") || productName.contains("mobile") || 
                  productName.contains("스마트폰") || productName.contains("핸드폰")) {
            return "스마트폰/태블릿";
        } else if (productName.contains("headphone") || productName.contains("speaker") || 
                  productName.contains("audio") || productName.contains("헤드폰") || 
                  productName.contains("스피커") || productName.contains("오디오")) {
            return "오디오/헤드폰";
        } else if (productName.contains("camera") || productName.contains("photo") || 
                  productName.contains("카메라") || productName.contains("사진")) {
            return "카메라/촬영";
        } else if (productName.contains("game") || productName.contains("gaming") || 
                  productName.contains("게임") || productName.contains("콘솔")) {
            return "게임/콘솔";
        } else if (productName.contains("watch") || productName.contains("fitness") || 
                  productName.contains("시계") || productName.contains("웨어러블")) {
            return "웨어러블/시계";
        } else if (productName.contains("tv") || productName.contains("monitor") || 
                  productName.contains("display") || productName.contains("모니터") || 
                  productName.contains("디스플레이")) {
            return "TV/모니터";
        } else if (productName.contains("home") || productName.contains("smart home") || 
                  productName.contains("홈") || productName.contains("가전")) {
            return "스마트홈/가전";
        } else {
            return "기타/액세서리";
        }
    }
    
    private List<CategoryInfo> getAllCategories() throws SQLException {
        List<Product> allProducts = productDAO.getAllProducts();
        Map<String, List<Product>> categoryMap = categorizeProducts(allProducts);
        
        List<CategoryInfo> categories = new ArrayList<>();
        
        // 카테고리별 정보 생성
        String[] categoryNames = {
            "컴퓨터/노트북", "스마트폰/태블릿", "오디오/헤드폰", "카메라/촬영",
            "게임/콘솔", "웨어러블/시계", "TV/모니터", "스마트홈/가전", "기타/액세서리"
        };
        
        String[] categoryIcons = {
            "💻", "📱", "🎧", "📷", "🎮", "⌚", "📺", "🏠", "🔧"
        };
        
        String[] categoryDescriptions = {
            "고성능 컴퓨터와 노트북", "최신 스마트폰과 태블릿", "프리미엄 오디오 기기",
            "전문 카메라와 촬영 장비", "게임 콘솔과 액세서리", "스마트 워치와 피트니스 밴드",
            "대형 TV와 고해상도 모니터", "스마트홈 기기와 가전제품", "다양한 전자기기 액세서리"
        };
        
        for (int i = 0; i < categoryNames.length; i++) {
            String categoryName = categoryNames[i];
            List<Product> categoryProducts = categoryMap.getOrDefault(categoryName, new ArrayList<>());
            
            if (!categoryProducts.isEmpty()) {
                CategoryInfo categoryInfo = new CategoryInfo();
                categoryInfo.name = categoryName;
                categoryInfo.icon = categoryIcons[i];
                categoryInfo.description = categoryDescriptions[i];
                categoryInfo.productCount = categoryProducts.size();
                
                // 가격 범위 계산
                BigDecimal minPrice = categoryProducts.stream()
                    .map(Product::getPrice)
                    .min(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
                BigDecimal maxPrice = categoryProducts.stream()
                    .map(Product::getPrice)
                    .max(BigDecimal::compareTo)
                    .orElse(BigDecimal.ZERO);
                categoryInfo.priceRange = String.format("₩%,d - ₩%,d", minPrice.intValue(), maxPrice.intValue());
                
                // 평균 가격
                double avgPrice = categoryProducts.stream()
                    .mapToDouble(p -> p.getPrice().doubleValue())
                    .average()
                    .orElse(0);
                categoryInfo.averagePrice = (int) avgPrice;
                
                categories.add(categoryInfo);
            }
        }
        
        // 상품 수 기준으로 정렬
        categories.sort((a, b) -> Integer.compare(b.productCount, a.productCount));
        
        return categories;
    }
    
    private CategoryInfo getCategoryInfo(String categoryName) throws SQLException {
        List<CategoryInfo> allCategories = getAllCategories();
        
        return allCategories.stream()
                .filter(cat -> cat.name.equals(categoryName))
                .findFirst()
                .orElse(createDefaultCategoryInfo(categoryName));
    }
    
    private CategoryInfo createDefaultCategoryInfo(String categoryName) {
        CategoryInfo info = new CategoryInfo();
        info.name = categoryName;
        info.icon = "📦";
        info.description = categoryName + " 카테고리 상품들";
        info.productCount = 0;
        info.priceRange = "₩0 - ₩0";
        info.averagePrice = 0;
        return info;
    }
    
    private PriceRange getCategoryPriceRange(String categoryName) throws SQLException {
        List<Product> categoryProducts = getCategoryProducts(categoryName);
        
        PriceRange range = new PriceRange();
        if (!categoryProducts.isEmpty()) {
            BigDecimal minPrice = categoryProducts.stream()
                .map(Product::getPrice)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            BigDecimal maxPrice = categoryProducts.stream()
                .map(Product::getPrice)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
            
            range.minPrice = minPrice.intValue();
            range.maxPrice = maxPrice.intValue();
            
            // 가격대별 구간 생성 (4구간)
            int priceGap = (range.maxPrice - range.minPrice) / 4;
            range.priceSegments = new ArrayList<>();
            
            for (int i = 0; i < 4; i++) {
                int segmentMin = range.minPrice + (priceGap * i);
                int segmentMax = (i == 3) ? range.maxPrice : range.minPrice + (priceGap * (i + 1)) - 1;
                
                PriceSegment segment = new PriceSegment();
                segment.label = String.format("₩%,d - ₩%,d", segmentMin, segmentMax);
                segment.minPrice = segmentMin;
                segment.maxPrice = segmentMax;
                segment.count = (int) categoryProducts.stream()
                        .filter(p -> p.getPrice().intValue() >= segmentMin && p.getPrice().intValue() <= segmentMax)
                        .count();
                
                range.priceSegments.add(segment);
            }
        }
        
        return range;
    }
    
    private List<Product> filterByPrice(List<Product> products, String minPriceParam, String maxPriceParam) {
        int minPrice = 0;
        int maxPrice = Integer.MAX_VALUE;
        
        try {
            if (minPriceParam != null && !minPriceParam.trim().isEmpty()) {
                minPrice = Integer.parseInt(minPriceParam);
            }
            if (maxPriceParam != null && !maxPriceParam.trim().isEmpty()) {
                maxPrice = Integer.parseInt(maxPriceParam);
            }
        } catch (NumberFormatException e) {
            // 잘못된 가격 파라미터는 무시
        }
        
        final int finalMinPrice = minPrice;
        final int finalMaxPrice = maxPrice;
        
        return products.stream()
                .filter(p -> p.getPrice().intValue() >= finalMinPrice && p.getPrice().intValue() <= finalMaxPrice)
                .collect(Collectors.toList());
    }
    
    private List<Product> sortProducts(List<Product> products, String sortBy) {
        switch (sortBy) {
            case "price_low":
                return products.stream().sorted(Comparator.comparing(Product::getPrice)).collect(Collectors.toList());
            case "price_high":
                return products.stream().sorted(Comparator.comparing(Product::getPrice).reversed()).collect(Collectors.toList());
            case "name":
                return products.stream().sorted(Comparator.comparing(Product::getName)).collect(Collectors.toList());
            case "popular":
                // 인기순은 ID 역순으로 시뮬레이션
                return products.stream().sorted(Comparator.comparing(Product::getId).reversed()).collect(Collectors.toList());
            default:
                return products;
        }
    }
    
    // 내부 클래스들
    public static class CategoryInfo {
        public String name;
        public String icon;
        public String description;
        public int productCount;
        public String priceRange;
        public int averagePrice;
    }
    
    public static class PriceRange {
        public int minPrice;
        public int maxPrice;
        public List<PriceSegment> priceSegments;
    }
    
    public static class PriceSegment {
        public String label;
        public int minPrice;
        public int maxPrice;
        public int count;
    }
}