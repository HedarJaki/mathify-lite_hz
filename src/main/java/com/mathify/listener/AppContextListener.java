package com.mathify.listener;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Enumeration;

/**
 * Cleans up JDBC resources when the web application is undeployed/stopped.
 *
 * Without this, Tomcat logs warnings on shutdown/redeploy because (1) the MySQL
 * driver's background "abandoned-connection cleanup" thread keeps running and
 * (2) the JDBC driver stays registered against this webapp's classloader -
 * both of which leak the classloader on redeploy. We stop the thread and
 * deregister any drivers this webapp registered.
 */
@WebListener
public class AppContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // 1. Stop MySQL Connector/J's abandoned-connection cleanup thread, if present.
        //    Done reflectively so we don't hard-couple to a specific connector version.
        try {
            Class<?> cleanup = Class.forName("com.mysql.cj.jdbc.AbandonedConnectionCleanupThread");
            cleanup.getMethod("checkedShutdown").invoke(null);
        } catch (ReflectiveOperationException ignored) {
            // Connector absent or method renamed - nothing to stop.
        }

        // 2. Deregister JDBC drivers loaded by THIS webapp's classloader so the
        //    classloader can be garbage-collected after undeploy.
        ClassLoader webappClassLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();
            if (driver.getClass().getClassLoader() == webappClassLoader) {
                try {
                    DriverManager.deregisterDriver(driver);
                } catch (Exception e) {
                    sce.getServletContext().log("Failed to deregister JDBC driver: " + driver, e);
                }
            }
        }
    }
}
