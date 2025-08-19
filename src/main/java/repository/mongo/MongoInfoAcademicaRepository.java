package repository.mongo;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;
import repository.InfoAcademicaRepository;
import utils.mongo.MongoConnection;

import java.util.HashMap;
import java.util.Map;

public class MongoInfoAcademicaRepository implements InfoAcademicaRepository {
    private MongoCollection<Document> col() {
        MongoCollection<Document> c = MongoConnection.getDatabase().getCollection("info_academica");
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
                .append("nivel", p.getOrDefault("nivel", ""))
                .append("sede", p.getOrDefault("sede", ""))
                .append("gradoAcademico", p.getOrDefault("gradoAcademico", ""))
                .append("periodoAdmision", p.getOrDefault("periodoAdmision", ""))
                .append("metodologia", p.getOrDefault("metodologia", ""))
                .append("jornada", p.getOrDefault("jornada", ""))
                .append("planDecision", p.getOrDefault("planDecision", ""))
                .append("gradoSeleccionado", p.getOrDefault("gradoSeleccionado", ""))
                .append("pais", p.getOrDefault("pais", ""))
                .append("gradoObtenido", p.getOrDefault("gradoObtenido", ""))
                .append("fechaGraduacion", p.getOrDefault("fechaGraduacion", ""));
        col().replaceOne(Filters.eq("email", email), doc, new ReplaceOptions().upsert(true));
        return true;
    }
}
