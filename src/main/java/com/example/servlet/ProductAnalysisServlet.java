package com.example.servlet;

import com.example.dao.ProductDAO;
import com.example.model.Product;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@WebServlet("/product-analysis")
public class ProductAnalysisServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ProductDAO productDAO;
    
    // 캐시 (정상적인 비즈니스 로직이지만 메모리 집약적)
    private static final Map<String, List<ProductAnalysisResult>> analysisCache = 
        new ConcurrentHashMap<>();

    @Override
    public void init() throws ServletException {
        productDAO = new ProductDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String analysisType = request.getParameter("type");
        if (analysisType == null) analysisType = "basic";
        
        long startTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long startMemory = runtime.totalMemory() - runtime.freeMemory();
        
        System.out.printf("[%s] 상품 분석 시작 - Type: %s, 시작 메모리: %.2f MB%n", 
            new Date(), analysisType, startMemory / 1024.0 / 1024.0);

        try {
            List<ProductAnalysisResult> results = performProductAnalysis(analysisType);
            
            // 결과를 request에 설정
            request.setAttribute("analysisResults", results);
            request.setAttribute("analysisType", analysisType);
            request.setAttribute("processingTime", System.currentTimeMillis() - startTime);
            
            long endMemory = runtime.totalMemory() - runtime.freeMemory();
            long memoryUsed = endMemory - startMemory;
            request.setAttribute("memoryUsed", memoryUsed / 1024.0 / 1024.0);
            
            System.out.printf("[%s] 상품 분석 완료 - 사용 메모리: %.2f MB, 처리 시간: %d ms%n", 
                new Date(), memoryUsed / 1024.0 / 1024.0, System.currentTimeMillis() - startTime);
            
            request.getRequestDispatcher("product-analysis.jsp").forward(request, response);
            
        } catch (SQLException | OutOfMemoryError e) {
            System.err.printf("[%s] 상품 분석 실패: %s%n", new Date(), e.getMessage());
            e.printStackTrace();
            
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<html><body>");
            response.getWriter().println("<h2>메모리 부족 오류 발생</h2>");
            response.getWriter().printf("<p>오류: %s</p>", e.getMessage());
            response.getWriter().println("<p><a href='products'>메인으로 돌아가기</a></p>");
            response.getWriter().println("</body></html>");
        }
    }

    private List<ProductAnalysisResult> performProductAnalysis(String analysisType) 
            throws SQLException {
        
        // 캐시 확인 (정상적인 최적화 로직)
        String cacheKey = analysisType + "_" + System.currentTimeMillis() / 60000; // 1분 캐시
        if (analysisCache.containsKey(cacheKey)) {
            System.out.println("캐시에서 분석 결과 반환");
            return analysisCache.get(cacheKey);
        }

        // 상품 데이터 로드
        List<Product> products = productDAO.getAllProducts();
        List<ProductAnalysisResult> results = new ArrayList<>();

        switch (analysisType) {
            case "detailed":
                results = performDetailedAnalysis(products);
                break;
            case "statistical":
                results = performStatisticalAnalysis(products);
                break;
            case "comprehensive":
                results = performComprehensiveAnalysis(products);
                break;
            default:
                results = performBasicAnalysis(products);
        }

        // 캐시에 저장 (메모리 사용량 증가)
        analysisCache.put(cacheKey, results);
        
        // 캐시 크기 제한 (100개까지만)
        if (analysisCache.size() > 100) {
            String oldestKey = analysisCache.keySet().iterator().next();
            analysisCache.remove(oldestKey);
        }

        return results;
    }

    private List<ProductAnalysisResult> performBasicAnalysis(List<Product> products) {
        System.out.println("기본 분석 수행 중...");
        
        return products.stream().map(product -> {
            // 기본 분석 (적은 메모리 사용)
            ProductAnalysisResult result = new ProductAnalysisResult();
            result.setProductId(product.getId());
            result.setProductName(product.getName());
            result.setPrice(product.getPrice());
            result.setAnalysisData(generateBasicAnalysisData(product));
            return result;
        }).collect(Collectors.toList());
    }

    private List<ProductAnalysisResult> performDetailedAnalysis(List<Product> products) {
        System.out.println("상세 분석 수행 중... (메모리 집약적)");
        
        return products.stream().map(product -> {
            ProductAnalysisResult result = new ProductAnalysisResult();
            result.setProductId(product.getId());
            result.setProductName(product.getName());
            result.setPrice(product.getPrice());
            
            // 상세 분석 데이터 생성 (더 많은 메모리 사용)
            Map<String, Object> detailedData = new HashMap<>();
            
            // 가격 히스토리 시뮬레이션 (큰 배열)
            List<BigDecimal> priceHistory = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                priceHistory.add(product.getPrice().multiply(
                    BigDecimal.valueOf(0.8 + Math.random() * 0.4)));
            }
            detailedData.put("priceHistory", priceHistory);
            
            // 유사 상품 분석 (메모리 집약적)
            List<String> similarProducts = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                similarProducts.add("유사상품_" + product.getId() + "_" + i + "_" + UUID.randomUUID());
            }
            detailedData.put("similarProducts", similarProducts);
            
            // 고객 리뷰 시뮬레이션 (큰 문자열 배열)
            List<String> reviews = new ArrayList<>();
            for (int i = 0; i < 200; i++) {
                StringBuilder review = new StringBuilder();
                for (int j = 0; j < 100; j++) {
                    review.append("이 상품은 정말 좋습니다. 품질이 우수하고 가격도 합리적입니다. ");
                }
                reviews.add(review.toString());
            }
            detailedData.put("customerReviews", reviews);
            
            result.setAnalysisData(detailedData);
            return result;
        }).collect(Collectors.toList());
    }

    private List<ProductAnalysisResult> performStatisticalAnalysis(List<Product> products) {
        System.out.println("통계 분석 수행 중... (중간 메모리 사용)");
        
        // 전체 통계 계산 (메모리 집약적)
        Map<String, Object> globalStats = new HashMap<>();
        
        // 큰 통계 매트릭스 생성
        double[][] correlationMatrix = new double[products.size()][products.size()];
        for (int i = 0; i < products.size(); i++) {
            for (int j = 0; j < products.size(); j++) {
                correlationMatrix[i][j] = Math.random();
            }
        }
        globalStats.put("correlationMatrix", correlationMatrix);

        return products.stream().map(product -> {
            ProductAnalysisResult result = new ProductAnalysisResult();
            result.setProductId(product.getId());
            result.setProductName(product.getName());
            result.setPrice(product.getPrice());
            
            Map<String, Object> statsData = new HashMap<>(globalStats);
            statsData.put("basicStats", generateBasicAnalysisData(product));
            result.setAnalysisData(statsData);
            
            return result;
        }).collect(Collectors.toList());
    }

    private List<ProductAnalysisResult> performComprehensiveAnalysis(List<Product> products) {
        System.out.println("종합 분석 수행 중... (매우 높은 메모리 사용)");
        
        // 모든 분석을 결합 (최대 메모리 사용)
        List<ProductAnalysisResult> basicResults = performBasicAnalysis(products);
        List<ProductAnalysisResult> detailedResults = performDetailedAnalysis(products);
        List<ProductAnalysisResult> statResults = performStatisticalAnalysis(products);
        
        // 결과 병합 (메모리 사용량 폭증)
        return products.stream().map(product -> {
            ProductAnalysisResult result = new ProductAnalysisResult();
            result.setProductId(product.getId());
            result.setProductName(product.getName());
            result.setPrice(product.getPrice());
            
            Map<String, Object> comprehensiveData = new HashMap<>();
            comprehensiveData.put("basic", basicResults.stream()
                .filter(r -> r.getProductId() == product.getId())
                .findFirst().orElse(null));
            comprehensiveData.put("detailed", detailedResults.stream()
                .filter(r -> r.getProductId() == product.getId())
                .findFirst().orElse(null));
            comprehensiveData.put("statistical", statResults.stream()
                .filter(r -> r.getProductId() == product.getId())
                .findFirst().orElse(null));
            
            // 추가 대용량 데이터
            List<byte[]> largeDataSets = new ArrayList<>();
            for (int i = 0; i < 50; i++) {
                byte[] largeArray = new byte[1024 * 1024]; // 1MB씩
                Arrays.fill(largeArray, (byte) i);
                largeDataSets.add(largeArray);
            }
            comprehensiveData.put("largeDataSets", largeDataSets);
            
            result.setAnalysisData(comprehensiveData);
            return result;
        }).collect(Collectors.toList());
    }

    private Map<String, Object> generateBasicAnalysisData(Product product) {
        Map<String, Object> data = new HashMap<>();
        data.put("rank", Math.random() * 100);
        data.put("category", "Category_" + (product.getId() % 10));
        data.put("trend", Math.random() > 0.5 ? "상승" : "하락");
        return data;
    }

    // 분석 결과 클래스
    public static class ProductAnalysisResult {
        private int productId;
        private String productName;
        private BigDecimal price;
        private Map<String, Object> analysisData;

        // Getters and Setters
        public int getProductId() { return productId; }
        public void setProductId(int productId) { this.productId = productId; }
        
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        
        public Map<String, Object> getAnalysisData() { return analysisData; }
        public void setAnalysisData(Map<String, Object> analysisData) { this.analysisData = analysisData; }
    }
}
