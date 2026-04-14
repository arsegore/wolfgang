package wolfgang.repositories;

import wolfgang.config.DatabaseConfig;
import wolfgang.entities.User;
import java.sql.*;
import java.time.LocalDateTime;

// TODO vérifications mdp, hachage, gestion d'erreurs, logs propres...

public class UserRepository {

    /**
     * Insère un utilisateur dans la bdd
     * @param username
     * @param email
     * @param password
     */
    public static void createUser(String username, String email, String password) {
        try {
            Connection con = DriverManager.getConnection(
                    DatabaseConfig.DB_URL,
                    DatabaseConfig.DB_LOGIN,
                    DatabaseConfig.DB_PASSWD
            );

            String sql = """
					INSERT INTO users (username, email, password)
					VALUES (?, ?, ?);
					""";

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            int result = stmt.executeUpdate();

            stmt.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Renvoie l'utilisateur correspondant à un id (ou null)
     * @param id
     */
    public static User findById(int id) {
        User user = null;

        try {
            Connection con = DriverManager.getConnection(
                    DatabaseConfig.DB_URL,
                    DatabaseConfig.DB_LOGIN,
                    DatabaseConfig.DB_PASSWD
            );

            String sql = """
					SELECT *
					FROM users
					WHERE id = ?;
					""";

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, id);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                user = new User(
                        resultSet.getInt("id"),
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password"),
                        resultSet.getObject("created_at", LocalDateTime.class),
                        resultSet.getObject("updated_at", LocalDateTime.class)
                );
            }

            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
