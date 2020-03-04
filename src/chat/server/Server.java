package chat.server;

import chat.Connect;
import chat.Connectable;
import chat.utils.Msg;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.*;
import java.util.LinkedList;

public class Server implements Connectable {
    private ServerSocket serverSocket;
    private LinkedList<Connect> connectList;
    private ObservableList<String> clientListName;
    private volatile boolean serverReady;
    private int i;

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
        Thread.currentThread().setName("Server");
        try (BufferedReader myIP = new BufferedReader(new InputStreamReader(new URL("http://checkip.amazonaws.com").openStream()))) {
            String ip = myIP.readLine();
            ipField.setText(ip);
        } catch (Exception e) {
            try {
                ipField.setText(InetAddress.getLocalHost().getHostName());
            } catch (UnknownHostException uhe) {
                ipField.setText("хз");
            }
        }
        connectList = new LinkedList<>();
        clientListName = FXCollections.observableArrayList();
        clientListView.setItems(clientListName);
        clientListView.setPlaceholder(new ImageView(getClass().getResource("/files/img/bg/jdun.png").toExternalForm()));
    }

    @FXML
    public void connect() {
        connectButton.setDisable(true);
        disconnectButton.setDisable(false);

        try {
            serverSocket = new ServerSocket(portField.getValue());
            //serverSocket.setSoTimeout(3000);
        } catch (Exception e) {
            systemOutMessage("Ошибка создания сервера");
            disconnect();
            return;
        }
        serverReady = true;
        systemOutMessage("Сервер запущен");

        Thread serverThread = new Thread(() -> {
            while (serverReady) {
                try {
                    Socket socket = serverSocket.accept();
                    Connect connect = new Connect(socket, this);
                    connect.setDaemon(true);
                    connect.start();
                    connectList.add(connect);
                } catch (Exception e) {
                    disconnect();
                }
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    @FXML
    public void disconnect() {
        if (serverReady) {
            try {
                sendMessage(getTime() + "[" + nameField.getText() + "] " + "Сервер остановлен");
                serverSocket.close();
            } catch (Exception e) {
                systemOutMessage("Ошибка закрытия сервера");
            }
        }

        serverReady = false;
        connectList.forEach(Connect::disconnect);
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
    }

    @Override
    public synchronized void deleteConnect(Connect client) {
        Platform.runLater(() -> {
            connectList.remove(client);
            clientListName.remove(client.getNickName());
            paneListView.setText("Участники: " + connectList.size());
            sendMessage(Msg.ADD_ALL + String.join(",", clientListName));
            sendMessage(getTime() + " " + client.getNickName() + " вышел из чата");
        });
    }

    @FXML
    private void sendMessage() {
        if (serverReady && !outMessage.getText().trim().equals("")) {
            sendMessage(getTime() + "[" + nameField.getText() + "] " + outMessage.getText());
            outMessage.clear();
        }
    }

    @Override
    public synchronized void readMessage(Connect connect, String message) {
        if (message.contains(Msg.ADD_ONE)) {
            connect.setNickName(checkNickName(message.replace(Msg.ADD_ONE, "")));
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

    private String checkNickName(String nickName) {
        if (clientListName.contains(nickName) || nameField.getText().equals(nickName)) {
            return checkNickName(nickName + (++i));
        }
        return nickName;
    }

    @FXML
    public synchronized void sendMessage(String message) {
        if (message.contains(Msg.ADD_ALL)) {
            connectList.forEach(client -> client.sendMessage(message));
        } else {
            connectList.forEach(client -> client.sendMessage(message));
            inMessage.appendText(message + "\n");
        }
    }

    @Override
    public void systemOutMessage(String message) {
        inMessage.appendText(getTime() + " " + message + "\n");
    }

    @Override
    public void connectReady() {
        System.out.println(Thread.activeCount());
    }
}