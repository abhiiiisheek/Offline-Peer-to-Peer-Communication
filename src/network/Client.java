package network;

import ui.ChatWindow;

import java.io.*;
import java.net.Socket;

public class Client {

    private Socket socket;
    private BufferedWriter bufferedWriter;
    private BufferedReader bufferedReader;
    private String username;

    private ChatWindow chatWindow;

    public Client(Socket socket, String username) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;

            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (IOException e) {
            closeEverything();
        }
    }

    public void setChatWindow(ChatWindow chatWindow) {
        this.chatWindow = chatWindow;
    }

    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGroupChat;

            while (socket.isConnected()) {
                try {
                    msgFromGroupChat = bufferedReader.readLine();

                    if (msgFromGroupChat == null) break;

                    String finalMsg = msgFromGroupChat;
                    if (finalMsg.startsWith(username + ":")) {
                        continue;
                    }

                    if (chatWindow != null) {

                        javax.swing.SwingUtilities.invokeLater(() -> {
                            chatWindow.displayMessage(finalMsg);
                            chatWindow.updateSuggestions(finalMsg);
                        });

                    } else {
                        System.out.println(finalMsg);
                    }

                } catch (IOException e) {
                    closeEverything();
                    break;
                }
            }
        }).start();
    }

    public void sendDirectMessage(String message) {
        try {
            bufferedWriter.write(username + ": " + message);
            bufferedWriter.newLine();
            bufferedWriter.flush();

        } catch (Exception e) {
            closeEverything();
        }
    }

    public void closeEverything() {
        try {
            if (bufferedReader != null) bufferedReader.close();
            if (bufferedWriter != null) bufferedWriter.close();
            if (socket != null) socket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}