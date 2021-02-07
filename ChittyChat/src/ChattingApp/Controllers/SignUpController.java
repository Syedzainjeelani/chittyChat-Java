package ChattingApp.Controllers;

import ChattingApp.OtherClasses.DBConnectivity;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SignUpController {

    //All sign up fields
    private List<String> userNames;
    private List<String> passwords;
    private DBConnectivity database;

    //fxml fields
    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    @FXML
    private TextField confirm;
    //Caution labels
    @FXML
    private Label userCaution;
    @FXML
    private Label passCaution;
    //Button
    @FXML
    private Button signUpButton;
    //Loading
    @FXML
    private ImageView loading;

    //// Root pane for resizing...
    @FXML
    private Pane rootPane;
    @FXML
    private AnchorPane bgAnchorPane;
    @FXML
    private AnchorPane bgAnchorPaneBlack;
    @FXML
    private AnchorPane signupAnchorPane;

    public void initialize() {
        userNames = new ArrayList<>();
        passwords = new ArrayList<>();
        database = new DBConnectivity();
        //data will be read only in the beginning when loading sign up controller means the application.
        database.readUserData(userNames, passwords);
        userName.clear();
        password.clear();
        confirm.clear();
        signUpButton.setDisable(true);

    }

    // action and show methods.
    @FXML
    void signUp(ActionEvent event) throws Exception {
        String user, pass, conf;
        boolean noCaution = true;
        user = userName.getText();
        pass = password.getText();
        conf = this.confirm.getText();
        if (!user.matches(".*\\d+.*")) {   //only numbers are not allowed regex
            if (!userNames.contains(user)) {  // if user doesn't already exists.
                userCaution.setText("");
                userNames.add(user);          // individually adding data to array list
            } else {
                userCaution.setTextFill(Color.RED);
                userCaution.setText("Username already exists");
                noCaution = false;
            }
        } else {
            userCaution.setTextFill(Color.RED);
            userCaution.setText("Numbers are not allowed");
            noCaution = false;
        }
        if (pass.equals(conf)) {
            passCaution.setText("");
            passwords.add(pass);
        } else {
            passCaution.setTextFill(Color.RED);
            passCaution.setText("Password does not match");
            noCaution = false;
        }

        if (noCaution) {
            database.insertUserData(user, pass);             // adding data to database too ..
            passCaution.setTextFill(Color.WHITE);
            userCaution.setText("Sign Up successful");
            userName.setEditable(false);
            password.setEditable(false);
            confirm.setEditable(false);
            loading.setVisible(true);
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    ie.getStackTrace();
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            loginStage(event);
                            loading.setVisible(false);
                            userCaution.setText("");
                            signUpButton.setDisable(true);
                            userName.clear();
                            password.clear();
                            confirm.clear();
                            userName.setEditable(true);
                            password.setEditable(true);
                            confirm.setEditable(true);
                        } catch (Exception e) {
                            e.getStackTrace();
                        }
                    }
                });
            }).start();
        }


    }

    @FXML
    void handleKeyReleased() {
        boolean isUserEmpty = userName.getText().trim().isEmpty();
        boolean isPassEmpty = password.getText().trim().isEmpty();
        boolean isConfirmEmpty = confirm.getText().trim().isEmpty();
        boolean disable = !isUserEmpty && !isPassEmpty && !isConfirmEmpty;
        signUpButton.setDisable(!disable);
    }


    //Overloaded method for mouseEvent
    @FXML
    void showLoginStage(MouseEvent event) throws Exception {
            loginStage(event);
    }

    //private method for generic parameter to be accessed by any event argument passed
    //Mouse event from shoLoginStage and action event from sign up button action.
    private void loginStage(Event genericEvent) throws IOException {
        //reading data here..
        userNames.clear();
        passwords.clear();
        database.readUserData(userNames, passwords);
         //not using ... in the resource location because getClassLoader()
        // loads the absolute path from the root of the project that is ChattingApp
        //the jar file needs the absolute relative path to open new fxml files..from the root of the project like bellow
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ChattingApp/UIViews/login.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 775, 605);
        Stage stage = ((Stage) ((Node) genericEvent.getSource()).getScene().getWindow());
        stage.setTitle("Chitty chat");
        stage.setScene(scene);
        //setting lists
        LoginController loginController = loader.getController();
        loginController.setLists(this.userNames, this.passwords);
        loginController.setDatabase(this.database);
        userName.clear();
        password.clear();
        confirm.clear();
        signUpButton.setDisable(true);
        stage.show();
    }

}
