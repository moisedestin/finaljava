package com.example.finalpro.background;

import com.example.finalpro.MainController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private int port;
    private int poolSize;
    private ExecutorService pool;
    private ServerSocket serverSocket;

    /**
     * init server
     * @param port
     * @param poolSize
     */
    public Server(int port, int poolSize) {
        this.port = port;
        this.poolSize = poolSize;
        try {
            serverSocket = new ServerSocket(this.port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.pool = Executors.newFixedThreadPool(this.poolSize);
    }


    /**
     * starting server
     */
    public void startServer(){
        Socket client = null;
        try {
            this.serverSocket.setSoTimeout(1000);
            while (true) {
                try {
                    client = this.serverSocket.accept();
                    System.out.println("Connection  [OK] ");
                    System.out.println("Client [connected]: " + client.getInetAddress().getHostAddress());
                    Slave slave = new Slave(client);
                    this.pool.submit(slave);
                } catch (IOException e) {
//                    e.printStackTrace();
                }

            }

        } catch (SocketException e) {
//            e.printStackTrace();

        }


    }



}
