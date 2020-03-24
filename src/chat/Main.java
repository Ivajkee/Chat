package chat;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/chat/fxml/startWindow.fxml"));
        loader.setControllerFactory(param -> new StartWindow(stage));
        loader.load();
        Parent root = loader.getRoot();
        stage.setTitle("Чат");
        Scene scene = new Scene(root, 800, 600);
        scene.setUserAgentStylesheet("/files/css/default.css");
        stage.setScene(scene);
        stage.getIcons().add(new Image("/files/img/ico/chat.png"));
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}