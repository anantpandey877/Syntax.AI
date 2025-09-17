package ai.syntax.servlets; 

import ai.syntax.model.User; 
import ai.syntax.service.UserService; 

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserService(); 
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String usernameOrEmail = request.getParameter("username");
        String password = request.getParameter("password");

        User authenticatedUser = userService.authenticateUser(usernameOrEmail, password);

        if (authenticatedUser != null) {
            HttpSession session = request.getSession();
            session.setAttribute("loggedInUser", authenticatedUser); 
            session.setMaxInactiveInterval(30 * 60);

            response.sendRedirect("code_analysis.html");
        } else {
            response.sendRedirect("login.html?error=invalid_credentials");
        }
    }

   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        request.getRequestDispatcher("/login.html").forward(request, response);
    }
}