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
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.net.URI;

public class Client implements Connectable {
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
    public final Stage startWindow;
    public final Stage stage;
    private Connect connect;
    private PopupControl popupControl;

    public Client(Stage startWindow, Stage stage) {
        this.stage = stage;
        this.startWindow = startWindow;
        Thread.currentThread().setName("Client");
        onlineStr = "В сети: ";
    }

    public void initialize() {
        clientListName = FXCollections.observableArrayList();
        clientListView.setItems(clientListName);
        clientListView.setPlaceholder(new ImageView("\\files\\img\\bg\\jdun.png"));
        stage.addEventHandler(WindowEvent.WINDOW_CLOSE_REQUEST, event -> disconnect());
        outMessage.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
        createAbout();
    }

    public void connectClick() {
        outMessage.requestFocus();
        connect();
    }

    @Override
    public void connect() {
        disconnectBtn.setDisable(false);
        disableControls(true);

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

    @Override
    public void connectReady() {
        sendMessage(Msg.ADD + connect.getNickName());
    }

    @Override
    public void readMessage(Connect connect, String message) {
        Platform.runLater(() -> {
            if (message.contains(Msg.ADD_ALL)) {
                clientListName.clear();
                clientListName.addAll(message.replace(Msg.ADD_ALL, "").split(","));
                paneListView.setText(onlineStr + clientListName.size());
            } else {
                inMessage.appendText(message + "\n");
                //if (saveMessage.isSelected()) Logger.saveMessage(message);
            }
        });
    }

    @Override
    public synchronized void sendMessage(String message) {
        connect.sendMessage(message);
    }

    @FXML
    private void sendMessage() {
        if (connect != null && connect.isConnected() && !outMessage.getText().trim().equals("") && outMessage.getText().trim().length() <= 200) {
            sendMessage(outMessage.getText().trim());
            outMessage.clear();
            outMessage.requestFocus();
        }
    }

    public void disconnectClick() {
        disconnect();
    }

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
            paneListView.setText(onlineStr + clientListName.size());
            disconnectBtn.setDisable(true);
            disableControls(false);
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

    private void createAbout() {
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

    private void disableControls(boolean disabled) {
        labelIp.setDisable(disabled);
        labelPort.setDisable(disabled);
        labelName.setDisable(disabled);
        ipField.setDisable(disabled);
        portField.setDisable(disabled);
        nameField.setDisable(disabled);
        connectBtn.setDisable(disabled);
    }
}