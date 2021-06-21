package academy.mindswap;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static academy.mindswap.Messages.*;
import static academy.mindswap.Commands.*;

public class Server {
    private ServerSocket serverSocket;
    private final ExecutorService pool;
    private final int port;
    private final List<ClientHandler> clientHandlerList;
    public Server(int port, int poolSize){
        this.port = port;
        pool = Executors.newFixedThreadPool(poolSize);
        clientHandlerList = new LinkedList<>();
    }

    public static void main(String[] args) {
        Server server = new Server(8080,10);
        try {
            server.startServer();
            server.listen();
        }catch (IOException e){
            System.out.println(e.getMessage());
        }

    }
    private void startServer() throws IOException {
        serverSocket = new ServerSocket(port);
    }

    private void listen() throws IOException {
        while (serverSocket.isBound()) {
            ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
            clientHandlerList.add(clientHandler);
            clientHandler.task= pool.submit(clientHandler);

        }
    }

    private void broadcast(String message, ClientHandler clientHandler) {
        clientHandlerList.stream()
                .filter(cl -> !cl.equals(clientHandler))
                .forEach(cl -> cl.sendMessage(clientHandler.userName+" says: " + message));
    }

    private void listUsers(ClientHandler clientHandler){
        clientHandlerList
                .forEach(cl -> clientHandler.sendMessage(cl.userName));
    }

    private void whisper(String messageToSend, String userToWhisper, String senderUserName){
        clientHandlerList.stream()
                .filter(cl -> cl.userName .equals(userToWhisper)).
                forEach(c -> c.sendMessage(senderUserName+" whispers: " +messageToSend) );
    }

    private boolean userNameExists(String newUserName){
      return  clientHandlerList.stream()
               .anyMatch(cl ->cl.userName.equals(newUserName));
    }

    private void removeClient(ClientHandler clientHandler){
        clientHandler.task.cancel(true);
        clientHandlerList.remove(clientHandler);

    }



    private class ClientHandler implements Runnable{
        private final Socket clientSocket;
        private String userName ="temp00";
        private Future task;

        public ClientHandler(Socket clientSocket){
            this.clientSocket=clientSocket;
        }


        private String receiveMessage()  {
            String line=null;
            try {
                BufferedReader message= new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                line=message.readLine();
            }catch (IOException e){
                System.out.println("1");
                System.out.println(e.getMessage());
            }
            return line;
        }

        private void sendMessage(String message)  {
            try{
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);
                out.println(message);
            }catch (IOException e){
                System.out.println("2");
                System.out.println(e.getMessage());
            }
        }

        private void askName()  {
            sendMessage(INPUT_NAME);
            String tryUserName = receiveMessage();
            if(userNameExists(tryUserName)){
               sendMessage(USER_ALREADY_EXISTS);
               askName();
               return;
            }
            userName=tryUserName;
            sendMessage(GREETING + userName);
        }

        private void quit(boolean sendGoodBye){
            try {
                if(sendGoodBye){
                    sendMessage(GOODBYE + userName);
                }
                clientSocket.close();
                removeClient(this);
            } catch (IOException e) {
                System.out.println("3");
                e.getMessage();
            }
        }


        private void executeCommand(String message){

            message = message.replaceFirst("/","");
            String[] commands = message.split(" ",3);
            switch (commands[0].toLowerCase()){
                case QUIT:
                    quit(true);
                    break;
                case LIST:
                    sendMessage(USER_LIST);
                    listUsers(this);
                    break;
                case WHISPER:
                    String userToWhisper=commands[1];
                    if(!userNameExists(userToWhisper)){
                        sendMessage(USER_NOT_EXISTS);
                        break;
                    }
                    if(commands.length<3){
                        sendMessage(MESSAGE_NOT_PROVIDED);
                        break;
                    }
                    whisper(commands[2] , userToWhisper, userName);
                    break;
                case HELP:
                    sendMessage(FULL_HELP);
                    break;
                default:
                    sendMessage(COMMAND_NOT_EXISTS);
                    break;
            }

        }

        @Override
        public void run() {
                sendMessage(WELCOME);
                askName();
                sendMessage(HELP_HELP);
                String message;
                while ((message=receiveMessage())!=null){
                    if(message.startsWith("/")){
                        executeCommand(message);
                        continue;
                    }
                    broadcast(message,this);
                }
                quit(false);
        }
    }
}
