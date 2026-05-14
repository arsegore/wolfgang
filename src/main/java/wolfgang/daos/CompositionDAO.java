package wolfgang.daos;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Composition;
import wolfgang.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompositionDAO {

    /**
     * Insère une nouvelle composition dans la bdd
     * @param composition
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean create(Composition composition) {
        String sql = """
					INSERT INTO compositions (title, tempo, access_type, owner_id)
					VALUES (?, ?, ?, ?);
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, composition.getTitle());
            stmt.setInt(2, composition.getTempo());
            stmt.setString(3, composition.getAccessType());
            stmt.setInt(4, composition.getOwner().getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour une composition
     * @param composition
     * @return vrai si la mise à jour a réussi, faux sinon
     */
    public boolean update(Composition composition) {
        String sql = """
                UPDATE compositions
                SET title = ?, description = ?, tempo = ?, access_type = ?, owner_id = ?, updated_at = ?
                WHERE id = ?;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, composition.getTitle());
            stmt.setString(2, composition.getDescription());
            stmt.setInt(3, composition.getTempo());
            stmt.setString(4, composition.getAccessType());
            stmt.setInt(5, composition.getOwner().getId());
            stmt.setObject(6, LocalDateTime.now());
            stmt.setInt(7, composition.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime une composition
     * @param id
     * @return vrai si la suppression a réussi, faux sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM compositions WHERE id = ?;";

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
     * @param id
     * @return La composition correspondante ou null
     */
    public Composition findById(int id) {
        Composition composition = null;
        String sql = """
					SELECT c.*, u.id as u_id, u.username, u.email, u.password,
					       u.created_at as u_created_at, u.updated_at as u_updated_at
					FROM compositions c
					JOIN users u ON c.owner_id = u.id
					WHERE c.id = ?;
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
                User owner = new User(
                        rs.getInt("u_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getObject("u_created_at", LocalDateTime.class),
                        rs.getObject("u_updated_at", LocalDateTime.class)
                );
                composition = new Composition(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("tempo"),
                        rs.getString("access_type"),
                        owner,
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return composition;
    }

    /**
     * Retourne toutes les compositions dont l'utilisateur est membre
     * @param userId
     * @return liste des compositions
     */
    public List<Composition> findByUser(int userId) {
        List<Composition> compositions = new ArrayList<>();
        String sql = """
					SELECT c.*, u.id as u_id, u.username, u.email, u.password,
					       u.created_at as u_created_at, u.updated_at as u_updated_at
					FROM compositions c
					JOIN users u ON c.owner_id = u.id
					JOIN composition_members cm ON c.id = cm.composition_id
					WHERE cm.user_id = ?;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User owner = new User(
                        rs.getInt("u_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getObject("u_created_at", LocalDateTime.class),
                        rs.getObject("u_updated_at", LocalDateTime.class)
                );
                compositions.add(new Composition(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("tempo"),
                        rs.getString("access_type"),
                        owner,
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return compositions;
    }

    /**
     * Ajoute un membre à une composition
     * @param compositionId
     * @param userId
     * @param role
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean addMember(int compositionId, int userId, String role) {
        String sql = """
					INSERT INTO composition_members (composition_id, user_id, role)
					VALUES (?, ?, ?);
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, compositionId);
            stmt.setInt(2, userId);
            stmt.setString(3, role);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour le rôle d'un membre
     * @param compositionId
     * @param userId
     * @param role
     * @return vrai si la mise à jour a réussi, faux sinon
     */
    public boolean updateRole(int compositionId, int userId, String role) {
        String sql = """
					UPDATE composition_members
					SET role = ?
					WHERE composition_id = ? AND user_id = ?;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, role);
            stmt.setInt(2, compositionId);
            stmt.setInt(3, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retire un membre d'une composition
     * @param compositionId
     * @param userId
     * @return vrai si la suppression a réussi, faux sinon
     */
    public boolean removeMember(int compositionId, int userId) {
        String sql = """
					DELETE FROM composition_members
					WHERE composition_id = ? AND user_id = ?;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, compositionId);
            stmt.setInt(2, userId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retourne tous les membres d'une composition avec leur rôle
     * @param compositionId
     * @return map user -> role
     */
    public Map<User, String> findMembers(int compositionId) {
        Map<User, String> members = new HashMap<>();
        String sql = """
					SELECT cm.role, u.id as u_id, u.username, u.email, u.password,
					       u.created_at as u_created_at, u.updated_at as u_updated_at
					FROM composition_members cm
					JOIN users u ON cm.user_id = u.id
					WHERE cm.composition_id = ?;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, compositionId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getInt("u_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getObject("u_created_at", LocalDateTime.class),
                        rs.getObject("u_updated_at", LocalDateTime.class)
                );
                members.put(user, rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }

    /**
     * @return la liste de toutes les compositions
     */
    public List<Composition> findAll() {
        List<Composition> compositions = new ArrayList<>();
        String sql = """
                    SELECT c.*, u.id as u_id, u.username, u.email, u.password,
                           u.created_at as u_created_at, u.updated_at as u_updated_at
                    FROM compositions c
                    JOIN users u ON c.owner_id = u.id
                    ORDER BY c.id;
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
                User owner = new User(
                        rs.getInt("u_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getObject("u_created_at", LocalDateTime.class),
                        rs.getObject("u_updated_at", LocalDateTime.class)
                );
                compositions.add(new Composition(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("tempo"),
                        rs.getString("access_type"),
                        owner,
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return compositions;
    }

    /**
     * @return L'ensemble des compositions publiques
     */
    public  List<Composition> findPublic() {
        List<Composition> compositions = new ArrayList<>();
        String sql = """
					SELECT c.*, u.id as u_id, u.username, u.email, u.password,
					       u.created_at as u_created_at, u.updated_at as u_updated_at
					FROM compositions c
					JOIN users u ON c.owner_id = u.id
					WHERE c.access_type = 'public';
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
                User owner = new User(
                        rs.getInt("u_id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getObject("u_created_at", LocalDateTime.class),
                        rs.getObject("u_updated_at", LocalDateTime.class)
                );
                compositions.add(new Composition(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getInt("tempo"),
                        rs.getString("access_type"),
                        owner,
                        rs.getObject("created_at", LocalDateTime.class),
                        rs.getObject("updated_at", LocalDateTime.class)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return compositions;
    }
}
