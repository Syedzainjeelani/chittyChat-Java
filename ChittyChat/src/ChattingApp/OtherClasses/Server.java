package ChattingApp.OtherClasses;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    //    private Socket connectionSocket;
    private String userName;
    private List<Client> clients;
    public static boolean isConnected = false;
    private String inpUsername;

    public Server() {
//        this.userName = userName;
        clients = new ArrayList<>();
    }

    @Override
    public void run() {
        getConnections();
    }

    private void getConnections() {
        /**
         //All the messages come and go from here '
         //Every client must be connected to a database to be able to access all other users and the messages.
         // we need to create a new thread for each client that connects to this server
         //This server receives messages from multiple clients in group chat and
         // prints msg on all the clients including this one with a loop on the list of all clients.
         // also store the messages on the database including username and password.
         // With each thread having a username we can send messages in single chat as well.
         **/
        try (ServerSocket serverSocket = new ServerSocket(3033)) {
            while (true) {
                // Server always listens for the connection and creates clients (individual threads for each)

                System.out.println("Server Thread waiting for new connections...");
                Socket socket = serverSocket.accept();
                Client client = new Client(socket);
                inpUsername = client.getInput();
                System.out.println("first client: " + inpUsername + " Current user = " + userName);
//                client.sendOutput(userName);
                client.setUserName(inpUsername);
                client.setServerClientName(userName);
                clients.add(client);
                client.setClientsList(clients);
                client.start();
                if (socket.isConnected()) {
                    isConnected = true;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException ioException) {
            ioException.getStackTrace();
            System.out.println(ioException.getMessage());
        }
    }


    ////////////////////////Input output methods....
    public String getInputMessage(String selectedFriendName) throws IOException {
        if (clients.size() > 0) {
            System.out.println("Friend name -> " + selectedFriendName);
            Client client = searchClient(selectedFriendName);
            if (client != null && client.isClientConnected) {
                String inputMsg = client.getServerClientInput();
                System.out.println("Client is not null.. and it is connected");
                client.isClientConnected = false;
                return inputMsg;
            } else {
                System.out.println("Client is null with selected friend");
            }
        }
        return null;
    }

    public void sendOutputMessage(String selectedFriend, String message) {
        if (clients.size() > 0) {
            Client client = searchClient(selectedFriend);
            if (client != null) {
                client.sendOutput(message);
            }
        }
    }

    public void sendGroupMessage(Group group, String message) {
    // send this message to all the clients in the list that are in the selected group ,loop it
        for (String friend : group.getGroupFriendsList()) {
            Client client = searchClient(friend);
            if(client != null) {
                client.sendOutput(message);
            }

        }

    }

    public String receiveGroupMessage(Group selectedGroup) throws IOException{
        if(clients.size() > 0) {
            for(String friend: selectedGroup.getGroupFriendsList()) {
                Client client = searchClient(friend);
                if (client != null && client.isClientConnected) {
//                    String inputMsg = client.getInput();
                    String inputMsg = client.getServerClientInput();
                    System.out.println("Group Client is not null.. and it is connected");
                    client.isClientConnected = false;
                    return inputMsg;
                } else {
                    System.out.println("Client is null with selected Group");
                }
            }
        }
        return null;
        // receives the messages of groups from their connected friends
    }

    public void setUserName(String userName) {
        this.userName = userName;
//        searchClient(inpUsername).setServerClientName(userName);//////////////

    }

    public Client searchClient(String friendName) {
        if (friendName != null) {
            for (Client client : clients) {
                if (client.getUserName().equals(friendName)) {
                    return client;
                }

            }
        } else {
            System.out.println("Can't Search Friend name: null");
        }
        return null;

    }

    public List<Client> getClientsList() {
        return this.clients;
    }
}
