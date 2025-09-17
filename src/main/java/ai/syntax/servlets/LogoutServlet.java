package ai.syntax.servlets;

import ai.syntax.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("[LogoutServlet] Attempting to log out user...");
        
        HttpSession session = request.getSession(false);

        if (session != null) {
            String usernameForLog = "Unknown User";
            Object loggedInUserObject = session.getAttribute("loggedInUser");

            if (loggedInUserObject instanceof User) {
                User loggedInUser = (User) loggedInUserObject;
                usernameForLog = loggedInUser.getUsername();
            } else {
                System.out.println("[LogoutServlet] 'loggedInUser' attribute found but not an instance of User or is null.");
            }

            session.invalidate();
            System.out.println("[LogoutServlet] Session invalidated for user: " + usernameForLog);
        } else {
            System.out.println("[LogoutServlet] No active session found to invalidate.");
        }

        response.sendRedirect("login.html?message=logged_out");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}