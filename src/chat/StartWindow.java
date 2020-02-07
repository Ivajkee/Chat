package chat;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class StartWindow {
    public void startClient() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/chat/client/client.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Клиент");
        stage.setScene(new Scene(root, 800, 600));
        stage.getIcons().add(new Image(getClass().getResource("/chat/files/img/ico/clienttitle.png").toExternalForm()));
        stage.show();
    }

    public void startServer() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/chat/server/server.fxml"));
        Stage stage = new Stage();
        stage.setTitle("Сервер");
        stage.setScene(new Scene(root, 800, 600));
        stage.getIcons().add(new Image(getClass().getResource("/chat/files/img/ico/servertitle.png").toExternalForm()));
        stage.show();
    }
}