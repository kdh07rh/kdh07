package com.example.servlet;

import com.example.dao.CartDAO;
import com.example.model.CartItem;
import com.example.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/cart")
public class CartServlet extends HttpServlet {
    private CartDAO cartDAO = new CartDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        try {
            List<CartItem> cartItems = cartDAO.getCartItems(user.getId());
            request.setAttribute("cartItems", cartItems);
            RequestDispatcher dispatcher = request.getRequestDispatcher("cart.jsp");
            dispatcher.forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ServletException("Error retrieving cart items", e);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");

        try {
            if ("add".equals(action)) {
                int productId = Integer.parseInt(request.getParameter("productId"));
                int quantity = 1; // Simple case: add 1 at a time
                cartDAO.addToCart(user.getId(), productId, quantity);
                response.sendRedirect("products");
            } else if ("remove".equals(action)) {
                int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
                cartDAO.removeFromCart(cartItemId);
                response.sendRedirect("cart");
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            throw new ServletException("Cart operation failed", e);
        }
    }
}
