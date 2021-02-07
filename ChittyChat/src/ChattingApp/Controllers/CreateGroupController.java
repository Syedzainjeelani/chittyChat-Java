package ChattingApp.Controllers;

import ChattingApp.OtherClasses.DBConnectivity;
import ChattingApp.OtherClasses.Group;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;

public class CreateGroupController {

    private List<String> friendsList;
    private List<String> userNames;
    private List<Group> groupsList;           // groupsList list might not be necessary
    private DBConnectivity database;
    private String currentUser;
    private ListView<String> fgListView;
    private ObservableList<String> selectedFriends;

    @FXML
    private TextField groupNameField;

    @FXML
    private JFXListView<String> createGroupListView;

    @FXML
    private JFXButton AddButton;

    @FXML
    private JFXButton CancelButton;

    @FXML
    private Label groupCaution;


    /**
     * Algorithm to create group
     * 1-> initialize the group friend list with friend list of user
     * 2-> set group list to multi select and limited to only five selects
     * 3-> fetch groupName and selected friends from group dialog
     * and add them to fgListView showing group name only and in panel shows five friend names at the top
     * 4-> when the group is created let it be created for all the friends selected including this user and themselves
     * by storing groupsList to database and accessing it
     */
    @FXML
    void initialize() {
//        groupsList = new ArrayList<>();
        createGroupListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        groupCaution.setTextFill(Color.RED);

        //Generic Change Listener...
        createGroupListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                groupCaution.setText("");
                selectedFriends = createGroupListView.getSelectionModel().getSelectedItems();
                System.out.println("run Check");
                if (selectedFriends.size() > 5) {  // max of five
                    groupCaution.setText("Limit exceeded! select only 5");
                    createGroupListView.getSelectionModel().clearAndSelect(0);
                } else {//min of 2 must be selected for group to be created
                    groupCaution.setText("");
                }
            }
        });

    }

    @FXML
    private void createGroup(ActionEvent event) {
        // this list contains all the selected friends from group list view.
        String groupName = groupNameField.getText().trim();
        Group newGroup;
        if (!fgListView.getItems().contains(groupName) && !groupName.isEmpty()) {
            //Might need overloaded constructors...................
            //Because below code will produce an index out of bounds exception.
            if (selectedFriends.size() <= 5 && selectedFriends.size() >= 2) {
                newGroup = new Group(groupName, this.currentUser, selectedFriends);
                groupsList.add(newGroup);
                fgListView.getItems().add(newGroup.getGroupName());
                int index = fgListView.getItems().indexOf(newGroup.getGroupName());
                fgListView.getSelectionModel().select(index);
                /////////create and add data in databases....................
                database.insertGroupData(newGroup);
                cancel(event);
            } else if (selectedFriends.size() < 2) {
                groupCaution.setText("Please select minimum of 2 friends");
            } else {
                groupCaution.setText("Please select participants");
            }
//            switch (selectedFriends.size()) {
//                case 5:
//                    //////user list group parameter.....................
//                    Group newGroup = new Group(groupName, this.currentUser, selectedFriends.get(0), selectedFriends.get(1),
//                            selectedFriends.get(2), selectedFriends.get(3), selectedFriends.get(4));
//                    groupsList.add(newGroup);
//                    fgListView.getItems().add(newGroup.getGroupName());
//                    int index = fgListView.getItems().indexOf(newGroup.getGroupName());
//                    fgListView.getSelectionModel().select(index);
//                    cancel(event);
//                    break;
//                case 4:
//                    newGroup = new Group(groupName, this.currentUser, selectedFriends.get(0), selectedFriends.get(1),
//                            selectedFriends.get(2), selectedFriends.get(3));
//                    groupsList.add(newGroup);
//                    fgListView.getItems().add(newGroup.getGroupName());
//                    index = fgListView.getItems().indexOf(newGroup.getGroupName());
//                    fgListView.getSelectionModel().select(index);
//                    cancel(event);
//                    break;
//                case 3:
//                    newGroup = new Group(groupName, this.currentUser, selectedFriends.get(0), selectedFriends.get(1),
//                            selectedFriends.get(2));
//                    groupsList.add(newGroup);
//                    fgListView.getItems().add(newGroup.getGroupName());
//                    index = fgListView.getItems().indexOf(newGroup.getGroupName());
//                    fgListView.getSelectionModel().select(index);
//                    cancel(event);
//                    break;
//                case 2:
//                    newGroup = new Group(groupName, this.currentUser, selectedFriends.get(0), selectedFriends.get(1));
//                    groupsList.add(newGroup);
//                    fgListView.getItems().add(newGroup.getGroupName());
//                    index = fgListView.getItems().indexOf(newGroup.getGroupName());
//                    fgListView.getSelectionModel().select(index);
//                    cancel(event);
//                    break;
//                default:
//                    if(selectedFriends.size() <= 5 && selectedFriends.size() >= 2) {
//                        newGroup = new Group(groupName, this.currentUser, selectedFriends);
//                        groupsList.add(newGroup);
//                        fgListView.getItems().add(newGroup.getGroupName());
//                        index = fgListView.getItems().indexOf(newGroup.getGroupName());
//                        fgListView.getSelectionModel().select(index);
//                        cancel(event);
//                    } else if (selectedFriends.size() < 2) {
//                        groupCaution.setText("Please select minimum of 2 friends");
//                    } else {
//                        groupCaution.setText("Please select participants");
//                    }
//            }
            //Update groupsList in database here............
        } else if (!groupName.trim().isEmpty()) {
            groupCaution.setText("Similar group name already exists");
        } else if (groupName.isEmpty()) {
            groupCaution.setText("Please Enter group name");
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
        if (fgListView.getItems().size() == 0) {
            fgListView.getItems().add(0, "No Groups are created yet");

        }
    }


    //Setters
    void setFriendsList(List<String> friendsList) {
        this.friendsList = friendsList;
        createGroupListView.getItems().addAll(friendsList);
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
        if (fgListView.getItems().get(0).equals("No Groups are created yet")) {
            fgListView.getItems().clear();
        }
    }

    void setGroupsList(List<Group> groupsList) {
        this.groupsList = groupsList;

    }


}
