import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;

public class Writer implements Runnable {
    private Socket socket;
    private Message message;
    private Scanner scanner;
    private Random random;

    public Writer(Socket socket) {
        this.socket = socket;
//        this.message = message;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
    }

    public void run() {
        while(true) {

            //Use linked list to write/read meg first..
            System.out.print("Enter msg: " );
            String msg = scanner.nextLine();
            write(msg);
//            message.write(msg);
            if(msg.equals("exit")) {
                System.out.println("Writer thread terminating");
                break;
            }
            try{
                Thread.sleep(random.nextInt(2000));
            }catch(InterruptedException ie) {

            }
        }
    }

    public void write(String msg) {
        try {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);
            output.println(msg);
        }catch(IOException ioe) {
            System.out.println("Output Writer Exception " + ioe.getMessage());
        }
    }
}
