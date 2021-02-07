package ChattingApp.Controllers;

import ChattingApp.Main;
import ChattingApp.OtherClasses.DBConnectivity;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private List<String> userNames;
    private List<String> passwords;
    private DBConnectivity database;

    //All Login Fields
    @FXML
    private TextField userName;
    @FXML
    private TextField password;
    //Button
    @FXML
    private Button loginButton;
    @FXML
    private ImageView loading;
    @FXML
    private Label userCaution;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setDisable(true);
//        database.readUserData(userNames, passwords);
        /**
         * setting the rippler effect on action event login call ..
         */

    }

    //show and action methods.
    @FXML
    void login(ActionEvent event) {

        String currentUser, pass;
        boolean invalidUser = false, invalidPass = false;
        currentUser = userName.getText();
        pass = password.getText();
        if (currentUser != null && pass != null) {
            for (int i = 0; i < userNames.size(); i++) {
                if (userNames.get(i).equals(currentUser)) {
                    invalidUser = false;
                    if (passwords.get(i).equals(pass)) {
                        invalidPass = false;
                        userName.setEditable(false);
                        password.setEditable(false);
                        userCaution.setText("Login successful");
                        //presentation 2 sec sleep and loading gif then open messenger app.
                        new Thread(() -> {
                            try {
                                loading.setVisible(true);
                                Thread.sleep(3000);
                            } catch (InterruptedException ie) {
                                ie.getStackTrace();
                            }
                            Platform.runLater(() -> {
                                try {
                                    openMessenger(event, currentUser);
                                } catch (Exception e) {
                                    e.getStackTrace();
                                }
                            });
                        }).start();           ///////////////don't forget to start the thread....
                        break;  //break will only work if openMessenger doesn't work.
                    } else {
                        invalidPass = true;
                        break;
                    }
                } else {
                    invalidUser = true;
                }
            }
            if (invalidUser || invalidPass) {
                userCaution.setTextFill(Color.RED);
                userCaution.setText("Invalid Username or password");

            }
        }

    }

    @FXML
    void handleKeyReleased() {
        boolean isUserEmpty = userName.getText().trim().isEmpty();
        boolean isPassEmpty = password.getText().trim().isEmpty();
        boolean disable = !isUserEmpty && !isPassEmpty;
        loginButton.setDisable(!disable);
    }

    @FXML
    void showSignUpStage(MouseEvent event) throws Exception {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chitty chat");
        stage.setScene(Main.getScene());
        stage.show();

    }


    private void openMessenger(ActionEvent event, String currentUser) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ChattingApp/UIViews/messenger.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root, 775, 605);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Chitty chat");
        stage.setScene(scene);
        // setting lists..
        MessengerController messenger = loader.getController();
        messenger.setLists(userNames, passwords);
        messenger.setDatabase(this.database);
        messenger.setCurrentUser(currentUser);
        stage.show();

    }


    //// Controllers Data transfer Methods.
    void setLists(List<String> userNames, List<String> passwords) {
        this.userNames = userNames;
        this.passwords = passwords;
    }

    void setDatabase(DBConnectivity database) {
        this.database = database;
    }


}
