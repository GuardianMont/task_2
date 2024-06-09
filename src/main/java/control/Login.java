package control;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.DriverManagerConnectionPool;
import model.OrderModel;
import model.UserBean;
import util.HashUtil;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Login() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String email = request.getParameter("j_email");
        String password = request.getParameter("j_password");
        String redirectedPage = "/loginPage.jsp";
        boolean control = false;

        try (Connection con = DriverManagerConnectionPool.getConnection()) {
            String sql = "SELECT email, passwordUser, ruolo, nome, cognome, indirizzo, telefono, numero, intestatario, CVV FROM UserAccount WHERE email = ?";
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("passwordUser");
                        String hashedPassword = HashUtil.hashPassword(password);
                        if (hashedPassword.equals(storedHash)) {
                            control = true;
                            UserBean registeredUser = new UserBean();
                            registeredUser.setEmail(rs.getString("email"));
                            registeredUser.setNome(rs.getString("nome"));
                            registeredUser.setCognome(rs.getString("cognome"));
                            registeredUser.setIndirizzo(rs.getString("indirizzo"));
                            registeredUser.setTelefono(rs.getString("telefono"));
                            registeredUser.setNumero(rs.getString("numero"));
                            registeredUser.setIntestatario(rs.getString("intestatario"));
                            registeredUser.setCvv(rs.getString("CVV"));
                            registeredUser.setRole(rs.getString("ruolo"));
                            request.getSession().setAttribute("registeredUser", registeredUser);
                            request.getSession().setAttribute("role", registeredUser.getRole());
                            request.getSession().setAttribute("email", rs.getString("email"));
                            request.getSession().setAttribute("nome", rs.getString("nome"));

                            OrderModel model = new OrderModel();
                            request.getSession().setAttribute("listaOrdini", model.getOrders(rs.getString("email")));

                            redirectedPage = "/index.jsp";
                        }
                    }
                }
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            redirectedPage = "/loginPage.jsp";
        }

        if (!control) {
            request.getSession().setAttribute("login-error", true);
        } else {
            request.getSession().setAttribute("login-error", false);
        }
        response.sendRedirect(request.getContextPath() + redirectedPage);
    }
}
