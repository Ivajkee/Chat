package chat.server;

import chat.utils.Msg;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Server {
    private ServerSocket serverSocket;
    private LinkedList<ServerConnect> connectList;
    private ObservableList<String> clientListName;
    private volatile boolean serverReady;
    private AtomicInteger atomicInt = new AtomicInteger();

    @FXML
    private TextField ipField;
    @FXML
    private Spinner<Integer> portField;
    @FXML
    private TextField nameField;
    @FXML
    private Button connectButton;
    @FXML
    private Button disconnectButton;
    @FXML
    private TextArea inMessage;
    @FXML
    private TextArea outMessage;
    @FXML
    private TitledPane paneListView;
    @FXML
    private ListView<String> clientListView;

    @FXML
    private void initialize() {
        try (BufferedReader myIP = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))) {
            String ip = myIP.readLine();
            ipField.setText(ip);
        } catch (IOException ioe) {
            try {
                ipField.setText(InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException uhe) {
                ipField.setText("хз");
            }
        }
        connectList = new LinkedList<>();
        clientListName = FXCollections.observableArrayList();
        clientListView.setItems(clientListName);
        clientListView.setPlaceholder(new ImageView(getClass().getResource("/chat/files/img/bg/jdun.png").toExternalForm()));
    }

    @FXML
    private void connect() {
        connectButton.setDisable(true);
        disconnectButton.setDisable(false);

        try {
            serverSocket = new ServerSocket(portField.getValue());
        } catch (Exception e) {
            systemOutMessage("Ошибка создания сервера");
            disconnect();
            return;
        }
        serverReady = true;
        systemOutMessage("Сервер запущен");

        Thread serverThread = new Thread(() -> {
            while (serverSocket != null && serverReady) {
                try {
                    Socket connectSocket = serverSocket.accept();
                    ServerConnect connect = new ServerConnect(connectSocket, this);
                    connectList.add(connect);
                    connect.setDaemon(true);
                    connect.start();
                } catch (IOException e) {
                    disconnect();
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @FXML
    protected void disconnect() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                sendMessage(getTime() + "[" + nameField.getText() + "] " + "Сервер остановлен");
                serverSocket.close();
            } catch (IOException e) {
                systemOutMessage("Ошибка закрытия сервера");
            }
        }
        serverReady = false;
        connectList.forEach(ServerConnect::disconnect);
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
    }

    @FXML
    private void sendMessage() {
        sendMessage(getTime() + "[" + nameField.getText() + "] " + outMessage.getText());
        outMessage.clear();
    }

    protected synchronized void deleteClient(ServerConnect connect) {
        Platform.runLater(() -> {
            connectList.remove(connect);
            clientListName.remove(connect.getNickName());
            paneListView.setText("Участники: " + connectList.size());
            sendMessage(Msg.ADD_ALL + String.join(",", clientListName));
            sendMessage(getTime() + " " + connect.getNickName() + " вышел из чата");
        });
    }

    protected synchronized void readMessage(ServerConnect connect, String message) {
        if (message.contains(Msg.ADD_ONE)) {
            if (clientListName.stream().noneMatch(name -> name.toLowerCase().equals(message.replace(Msg.ADD_ONE, "").toLowerCase()))
                    && !message.replace(Msg.ADD_ONE, "").toLowerCase().equals(nameField.getText().toLowerCase())) {
                connect.setNickName(message.replace(Msg.ADD_ONE, ""));
            } else {
                connect.setNickName(message.replace(Msg.ADD_ONE, "") + atomicInt.incrementAndGet());
            }
            Platform.runLater(() -> {
                clientListName.add(connect.getNickName());
                paneListView.setText("Участники: " + connectList.size());
                sendMessage(Msg.ADD_ALL + String.join(",", clientListName));
                sendMessage(getTime() + " " + connect.getNickName() + " вошел в чат");
            });
        } else {
            sendMessage(getTime() + "[" + connect.getNickName() + "] " + message);
        }
    }

    @FXML
    private synchronized void sendMessage(String message) {
        if (message.contains(Msg.ADD_ALL)) {
            connectList.forEach(client -> client.sendMessage(message));
        } else {
            connectList.forEach(client -> client.sendMessage(message));
            inMessage.appendText(message + "\n");
        }
    }

    protected void systemOutMessage(String message) {
        inMessage.appendText(getTime() + " " + message + "\n");
    }

    private String getTime() {
        LocalTime ldt = LocalTime.now();
        return "[" + ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }
}