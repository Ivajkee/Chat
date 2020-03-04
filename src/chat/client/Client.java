package chat.client;

import chat.Connect;
import chat.Connectable;
import chat.utils.Language;
import chat.utils.Msg;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.net.Socket;

public class Client implements Connectable {
    public MenuBar menuBar;
    public Label labelIp, labelPort, labelName;
    public TextField ipField, nameField;
    public TextArea inMessage, outMessage;
    public Spinner<Integer> portField;
    public Button connectBtn, disconnectBtn, sendBtn;
    public ToggleGroup lang, theme;
    public Menu menuFile, menuSettings, menuTheme, menuLang, menuFont, menuReference;
    public MenuItem menuOpen, menuSave, menuExit, menuFamily, menuStyle, menuSize, menuColor, menuGitHub, menuAbout, menuAutor;
    public RadioMenuItem menuDefault, menuDark, menuRu, menuEn, menuDe, menuFr, menuEs, menuIt;
    public ObservableList<String> clientListName;
    private Connect connect;
    private Stage stage;

    public TitledPane paneListView;
    @FXML
    private ListView<String> clientListView;

    public Client(Stage stage) {
        this.stage = stage;
    }


    @FXML
    public void initialize() {
        Thread.currentThread().setName("Client");
        clientListName = FXCollections.observableArrayList();
        clientListView.setItems(clientListName);
        clientListView.setPlaceholder(new ImageView(getClass().getResource("/files/img/bg/jdun.png").toExternalForm()));
    }

    @FXML
    public void connect() {
        connectBtn.setDisable(true);
        disconnectBtn.setDisable(false);

        new Thread(() -> {
            try {
                connect = new Connect(new Socket(ipField.getText().trim(), portField.getValue()), this);
                connect.setNickName(nameField.getText().trim());
                connect.setDaemon(true);
                connect.start();
            } catch (Exception e) {
                systemOutMessage("Время ожидания вышло");
                disconnect();
            }
        }).start();
    }

    public void connectReady() {
        sendMessage(Msg.ADD_ONE + connect.getNickName());
    }

    @Override
    public void readMessage(Connect connect, String message) {
        Platform.runLater(() -> {
            if (message.contains(Msg.ADD_ALL)) {
                clientListName.clear();
                clientListName.addAll(message.replace(Msg.ADD_ALL, "").split(","));
                paneListView.setText(paneListView.getText() + clientListName.size());
            } else {
                inMessage.appendText(message + "\n");
            }
        });
    }

    @Override
    public synchronized void sendMessage(String message) {
        connect.sendMessage(message);
    }

    @FXML
    private void sendMessage() {
        if (connect != null && connect.isConnected() && !outMessage.getText().trim().equals("") && outMessage.getText().length() <= 200) {
            sendMessage(outMessage.getText());
            outMessage.clear();
            outMessage.requestFocus();
        }
    }

    @FXML
    public void disconnect() {
        if (connect != null && connect.isConnected()) {
            connect.disconnect();
            systemOutMessage("Соединение прервано");
        } else {
            clearState();
        }
    }

    private void clearState() {
        Platform.runLater(() -> {
            clientListName.clear();
            paneListView.setText(paneListView.getText() + clientListName.size());
            connectBtn.setDisable(false);
            disconnectBtn.setDisable(true);
        });
    }

    @Override
    public void deleteConnect(Connect client) {
        clearState();
    }

    @Override
    public synchronized void systemOutMessage(String message) {
        inMessage.appendText(getTime() + " " + message + "\n");
    }

    public void menuAction(ActionEvent actionEvent) {
        MenuItem item = (MenuItem) actionEvent.getTarget();
        switch (item.getId()) {
            case "menuOpen":
                System.out.println("Open");
                break;
            case "menuSave":
                System.out.println("Save");
                break;
            case "menuExit":
                disconnect();
                stage.close();
                break;
            case "menuDefault":
                System.out.println("Default");
                break;
            case "menuDark":
                System.out.println("Dark");
                break;
            case "menuRu":
                Language.setRussian(this);
                System.out.println(item.getId());
                break;
            case "menuEn":
                Language.setEnglish(this);
                break;
            case "menuDe":
                Language.setDeutsch(this);
                break;
            case "menuFr":
                Language.setFrench(this);
                break;
            case "menuEs":
                Language.setSpain(this);
                break;
            case "menuIt":
                Language.setItalian(this);
                break;
            case "menuFamily":
                System.out.println("menuFamily");
                break;
            case "menuStyle":
                System.out.println("menuStyle");
                break;
            case "menuSize":
                System.out.println("menuSize");
                break;
            case "menuGitHub":
                System.out.println("menuGitHub");
                break;
            case "menuAbout":
                System.out.println("menuAbout");
                break;
            case "menuAutor":
                System.out.println("menuAutor");
                break;
        }
    }
}