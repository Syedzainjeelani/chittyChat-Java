import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;

public class Reader implements Runnable {
    private Socket socket;
    private Message message;
    private Random random;

    public Reader(Socket socket) {
        this.socket = socket;
//        this.message = message;
        this.random = new Random();
    }

    public void run() {
        while (true) {
//            String msg = message.read();
            String msg = read();
            if (msg == null || msg.equals("exit")) {
                System.out.println("Reader thread terminating");
                break;
            }
            System.out.println("\n" + msg);
            try {
                Thread.sleep(random.nextInt(2000));
            } catch (InterruptedException ie) {

            }
        }
    }

    public String read() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            return input.readLine();
        } catch (IOException ioe) {
            System.out.println("Input Reader Exception " + ioe.getMessage());
        }
        return "";
    }

}
