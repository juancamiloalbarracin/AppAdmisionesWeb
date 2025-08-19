package repository.mongo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Sorts;

import repository.SolicitudRepository;
import utils.mongo.MongoConnection;

public class MongoSolicitudRepository implements SolicitudRepository {

    private MongoCollection<Document> collection() {
        MongoDatabase db = MongoConnection.getDatabase();
        MongoCollection<Document> col = db.getCollection("solicitudes");
        // Ensure useful indexes
        try {
            col.createIndex(Indexes.ascending("email"));
            col.createIndex(Indexes.descending("fecha_radicado"));
            col.createIndex(Indexes.ascending("numero_radicado"), new IndexOptions().unique(false));
        } catch (Exception ignored) {
            // index may already exist
        }
        return col;
    }

    @Override
    public boolean create(String userEmail, Map<String, Object> data) {
        Document doc = new Document();
        doc.put("email", userEmail);
        putIfPresent(doc, "tipo_solicitud", data.get("tipo_solicitud"));
        putIfPresent(doc, "asunto", data.get("asunto"));
        putIfPresent(doc, "descripcion", data.get("descripcion"));
        putIfPresent(doc, "telefono_contacto", data.get("telefono_contacto"));
        putIfPresent(doc, "email_notificacion", data.get("email_notificacion"));
        // numero_radicado optional but helpful if servlet provided it
        putIfPresent(doc, "numero_radicado", data.get("numero_radicado"));
        doc.put("fecha_radicado", new Date());

        collection().insertOne(doc);
        return true;
    }

    @Override
    public List<Map<String, Object>> listByUserEmail(String userEmail, int limit, int offset) {
        List<Map<String, Object>> list = new ArrayList<>();
        Bson filter = Filters.eq("email", userEmail);
        for (Document d : collection().find(filter)
                .sort(Sorts.descending("fecha_radicado"))
                .skip(Math.max(0, offset))
                .limit(Math.max(1, limit))) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", d.getObjectId("_id").toHexString());
            row.put("tipo_solicitud", d.getString("tipo_solicitud"));
            row.put("asunto", d.getString("asunto"));
            row.put("descripcion", d.getString("descripcion"));
            row.put("telefono_contacto", d.getString("telefono_contacto"));
            row.put("email_notificacion", d.getString("email_notificacion"));
            row.put("fecha_radicado", d.getDate("fecha_radicado"));
            row.put("numero_radicado", d.getString("numero_radicado"));
            list.add(row);
        }
        return list;
    }

    @Override
    public Map<String, Object> findByNumeroRadicado(String numero) {
        Document d = collection().find(Filters.eq("numero_radicado", numero)).first();
        if (d == null) return null;
        Map<String, Object> row = new HashMap<>();
        row.put("id", d.getObjectId("_id").toHexString());
        row.put("tipo_solicitud", d.getString("tipo_solicitud"));
        row.put("asunto", d.getString("asunto"));
        row.put("descripcion", d.getString("descripcion"));
        row.put("telefono_contacto", d.getString("telefono_contacto"));
        row.put("email_notificacion", d.getString("email_notificacion"));
        row.put("fecha_radicado", d.getDate("fecha_radicado"));
        row.put("numero_radicado", d.getString("numero_radicado"));
        row.put("email", d.getString("email"));
        return row;
    }

    private void putIfPresent(Document doc, String key, Object val) {
        if (val != null) {
            doc.put(key, val);
        }
    }
}
