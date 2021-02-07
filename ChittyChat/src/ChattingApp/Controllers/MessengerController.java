package ChattingApp.Controllers;

import ChattingApp.OtherClasses.Client;
import ChattingApp.OtherClasses.DBConnectivity;
import ChattingApp.OtherClasses.Group;
import ChattingApp.OtherClasses.Server;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
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
    private List<Client> clientsList;
    private String currentUser;
    private String selectedFriend;
    private Group selectedGroup;
    private double layoutYValue = 0;
    private boolean isUserSelected;
    private boolean isGroupSelected;
    //Server class for clients connection..
    private Server server;
    //Database containing all the data.
    private DBConnectivity database;

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
    private FontAwesomeIconView send;
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
//    private ListView<String> fgListView;
    @FXML
    private JFXListView<String> fgListView;
    private JFXPopup popup;
    private JFXButton delete;
    private JFXPopup logoutPopup;


    /**
     * Algorithm for Group msgs
     * >>> first of all set the changes to client program... Done
     * 1 -> send group message to all the clients connected that are same as group friends(use names) when group is selected
     * 2 -> save the sent msg data to database in group msgs table (use same userChat Bubble)
     * 3 -> receive the msgs from other clients save them to database too (show friends names on chat Bubbles)
     * 4 -> work out delete method for groups
     * 5 -> KISS
     */


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        showLoadingGiff(false);
        popup = new JFXPopup();
        logoutPopup = new JFXPopup();
        server = new Server();
        server.start();  // this thread runs infinitly...
        friendsList = new ArrayList<>();
        groupsList = new ArrayList<>();
        clientsList = server.getClientsList();
        sentMessages = new ArrayList<>();
        receivedMessages = new ArrayList<>();
        senderUsernames = new ArrayList<>();
        friendNameLabel.setText("");
        fgListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //Generic ChangeListener....
//            if (newValue != null) {
                String selectedFriend = fgListView.getSelectionModel().getSelectedItem();
                leftClick(selectedFriend);
//            }

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
            friendsList.clear();
            database.readFriends(currentUser, friendsList); //////////reading friends from database
            fgListView.getItems().setAll(friendsList);
            //if nothing is in the list without current user.
            if (friendsList.size() == 0) {
                fgListView.getItems().add(0, "No Friends added");
            }
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
        //List view Popop...
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
    }

    @FXML
    void handleListViewClick(MouseEvent event) {
        MouseButton mouseButton = event.getButton();
        if (mouseButton == MouseButton.SECONDARY) {
            //RightClick... show delete popop..
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
            //can put loading giff here ....................
            showLoadingGiff(true);
            new Thread(() -> {
                database.readMsgsData(currentUser, friendOrGroup, sentMessages, receivedMessages);   //reads all the msgs and stores in the lists.

                for (int i = 0; i < sentMessages.size(); i++) {    //sentMessages and recevied messaged size will be same because of empty saved msgs..
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
            //Group must contain list of all friends from which we get friend names.
            friendNameLabel.setText(group.getFriends());
            scrollVBox.getChildren().clear();
            sentMessages.clear();
            receivedMessages.clear();
            layoutYValue = 0;
            //print read msgs from database..
            //Same logic can be used.
            //can put loading giff here ....................
            showLoadingGiff(true);////////////////////////
            new Thread(() -> {
                database.readGroupMsgsData(group, currentUser, senderUsernames, sentMessages, receivedMessages);   //reads all the msgs and stores in the lists.

                for (int i = 0; i < sentMessages.size(); i++) {    //sentMessages and recevied messaged size will be same because of empty saved msgs..
                    String sntMsg = sentMessages.get(i);          //cause each sent or received mesg saves the other too.
                    String rcdMsg = receivedMessages.get(i);
                    String sndrUsername = senderUsernames.get(i);
                    if (!sntMsg.isEmpty()) {
                        Platform.runLater(() -> {
                            createUserChatBubble(sntMsg, layoutYValue);
                            layoutYValue += 70;
                        });
                    } else if (!rcdMsg.isEmpty()) {
                        Platform.runLater(() -> {
                            createGroupChatBubble(sndrUsername, rcdMsg, layoutYValue);
                            layoutYValue += 70;
                        });
                    }
                }
            }).start();
            showLoadingGiff(false); //////////////////////
            messageField.setDisable(false);
            searchField.setDisable(false);
            dataTransfer.setDisable(false);
        } else {
            isUserSelected = false;
            isGroupSelected = false;
        }
    }

    private void showLoadingGiff(boolean isShown) {////...........see.......
        //create an imageview in the fxml file at the middle of scroll pane of chat messages
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
            database.removeMsgs(currentUser, toDelete);  // delets all its messages because these are only for this user ...
            ///{
            database.removeFriends(currentUser, toDelete);    //go down ->>!
            ///}
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
                //delete group messages of this user from database.............................
                //also delete group of this user from database....................................
                database.removeGroupMsgs(group, currentUser);
                database.removeGroup(group, currentUser);
//                scrollVBox.getChildren().clear();
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
        } catch (IOException ioe) {
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
        stage.setX(primaryStage.getX() + 250);    ///////////these or for the middle position of mini stage..
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
            server.sendOutputMessage(selectedFriend, currentUser + ":" + message);
            createUserChatBubble(message, layoutYValue);
            database.insertMsgsData(currentUser, selectedFriend, message, ""); // empty receive msg to be stored in the row ->Record.
            layoutYValue += 70;
            messageField.clear();
            send.setDisable(true);
        } else if (isGroupSelected) {
            //for group messages  use isGroupSelected cause groupmsgs database table is different
            //and also groupChatBubble is also different contains names of friends...
            //.........
            String message = messageField.getText();
            server.sendGroupMessage(selectedGroup, currentUser + ":" + message);
            createUserChatBubble(message, layoutYValue);
            System.out.println("Group message sent>>>");
            //we don't need a sender username here thus making it empty ... cause it is us who are sending this...
            database.insertGroupMsgsData(selectedGroup, currentUser, "", message, "");
            //////////////////////
            layoutYValue += 70;
            messageField.clear();
            send.setDisable(true);

        }
    }

    void receiveMessage() {
        //input reciever background thread
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (Server.isConnected && isUserSelected) {
                            String input = server.getInputMessage(selectedFriend);
                            if (input == null) {
                                System.out.println("Input is null");
                                Server.isConnected = false;
                                continue;
                            }
                            String sndUsername = input.split(":")[0];
                            String message = input.split(":")[1];
                            System.out.println("Message received " + message);
                            database.insertMsgsData(currentUser, selectedFriend, "", message);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (message != null) {
                                        //Changing to javafx must be made on  javafx main thread only...
                                        createFriendChatBubble(message, layoutYValue);
                                        layoutYValue += 70;
                                    }
                                }
                            });
                        } else if (Server.isConnected && isGroupSelected) {
                            String input = server.receiveGroupMessage(selectedGroup);
                            if (input == null) {
                                System.out.println("Group input is null");
                                Server.isConnected = false;
                                continue;
                            }
                            String sndUsername = input.split(":")[0];
                            String message = input.split(":")[1];
                            System.out.println("Group Message received " + message);
                            database.insertGroupMsgsData(selectedGroup, currentUser, sndUsername, "", message);
                            System.out.println("Sender >> " + sndUsername);

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    if (message != null) {
                                        //Changing to javafx must be made on  javafx main thread only...
                                        createGroupChatBubble(sndUsername, message, layoutYValue);
                                        server.searchClient(sndUsername).setServerClientInput(null);      /////////this to stop double messgs..
                                        layoutYValue += 80;/////////////
                                    }
                                }
                            });
                        }
                        //for group messages  use isGroupSelected cause groupmsgs table is different
                        //and also groupChatBubble is also different contains names of friends...
                        //.........

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
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

    //When receiving a group message it comes with the users' name on it
    private void createGroupChatBubble(String sndUsername, String message, double layoutYValue) {
        ////////sender user name is not showing correctly it rightly....
        System.out.println("Creating group chat bubble..." + sndUsername);
        VBox outerVbox = new VBox();
        outerVbox.setPrefHeight(80);
        outerVbox.setPrefWidth(510);
        HBox outerHbox = new HBox();
        outerHbox.setAlignment(Pos.BOTTOM_LEFT);
        outerHbox.setPrefHeight(70); /////////
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
        textArea.setText(sndUsername +": " + message);
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
        //////////////////chek it if its working or not..
        Label nameLabel = new Label();
        nameLabel.setText(sndUsername);
        VBox.setMargin(nameLabel, new Insets(0, 10, 0, 0));
        outerVbox.getChildren().add(nameLabel);
        outerVbox.getChildren().add(outerHbox);
        /////////////
        VBox.setVgrow(chatScrollPane, Priority.ALWAYS);
        scrollVBox.getChildren().add(outerHbox);
        chatScrollPane.setContent(scrollVBox);
        chatScrollPane.applyCss();     // these are for the scroll bar auto scroll with messages.
        chatScrollPane.layout();
        chatScrollPane.setVvalue(1d);
    }


    //Message field key released action handler.
    @FXML
    void handleMessageKeyReleased() {
        boolean isMessageFieldEmpty = messageField.getText().trim().isEmpty();
        send.setDisable(isMessageFieldEmpty);
    }


    @FXML
    void sendData() {

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
    void setLists(List<String> userNames, List<String> passwords) {
        this.userNames = userNames;
        this.passwords = passwords;
    }

    void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
        userLabel.setText(currentUser);
        server.setUserName(currentUser);   //setting it here cause during initialization username is null.
        showFList();
    }

    void setDatabase(DBConnectivity database) {
        this.database = database;
    }


}
