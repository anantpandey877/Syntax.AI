package ai.syntax.util;

import ai.syntax.db.DbUtil;

import jakarta.servlet.ServletContext;

import jakarta.servlet.ServletContextEvent;

import jakarta.servlet.ServletContextListener;

import jakarta.servlet.annotation.WebListener;

@WebListener

public class AppInitializerListener implements ServletContextListener {

	@Override

	public void contextInitialized(ServletContextEvent sce) {

		System.out.println("[AppInitializer] Web Application is starting up...");

		ServletContext context = sce.getServletContext();


		String driver = context.getInitParameter("db.driver");

		String url = context.getInitParameter("db.url");

		String user = context.getInitParameter("db.user");

		String password = context.getInitParameter("db.password");


		DbUtil.init(driver, url, user, password);

		System.out.println("[AppInitializer] Database utility has been initialized.");

	}

	@Override

	public void contextDestroyed(ServletContextEvent sce) {

		System.out.println("[AppInitializer] Web Application is shutting down.");

	}

}