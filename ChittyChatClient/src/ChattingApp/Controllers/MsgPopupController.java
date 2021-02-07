package ChattingApp.Controllers;

import ChattingApp.OtherClasses.DBConnectivity;
import ChattingApp.OtherClasses.Group;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;


public class MsgPopupController {

    private boolean isGroupMsg;
    private String senderUsername;
    private String message;
    private Group group;
    private List<Group> groupsList;
    private ListView<String> fgListView;
    private List<String> friendsList;
    private DBConnectivity database;
    private String currentUser;

    private Button addButton;
    private TextField messageField;
    private JFXButton send;
    private FontAwesomeIconView dataTransfer;
    private Label friendNameLabel;
    private VBox scrollVBox;


    //message pop up Fields...
    @FXML
    private AnchorPane msgPopupPane;

    @FXML
    private JFXTextArea msgPopupTextArea;

    @FXML
    private JFXButton msgPopupReply;

    @FXML
    private JFXButton msgPopupCancel;

    @FXML
    private Label msgPopupLabel;


    /**
     * Server program set stop button
     * and decrement after client connection ends
     * <p>
     * client create cautions for ip and portNo
     * client correct the popupStage... DONE
     * its positions and size and dropShadow DONE
     *
     * @param event
     */


    //Message pop up methods...
    @FXML
    void replyToFriend(ActionEvent event) {
        // this action handler will select that friend who has send the msg so as to chat with him normally
        if (fgListView.getItems().contains(senderUsername) && group == null) { // cause at the single chat group is sent null
            int index = fgListView.getItems().indexOf(senderUsername);
            fgListView.getSelectionModel().select(index);
        } else if (!fgListView.getItems().contains(senderUsername) && group == null) {
            showFList();
        } else if (fgListView.getItems().contains(group.getGroupName())) {  //For groups
            int index = fgListView.getItems().indexOf(group.getGroupName());
            fgListView.getSelectionModel().select(index);
        } else if (!fgListView.getItems().contains(group.getGroupName())) {
            showGList();
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showFList() {
        addButton.setText("Add Friends");
        clearSet(true);
        if (fgListView != null) {
            friendsList.clear(); // cause it will run every time the user clicks the button
            database.readFriends(currentUser, friendsList);
            fgListView.getItems().setAll(friendsList);
            //selects the friend in the list
            if (fgListView.getItems().contains(senderUsername)) {
                int index = fgListView.getItems().indexOf(senderUsername);
                fgListView.getSelectionModel().select(index);
            } else {
                System.out.println("Friend doesn't exist");
            }
        } else {
            System.out.println("List View is null in showFriends..");
        }
    }

    private void showGList() {
        addButton.setText("Create Group");
        clearSet(true);
        if (fgListView != null) {
            // group list needs groupsList.
            fgListView.getItems().clear();
            groupsList.clear();
            database.readGroupData(currentUser, groupsList);
            for (Group group : groupsList) {
                String name = group.getGroupName();
                if (!fgListView.getItems().contains(name)) {
                    fgListView.getItems().add(group.getGroupName());
                }
            }

            if (fgListView.getItems().contains(group.getGroupName())) {
                int index = fgListView.getItems().indexOf(group.getGroupName());
                fgListView.getSelectionModel().select(index);
            } else {
                System.out.println("Group doesn't exist");
            }

        } else {
            System.out.println("List View is null in showGroups..");
        }
    }

    private void clearSet(boolean isTrue) {
        scrollVBox.getChildren().clear();
        friendNameLabel.setText("");
        messageField.setDisable(isTrue);
        send.setDisable(isTrue);
        dataTransfer.setDisable(isTrue);
    }


    @FXML
    void cancelMsgPopup(ActionEvent event) {
        //this will just close the msg pop window
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    void setPopup() {
        if ((!isGroupMsg) && (group == null)) {
            msgPopupLabel.setText("New Friend Message");
            msgPopupTextArea.setText(senderUsername + ": " + message);
        } else {
            msgPopupLabel.setText("New Group Message");
            msgPopupTextArea.setText(group.getGroupName() + ">> \n" + senderUsername + ": " + message);
        }
    }

    void setIsGroupMsg(boolean isGroupMsg) {
        this.isGroupMsg = isGroupMsg;
    }

    void setSenderUsername(String username) {
        senderUsername = username;
    }

    void setMessage(String message) {
        this.message = message;
    }

    void setGroup(Group group) {
        this.group = group;
    }

    void setFgListView(ListView<String> fgListView) {
        this.fgListView = fgListView;

    }

    void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
    }

    void setGroupsList(List<Group> groupsList) {
        this.groupsList = groupsList;
    }

    void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    void setDatabase(DBConnectivity database) {
        this.database = database;
    }

    void setFields(Button addButton, TextField messageField, JFXButton send,
                   FontAwesomeIconView dataTransfer, Label friendNameLabel,
                   VBox scrollVBox) {
        this.addButton = addButton;
        this.messageField = messageField;
        this.send = send;
        this.dataTransfer = dataTransfer;
        this.friendNameLabel = friendNameLabel;
        this.scrollVBox = scrollVBox;
    }

}
