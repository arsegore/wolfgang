package wolfgang.daos;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Instrument;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InstrumentDAO {

    /**
     * @param id
     * @return L'instrument correspondant ou null
     */
    public Instrument findById(int id) {
        Instrument instrument = null;
        String sql = """
					SELECT *
					FROM instruments
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
                instrument = new Instrument(
                        rs.getInt("id"),
                        rs.getString("name")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instrument;
    }

    /**
     * @return La liste de tous les instruments
     */
    public List<Instrument> findAll() {
        List<Instrument> instruments = new ArrayList<>();
        String sql = "SELECT * FROM instruments;";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                instruments.add(new Instrument(
                        rs.getInt("id"),
                        rs.getString("name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return instruments;
    }
}
