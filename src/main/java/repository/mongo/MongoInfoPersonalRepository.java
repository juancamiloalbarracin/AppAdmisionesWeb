package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import repository.InfoPersonalRepository;
import utils.mongo.MongoConnection;

import java.util.HashMap;
import java.util.Map;

public class MongoInfoPersonalRepository implements InfoPersonalRepository {
    private MongoCollection<Document> col() {
        MongoCollection<Document> c = MongoConnection.getDatabase().getCollection("info_personal");
        // índice por email único
        c.createIndex(new Document("email", 1), new IndexOptions().unique(true));
        return c;
    }

    @Override
    public Map<String, String> getByEmail(String email) {
        Document d = col().find(Filters.eq("email", email)).first();
        Map<String, String> out = new HashMap<>();
        if (d != null) {
            d.forEach((k, v) -> { if (v != null) out.put(k, String.valueOf(v)); });
            out.remove("_id");
        }
        return out;
    }

    @Override
    public boolean saveFromApi(String email, Map<String, String> p) {
        Document doc = new Document("email", email)
                .append("nombres", p.getOrDefault("nombres", ""))
                .append("apellidos", p.getOrDefault("apellidos", ""))
                .append("fechaNacimiento", p.getOrDefault("fechaNacimiento", ""))
                .append("lugarNacimiento", p.getOrDefault("lugarNacimiento", ""))
                .append("tipoDocumento", p.getOrDefault("tipoDocumento", ""))
                .append("numeroDocumento", p.getOrDefault("numeroDocumento", ""))
                .append("genero", p.getOrDefault("genero", ""))
                .append("estadoCivil", p.getOrDefault("estadoCivil", ""))
                .append("direccion", p.getOrDefault("direccion", ""))
                .append("telefono", p.getOrDefault("telefono", ""))
                .append("celular", p.getOrDefault("celular", ""))
                .append("emailPersonal", p.getOrDefault("emailPersonal", ""));
        col().replaceOne(Filters.eq("email", email), doc, new ReplaceOptions().upsert(true));
        return true;
    }
}
