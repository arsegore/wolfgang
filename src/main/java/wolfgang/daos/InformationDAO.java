package wolfgang.daos;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Information;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InformationDAO {

    /**
     * Insère une nouvelle actualité dans la bdd
     * @param i
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean create(Information i) {
        String sql = """
                INSERT INTO informations (title, created_at, description)
                VALUES (?, ?, ?);
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
            stmt.setString(1, i.getTitle());
            stmt.setObject(2, LocalDateTime.now());
            stmt.setString(3, i.getDescription());
            stmt.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime une actualité
     * @param id
     * @return vrai si la suppression a réussi, faux sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM informations WHERE id = ?;";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD
                );
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
     * @return La liste complete des actualités
     */
    public List<Information> findAll() {
        List<Information> infos = new ArrayList<>();
        String sql = """
                SELECT *
                FROM informations
                ORDER BY created_at DESC;
                """;

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL, DatabaseConfig.DB_LOGIN, DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql)
        ) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {

                infos.add(new Information(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getObject("created_at", LocalDateTime.class)
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return infos;
    }
}

