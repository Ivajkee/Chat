package chat;

import chat.client.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StartWindow {
    public void startClient() throws IOException {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat/client/client.fxml"));
        loader.setControllerFactory(param -> new Client(stage));
        loader.load();
        Parent root = loader.getRoot();
        stage.setTitle("Клиент");
        stage.setScene(new Scene(root, 800, 600));
        stage.getIcons().add(new Image(getClass().getResource("/files/img/ico/clienttitle.png").toExternalForm()));
        stage.show();
    }

    public void startServer() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/chat/server/server.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Сервер");
        stage.setScene(new Scene(root, 800, 600));
        stage.getIcons().add(new Image(getClass().getResource("/files/img/ico/servertitle.png").toExternalForm()));
        stage.show();
    }
}