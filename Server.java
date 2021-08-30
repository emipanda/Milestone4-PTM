package test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public interface ClientHandler {
        // define...
        public void clientHandler(InputStream inFromClient, OutputStream outToClient);

    }

    volatile boolean stop;

    public Server() {
        stop = false;
    }


    private void startServer(int port, ClientHandler ch) {
        // implement here the server...
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(1000);
            while (!stop) {
                Socket client = serverSocket.accept();
                InputStream in = client.getInputStream();
                OutputStream out = client.getOutputStream();
                ch.clientHandler(in, out);
                client.close();
            }
            serverSocket.close();
        } catch (Exception e) {

        }
    }

    // runs the server in its own thread
    public void start(int port, ClientHandler ch) {
        new Thread(() -> startServer(port, ch)).start();
    }

    public void stop() {
        stop = true;
    }
}
