package com.example.servlet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/connection-leak-test")
public class ConnectionLeakServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<Connection> leakedConnections = new ArrayList<>();
    private DataSource dataSource;

    @Override
    public void init() throws ServletException {
        try {
            Context initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:/comp/env");
            this.dataSource = (DataSource) envContext.lookup("jdbc/shopping");
        } catch (NamingException e) {
            throw new ServletException("Cannot initialize ConnectionLeakServlet", e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");

        if ("leak".equals(action)) {
            try {
                // Get a connection and "forget" to close it
                Connection conn = dataSource.getConnection();
                leakedConnections.add(conn);
            } catch (SQLException e) {
                throw new ServletException("Failed to get database connection", e);
            }
        } else if ("reset".equals(action)) {
            for (Connection conn : leakedConnections) {
                try {
                    if (conn != null && !conn.isClosed()) {
                        conn.close();
                    }
                } catch (SQLException e) {
                    // Log error or handle it as needed
                    e.printStackTrace();
                }
            }
            leakedConnections.clear();
        }

        request.setAttribute("leakSize", leakedConnections.size());
        request.getRequestDispatcher("connection-leak-test.jsp").forward(request, response);
    }
}
