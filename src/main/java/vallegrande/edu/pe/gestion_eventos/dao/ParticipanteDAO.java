package vallegrande.edu.pe.gestion_eventos.dao;

import vallegrande.edu.pe.gestion_eventos.model.Participante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParticipanteDAO {

    public List<Participante> listar() {
        List<Participante> lista = new ArrayList<>();
        String sql = "SELECT * FROM participantes ORDER BY id_participante DESC";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Participante p = new Participante();
                p.setIdParticipante(rs.getInt("id_participante"));
                p.setNombres(rs.getString("nombres"));
                p.setApellidos(rs.getString("apellidos"));
                p.setDni(rs.getString("dni"));
                p.setCorreo(rs.getString("correo"));
                p.setTelefono(rs.getString("telefono"));
                p.setInstitucion(rs.getString("institucion"));
                lista.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertar(Participante p) {
        String sql = "INSERT INTO participantes (nombres, apellidos, dni, correo, telefono, institucion) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());
            ps.setString(3, p.getDni());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getInstitucion());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean actualizar(Participante p) {
        String sql = "UPDATE participantes SET nombres=?, apellidos=?, dni=?, correo=?, telefono=?, institucion=? WHERE id_participante=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, p.getNombres());
            ps.setString(2, p.getApellidos());
            ps.setString(3, p.getDni());
            ps.setString(4, p.getCorreo());
            ps.setString(5, p.getTelefono());
            ps.setString(6, p.getInstitucion());
            ps.setInt(7, p.getIdParticipante());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminar(int idParticipante) {
        String sql = "DELETE FROM participantes WHERE id_participante=?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idParticipante);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean existeDni(String dni, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM participantes WHERE dni = ? AND id_participante <> ?";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, dni);
            ps.setInt(2, idExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Participante> buscar(String filtro) {
        List<Participante> lista = new ArrayList<>();
        String sql = "SELECT * FROM participantes WHERE nombres LIKE ? OR apellidos LIKE ? OR dni LIKE ? OR institucion LIKE ? ORDER BY id_participante DESC";

        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String criterio = "%" + filtro + "%";
            ps.setString(1, criterio);
            ps.setString(2, criterio);
            ps.setString(3, criterio);
            ps.setString(4, criterio);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Participante p = new Participante();
                    p.setIdParticipante(rs.getInt("id_participante"));
                    p.setNombres(rs.getString("nombres"));
                    p.setApellidos(rs.getString("apellidos"));
                    p.setDni(rs.getString("dni"));
                    p.setCorreo(rs.getString("correo"));
                    p.setTelefono(rs.getString("telefono"));
                    p.setInstitucion(rs.getString("institucion"));
                    lista.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
