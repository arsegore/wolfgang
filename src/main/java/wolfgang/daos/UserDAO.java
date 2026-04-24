package wolfgang.daos;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.User;
import wolfgang.utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDateTime;

// TODO vérifications mdp, hachage, gestion d'erreurs, logs propres...

public class UserDAO {

    /**
     * Insère un utilisateur dans la bdd
     * @param user
     * @returns vrai si l'insertion a réussi, faux sinon
     */
    public boolean create(User user) {
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
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void update(User user) {
        String sql = """
					UPDATE users
					SET username = ?, email = ?, password = ?, updated_at = ?
					WHERE id = ?;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD)
                ;
                PreparedStatement stmt = con.prepareStatement(sql);
        ){
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setObject(4, LocalDateTime.now());
            stmt.setInt(5, user.getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param username
     * @param password
     * @return L'utilisateur correspondant
     */
    public User authenticate(String username, String password) {
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

    /**
     * @param id
     * @return L'utilisateur correspondant ou null
     */
    public User findById(int id) {
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
}
