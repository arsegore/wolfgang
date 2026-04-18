package wolfgang.repositories;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.User;
import wolfgang.utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDateTime;

// TODO vérifications mdp, hachage, gestion d'erreurs, logs propres...

public class UserRepository {

    /**
     * Insère un utilisateur dans la bdd
     * @param username
     * @param email
     * @param password
     * @returns vrai si l'insertion a réussi, faux sinon
     */
    public static boolean createUser(String username, String email, String password) {
        String sql = """
					INSERT INTO users (username, email, password)
					VALUES (?, ?, ?);
					""";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD)
            ;
            PreparedStatement stmt = con.prepareStatement(sql);
        ){
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Renvoie l'utilisateur correspondant à un id (ou null)
     * @param id
     */
    public static User findById(int id) {
        User user = null;
        String sql = """
					SELECT *
					FROM users
					WHERE id = ?;
					""";

        try (
            Connection con = DriverManager.getConnection(
                    DatabaseConfig.DB_URL,
                    DatabaseConfig.DB_LOGIN,
                    DatabaseConfig.DB_PASSWD
            );
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
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
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     *
     * @param username
     * @param password
     * @return L'utilisateur correspondant
     */
    public static User authenticate(String username, String password) {
        User user = null;
        String sql = """
                SELECT id, username, email, password, created_at, updated_at
                FROM users
                WHERE username = ?;
                """;

        try (
            Connection con = DriverManager.getConnection(
                    DatabaseConfig.DB_URL,
                    DatabaseConfig.DB_LOGIN,
                    DatabaseConfig.DB_PASSWD
            );
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, username);
            ResultSet resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                // vérification du mdp saisi par l'utilisateur
                if (PasswordUtils.verifyPassword(password, resultSet.getString("password"))) {
                    user = new User(
                            resultSet.getInt("id"),
                            resultSet.getString("username"),
                            resultSet.getString("email"),
                            resultSet.getString("password"),
                            resultSet.getObject("created_at", LocalDateTime.class),
                            resultSet.getObject("updated_at", LocalDateTime.class)
                    );
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }
}
