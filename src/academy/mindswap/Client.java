package academy.mindswap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Locale;

public class Client {

    Socket socket;
    String server;
    int port;
    Thread receiveThread;

    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public void start() throws IOException, InterruptedException {
        socket = new Socket(server, port);

        receiveThread = new Thread(new ReceiveThread(socket));
        receiveThread.start();
        while (!socket.isClosed()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            sendMessage(reader.readLine());
        }
    }

    private void quit() throws InterruptedException, IOException {
        receiveThread.join();
        socket.close();
    }

    private void sendMessage(String message) {
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(message);
            if (message.toLowerCase().startsWith("/quit")) {
                quit();
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        Client client = new Client("localhost", 8080);
        try {
            client.start();
        } catch (IOException | InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }

    private class ReceiveThread implements Runnable {

        BufferedReader message;
        Socket socket;

        public ReceiveThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                message = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            String message;
            while ((message = receiveMessage()) != null) {
                System.out.println(message);
            }
        }

        private String receiveMessage() {
            String line = null;

            try {
                line = message.readLine();

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            return line;
        }
    }
        private void  newFeature(){
            //feature done
    }
}
