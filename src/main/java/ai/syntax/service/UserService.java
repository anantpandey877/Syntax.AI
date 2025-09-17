package ai.syntax.service;

import ai.syntax.dao.UserDAO;
import ai.syntax.model.User;

public class UserService {

	private UserDAO userDAO;

	public UserService() {
		this.userDAO = new UserDAO();
	}

	public void registerUser(User user) {

		if (user.getProfile_image_url() == null || user.getProfile_image_url().isEmpty()) {
			user.setProfile_image_url("/images/default_avatar.png");
		}

		userDAO.registerUser(user);
	}

	public User authenticateUser(String usernameOrEmail, String plainPassword) {
		User user = userDAO.getUserByUsernameOrEmail(usernameOrEmail);

		if (user != null) {

			if (user.getPassword_hash().equals(plainPassword)) {
				return user;
			}
		}
		return null;
	}
}
