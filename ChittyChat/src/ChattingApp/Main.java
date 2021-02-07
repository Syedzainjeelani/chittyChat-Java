package ChattingApp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UIViews/signUp.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("Chitty Chat");
        scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setWidth(781);
        primaryStage.setHeight(634);
        primaryStage.setResizable(false);
        primaryStage.setAlwaysOnTop(true);
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    public static Scene getScene() {
        return scene;
    }


}
