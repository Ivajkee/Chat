package chat.server;

import chat.Connect;
import chat.Connectable;
import chat.utils.Language;
import chat.utils.Logger;
import chat.utils.Msg;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.LinkedList;

public class Server implements Connectable {
    private ServerSocket serverSocket;
    private LinkedList<Connect> connectList;
    private volatile boolean serverReady;
    private int i;

    public MenuBar menuBar;
    public CheckMenuItem saveMessage;
    public Label labelIp, labelPort, labelName;
    public TextField ipField, nameField;
    public TextArea inMessage, outMessage;
    public Spinner<Integer> portField;
    public TitledPane paneListView;
    public ListView<String> clientListView;
    public Button connectBtn, disconnectBtn, sendBtn;
    public ToggleGroup lang, theme, font, fontStyle, fontSize, fontColor;
    public ObservableList<String> clientListName;
    public String onlineStr;
    public Stage startWindow, stage;
    private PopupControl popupControl;

    public Server(Stage startWindow, Stage stage) {
        this.stage = stage;
        this.startWindow = startWindow;
        Thread.currentThread().setName("Server");
        onlineStr = "В сети: ";
    }

    @FXML
    private void initialize() {
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
        clientListView.setPlaceholder(new ImageView("/files/img/bg/jdun.png"));

        popupControl = new PopupControl();
        popupControl.setAutoHide(true);
        popupControl.setAutoFix(true);
        popupControl.setHideOnEscape(true);
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("/chat/fxml/about.fxml"));
        } catch (IOException e) {
            System.out.println("Не удалось загрузить файл");
        }
        assert root != null;
        root.getChildrenUnmodifiable().get(4).setOnMouseClicked(e -> goToURL("skype:live:lamerinho_1?call"));
        root.getChildrenUnmodifiable().get(5).setOnMouseClicked(e -> goToURL("https://vk.com/thx4game"));
        root.getChildrenUnmodifiable().get(6).setOnMouseClicked(e -> goToURL("mailto:lamerinho@ya.ru"));
        popupControl.getScene().setRoot(root);

        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> disconnect());
    }

    public void connectClick() {
        connect();
    }

    public void connect() {
        connectBtn.setDisable(true);
        disconnectBtn.setDisable(false);

        try {
            serverSocket = new ServerSocket(portField.getValue());
        } catch (Exception e) {
            systemOutMessage("Ошибка создания сервера");
            disconnect();
            return;
        }
        serverReady = true;
        sendMessage(getTime() + " Сервер запущен");

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

    public void disconnectClick() {
        disconnect();
    }

    public void disconnect() {
        if (serverReady) {
            try {
                sendMessage(getTime() + " Сервер остановлен");
                serverSocket.close();
            } catch (Exception e) {
                systemOutMessage("Ошибка закрытия сервера");
            }
        }

        serverReady = false;
        connectList.forEach(Connect::disconnect);
        connectBtn.setDisable(false);
        disconnectBtn.setDisable(true);
    }

    @Override
    public synchronized void deleteConnect(Connect client) {
        Platform.runLater(() -> {
            connectList.remove(client);
            clientListName.remove(client.getNickName());
            paneListView.setText(onlineStr + connectList.size());
            sendMessage(Msg.ADD_ALL + String.join(",", clientListName));
            sendMessage(getTime() + " " + client.getNickName() + " вышел из чата");
        });
    }

    @FXML
    private void sendMessage() {
        if (serverReady && !outMessage.getText().trim().equals("")) {
            sendMessage(getTime() + "[" + nameField.getText() + "] " + outMessage.getText().trim());
            outMessage.clear();
            outMessage.requestFocus();
        }
    }

    @Override
    public synchronized void readMessage(Connect connect, String message) {
        if (message.contains(Msg.ADD)) {
            connect.setNickName(checkNickName(message.replace(Msg.ADD, "")));
            Platform.runLater(() -> {
                clientListName.add(connect.getNickName());
                paneListView.setText(onlineStr + connectList.size());
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
            if (saveMessage.isSelected()) Logger.saveMessage(message);
        }
    }

    @Override
    public void systemOutMessage(String message) {
        inMessage.appendText(getTime() + " " + message + "\n");
    }

    @Override
    public void connectReady() {
        //System.out.println(Thread.activeCount());
    }

    public void menuAction(ActionEvent actionEvent) {
        MenuItem item = (MenuItem) actionEvent.getTarget();
        switch (item.getId()) {
            case "menuExit":
                disconnect();
                stage.close();
                break;
            case "menuDefault":
                setTheme("/files/css/default.css", 0);
                break;
            case "menuDark":
                setTheme("/files/css/dark.css", 4);
                break;
            case "menuRu":
                Language.setRussian(this);
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
            case "menuSystem":
            case "menuArial":
            case "menuArialBlack":
            case "menuComicSans":
            case "menuCourier":
            case "menuTahoma":
            case "menuTimes":
            case "menuVerdana":
            case "menuRegular":
            case "menuBold":
            case "menuItalic":
            case "menuBoldItalic":
            case "menuFont10":
            case "menuFont11":
            case "menuFont12":
            case "menuFont13":
            case "menuFont14":
            case "menuBlack":
            case "menuRed":
            case "menuGreen":
            case "menuBlue":
            case "menuWhite":
                setFontStyle();
                break;
            case "menuGitHub":
                goToURL("https://github.com/Ivajkee/Chat");
                break;
            case "menuAbout":
                showAbout();
                break;
        }
    }

    private void setTheme(String url, int index) {
        stage.getScene().setUserAgentStylesheet(url);
        stage.show();
        startWindow.getScene().setUserAgentStylesheet(url);
        startWindow.show();
        fontColor.selectToggle(fontColor.getToggles().get(index));
        setFontStyle();
    }

    private void setFontStyle() {
        inMessage.setStyle((String) font.getSelectedToggle().getUserData()
                + fontStyle.getSelectedToggle().getUserData()
                + fontSize.getSelectedToggle().getUserData()
                + fontColor.getSelectedToggle().getUserData());
    }

    private void showAbout() {
        popupControl.show(stage, stage.getX() + (stage.getWidth() / 2 - 150), stage.getY() + (stage.getHeight() / 2 - 150));
    }

    private void goToURL(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (Exception e) {
            System.out.println("Не удалось загрузить страницу");
        }
    }
}