package ChattingApp.OtherClasses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class Client extends Thread {
    private Socket connectionSocket;
    private BufferedReader input;
    private PrintWriter output;
    private String userName;
    private List<Client> clientsList;
    private String message;
    private String currentUser;
    public boolean isClientConnected = false;


    public Client(Socket connectionSocket) {
        this.connectionSocket = connectionSocket;
//        this.userName = userName;
        setStreams();
    }


    public String getUserName() {
        return this.userName;
    }

    private void setStreams() {
        try {
            //should input be in a while loop infinite.?
            System.out.println(connectionSocket.getInetAddress());
            output = new PrintWriter(connectionSocket.getOutputStream(), true); // true for auto flushing.
            input = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

        } catch (IOException ioe) {
            ioe.getStackTrace();
            System.out.println(ioe.getMessage());
        }

    }


    //Public outer methods for messenger to use.
    public String getInput() throws IOException {
        if (input != null) {
            System.out.println("Waiting for input message...");
            String msg;
            if ((msg = input.readLine()) != null) {
//                System.out.println("msg received is" + msg);
                return msg;    //input is on this another thread
            } else {
                System.out.println("Msg is null...");
            }
        }
        return null;
    }


    public void sendOutput(String message) {
        if (output != null) {
            output.println(message);   // output should be sent on another thread as well    // the bug was in print of print writer... println works fine.
            System.out.println("output msg sent.");
        } else {
            System.out.println("couldn't send message");
        }
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setServerClientName(String currentUser) {
        this.currentUser = currentUser;
    }

    public void setClientsList(List<Client> clientsList) {
        this.clientsList = clientsList;
    }

    public void setServerClientInput(String message) {
        this.message = message;
    }

    public String getServerClientInput() {
//        System.out.println("message-> " + message);
        return this.message;
    }

    @Override
    public void run() {
        while (true) {
            //This thread might be a problem for group messages as it is the only thread that listens to all group members msgs
            if (connectionSocket.isConnected()) {
                try {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Waiting for messages...");
                    String input = getInput();
                    //list of receiver user names last index contains msg
                    //When the message is single chat message and contains only one receiver
                    String recUsername = input.split(":")[0];
                    String message = input.split(":")[1];
                    System.out.println("Thread input message is-> " + message);
                    System.out.println("Receiver Username " + recUsername);
                    System.out.println("Current Username " + currentUser);
                    sendServerMessage(recUsername, message);
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    break;
                }
            }
        }
    }

    void sendServerMessage(String recUsername, String message) {
        if (recUsername.equals(currentUser)) {
            setServerClientInput(userName + ":" + message);
            isClientConnected = true;
            Server.isConnected = true;
        } else {
            //this is when some other client sends to another client not this server client
            for (Client client : clientsList) {
                if (client.getUserName().equals(recUsername)) {
                    client.sendOutput(userName + ":" + message);
                    isClientConnected = true;
                    Server.isConnected = true;
                    //if current user is not the receiver then find the client in the list
                    //if that client is equal to the receiver username
                    //then send it,  this message sent by this username client
                    // this output will be the input of receiver client as it's connected to the server
                }
            }
        }
    }
}
