import java.io.IOException;
import java.net.Socket;

public class Main {
    public static Thread t1 = null;
    public static Thread t2 = null;
    //Producer Consumer example for thread synchronization (thread safety, Critical section).
    public static void main(String[] args) {
        /////////////////////////
        //Please don't forget to set the flushing to true in the printWriter parameter output.
        try (Socket socket = new Socket("localhost", 5000)) {
            // Both writer and reader threads will use the same object Message's methods, read and write,
            // that's why they got to be synchronized.
//            Thread.currentThread().setName("Client");
        //////////////////////...LOgic here now Reader and writer concurrency.
            boolean threads = true;
            while (true) {
//                 output = new PrintWriter(socket.getOutputStream(), true);
//                 input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                Message msg = new Message(socket);
                if (threads) {
                    t1 = new Thread(new Writer(socket));
                    t1.start();
                    t2 = new Thread(new Reader(socket));
                    t2.start();
                    threads = false;
                }
                if (!t1.isAlive()) {
                    //when both threads are dead
                    //break out of the loop so that socket gets
                    // stopped by try with resources automatically.
                    break;
                }
            }
        } catch (
                IOException ioe) {
            System.out.println("Clients IOE ");
        }
    }
}


