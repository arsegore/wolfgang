package wolfgang.daos;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Composition;
import wolfgang.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CompositionDAO {

    /**
     * Insère une nouvelle composition dans la bdd
     * @param composition
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public static int create(Composition composition) {
        String sql = """
                INSERT INTO compositions (title, tempo, access_type, owner_id)
                VALUES (?, ?, ?, ?);
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, 
                        DatabaseConfig.DB_LOGIN, 
                        DatabaseConfig.DB_PASSWD
                    );
                PreparedStatement stmt = con.prepareStatement(
                    sql, 
                    Statement.RETURN_GENERATED_KEYS
                )
        ) {
            stmt.setString(1, composition.getTitle());
            stmt.setInt(2, composition.getTempo());
            stmt.setString(3, composition.getAccessType());
            stmt.setInt(4, composition.getOwner().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) return rs.getInt(1);
            else return -1;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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
                SET title = ?, description = ?, tempo = ?, access_type = ?,
                    public_editable = ?, owner_id = ?, updated_at = ?
                WHERE id = ?;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setString(1, composition.getTitle());
            stmt.setString(2, composition.getDescription());
            stmt.setInt(3, composition.getTempo());
            stmt.setString(4, composition.getAccessType());
            stmt.setBoolean(5, composition.isPublicEditable());
            stmt.setInt(6, composition.getOwner().getId());
            stmt.setObject(7, LocalDateTime.now());
            stmt.setInt(8, composition.getId());

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
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
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
        String sql = """
                SELECT c.*, u.id as u_id, u.username, u.email, u.password,
                       u.created_at as u_created_at, u.updated_at as u_updated_at
                FROM compositions c
                JOIN users u ON c.owner_id = u.id
                WHERE c.id = ?;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return buildComposition(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Compositions dont l'utilisateur est le propriétaire.
     */
    public List<Composition> findOwned(int userId) {
        List<Composition> compositions = new ArrayList<>();
        String sql = """
                SELECT c.*, u.id as u_id, u.username, u.email, u.password,
                       u.created_at as u_created_at, u.updated_at as u_updated_at
                FROM compositions c
                JOIN users u ON c.owner_id = u.id
                WHERE c.owner_id = ?
                ORDER BY c.id DESC;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) compositions.add(buildComposition(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compositions;
    }

    /**
     * Compositions où l'utilisateur est membre (non-propriétaire), avec son rôle.
     * @return Map composition => rôle ('editor' ou 'viewer')
     */
    public Map<Composition, String> findMemberships(int userId) {
        Map<Composition, String> memberships = new LinkedHashMap<>();
        String sql = """
                SELECT c.*, cm.role,
                       u.id as u_id, u.username, u.email, u.password,
                       u.created_at as u_created_at, u.updated_at as u_updated_at
                FROM composition_members cm
                JOIN compositions c ON cm.composition_id = c.id
                JOIN users u ON c.owner_id = u.id
                WHERE cm.user_id = ? AND c.owner_id != ?
                ORDER BY c.id DESC;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) memberships.put(buildComposition(rs), rs.getString("role"));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberships;
    }

    /**
     * @deprecated Utiliser findOwned() ou findMemberships() selon le cas.
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
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) compositions.add(buildComposition(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compositions;
    }

    public boolean addMember(int compositionId, int userId, String role) {
        String sql = """
                INSERT INTO composition_members (composition_id, user_id, role)
                VALUES (?, ?, ?)
                ON DUPLICATE KEY UPDATE role = VALUES(role);
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
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

    public boolean updateRole(int compositionId, int userId, String role) {
        String sql = """
                UPDATE composition_members
                SET role = ?
                WHERE composition_id = ? AND user_id = ?;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
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

    public boolean removeMember(int compositionId, int userId) {
        String sql = """
                DELETE FROM composition_members
                WHERE composition_id = ? AND user_id = ?;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            stmt.setInt(1, compositionId);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Map<User, String> findMembers(int compositionId) {
        Map<User, String> members = new LinkedHashMap<>();
        String sql = """
                SELECT cm.role, u.id as u_id, u.username, u.email, u.password,
                       u.created_at as u_created_at, u.updated_at as u_updated_at
                FROM composition_members cm
                JOIN users u ON cm.user_id = u.id
                WHERE cm.composition_id = ?
                ORDER BY u.username;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
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

    public List<Composition> findPublic() {
        List<Composition> compositions = new ArrayList<>();
        String sql = """
                SELECT c.*, u.id as u_id, u.username, u.email, u.password,
                       u.created_at as u_created_at, u.updated_at as u_updated_at
                FROM compositions c
                JOIN users u ON c.owner_id = u.id
                WHERE c.access_type = 'public'
                ORDER BY c.id DESC;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) compositions.add(buildComposition(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compositions;
    }

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
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) compositions.add(buildComposition(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compositions;
    }

    // --- helpers ---

    private Composition buildComposition(ResultSet rs) throws SQLException {
        User owner = new User(
                rs.getInt("u_id"),
                rs.getString("username"),
                rs.getString("email"),
                rs.getString("password"),
                rs.getObject("u_created_at", LocalDateTime.class),
                rs.getObject("u_updated_at", LocalDateTime.class)
        );
        Composition comp = new Composition(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getInt("tempo"),
                rs.getString("access_type"),
                owner,
                rs.getObject("created_at", LocalDateTime.class),
                rs.getObject("updated_at", LocalDateTime.class)
        );
        comp.setPublicEditable(rs.getBoolean("public_editable"));
        return comp;
    }
}
