package com.example.finalpro.background;

import com.example.finalpro.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private int port;
    private ExecutorService pool;
    private Socket socket;

    /**
     * init client
     * @param port
     * @param hostname
     * @throws ConnectException
     */
    public Client(int port, String hostname ) throws ConnectException {
        this.port = port;
        try {
            socket = new Socket(hostname, this.port);
            System.out.println("Connection Client [OK]");
        } catch (ConnectException e) {
            throw new ConnectException();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * request the peer username
     * @return Response
     */
    public Response  askNameToPeer(){

        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // ask a connected peer to its name
            output.writeObject(Message.MESSAGE_TYPE.name.toString());

            // receive the peer nickname
            try {
                Response response = (Response<String>) input.readObject();
               return response;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            this.closeClient();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }

    /**
     * request to a peer its list of known peer
     * @return Response
     */
    public Response knownPeers() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            // asking this peer of the list of peer that it knows
            output.writeObject(Message.MESSAGE_TYPE.known_peers.toString());

            // receive the list of peers
            try {
                Response response = (Response) input.readObject();

                return response;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            this.closeClient();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * greeting other peer
     * @return Response
     */
    public Response greetingOtherPeer() {
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(Message.MESSAGE_TYPE.it_s_me.toString());
            output.writeObject(Main.myPort);
            output.writeObject(Config.HOSTNAME);
            output.writeObject(Main.myUsername);

            try {
                Response response = (Response) input.readObject();
              return response;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            this.closeClient();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * request a peer for the existence of a file
     * @param filename
     * @param portWhoAsk the  original peer who ask for the file
     * @return
     */
    public void doYouHaveThisFile(String filename, int portWhoAsk) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            output.writeObject(Message.MESSAGE_TYPE.file.toString());
            output.writeObject(filename);
            output.writeObject(portWhoAsk);
            output.writeObject(Config.CONSTANT_INTEGER);  // constant integer i

            this.closeClient();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * inform the peer of the file that it is looking for
     * @param filename
     */
    public void voila(String filename ) {

        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(socket.getInputStream());

            output.writeObject(Message.MESSAGE_TYPE.voila.toString());
            output.writeObject(Main.myPort);
            output.writeObject(filename);

            this.closeClient();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * close client
     */
    public void closeClient(){
        try {
            this.socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * no longer want to be part of the network
     */
    public void bye(int portWhoAsk) {
        try {
            ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

            output.writeObject(Message.MESSAGE_TYPE.bye.toString());
            output.writeObject(portWhoAsk);

            this.closeClient();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
