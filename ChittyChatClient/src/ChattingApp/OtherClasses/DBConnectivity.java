package ChattingApp.OtherClasses;

import java.sql.*;
import java.util.List;

public class DBConnectivity {
    private Statement statement;
    private final String DB_NAME = "chatappdata";
    private final String username = "root";
    private final String password = "shah";
    private final String CONNECTION_STRING = "jdbc:mysql://localhost:3306/" + DB_NAME;

    private final String LOGIN_TABLE = "login_table";
    private final String MESSAGES_TABLE = "messages_table";
    private final String FRIENDS_TABLE = "friends_table";
    private final String GROUPS_TABLE = "groups_table";
    private final String GROUP_MESSAGES_TABLE = "group_messages_table";


    //Login table columns
    private final String USER = "user";
    private final String PASS = "pass";
    //Messages table columns
    private final String MSGS_USER = "user";
    private final String SELECTED_FRIEND = "selectedFriend";
    private final String SENT_MESSAGES = "sentMessages";
    private final String RECEIVED_MESSAGES = "receivedMessages";
    //Friends table columns
    private final String FRIENDS_USER = "user";
    private final String FRIENDS = "friends";
    //Group table columns
    private final String GROUP_NAME = "group_name";
    private final String GROUP_USER = "user";
    private final String GROUP_F1 = "friend1";
    private final String GROUP_F2 = "friend2";
    private final String GROUP_F3 = "friend3";
    private final String GROUP_F4 = "friend4";
    private final String GROUP_F5 = "friend5";
    //Group messages table columns
    private final String GROUP_NAME_MESSAGES = "group_name";
    private final String SENDER_USERNAME = "sender_username";
    private final String SENT_MESSAGES_GROUP = "sent_messages";
    private final String RECEIVED_MESSAGES_GROUP = "received_messages";

    public DBConnectivity() {
        setConnections();
    }

    public void setConnections() {
        Connection conn;
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING, username, password);
            statement = conn.createStatement();
            System.out.println("Database Connection Established");

        } catch (SQLException sqle) {
            sqle.getStackTrace();
        }
    }

    // Create insert and retrieve data methods
    //Inserts the user data into the database correspondingly.
    public void insertUserData(String user, String pass) {
        try {
            statement.executeUpdate("INSERT INTO " + LOGIN_TABLE + " VALUES('" + user + "' ,'" + pass + "')");
        } catch (SQLException sqle) {
            sqle.getStackTrace();
            System.out.println(sqle.getMessage());
        }

    }

    /////////////// /////changesss saved
    public void insertMsgsData(String currentUser, String selectedFriend, String sentMessage, String receivedMessage) {
        //store messages in sequence even if either should be saved empty.. for sequence..
        String temp, tempMsg;
        try {
            for(int i = 0; i < 2; i++) {   //just two times  for this user and friend

                if(i == 1) {
                    temp = currentUser;
                    currentUser = selectedFriend;
                    selectedFriend = temp;
                    //
                    tempMsg = sentMessage;
                    sentMessage = receivedMessage;
                    receivedMessage = tempMsg;
                }

                statement.executeUpdate("INSERT INTO " + MESSAGES_TABLE + " VALUES('" + currentUser + "', '" + selectedFriend
                        + "', '" + sentMessage + "', '" + receivedMessage + "')");

            }
        } catch (SQLException sqle) {
            sqle.getStackTrace();
            System.out.println(sqle.getMessage());
        }

    }

    public void insertNewFriend(String user, String friend) {
        try {
            statement.executeUpdate("INSERT INTO " + FRIENDS_TABLE + " VALUES('" + user + "' ,'" + friend + "')");
        } catch (SQLException sqle) {
            sqle.getStackTrace();
            System.out.println(sqle.getMessage());
        }

    }

    public void removeFriends(String user, String selectedFriend) {
        try {
            // can't delete from database because there is no unique id for user find a way ....//////////////////////////////////
            //if And operator works put it here too,..
            statement.executeUpdate("DELETE FROM " + FRIENDS_TABLE + " WHERE user = '" + user + "' AND friends = '" + selectedFriend + "' ");
        } catch (SQLException sqle) {
            sqle.getStackTrace();
            System.out.println(sqle.getMessage());
        }
    }

    /////changesss saved
    public void removeMsgs(String user, String selectedFriend) {
        try {
            // can't delete from database because there is no unique id for user find a way ....//////////////////////////////////
            statement.executeUpdate("DELETE FROM " + MESSAGES_TABLE + " WHERE user = '" + user + "' AND selectedFriend = '" + selectedFriend + "' ");
        } catch (SQLException sqle) {
            sqle.getStackTrace();
            System.out.println(sqle.getMessage());
        }
    }


    //Reads the data from database and stores it in the corresponding list.
    public void readUserData(List<String> users, List<String> pass) {
        try {
            String query = "SELECT * FROM " + LOGIN_TABLE;
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                users.add(resultSet.getString(USER));
                pass.add(resultSet.getString(PASS));
            }
        } catch (SQLException sqle) {
            sqle.getStackTrace();
            System.out.println(sqle.getMessage());
        }
    }

    //changes saved...........
    public void readMsgsData(String currentUser, String selectedFriend, List<String> sentMessages, List<String> receivedMessages) {
        //READ messages in sequence even if either IS empty.. for sequence PRINTING.
        try {
            String query = "SELECT * FROM " + MESSAGES_TABLE + " WHERE user = '" + currentUser + "' AND selectedFriend = '" + selectedFriend + "' ";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                //not checking if empty here because it will be checked in the messenger controller.
                sentMessages.add(resultSet.getString(SENT_MESSAGES));
                receivedMessages.add(resultSet.getString(RECEIVED_MESSAGES));
            }
        } catch (SQLException sqle) {
            sqle.getStackTrace();
            System.out.println(sqle.getMessage());
        }
    }


    public void readFriends(String user, List<String> friendsList) {
        try {
            String query = "SELECT * FROM " + FRIENDS_TABLE + " WHERE user = '" + user + "' ";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                friendsList.add(resultSet.getString(FRIENDS));
            }
        } catch (SQLException sqle) {
            System.out.println(sqle.getMessage());
            System.out.println(sqle.toString());
        }
    }

    //Group methods....
    public void insertGroupData(Group group) {
        //at the time of group creation create it for all users as the friends selected
        //Different rows of group for each user...
        String groupName, user, friend1, friend2, friend3, friend4, friend5, temp;
        groupName = group.getGroupName();
        user = group.getUser();
        friend1 = group.getF1();
        friend2 = group.getF2();
        friend3 = group.getF3();
        friend4 = group.getF4();
        friend5 = group.getF5();
        try {
            for (int i = 0; i < 6; i++) { //six  = 5 friends + user

                //Bellow logic is for the creation of same group for all the user friends selected in it..
                if (i == 1) {
                    //will creates group for user friend1
                    temp = user;
                    user = friend1;
                    friend1 = temp;
                } else if (i == 2) {
                    temp = user;
                    user = friend2;
                    friend2 = temp;
                } else if (!friend3.isEmpty() && i == 3) {
                    temp = user;
                    user = friend3;
                    friend3 = temp;
                } else if (!friend4.isEmpty() && i == 4) {
                    temp = user;
                    user = friend4;
                    friend4 = temp;
                } else if (!friend5.isEmpty() && i == 5) {
                    temp = user;
                    user = friend5;
                    friend5 = temp;
                } else if((friend3.isEmpty() || friend4.isEmpty() || friend5.isEmpty()) && i != 0){
                    break;
                }

                statement.executeUpdate("INSERT INTO " + GROUPS_TABLE +
                        " VALUES('" + groupName + "', '" + user + "', '" + friend1 +
                        "', '" + friend2 + "', '" + friend3 + "', '" + friend4 + "', '" + friend5 + "')");

            }

        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }

    public void readGroupData(String currentUser, List<Group> groupsList) {
        String groupName, user, friend1, friend2, friend3, friend4, friend5;

        try {
            //reads all the groups for the current user..
            String query = "SELECT * FROM " + GROUPS_TABLE + " WHERE user = '" + currentUser + "' ";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                groupName = resultSet.getString(GROUP_NAME);
                user = resultSet.getString(GROUP_USER);
                friend1 = resultSet.getString(GROUP_F1);
                friend2 = resultSet.getString(GROUP_F2);
                friend3 = resultSet.getString(GROUP_F3);
                friend4 = resultSet.getString(GROUP_F4);
                friend5 = resultSet.getString(GROUP_F5);
                Group group = new Group(groupName, user, friend1, friend2, friend3, friend4, friend5);
                groupsList.add(group);
            }
        } catch (SQLException sql) {
            sql.printStackTrace();
            System.out.println("Can't read Group data");
        }
    }

    //Create group messages table and its insert msgs method
    //Create methods (delete group msgs) and (groups)
    public void insertGroupMsgsData(Group selectedGroup, String currentUsername, String senderUsername, String sentMessage, String receivedMessage) {
        //here data will be stored and fetched according to the selected group name and sndUsername is for showing sender names
        try {
            /////////////////////////////work on it
            String groupName, friend1, friend2, friend3, friend4, friend5, tempMsg;
            groupName = selectedGroup.getGroupName();
            friend1 = selectedGroup.getF1();
            friend2 = selectedGroup.getF2();
            friend3 = selectedGroup.getF3();
            friend4 = selectedGroup.getF4();
            friend5 = selectedGroup.getF5();
            for (int i = 0; i < 6; i++) {
                // sender friend and the current user need to be swapped..
                // and the sent message of current user will be the received message of other friends
                //thus they are also swapped but once for all friends.
                //swapping msgs cause sent msg of current user will be received msg of others
                if (i == 1) {
                    //will creates group msg for user friend1
                    tempMsg = receivedMessage;
                    receivedMessage = sentMessage;  //if not empty
                    sentMessage = tempMsg;
                    //
                    senderUsername = currentUsername;
                    currentUsername = friend1;

                } else if (i == 2) {
                    currentUsername = friend2;
                } else if (!friend3.isEmpty() && i == 3) {
                    currentUsername = friend3;
                } else if (!friend4.isEmpty() && i == 4) {
                    currentUsername = friend4;
                } else if (!friend5.isEmpty() && i == 5) {
                    currentUsername = friend5;
                } else if((friend3.isEmpty() || friend4.isEmpty() || friend5.isEmpty()) && i != 0) {
                    break;
                }

                //first one is for current user with empty senderUsername and empty received msg
                //this group data should be saved for every group member
                statement.executeUpdate("INSERT INTO " + GROUP_MESSAGES_TABLE + " VALUES('" + groupName + "', '" + currentUsername + "', '"
                        + senderUsername + "', '" + sentMessage + "', '" + receivedMessage + "')");
                System.out.println("Group msg data saved  " + i);

            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println(sqle.getMessage());
        }
    }


    public void readGroupMsgsData(Group selectedGroup, String currentUser, List<String> senderUsernames,
                                  List<String> sentMessages, List<String> receivedMessages) {
        try {
            String query = "SELECT * FROM " + GROUP_MESSAGES_TABLE + " WHERE group_name = '" + selectedGroup.getGroupName()
                    + "' AND user = '" + currentUser + "' ";
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                //not checking if empty here because it will be checked in the messenger controller.
                senderUsernames.add(resultSet.getString(SENDER_USERNAME));
                sentMessages.add(resultSet.getString(SENT_MESSAGES_GROUP));
                receivedMessages.add(resultSet.getString(RECEIVED_MESSAGES_GROUP));
            }
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            System.out.println("Group read msgs data doesn't work ");
        }
    }

    public void removeGroup(Group selectedGroup, String currentUser) {
        try {
            statement.executeUpdate("DELETE FROM " + GROUPS_TABLE + " WHERE group_name = '" + selectedGroup.getGroupName()
                    + "' AND user = '" + currentUser + "' ");
            //trying And operator for mysqlllll...............................
        } catch (SQLException sql) {
            sql.printStackTrace();
            System.out.println("And Operator doesn't work");
        }
    }

    public void removeGroupMsgs(Group selectedGroup, String currentUser) {
        try {
            statement.executeUpdate("DELETE FROM " + GROUP_MESSAGES_TABLE + " WHERE group_name = '" + selectedGroup.getGroupName()
                    + "' AND user = '" + currentUser + "' ");
        } catch (SQLException sql) {
            sql.printStackTrace();
            System.out.println("And Operator doesn't work while removing msgs");

        }
    }

}
