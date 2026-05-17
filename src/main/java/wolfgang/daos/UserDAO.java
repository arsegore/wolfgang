package wolfgang.daos;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.User;
import wolfgang.utils.PasswordUtils;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Insère un utilisateur dans la bdd
     * @param user l'utilisateur à insérer
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean create(User user, String token) {
        String sql = """
                    INSERT INTO users (username, email, password, verification_token)
                    VALUES (?, ?, ?, ?);
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setString(4, token);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour un utilisateur
     * @param user l'utilisateur à mettre à jour
     * @return vrai si la mise à jour a réussi, faux sinon
     */
    public boolean update(User user) {
        String sql = """
                    UPDATE users
                    SET username = ?, email = ?, password = ?, is_admin = ?, updated_at = ?
                    WHERE id = ?;
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isAdmin());
            stmt.setObject(5, LocalDateTime.now());
            stmt.setInt(6, user.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime un utilisateur
     * @param id l'identifiant de l'utilisateur
     * @return vrai si la suppression a réussi, faux sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?;";

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @param username le nom d'utilisateur
     * @param password le mot de passe en clair
     * @return l'utilisateur authentifié ou null
     */
    public User authenticate(String username, String password) {
        User user = null;
        String sql = """
                SELECT id, username, email, password, is_admin, is_verified, created_at, updated_at
                FROM users
                WHERE username = ?;
                """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (PasswordUtils.verifyPassword(password, rs.getString("password"))) {
                    user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("email"),
                            rs.getString("password"),
                            rs.getBoolean("is_admin"),
                            rs.getBoolean("is_verified"),
                            rs.getObject("created_at", LocalDateTime.class),
                            rs.getObject("updated_at", LocalDateTime.class)
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * @param id l'identifiant de l'utilisateur
     * @return l'utilisateur correspondant ou null
     */
    public User findById(int id) {
        User user = null;
        String sql = """
                    SELECT id, username, email, password, is_admin, is_verified, created_at, updated_at
                    FROM users
                    WHERE id = ?;
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin"),
                        rs.getBoolean("is_verified"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * @return la liste de tous les utilisateurs
     */
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = """
                    SELECT id, username, email, password, is_admin, is_verified, created_at, updated_at
                    FROM users
                    ORDER BY id;
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin"),
                        rs.getBoolean("is_verified"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }


    public boolean verifyUser(String token) {
        String sql = """
            UPDATE users 
            SET is_verified = TRUE, verification_token = NULL 
            WHERE verification_token = ?;
            """;
        try (Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
             PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, token);
                return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User findByUsername(String username) {
        User user = null;
        String sql = """
                    SELECT id, username, email, password, is_admin, is_verified, created_at, updated_at
                    FROM users
                    WHERE username = ?;
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin"),
                        rs.getBoolean("is_verified"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public boolean verifyExistingEmail(String email){
        String sql = """
                SELECT id
                FROM users
                WHERE email = ?
                LIMIT 1;
                """;
        try (Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean verifyExistingUsername(String username){
        String sql = """
                SELECT id
                FROM users
                WHERE username = ?
                LIMIT 1;
                """;
        try (Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
             PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }
}
