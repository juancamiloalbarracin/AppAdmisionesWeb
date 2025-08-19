package utils.mongo;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static MongoClient client;
    private static MongoDatabase database;

    public static synchronized MongoDatabase getDatabase() {
        if (database == null) {
            String uri = System.getenv().getOrDefault("MONGODB_URI", "mongodb://localhost:27017");
            String dbName = System.getenv().getOrDefault("MONGODB_DB", "admisiones");
            ConnectionString cs = new ConnectionString(uri);
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(cs)
                    .build();
            client = MongoClients.create(settings);
            database = client.getDatabase(dbName);
        }
        return database;
    }

    public static synchronized void close() {
        if (client != null) {
            client.close();
            client = null;
            database = null;
        }
    }
}
