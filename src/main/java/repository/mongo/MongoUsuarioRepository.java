package repository.mongo;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Indexes.ascending;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import models.Usuario;
import repository.UsuarioRepository;
import utils.mongo.MongoConnection;

/**
 * Implementación Mongo del repositorio de usuarios (Fase 2).
 * Nota: Por compatibilidad, se preserva password en texto como en MySQL actual.
 */
public class MongoUsuarioRepository implements UsuarioRepository {

    private MongoCollection<Document> col() {
        MongoDatabase db = MongoConnection.getDatabase();
        MongoCollection<Document> c = db.getCollection("usuarios");
        // Asegurar índice en email (idempotente)
        try { c.createIndex(ascending("email")); } catch (Exception ignore) {}
        return c;
    }

    @Override
    public boolean validateLogin(String email, String password) {
        Document doc = col().find(eq("email", email)).first();
        if (doc == null) return false;
        String stored = doc.getString("password");
        return password != null && password.equals(stored);
    }

    @Override
    public Usuario findByEmail(String email) {
        Document d = col().find(eq("email", email)).first();
        if (d == null) return null;
        Usuario u = new Usuario();
        u.setEmail(d.getString("email"));
        u.setNombre(d.getString("nombre"));
        u.setApellido(d.getString("apellido"));
        u.setUsername(d.getString("username"));
        u.setPassword(d.getString("password"));
        return u;
    }

    @Override
    public String create(Usuario usuario) {
        try {
            if (col().find(eq("email", usuario.getEmail())).first() != null) {
                return "ERROR_EMAIL_DUPLICADO";
            }
            Document d = new Document()
                .append("nombre", usuario.getNombre())
                .append("apellido", usuario.getApellido())
                .append("username", usuario.getUsername())
                .append("password", usuario.getPassword())
                .append("email", usuario.getEmail());
            col().insertOne(d);
            return "EXITO";
        } catch (Exception e) {
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("duplicate")) return "ERROR_DATOS_DUPLICADOS";
            return "ERROR_BD";
        }
    }
}
