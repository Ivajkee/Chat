package chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //setUserAgentStylesheet(getClass().getResource("/chat/files/css/style.css").toExternalForm());
        Parent root = FXMLLoader.load(getClass().getResource("/chat/fxml/startWindow.fxml"));
        primaryStage.setTitle("Чат");
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.getIcons().add(new Image(getClass().getResource("/files/img/ico/chat.png").toExternalForm()));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}