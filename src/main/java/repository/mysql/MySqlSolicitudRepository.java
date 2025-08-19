package repository.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import repository.SolicitudRepository;
import utils.DatabaseConnection;

/**
 * Implementación MySQL del repositorio de solicitudes/radicados.
 */
public class MySqlSolicitudRepository implements SolicitudRepository {

    @Override
    public boolean create(String userEmail, Map<String, Object> data) {
        DatabaseConnection db = new DatabaseConnection();
        try {
            String tipo = (String) data.getOrDefault("tipo_solicitud", null);
            String asunto = (String) data.getOrDefault("asunto", null);
            String descripcion = (String) data.getOrDefault("descripcion", null);
            String tel = (String) data.getOrDefault("telefono_contacto", null);
            String notify = (String) data.getOrDefault("email_notificacion", null);
            return db.insertRadicado(userEmail, tipo, asunto, descripcion, tel, notify);
        } finally {
            db.closeConnection();
        }
    }

    @Override
    public List<Map<String, Object>> listByUserEmail(String userEmail, int limit, int offset) {
        DatabaseConnection db = new DatabaseConnection();
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            Connection conn = db.getConnection();
            String sql = "SELECT id, tipo_solicitud, asunto, fecha_radicado FROM radicados WHERE email = ? ORDER BY fecha_radicado DESC LIMIT ? OFFSET ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, userEmail);
                ps.setInt(2, Math.max(1, limit));
                ps.setInt(3, Math.max(0, offset));
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("tipo_solicitud", rs.getString("tipo_solicitud"));
                    row.put("asunto", rs.getString("asunto"));
                    row.put("fecha_radicado", rs.getTimestamp("fecha_radicado"));
                    list.add(row);
                }
            }
        } catch (Exception e) {
            // log simple
            e.printStackTrace();
        } finally {
            db.closeConnection();
        }
        return list;
    }

    @Override
    public Map<String, Object> findByNumeroRadicado(String numero) {
        // No existe numero_radicado en tabla "radicados" actual. Se devuelve null.
        return null;
    }
}
