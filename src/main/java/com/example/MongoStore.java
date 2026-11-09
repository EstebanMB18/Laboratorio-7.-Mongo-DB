package com.example;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/** MongoDB modern driver bridge for the older academic project. */
public final class MongoStore {
    private static final MongoClient CLIENT = MongoClients.create("mongodb://localhost:27017/?serverSelectionTimeoutMS=4000");
    private static final MongoDatabase DATABASE = CLIENT.getDatabase("ArquiSW");
    private MongoStore() { }
    public static MongoCollection<Document> competitors() { return DATABASE.getCollection("COMPETITOR"); }
    public static MongoCollection<Document> productos() { return DATABASE.getCollection("PRODUCTO"); }
    public static void ping() {
        DATABASE.runCommand(new Document("ping", 1));
        System.out.println("MongoDB moderno conectado: ArquiSW / localhost:27017");
    }
}
