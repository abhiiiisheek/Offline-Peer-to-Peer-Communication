package ui;

import ml.python.PythonPredictor;
import network.Client;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ChatWindow {

    private JFrame frame;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JPanel suggestionPanel;

    private Client client;
    private PythonPredictor predictor;

    private boolean hasStartedChat = false;

    public ChatWindow(Client client) {

        this.client = client;
        predictor = new PythonPredictor();

        frame = new JFrame("💬 Chat App");
        frame.setSize(450, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(230, 230, 230));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(null);
        frame.add(scrollPane, BorderLayout.CENTER);

        suggestionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        suggestionPanel.setBackground(Color.WHITE);
        frame.add(suggestionPanel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inputPanel.add(inputField, BorderLayout.CENTER);

        JButton sendBtn = new JButton("➤");
        sendBtn.setBackground(new Color(0, 132, 255));
        sendBtn.setForeground(Color.WHITE);
        sendBtn.setFocusPainted(false);
        sendBtn.addActionListener(e -> sendMessage(inputField.getText()));

        inputPanel.add(sendBtn, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        addListeners();
        showDefaultSuggestions();

        frame.setVisible(true);
    }

    private void addListeners() {
        inputField.addActionListener(e -> sendMessage(inputField.getText()));
    }

    private void sendMessage(String message) {
        if (message == null || message.trim().isEmpty()) return;

        hasStartedChat = true;

        client.sendDirectMessage(message);
        addMessage(message, true);

        inputField.setText("");
    }

    private void addMessage(String message, boolean isMe) {

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);

        JPanel bubble = new JPanel();
        bubble.setLayout(new BorderLayout());
        bubble.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel text = new JLabel("<html>" + message + "</html>");
        text.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        bubble.add(text);

        if (isMe) {
            bubble.setBackground(new Color(0, 132, 255));
            text.setForeground(Color.WHITE);
            wrapper.add(bubble, BorderLayout.EAST);
        } else {
            bubble.setBackground(Color.WHITE);
            wrapper.add(bubble, BorderLayout.WEST);
        }

        bubble.setOpaque(true);
        bubble.setBorder(BorderFactory.createLineBorder(new Color(200,200,200), 1, true));

        wrapper.setBorder(new EmptyBorder(5, 10, 5, 10));

        chatPanel.add(wrapper);
        chatPanel.revalidate();

        SwingUtilities.invokeLater(() ->
                scrollPane.getVerticalScrollBar().setValue(
                        scrollPane.getVerticalScrollBar().getMaximum()));
    }

    private void showDefaultSuggestions() {

        suggestionPanel.removeAll();

        String[] defaults = {"Hello", "Hi", "How are you?"};

        for (String text : defaults) {
            JButton btn = new JButton(text);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(240,240,240));
            btn.addActionListener(e -> sendMessage(text));
            suggestionPanel.add(btn);
        }

        suggestionPanel.revalidate();
    }

    public void updateSuggestions(String message) {

        if (!hasStartedChat) return;

        suggestionPanel.removeAll();
        suggestionPanel.add(new JLabel("Thinking..."));

        new Thread(() -> {

            List<String> replies = predictor.getReplies(message);

            SwingUtilities.invokeLater(() -> {

                suggestionPanel.removeAll();

                for (String reply : replies) {
                    JButton btn = new JButton(reply);
                    btn.addActionListener(e -> sendMessage(reply));
                    suggestionPanel.add(btn);
                }

                suggestionPanel.revalidate();
                suggestionPanel.repaint();
            });

        }).start();
    }

    public void displayMessage(String message) {
        addMessage(message, false);
    }
}