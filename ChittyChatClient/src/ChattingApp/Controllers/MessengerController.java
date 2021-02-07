package ChattingApp.Controllers;

import ChattingApp.OtherClasses.Client;
import ChattingApp.OtherClasses.DBConnectivity;
import ChattingApp.OtherClasses.Group;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPopup;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MessengerController implements Initializable {

    private List<String> userNames;
    private List<String> passwords;
    private List<String> sentMessages;
    private List<String> receivedMessages;
    private List<String> senderUsernames;
    private List<Group> groupsList;
    private List<String> friendsList;
    private String currentUser;
    private String selectedFriend;
    private Group selectedGroup;
    private double layoutYValue = 0;
    private Client thisClient;
    private DBConnectivity database;
    private boolean isUserSelected;
    private boolean isGroupSelected;
    private Socket connectionSocket;
    //All fxml fields go here
    @FXML
    private AnchorPane messengerRoot;
    @FXML
    private Text userLabel;
    @FXML
    private TextField messageField;
    @FXML
    private TextField searchField;
    @FXML
    private JFXButton send;
    @FXML
    private FontAwesomeIconView dataTransfer;
    @FXML
    private Button friendsButton;
    @FXML
    private Button groupsButton;
    @FXML
    private Button addButton;
    @FXML
    private Label friendNameLabel;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private VBox scrollVBox;
    @FXML
    private Circle onOffCircle;
    @FXML
    private Text onOffText;
    @FXML
    private Pane loadingPane;
    //List view Friends and groupsList
    @FXML
    private ListView<String> fgListView;
    private JFXPopup popup;
    private JFXButton delete;
    private JFXPopup logoutPopup;

    /**
     * set message pop up
     */


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showLoadingGiff(false);
        popup = new JFXPopup();
        logoutPopup = new JFXPopup();
//        msgPopup = new JFXPopup();
        friendsList = new ArrayList<>();
        groupsList = new ArrayList<>();
        sentMessages = new ArrayList<>();
        receivedMessages = new ArrayList<>();
        senderUsernames = new ArrayList<>();
        friendNameLabel.setText("");
        fgListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //Generic ChangeListener....
            String selectedFriend = fgListView.getSelectionModel().getSelectedItem();
            leftClick(selectedFriend);

        });
        fgListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        initPopup();
        receiveMessage();
    }

    void clearSet(boolean isTrue) {
        scrollVBox.getChildren().clear();
        friendNameLabel.setText("");
        messageField.setDisable(isTrue);
        send.setDisable(isTrue);
        dataTransfer.setDisable(isTrue);
    }

    @FXML
    void showFriendList(ActionEvent event) {
        showFList();
    }

    private void showFList() {
        addButton.setText("Add Friends");
        clearSet(true);
        if (fgListView != null) {
            friendsList.clear(); // cause it will run every time the user clicks the button
            database.readFriends(currentUser, friendsList);
            fgListView.getItems().setAll(friendsList);
            //if nothing is in the list without current user.
            if (friendsList.size() == 0) {
                fgListView.getItems().add(0, "No Friends added yet");
            }
        } else {
            System.out.println("Listveiw is null in showFriends..");
        }
    }

    @FXML
    void showGroupList(ActionEvent event) {
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
            if (groupsList.size() == 0) {
                fgListView.getItems().add(0, "No Groups are created yet");
            }
        }
    }

    private void initPopup() {
        //List view Popup...
        delete = new JFXButton("Delete");
        delete.setOnMouseClicked(MouseEvent -> {
            delete();
        });
        delete.setRipplerFill(Paint.valueOf("#00b7ff"));
        delete.setPadding(new Insets(5));
        popup.setPopupContent(delete);
        //Logout popup ...
        JFXButton offline = new JFXButton("Offline");
        JFXButton logout = new JFXButton("Logout");
        offline.setRipplerFill(Paint.valueOf("#00b7ff"));
        offline.setPadding(new Insets(5));
        logout.setRipplerFill(Paint.valueOf("#00b7ff"));
        logout.setPadding(new Insets(5));
        VBox vbox = new VBox(offline, logout);
        vbox.setStyle("-fx-background-color: #b5ffff");
        logoutPopup.setPopupContent(vbox);
        offline.setOnMouseClicked(MouseEvent -> {
            if (onOffText.getText().equals("Online")) {
                goOffline();
                offline.setText("Online");
            } else {
                goOnline();
                offline.setText("Offline");
            }
        });
        logout.setOnMouseClicked(MouseEvent -> {
            logout();
        });
        //message popUp works only when ever a message appears and that friend is not selected in listView.
//        msgPopup.setPopupContent(msgPopupPane);
    }


    @FXML
    void handleListViewClick(MouseEvent event) {
        MouseButton mouseButton = event.getButton();
        if (mouseButton == MouseButton.SECONDARY) {
            //RightClick... show delete popup..
            popup.show(fgListView, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.LEFT,
                    event.getX(), event.getY());
        }
    }


    private void leftClick(String friendOrGroup) {
        Group group = findGroup(friendOrGroup);
        if (friendOrGroup != null && userNames.contains(friendOrGroup)) {
            selectedFriend = friendOrGroup;
            isUserSelected = true;
            isGroupSelected = false;
            friendNameLabel.setText(friendOrGroup);
            //Clearing stuff before printing other friends' messages...
            scrollVBox.getChildren().clear();
            sentMessages.clear();
            receivedMessages.clear();
            layoutYValue = 0;
            //print read msgs from database..
            new Thread(() -> {
                showLoadingGiff(true);
                database.readMsgsData(currentUser, friendOrGroup, sentMessages, receivedMessages);   //reads all the msgs and stores in the lists.

                for (int i = 0; i < sentMessages.size(); i++) {    //sentMessages and received messaged size will be same because of empty saved msgs..
                    String sntMsg = sentMessages.get(i);
                    String rcdMsg = receivedMessages.get(i);
                    if (!sntMsg.isEmpty()) {
                        Platform.runLater(() -> {
                            createUserChatBubble(sntMsg, layoutYValue);
                            layoutYValue += 70;
                        });
                    } else if (!rcdMsg.isEmpty()) {
                        Platform.runLater(() -> {
                            createFriendChatBubble(rcdMsg, layoutYValue);
                            layoutYValue += 70;
                        });
                    }
                }
            }).start();
            showLoadingGiff(false);
            messageField.setDisable(false);
            searchField.setDisable(false);
            dataTransfer.setDisable(false);
        } else if (friendOrGroup != null && groupsList.contains(group)) {
            isGroupSelected = true;
            isUserSelected = false;
            selectedGroup = group;
            friendNameLabel.setText(group.getFriends());

            //Clearing stuff before printing other friends' messages...
            scrollVBox.getChildren().clear();
            senderUsernames.clear();
            sentMessages.clear();
            receivedMessages.clear();
            layoutYValue = 0;
//            //print read msgs from database..
            new Thread(() -> {
                //read data
                showLoadingGiff(true);
                database.readGroupMsgsData(group, currentUser, senderUsernames, sentMessages, receivedMessages);   //reads all the msgs and stores in the lists.

                for (int i = 0; i < sentMessages.size(); i++) {    //sentMessages and received messaged size will be same because of empty saved msgs..
                    String sntMsg = sentMessages.get(i);          //cause each sent or received msg saves the other too.
                    String rcdMsg = receivedMessages.get(i);
                    String sndrUsername = senderUsernames.get(i);
                    if (!sntMsg.isEmpty()) {
                        Platform.runLater(() -> {
                            createUserChatBubble(sntMsg, layoutYValue);
                            layoutYValue += 70;
                        });
                    } else if (!rcdMsg.isEmpty() && !sndrUsername.isEmpty()) {
                        Platform.runLater(() -> {
                            createGroupChatBubble(sndrUsername, rcdMsg, layoutYValue);
                            layoutYValue += 70;
                        });
                    }
                }
            }).start();
            showLoadingGiff(false);

            //Group must contain list of all friends from which we get friend names.
            messageField.setDisable(false);
            searchField.setDisable(false);
            dataTransfer.setDisable(false);
        } else {
            isUserSelected = false;
            isGroupSelected = false;
        }
    }

    private void showLoadingGiff(boolean isShown) {/////////////////////////////////................................see.......
        //create an image view in the fxml file at the middle of scroll pane of chat messages
        // and give it and id to be accessible here...
        loadingPane.setVisible(isShown);
    }

    @FXML
    void logoutPopup(MouseEvent event) {
        Node node = (Node) event.getSource();
        logoutPopup.show(node, JFXPopup.PopupVPosition.TOP, JFXPopup.PopupHPosition.RIGHT);
    }

    void delete() {
        String toDelete = fgListView.getSelectionModel().getSelectedItem();
        if (friendsList.contains(toDelete)) {
            friendsList.remove(toDelete);
            fgListView.getItems().remove(toDelete);
            popup.hide();
            database.removeFriends(currentUser, toDelete);
            database.removeMsgs(currentUser, toDelete);  // deletes all its messages
            if (friendsList.size() == 0) {
                fgListView.getItems().add(0, "No Friends added");
                friendNameLabel.setText("");
                messageField.setDisable(true);
                searchField.setDisable(true);
                dataTransfer.setDisable(true);
            }
        } else {
            Group group = findGroup(toDelete);
            if (groupsList.contains(group)) {
                groupsList.remove(group);
                fgListView.getItems().remove(group.getGroupName());
                popup.hide();
                //delete group messages from database.............................
                //also delete group from database....................................
                //but first create them ...
                database.removeGroup(group, currentUser);
                database.removeGroupMsgs(group, currentUser);

                if (fgListView.getItems().size() == 0) {
                    fgListView.getItems().add(0, "No Group created yet");
                    friendNameLabel.setText("");
                    messageField.setDisable(true);
                    searchField.setDisable(true);
                    dataTransfer.setDisable(true);
                }
            }
        }

    }


    // logout popup methods...
    private void goOffline() {
        onOffCircle.setFill(Paint.valueOf("#00b7ff"));
        onOffText.setText("Offline");
        logoutPopup.hide();
    }

    private void goOnline() {
        onOffCircle.setFill(Paint.valueOf("#25e448"));
        onOffText.setText("Online");
        logoutPopup.hide();
    }

    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ChattingApp/UIViews/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 775, 605);
            Stage stage = (Stage) addButton.getScene().getWindow();  //((Stage) ((Node) event.getSource()).getScene().getWindow());
            stage.setTitle("Chitty chat");
            stage.setScene(scene);
            //setting lists
            LoginController loginController = loader.getController();
            loginController.setLists(this.userNames, this.passwords);
            loginController.setDatabase(this.database);
            logoutPopup.hide();
            stage.show();
            connectionSocket.getOutputStream().close();
        } catch (Throwable ioe) {
            ioe.printStackTrace();
        }
    }


    @FXML
    void addRemoveFg(ActionEvent event) throws IOException {
        if (addButton.getText().equals("Add Friends")) {
            System.out.println("Adding friend...");
            addFriend(event);
        } else if (addButton.getText().equals("Create Group")) {
            System.out.println("Creating group...");
            createGroup(event);
        }
    }

    void addFriend(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ChattingApp/UIViews/addFriend.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 245, 145));
        AddFriendController controller = loader.getController();
        controller.setFriendsList(friendsList);
        controller.setUserNames(userNames);
        controller.setDatabase(database);
        controller.setCurrentUser(currentUser);
        controller.setfgListView(fgListView);
        Stage primaryStage = ((Stage) ((Node) event.getSource()).getScene().getWindow());
        stage.initOwner(primaryStage);
        stage.setX(primaryStage.getX() + 300);    ///////////these or for the middle position of mini stage..
        stage.setY(primaryStage.getY() + 200);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initStyle(StageStyle.DECORATED);
        stage.setResizable(false);

        stage.show();
        primaryStage.setOnCloseRequest(WindowEvent -> {
            stage.close();
        });
    }

    void createGroup(ActionEvent event) throws IOException {
        /**    ----Algortihm----
         * 1-> create an fxml file(Dailague pane) which asks user - Group Name,
         * add friends to it(limit 5)
         * from available friendslist (Show listview).
         * 2-> handle CAutions
         * 3-> ok and cancel default buttons..
         *
         */
        Stage primaryStage = (Stage) messengerRoot.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ChattingApp/UIViews/createGroup.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(root, 340, 320));
        stage.initOwner(primaryStage);
        stage.setX(primaryStage.getX() + 250);    ////these or for the middle position of mini stage..
        stage.setY(primaryStage.getY() + 100);
        CreateGroupController controller = loader.getController();
        controller.setFriendsList(friendsList);
        controller.setUserNames(userNames);
        controller.setDatabase(database);
        controller.setCurrentUser(currentUser);
        controller.setfgListView(fgListView);
        controller.setGroupsList(groupsList);
        stage.setResizable(false);
        stage.show();
    }


    //Message  field...
    @FXML
    void sendMessage() {
        if (isUserSelected) {
            String message = messageField.getText();
            thisClient.sendOutputMessage(selectedFriend, message);
            createUserChatBubble(message, layoutYValue);
            database.insertMsgsData(currentUser, selectedFriend, message, "");
            //work on this database insert msg data to be saved only once this time..
            layoutYValue += 70;
            messageField.clear();
            send.setDisable(true);
        } else if (isGroupSelected) {
            String message = messageField.getText();
            thisClient.sendGroupMessage(selectedGroup, message);
            createUserChatBubble(message, layoutYValue);
            database.insertGroupMsgsData(selectedGroup, currentUser, "", message, "");
            //this saves data for all the friends in the group.
            System.out.println("Group message sent>>>");
            layoutYValue += 70;
            messageField.clear();
            send.setDisable(true);
        }
    }

    private void receiveMessage() {
        ////input receiver background thread...
        new Thread(new Runnable() {
            @Override
            public void run() {
                String input;
                while (true) {
                    try {
                        Thread.sleep(1000);
                        if (thisClient != null) {
                            if (thisClient.isConnected) {
                                input = thisClient.getInputMessage();
                                if (input == null) {
                                    thisClient.isConnected = false;
                                } else if (isUserSelected && input.split(":")[0].equals(selectedFriend)) {                //what if the different user is selected for popup.....
                                    String message = input.split(":")[1];
                                    System.out.println("Message received " + message);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            createFriendChatBubble(message, layoutYValue);
                                            layoutYValue += 70;
                                        }
                                    });
                                } else if (isGroupSelected && input.split(":")[0].equals(selectedGroup.getGroupName())) {
                                    String groupName = input.split(":")[0];  //only for groups for popup
                                    String sndUsername = input.split(":")[1];
                                    String message = input.split(":")[2];
                                    System.out.println("Group: " + groupName + " msg received " + message);
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            createGroupChatBubble(sndUsername, message, layoutYValue);
                                            layoutYValue += 80;
                                        }
                                    });
                                } else {
                                    //this is for the msg pop up
                                    //the problem is how to know if the msg is of groups' or not
                                    String[] msgSplit = input.split(":");
                                    if (msgSplit.length == 2) {
                                        // friend msg ..
                                        String senderUsername = msgSplit[0];
                                        String message = msgSplit[1];
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                showFriendMsgPopup(senderUsername, message);
                                            }
                                        });
                                    } else if (msgSplit.length == 3){
                                        //Group msg...
                                        String groupName = msgSplit[0];  //only for groups for popup
                                        Group group = findGroup(groupName);
                                        String sndUsername = msgSplit[1];
                                        String message = msgSplit[2];
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                showGroupMsgPopup(group, sndUsername, message);
                                            }
                                        });
                                    }
                                }
                            }

                        }
                    } catch (InterruptedException | IOException e) {
                        System.out.println(e.getMessage());
                        break;
                    }
                }
            }
        }).start();
    }

    private void showFriendMsgPopup(String senderUsername, String message) {
        //showing the popup.. see if it work or not
        try {
            loadPopup(null, senderUsername, message);
            System.out.println("Msg popup show, called...");
        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    private void loadPopup(Group group, String senderUsername, String message) throws IOException {
        Stage primaryStage = (Stage) messengerRoot.getScene().getWindow();
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("ChattingApp/UIViews/msgPopup.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root, 250, 170);
        stage.setScene(scene);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.initOwner(messengerRoot.getScene().getWindow());
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setX(primaryStage.getX() + 400);
        stage.setY(primaryStage.getY() + 50);
        stage.setResizable(false);
        MsgPopupController controller = loader.getController();
        if (group != null) {
            controller.setGroup(group);
            controller.setIsGroupMsg(true);
        } else {
            controller.setGroup(null);
            controller.setIsGroupMsg(false);
        }
        controller.setFgListView(fgListView);
        controller.setDatabase(database);
        controller.setCurrentUser(currentUser);
        controller.setFriendsList(friendsList);
        controller.setGroupsList(groupsList);
        controller.setFields(addButton, messageField, send,
                dataTransfer, friendNameLabel, scrollVBox);
        controller.setSenderUsername(senderUsername);
        controller.setMessage(message);
        controller.setPopup();
        stage.show();

    }

    private void showGroupMsgPopup(Group group, String sndUsername, String message) {
        //showing the popup.. see if it work or not
        try {
            loadPopup(group, sndUsername, message);
            System.out.println("Group msg pop show called.");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void createUserChatBubble(String message, double layoutYValue) {
        HBox outerHbox = new HBox();
        outerHbox.setAlignment(Pos.BOTTOM_RIGHT);
        outerHbox.setPrefHeight(70);
        outerHbox.setPrefWidth(510);
        HBox innerHbox = new HBox();
        innerHbox.setAlignment(Pos.BOTTOM_RIGHT);
        innerHbox.setPrefWidth(451);
        innerHbox.setPrefHeight(60);
        HBox.setMargin(innerHbox, new Insets(5, 0, 5, 5));
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(60);
        textArea.setPrefWidth(435);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setStyle(String.valueOf(Color.BLUE));
        FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
        fontAwesomeIconView.setGlyphName("CARET_RIGHT");
        fontAwesomeIconView.setSize("35");
        fontAwesomeIconView.setFill(Paint.valueOf("#00b7ff"));
        fontAwesomeIconView.setStroke(Paint.valueOf("#4d4d4d"));
        textArea.setText(message);
        innerHbox.getChildren().add(textArea);
        innerHbox.getChildren().add(fontAwesomeIconView);
        outerHbox.getChildren().add(innerHbox);
        Circle circle = new Circle();
        circle.setRadius(15);
        HBox.setMargin(circle, new Insets(5, 15, 10, 0));
        circle.setFill(Paint.valueOf("#1fbdff"));
        circle.setStroke(Paint.valueOf("#464646"));
        outerHbox.getChildren().add(circle);
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);
        outerHbox.setLayoutY(layoutYValue);
        scrollVBox.getChildren().add(outerHbox);
        chatScrollPane.setContent(scrollVBox);
        chatScrollPane.applyCss();
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1d);

    }

    private void createFriendChatBubble(String message, double layoutYValue) {
        //////////////////////////Friends Bubble of chat messages....
        HBox outerHbox = new HBox();
        outerHbox.setAlignment(Pos.BOTTOM_LEFT);
        outerHbox.setPrefHeight(70);
        outerHbox.setPrefWidth(510);
        HBox innerHbox = new HBox();
        innerHbox.setAlignment(Pos.BOTTOM_LEFT);
        innerHbox.setPrefWidth(451);
        innerHbox.setPrefHeight(60);
        HBox.setMargin(innerHbox, new Insets(5, 0, 5, 5));
        FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
        fontAwesomeIconView.setGlyphName("CARET_LEFT");
        fontAwesomeIconView.setSize("35");
        fontAwesomeIconView.setFill(Paint.valueOf("#d74177"));
        fontAwesomeIconView.setStroke(Paint.valueOf("#4d4d4d"));
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(60);
        textArea.setPrefWidth(435);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setStyle(String.valueOf(Color.BLUE));
        textArea.setText(message);
        Circle circle = new Circle();
        circle.setRadius(15);
        HBox.setMargin(circle, new Insets(5, 0, 10, 10));
        circle.setFill(Paint.valueOf("#d74177"));
        circle.setStroke(Paint.valueOf("#464646"));
        innerHbox.getChildren().add(fontAwesomeIconView);
        innerHbox.getChildren().add(textArea);
        outerHbox.getChildren().add(circle);
        outerHbox.getChildren().add(innerHbox);
        outerHbox.setLayoutY(layoutYValue);
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);
        scrollVBox.getChildren().add(outerHbox);
        chatScrollPane.setContent(scrollVBox);
        chatScrollPane.applyCss();     // these are for the scroll bar auto scroll with messages.
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1d);

    }

    private void createGroupChatBubble(String sndUsername, String message, double layoutYValue) {
        HBox outerHbox = new HBox();
        outerHbox.setAlignment(Pos.BOTTOM_LEFT);
        outerHbox.setPrefHeight(70);
        outerHbox.setPrefWidth(510);
        HBox innerHbox = new HBox();
        innerHbox.setAlignment(Pos.BOTTOM_LEFT);
        innerHbox.setPrefWidth(451);
        innerHbox.setPrefHeight(60);
        HBox.setMargin(innerHbox, new Insets(5, 0, 5, 5));
        FontAwesomeIconView fontAwesomeIconView = new FontAwesomeIconView();
        fontAwesomeIconView.setGlyphName("CARET_LEFT");
        fontAwesomeIconView.setSize("35");
        fontAwesomeIconView.setFill(Paint.valueOf("#d74177"));
        fontAwesomeIconView.setStroke(Paint.valueOf("#4d4d4d"));
        TextArea textArea = new TextArea();
        textArea.setPrefHeight(60);
        textArea.setPrefWidth(435);
        textArea.setWrapText(true);
        textArea.setEditable(false);
        textArea.setStyle(String.valueOf(Color.BLUE));
        textArea.setText(sndUsername + ": " + message);
        Circle circle = new Circle();
        circle.setRadius(15);
        HBox.setMargin(circle, new Insets(5, 0, 10, 10));
        circle.setFill(Paint.valueOf("#d74177"));
        circle.setStroke(Paint.valueOf("#464646"));
        innerHbox.getChildren().add(fontAwesomeIconView);
        innerHbox.getChildren().add(textArea);
        outerHbox.getChildren().add(circle);
        outerHbox.getChildren().add(innerHbox);
        outerHbox.setLayoutY(layoutYValue);
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);
        scrollVBox.getChildren().add(outerHbox);
        chatScrollPane.setContent(scrollVBox);
        chatScrollPane.applyCss();     // these are for the scroll bar auto scroll with messages.
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1d);
    }

    //Message field key released action handler.
    @FXML
    private void handleMessageKeyReleased() {
        boolean isMessageFieldEmpty = messageField.getText().trim().isEmpty();
        send.setDisable(isMessageFieldEmpty);
    }


    @FXML
    void sendData() {
        ////implement data transfer ............
    }

    private Group findGroup(String groupName) {
        if (groupName != null) {
            for (Group group : groupsList) {
                if (group.getGroupName().equals(groupName)) {
                    return group;
                }
            }
        }
        return null;
    }

    //Data transfer methods...

    void setDatabase(DBConnectivity database) {
        this.database = database;
    }

    void setLists(List<String> userNames, List<String> passwords) {
        this.userNames = userNames;
        this.passwords = passwords;
    }

    void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        userLabel.setText(currentUser);
        database.readGroupData(currentUser, groupsList); //reading group data for popup issue..
        showFList();
    }

    void setSocket(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
        thisClient = new Client(connectionSocket, currentUser);
        thisClient.start();
    }


}
