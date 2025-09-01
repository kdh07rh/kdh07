package com.example.servlet;

import com.example.dao.ProductDAO;
import com.example.model.Product;

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

@WebServlet("/order-history")
public class OrderHistoryServlet extends HttpServlet {
    private ProductDAO productDAO = new ProductDAO();
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
            // 페이징 처리
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }
            
            int pageSize = 10;
            int offset = (page - 1) * pageSize;
            
            // 사용자별 주문 내역 조회 (시뮬레이션)
            List<OrderInfo> orderHistory = getOrderHistory(username, offset, pageSize);
            int totalOrders = getTotalOrderCount(username);
            int totalPages = (int) Math.ceil((double) totalOrders / pageSize);
            
            // 최근 주문 통계
            OrderStats stats = getOrderStats(username);
            
            request.setAttribute("orderHistory", orderHistory);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalOrders", totalOrders);
            request.setAttribute("orderStats", stats);
            request.setAttribute("username", username);
            
            request.getRequestDispatcher("order-history.jsp").forward(request, response);
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error retrieving order history", e);
        }
    }
    
    private List<OrderInfo> getOrderHistory(String username, int offset, int pageSize) throws SQLException {
        // DB에서 실제 제품 데이터를 가져와서 주문 내역 시뮬레이션
        List<Product> products = productDAO.getAllProducts();
        List<OrderInfo> orders = new ArrayList<>();
        
        // 사용자별로 고정된 시드 생성 (일관된 데이터)
        Random userRandom = new Random(username.hashCode());
        
        int orderCount = Math.min(pageSize, getTotalOrderCount(username) - offset);
        
        for (int i = 0; i < orderCount; i++) {
            OrderInfo order = new OrderInfo();
            order.orderId = "ORD" + username.hashCode() + String.format("%03d", offset + i + 1);
            
            // 주문 날짜 (최근 90일 내)
            long now = System.currentTimeMillis();
            long orderTime = now - (userRandom.nextLong(90L * 24 * 60 * 60 * 1000) % (90L * 24 * 60 * 60 * 1000));
            order.orderDate = new java.util.Date(orderTime);
            
            // 주문 상태
            String[] statuses = {"배송완료", "배송중", "주문확인", "결제완료", "취소"};
            order.status = statuses[userRandom.nextInt(statuses.length)];
            
            // 주문 상품 (1-3개)
            int itemCount = 1 + userRandom.nextInt(3);
            order.items = new ArrayList<>();
            order.totalAmount = 0;
            
            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(userRandom.nextInt(products.size()));
                OrderItem item = new OrderItem();
                item.productName = product.getName();
                item.quantity = 1 + userRandom.nextInt(3);
                item.price = product.getPrice().intValue();
                item.amount = item.price * item.quantity;
                
                order.items.add(item);
                order.totalAmount += item.amount;
            }
            
            orders.add(order);
        }
        
        return orders;
    }
    
    private int getTotalOrderCount(String username) {
        // 사용자별 주문 수 시뮬레이션 (15-50개)
        Random userRandom = new Random(username.hashCode());
        return 15 + userRandom.nextInt(36);
    }
    
    private OrderStats getOrderStats(String username) throws SQLException {
        Random userRandom = new Random(username.hashCode());
        
        OrderStats stats = new OrderStats();
        stats.totalOrders = getTotalOrderCount(username);
        stats.totalSpent = 50000 + userRandom.nextInt(500000); // 5만원 ~ 55만원
        stats.averageOrderAmount = stats.totalSpent / stats.totalOrders;
        stats.recentOrdersThisMonth = 1 + userRandom.nextInt(5);
        
        return stats;
    }
    
    // 내부 클래스들
    public static class OrderInfo {
        public String orderId;
        public java.util.Date orderDate;
        public String status;
        public List<OrderItem> items;
        public int totalAmount;
    }
    
    public static class OrderItem {
        public String productName;
        public int quantity;
        public int price;
        public int amount;
    }
    
    public static class OrderStats {
        public int totalOrders;
        public int totalSpent;
        public int averageOrderAmount;
        public int recentOrdersThisMonth;
    }
}