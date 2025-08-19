package com.example.dao;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.example.model.CartItem;
import com.example.model.Product;

public class CartDAO {

    private DataSource dataSource;

    public CartDAO() {
        try {
            Context initContext = new InitialContext();
            Context envContext  = (Context)initContext.lookup("java:/comp/env");
            this.dataSource = (DataSource)envContext.lookup("jdbc/shopping");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public List<CartItem> getCartItems(int userId) throws SQLException {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT c.id, c.quantity, p.id as product_id, p.name, p.price, p.description FROM cart_items c JOIN products p ON c.product_id = p.id WHERE c.user_id = ?";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CartItem item = new CartItem();
                    item.setId(rs.getInt("id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUserId(userId);

                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("name"));
                    product.setPrice(rs.getBigDecimal("price"));
                    product.setDescription(rs.getString("description"));
                    item.setProduct(product);

                    cartItems.add(item);
                }
            }
        }
        return cartItems;
    }

    public void addToCart(int userId, int productId, int quantity) throws SQLException {
        String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            pstmt.setInt(2, productId);
            pstmt.setInt(3, quantity);
            pstmt.executeUpdate();
        }
    }

    public void removeFromCart(int cartItemId) throws SQLException {
        String sql = "DELETE FROM cart_items WHERE id = ?";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, cartItemId);
            pstmt.executeUpdate();
        }
    }
}
