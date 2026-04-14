package wolfgang.config;

import jakarta.servlet.ServletContext;

public class DatabaseConfig {
    public static String DB_URL;

    public static String DB_LOGIN;

    public static String DB_PASSWD;

    public static void init(ServletContext context) {
        try {
            Class.forName(context.getInitParameter("JDBC_DRIVER"));
            DB_URL = context.getInitParameter("JDBC_URL");
            DB_LOGIN = context.getInitParameter("JDBC_LOGIN");
            DB_PASSWD = context.getInitParameter("JDBC_PASSWORD");

            System.out.println("URL = " + DB_URL);
            System.out.println("Login = " + DB_LOGIN);
            System.out.println("MDP =" + DB_PASSWD);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
