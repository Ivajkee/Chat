package chat;

import chat.client.Client;
import chat.server.Server;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StartWindow {
    private Stage startWindow;

    public StartWindow(Stage stage) {
        startWindow = stage;
    }

    public void startClient() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat/fxml/client.fxml"));
        loader.setControllerFactory(param -> new Client(startWindow, stage));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root, 800, 600);
        scene.setUserAgentStylesheet("/files/css/default.css");
        stage.setTitle("Клиент");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/files/img/ico/clienttitle.png"));
        stage.show();
    }

    public void startServer() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat/fxml/server.fxml"));
        loader.setControllerFactory(param -> new Server(startWindow, stage));
        loader.load();
        Parent root = loader.getRoot();
        Scene scene = new Scene(root, 800, 600);
        scene.setUserAgentStylesheet("/files/css/default.css");
        stage.setTitle("Сервер");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/files/img/ico/servertitle.png"));
        stage.show();
    }
}