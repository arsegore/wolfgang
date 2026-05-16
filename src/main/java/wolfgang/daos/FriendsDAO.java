package wolfgang.daos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Friendship;
import wolfgang.models.User;

public class FriendsDAO {

    /**
     * Crée une demande d'amitié entre 2 utilisateurs
     * @param user premier utilisateur
     * @param friend second utilisateur
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean demanderAmitie(User user, User friend) {
        String sql = """
                    INSERT INTO friends (user_id, friend_id, status)
                    VALUES (?, ?, 'pending');
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            // Plus petit ID en 1er
            int userId = Math.min(user.getId(), friend.getId());
            int friendId = Math.max(user.getId(), friend.getId());

            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ajoute une relation d'amitié entre 2 utilisateurs
     * @param user premier utilisateur
     * @param friend second utilisateur
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean ajouterAmitie(User user, User friend) {
        String sql = """
                    INSERT INTO friends (user_id, friend_id, status)
                    VALUES (?, ?, 'accepted');
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            // Plus petit ID en 1er
            int userId = Math.min(user.getId(), friend.getId());
            int friendId = Math.max(user.getId(), friend.getId());

            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime une amitié entre 2 utilisateurs
     * @param user premier utilisateur
     * @param friend second utilisateur
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean supprimerAmitie(User user, User friend) {
        String sql = """
                    DELETE FROM friends
                    WHERE user_id = ? AND friend_id = ?;
                    """;

        try (
            Connection con = DriverManager.getConnection(
                DatabaseConfig.DB_URL,
                DatabaseConfig.DB_LOGIN,
                DatabaseConfig.DB_PASSWD);
            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, friend.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère la liste des amis d'un utilisateur
     * @param user l'utilisateur
     * @return la liste des amis
     */
    public List<Friendship> findFriends(User user) {
        List<Friendship> friends = new ArrayList<>();
        String sql = """
                SELECT u.id,
                    u.username,
                    u.email,
                    u.password,
                    u.is_admin,
                    u.is_verified,
                    u.created_at,
                    u.updated_at,
                    f.created_at AS friendship_date
                FROM users u
                JOIN friends f
                    ON (
                        (f.user_id = ? AND f.friend_id = u.id)
                        OR
                        (f.friend_id = ? AND f.user_id = u.id)
                    )
                WHERE f.status = 'accepted';
                """;

        try (
            Connection con = DriverManager.getConnection(
                    DatabaseConfig.DB_URL,
                    DatabaseConfig.DB_LOGIN,
                    DatabaseConfig.DB_PASSWD);

            PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, user.getId());
            stmt.setInt(2, user.getId());

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User friend = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getBoolean("is_admin"),
                        rs.getBoolean("is_verified"),
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                );
                friends.add(new Friendship(
                        friend,
                        rs.getObject("friendship_date", LocalDateTime.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return friends;
    }
}