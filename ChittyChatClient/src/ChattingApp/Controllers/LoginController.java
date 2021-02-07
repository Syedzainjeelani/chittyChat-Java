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

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    private List<String> userNames;
    private List<String> passwords;
    private DBConnectivity database;
    private Socket connectionSocket;
    private boolean isConnected;

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

    //Server info fields
    @FXML
    private TextField serverIP;
    @FXML
    private TextField serverPortNo;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loginButton.setDisable(true);
    }

    //show and action methods.
    @FXML
    void login(ActionEvent event) {
        //ip port code...
        try {
            if (!isConnected) {
                //the test is to ensure that connection is made only once on each login.
                connectionSocket = new Socket(serverIP.getText().trim(), Integer.parseInt(serverPortNo.getText().trim()));
                isConnected = true;
            }
        } catch (IOException io) {
            userCaution.setText("Ip or Port no: is incorrect");
            return;
        }
        //user pass code
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
                                    e.printStackTrace();
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
                userCaution.setText("Invalid User name or password");
                userCaution.setTextFill(Color.RED);

            }
        }
    }

    @FXML
    void handleKeyReleased() {
        boolean isIpEmpty = serverIP.getText().trim().isEmpty();
        boolean isPortNoEmpty = serverPortNo.getText().trim().isEmpty();
        boolean isUserEmpty = userName.getText().trim().isEmpty();
        boolean isPassEmpty = password.getText().trim().isEmpty();
        boolean disable = !isUserEmpty && !isPassEmpty && !isIpEmpty && !isPortNoEmpty;
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
        messenger.setDatabase(this.database);
        messenger.setLists(userNames, passwords);
        messenger.setCurrentUser(currentUser);
        //instead of setting ip port no set the
        // connection here if it works pass it, otherwise use its exception as a caution.
//        PrintWriter output = new PrintWriter(connectionSocket.getOutputStream(), true);
        messenger.setSocket(connectionSocket);
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
