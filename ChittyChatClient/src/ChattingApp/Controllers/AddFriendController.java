package ChattingApp.Controllers;

import ChattingApp.OtherClasses.DBConnectivity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.List;

public class AddFriendController {

    private List<String> userNames;
    private List<String> friendsList;
    private DBConnectivity database;
    private String currentUser;
    private ListView<String> fgListView;


    @FXML
    private TextField textField;

    @FXML
    private Button add;

    @FXML
    private Label caution;

    @FXML
    private Button cancel;


    public void initialize() {
        add.setDefaultButton(true);
        cancel.setCancelButton(true);
    }


    @FXML
    void addFriend(ActionEvent event) {
        String friendsName = textField.getText().trim();
        if (userNames.contains(friendsName)) {
            if (!friendsList.contains(friendsName) && !friendsName.equals(currentUser)) {
                friendsList.add(friendsName);
//                System.out.println("current User " + currentUser + " and friend " + friendsName);
                database.insertNewFriend(currentUser, friendsName);      ////////// gives null pointer exception....................................
                Stage miniStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                miniStage.close();
                fgListView.getItems().setAll(friendsList);
            } else {
                caution.setText("Username already exists");
            }
        } else if (!friendsName.isEmpty()) {
            caution.setText("No account exists with this username");
        } else {
            caution.setText("Please Enter A Username");
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage miniStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        miniStage.close();
    }

    void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
    }

    void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }

    void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    void setDatabase(DBConnectivity database) {
        this.database = database;
    }

    void setfgListView(ListView<String> listView) {
        this.fgListView = listView;
    }
}
