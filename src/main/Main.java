package main;

import network.Client;
import network.Server;
import ui.ChatWindow;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import database.MongoDB;

public class Main {

    public static void main(String[] args) {

        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "error");

        MongoDB.connect();

        Scanner sc = new Scanner(System.in);

        System.out.println("1. Start Server");
        System.out.println("2. Start Client");
        System.out.print("Enter choice: ");

        int choice = sc.nextInt();
        sc.nextLine();

        try {
            if (choice == 1) {
                ServerSocket serverSocket = new ServerSocket(5000);
                Server server = new Server(serverSocket);
                server.startServer();

            } else if (choice == 2) {

                System.out.print("Enter username: ");
                String username = sc.nextLine();

                System.out.print("Enter server IP: ");
                String ip = sc.nextLine();

                Socket socket = new Socket(ip, 5000);
                Client client = new Client(socket, username);

                ChatWindow chatWindow = new ChatWindow(client);
                client.setChatWindow(chatWindow);
                client.listenForMessage();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}