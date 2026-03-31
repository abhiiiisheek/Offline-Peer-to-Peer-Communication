package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void connect() {
        try {
            System.out.println("Connecting to MongoDB...");

            mongoClient = MongoClients.create("mongodb://127.0.0.1:27017");

            database = mongoClient.getDatabase("chat_app");

            database.listCollectionNames().first();

            System.out.println("Connected to MongoDB");

        } catch (Exception e) {
            System.out.println("MongoDB connection failed");
            e.printStackTrace();
        }
    }

    public static MongoDatabase getDatabase() {
        return database;
    }
}
