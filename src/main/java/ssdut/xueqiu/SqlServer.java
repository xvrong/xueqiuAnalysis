package ssdut.xueqiu;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

public class SqlServer {
    static Connection conn;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:E:\\xueqiuAnalysis\\src\\main\\resources\\user.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @WebServlet("/register")
    public static class register extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String sql = "insert into userprofile (username, password) values (?, ?)";
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String[] params = {username, password};
            // 执行sql语句
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    pstmt.setString(i + 1, params[i]);
                }
                pstmt.executeUpdate();
                System.out.println("register successfully");
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("register failed");
            }
            // 重定向回index.jsp
            response.sendRedirect("index.jsp");
        }
    }

    @WebServlet("/login")
    public static class login extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String sql = "select * from userprofile where username = ? and password = ?";
            String username = request.getParameter("username");
            String password = request.getParameter("password");
            String[] params = {username, password};
            // 执行sql语句
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    pstmt.setString(i + 1, params[i]);
                }
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    System.out.println("login successfully");
                    request.getSession().setAttribute("username", username);
                    request.getSession().setAttribute("acl", rs.getInt("acl"));
                } else {
                    System.out.println("login failed");
                }
                rs.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("login failed");
            }
            // 重定向回index.jsp
            response.sendRedirect("index.jsp");
        }
    }

    @WebServlet("/logout")
    public static class logout extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.getSession().removeAttribute("username");
            request.getSession().removeAttribute("acl");
            System.out.println("logout successfully");
            // 重定向回index.jsp
            response.sendRedirect("index.jsp");
        }
    }

    @WebServlet("/getPriority")
    public static class getPriority extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String sql = "update userprofile set acl = 1 where username = ?";
            String username = request.getSession().getAttribute("username").toString();
            String[] params = {username};
            // 执行sql语句
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    pstmt.setString(i + 1, params[i]);
                }
                pstmt.executeUpdate();
                request.getSession().setAttribute("acl", 1);
                pstmt.close();
                System.out.println("getPriority successfully");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("getPriority failed");
            }
            // 重定向回index.jsp
            response.sendRedirect("index.jsp");
        }
    }

    @WebServlet("/removePriority")
    public static class removePriority extends HttpServlet {
        @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

            String sql = "update userprofile set acl = 0 where username = ?";
            String username = request.getSession().getAttribute("username").toString();
            String[] params = {username};
            // 执行sql语句
            try {
                PreparedStatement pstmt = conn.prepareStatement(sql);
                for (int i = 0; i < params.length; i++) {
                    pstmt.setString(i + 1, params[i]);
                }
                pstmt.executeUpdate();
                request.getSession().setAttribute("acl", 0);
                pstmt.close();
                System.out.println("removePriority successfully");
            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("removePriority failed");
            }
            // 重定向回index.jsp
            response.sendRedirect("index.jsp");
        }
    }
}