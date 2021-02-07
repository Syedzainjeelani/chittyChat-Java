import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.Buffer;

public class Message {
    private String message;
    private Socket socket;
    private BufferedReader input;
    private PrintWriter output;  //threads use fields as a shared resource.
    private boolean wait = false;

    public Message(Socket socket) {
        this.socket = socket;
    }
//
//    public Message(BufferedReader input, PrintWriter output) {
//        this.input = input;
//        this.output = output;
//    }

    //Both read and write methods are synchronized because when one thread has called the on of these methods
    //no other thread will be able to call either of these methods
    //so that we are only able to read data when the one thread has completed writing the data and vice versa.
    public synchronized void write(String message) {
        while (wait) {
            try {
                wait();
                //this makes the current thread wait until the other thread
                //calls notify, which will wakeup the current thread, that which is holding on to the
                //intrinsic lock of the shared object.
            } catch (InterruptedException ie) {

            }
        }
        wait = true;
        try {
            output = new PrintWriter(socket.getOutputStream(), true);
            output.println(message);
        }catch(IOException ioe) {
            System.out.println("Output Writer Exception " + ioe.getMessage());
        }
//        this.message = message;
        notifyAll(); //this will wake up all the threads that were waiting for objects lock
    }

    public synchronized String read() {
        String inputMsg = "";
        while (!wait) {
            try {
                wait();
            } catch (InterruptedException ie) {

            }
        }
        wait = false;
        try{
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            inputMsg = input.readLine();
        }catch(IOException ioe) {
            System.out.println("Input Reader Exception " + ioe.getMessage());
        }
        notifyAll();
        return inputMsg;
    }

}
