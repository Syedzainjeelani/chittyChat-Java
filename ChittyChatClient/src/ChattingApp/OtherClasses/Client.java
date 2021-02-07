package ChattingApp.OtherClasses;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client extends Thread {
    private Socket connectionSocket;
    private BufferedReader input;
    private PrintWriter output;
    private String currentUser;
    public boolean isConnected = false;

    public Client(Socket connectionSocket, String currentUser) {
      this.connectionSocket = connectionSocket;
        this.currentUser = currentUser;
    }

    public void run() {
        getConnections();
    }

    private void getConnections() {
        //can put while loop for recurring connection check but will hang the app
        try {
            output = new PrintWriter(connectionSocket.getOutputStream(), true);
            input = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            System.out.println("Client Connection established");
            output.println(currentUser);
            System.out.println("Current username sent: " + currentUser);
            if (connectionSocket.isConnected()) {
                isConnected = true;
            }
            // Closing the socket ???.................................
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println("Socket Connection Problem :::> " + ioe.getMessage());

        }
    }

    //Public outer Methods... for messenger to use.
    public String getInputMessage() throws IOException {
        if (input != null) {
            System.out.println("Waiting for input message...");
            String msg;
            if ((msg = input.readLine()) != null) {
                return msg;
            }
        }
        return null;
    }

    public void sendOutputMessage(String selectedUser, String message) {////////check selected friend this.........................
        if (output != null) {
            output.println(selectedUser + ":" + message);
            System.out.println("output msg sent -> " + message);
        } else {
            System.out.println("couldn't send message");
        }
    }

    public void sendGroupMessage(Group selectedGroup, String message) {
        for (String friend : selectedGroup.getGroupFriendsList()) {
            //here friend is the selected group friends..
            if (output != null) {
//                output.println(friend + ":" + message);
                //trying to send group name too for the pop up
                if(!friend.isEmpty()) {
                    output.println(selectedGroup.getGroupName() + ":" + friend + ":" + message);
                }

            } else {
                System.out.println("couldn't send group message");

            }
        }
    }

}
