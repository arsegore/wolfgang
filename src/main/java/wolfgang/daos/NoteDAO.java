package wolfgang.daos;

import wolfgang.config.DatabaseConfig;
import wolfgang.models.Note;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NoteDAO {

    /**
     * Insère une nouvelle note dans la bdd
     * @param note
     * @return vrai si l'insertion a réussi, faux sinon
     */
    public boolean create(Note note) {
        String sql = """
					INSERT INTO notes (track_id, pitch, start_beat, duration, velocity)
					VALUES (?, ?, ?, ?, ?);
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, note.getTrack().getId());
            stmt.setInt(2, note.getPitch());
            stmt.setFloat(3, note.getStartBeat());
            stmt.setFloat(4, note.getDuration());
            stmt.setInt(5, note.getVelocity());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Met à jour une note
     * @param note
     * @return vrai si la mise à jour a réussi, faux sinon
     */
    public boolean update(Note note) {
        String sql = """
					UPDATE notes
					SET pitch = ?, start_beat = ?, duration = ?, velocity = ?
					WHERE id = ?;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, note.getPitch());
            stmt.setFloat(2, note.getStartBeat());
            stmt.setFloat(3, note.getDuration());
            stmt.setInt(4, note.getVelocity());
            stmt.setInt(5, note.getId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Supprime une note
     * @param id
     * @return vrai si la suppression a réussi, faux sinon
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM notes WHERE id = ?;";

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
     * Supprime toutes les notes d'une piste
     * @param trackId
     * @return vrai si la suppression a réussi, faux sinon
     */
    public boolean deleteByTrack(int trackId) {
        String sql = "DELETE FROM notes WHERE track_id = ?;";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, trackId);
            return stmt.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retourne toutes les notes d'une piste
     * @param trackId
     * @return liste des notes, ordonnées par position
     */
    public List<Note> findByTrack(int trackId) {
        List<Note> notes = new ArrayList<>();
        String sql = """
					SELECT *
					FROM notes
					WHERE track_id = ?
					ORDER BY start_beat;
					""";

        try (
                Connection con = DriverManager.getConnection(
                        DatabaseConfig.DB_URL,
                        DatabaseConfig.DB_LOGIN,
                        DatabaseConfig.DB_PASSWD);
                PreparedStatement stmt = con.prepareStatement(sql);
        ) {
            stmt.setInt(1, trackId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                notes.add(new Note(
                        rs.getInt("id"),
                        null, // track non hydratée volontairement pour éviter une jointure inutile
                        rs.getInt("pitch"),
                        rs.getFloat("start_beat"),
                        rs.getFloat("duration"),
                        rs.getInt("velocity")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return notes;
    }
}
