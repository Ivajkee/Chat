package chat.client;

import chat.utils.Msg;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Client {
    private ObservableList<String> clientListName;
    private ClientConnect connect;

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
    public void initialize() {
        clientListName = FXCollections.observableArrayList();
        clientListView.setItems(clientListName);
        clientListView.setPlaceholder(new ImageView(getClass().getResource("/chat/files/img/bg/jdun.png").toExternalForm()));
    }

    @FXML
    private void connect() {
        connectButton.setDisable(true);
        disconnectButton.setDisable(false);
        connect = new ClientConnect(this, nameField.getText().trim(), ipField.getText().trim(), portField.getValue());
        connect.setDaemon(true);
        connect.start();
    }

    @FXML
    protected void disconnect() {
        clientListName.clear();
        paneListView.setText("Участники: " + clientListName.size());
        connectButton.setDisable(false);
        disconnectButton.setDisable(true);
        connect.disconnect();
    }

    protected void systemOutMessage(String message) {
        inMessage.appendText(getTime() + " " + message + "\n");
    }

    @FXML
    protected void sendMessage() {
        if (connect.isConnected() && !outMessage.getText().trim().equals("")) {
            connect.sendMessage(outMessage.getText());
            outMessage.clear();
        }
    }

    protected synchronized void readMessage(String message) {
        Platform.runLater(() -> {
            if (message != null && message.contains(Msg.ADD_ALL)) {
                clientListName.clear();
                clientListName.addAll(message.replace(Msg.ADD_ALL, "").split(","));
                paneListView.setText("Участники: " + clientListName.size());
            } else if (message != null) {
                inMessage.appendText(message + "\n");
            } else {
                System.out.println("Exit");
                disconnect();
            }
        });
    }

    private String getTime() {
        LocalTime ldt = LocalTime.now();
        return "[" + ldt.format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }
}