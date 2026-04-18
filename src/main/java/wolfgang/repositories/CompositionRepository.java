package wolfgang.repositories;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Composition;
import wolfgang.models.User;

import java.sql.*;
import java.time.LocalDateTime;

public class CompositionRepository {

    /**
     * Insére une nouvelle composition dans la bdd
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
                        DatabaseConfig.DB_PASSWD)
                ;
                PreparedStatement stmt = con.prepareStatement(sql);
        ){
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
     */
    public void update(Composition composition) {
        String sql = """
                UPDATE compositions
                SET title = ?, description = ?, tempo = ?, access_type = ?, owner_id = ?, updated_at = ?
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
            stmt.setString(1, composition.getTitle());
            stmt.setString(2, composition.getDescription());
            stmt.setInt(3, composition.getTempo());
            stmt.setString(4, composition.getAccessType());
            stmt.setInt(5, composition.getOwner().getId());
            stmt.setObject(6, LocalDateTime.now());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param id
     * @return La composition correspondante ou null
     */
    public Composition findById(int id) {
        Composition composition = null;
        String sql = """
					SELECT *
					FROM compositions
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
                UserRepository userRepository = new UserRepository();

                composition = new Composition(
                        resultSet.getInt("id"),
                        resultSet.getString("title"),
                        resultSet.getString("description"),
                        resultSet.getInt("tempo"),
                        resultSet.getString("access_type"),
                        userRepository.findById(resultSet.getInt("owner_id")),
                        resultSet.getObject("created_at", LocalDateTime.class),
                        resultSet.getObject("updated_at", LocalDateTime.class)
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return composition;
    }
}
