package wolfgang.repositories;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Composition;
import wolfgang.models.Instrument;
import wolfgang.models.Track;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TrackRepository {

    /**
     * Insère une nouvelle piste dans la bdd
     * @param track
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean create(Track track) {
        String sql = """
					INSERT INTO tracks (composition_id, name, instrument_id, color, position)
					VALUES (?, ?, ?, ?, ?);
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, track.getComposition().getId());
            stmt.setString(2, track.getName());
            stmt.setInt(3, track.getInstrument().getId());
            stmt.setString(4, track.getColor());
            stmt.setInt(5, track.getPosition());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour une piste
     * @param track
     * @return vrai si la mise à jour a réussi, faux sinon
     */
    public boolean update(Track track) {
        String sql = """
					UPDATE tracks
					SET name = ?, instrument_id = ?, color = ?, position = ?
					WHERE id = ?;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setString(1, track.getName());
            stmt.setInt(2, track.getInstrument().getId());
            stmt.setString(3, track.getColor());
            stmt.setInt(4, track.getPosition());
            stmt.setInt(5, track.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime une piste
     * @param id
     * @return vrai si la suppression a réussi, faux sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM tracks WHERE id = ?;";

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
     * @return La piste correspondante ou null
     */
    public Track findById(int id) {
        Track track = null;
        String sql = """
					SELECT t.*, i.id as i_id, i.name as i_name
					FROM tracks t
					JOIN instruments i ON t.instrument_id = i.id
					WHERE t.id = ?;
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
                track = hydrateTrack(rs, null);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return track;
    }

    /**
     * Retourne toutes les pistes d'une composition
     * @param compositionId
     * @return liste des pistes, ordonnées par position
     */
    public List<Track> findByComposition(int compositionId) {
        List<Track> tracks = new ArrayList<>();
        String sql = """
					SELECT t.*, i.id as i_id, i.name as i_name
					FROM tracks t
					JOIN instruments i ON t.instrument_id = i.id
					WHERE t.composition_id = ?
					ORDER BY t.position;
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
                tracks.add(hydrateTrack(rs, null));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tracks;
    }

    /**
     * Hydrate un objet Track depuis un ResultSet
     */
    private Track hydrateTrack(ResultSet rs, Composition composition) throws SQLException {
        Instrument instrument = new Instrument(
                rs.getInt("i_id"),
                rs.getString("i_name")
        );
        return new Track(
                rs.getInt("id"),
                composition,
                rs.getString("name"),
                instrument,
                rs.getString("color"),
                rs.getInt("position")
        );
    }
}
