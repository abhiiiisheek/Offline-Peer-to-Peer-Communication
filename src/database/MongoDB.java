package database;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDB {

    private static MongoClient mongoClient;
    private static MongoDatabase database;

    public static void connect() {

        mongoClient = MongoClients.create("mongodb+srv://shashinegi597_db_user:Mohitnegi1234@cluster0.os2p4dx.mongodb.net/?appName=Cluster0");
        database = mongoClient.getDatabase("chat_app");

        System.out.println("✅ Connected to MongoDB");
    }

    public static MongoDatabase getDatabase() {
        return database;
    }
}