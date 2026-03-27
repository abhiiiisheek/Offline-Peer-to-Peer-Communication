package network;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import database.MongoDB;

public class ClientHandler implements Runnable {

    public static List<ClientHandler> clientHandlers = Collections.synchronizedList(new ArrayList<>());

    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine();

            saveUser(clientUsername);
            clientHandlers.add(this);

            sendOldMessages();

            broadcastMessage("SERVER: " + clientUsername + " has joined the chat!");

        } catch (IOException e) {
            closeEverything();
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                String cleanMessage = messageFromClient.substring(messageFromClient.indexOf(":") + 2);
                saveMessage(clientUsername, cleanMessage);
                broadcastMessage(clientUsername + ": " + cleanMessage);

            } catch (IOException e) {
                closeEverything();
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                clientHandler.bufferedWriter.write(messageToSend);
                clientHandler.bufferedWriter.newLine();
                clientHandler.bufferedWriter.flush();

            } catch (IOException e) {
                closeEverything();
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");
    }

    private void saveMessage(String sender, String message) {

        MongoCollection<Document> collection =
                MongoDB.getDatabase().getCollection("messages");

        Document doc = new Document()
                .append("sender", sender)
                .append("message", message)
                .append("timestamp", System.currentTimeMillis());

        collection.insertOne(doc);
    }

    private void saveUser(String username) {

        MongoCollection<Document> collection =
                MongoDB.getDatabase().getCollection("users");

        Document doc = new Document("username", username);

        collection.insertOne(doc);
    }

    private void sendOldMessages() {

        MongoCollection<Document> collection =
                MongoDB.getDatabase().getCollection("messages");

        for (Document doc : collection.find().limit(20)) {

            String sender = doc.getString("sender");
            String message = doc.getString("message");

            try {
                bufferedWriter.write(sender + ": " + message);
                bufferedWriter.newLine();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            bufferedWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeEverything() {
        removeClientHandler();
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}