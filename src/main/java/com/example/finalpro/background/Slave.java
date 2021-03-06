package com.example.finalpro.background;

import com.example.finalpro.*;
import com.example.finalpro.database.DatabaseConnection;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Slave implements Runnable{
    private Socket socketClient;

    /**
     * init slave
     * @param socketClient
     */
    public Slave(Socket socketClient) {
        this.socketClient = socketClient;
    }


    /**
     * thread execution of a task
     */
    @Override
    public void run() {
        try {

            ObjectOutputStream output_client = new ObjectOutputStream(socketClient.getOutputStream());
            ObjectInputStream input_client = new ObjectInputStream(socketClient.getInputStream());

            Message.MESSAGE_TYPE messageType = Message.MESSAGE_TYPE.valueOf((String)input_client.readObject()) ;

            switch (messageType){
                case name :{
                    output_client.writeObject(new Response<String>(200, Main.myUsername));
                }
                break;
                case known_peers :
                    output_client.writeObject(new Response<HashMap<Integer, Peerr>>(200, DatabaseConnection.getPeers()));
                break;
                case it_s_me : {
                    int port = (int)input_client.readObject();
                    String hostName = (String)input_client.readObject();
                    String username = (String)input_client.readObject();
                    Peerr peerr = new Peerr(port, hostName, username);

                    if(DatabaseConnection.getPeers().size() == Config.MAXIMUM_CONFIGURED_PEER)
                        output_client.writeObject(new Response<String>(500, "maximum affortable peer reached"));
                    else{
                        if(DatabaseConnection.createOrUpdatePeer(peerr))
                            output_client.writeObject(new Response<String>(200, "You have been added"));
                        else
                            output_client.writeObject(new Response<String>(500, "could not be added"));

                    }

                }
                break;
                case file : {
                    String filename = (String)input_client.readObject();
                    int portWhoAsk = (int)input_client.readObject();

                    var wrapper = new Object(){ int i = (int)input_client.readObject(); }; // in order to user i inside lambda expression

                    String path = findFileInDir(filename, Main.myFolderName);

                    if(path != null){
                        Client client = new Client( portWhoAsk, Config.HOSTNAME);

                        client.voila(filename);
                        System.out.println("you have sent this file "+ filename+ " to "+portWhoAsk);

                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
                        fxmlLoader.load();
                        MainController mainController =  fxmlLoader.getController();

                        if (mainController != null) {
                            mainController.setListViewText("you have sent this file "+ filename+ " to "+portWhoAsk);
                        }
                    }
                    else{
                        Iterator<HashMap.Entry<Integer, Peerr>> entries = DatabaseConnection.getPeers().entrySet().iterator();
                        while (wrapper.i > 0 && entries.hasNext()) {
                            HashMap.Entry<Integer, Peerr> entry = entries.next();
                            wrapper.i--;
                            Peerr peerr = (Peerr) entry.getValue();
                            try {
                                Client client = new Client( peerr.getPortNumber(), Config.HOSTNAME);
                                client.doYouHaveThisFile(filename, portWhoAsk);

                            } catch (ConnectException e) {
                                e.printStackTrace();
                            }
                         }

                     }

                }
                break;
                case download : {
                    String filename = (String)input_client.readObject();
                    int portWhoAsk = (int)input_client.readObject();

                    File file = new File(Main.myFolderName+"/"+filename);
                    byte[] mybytearray = new byte[(int)file.length()];
                    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                    bis.read(mybytearray, 0, mybytearray.length);
                    output_client.write(mybytearray, 0, mybytearray.length);
                    output_client.flush();

                }
                break;
                case voila : {
                    int port = (int)input_client.readObject();
                    String filename = (String)input_client.readObject();

                    System.out.println("heres the file you are looking fo r"+ filename+ ", it was sent by "+port);
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
                    fxmlLoader.load();
                    MainController mainController =  fxmlLoader.getController();
                    if (mainController != null) {
                        mainController.setListViewText("heres the file you are looking for "+ filename+ ", it was sent by "+port);
                    }

                }
                break;
                case bye : {
                    int portWhoAsk = (int)input_client.readObject();


                    Iterator<HashMap.Entry<Integer, Peerr>> entries = DatabaseConnection.getPeers().entrySet().iterator();
                    while (entries.hasNext()) {
                        HashMap.Entry<Integer, Peerr> entry = entries.next();
                        Peerr peerr = (Peerr) entry.getValue();
                        try {
                            Client client = new Client( peerr.getPortNumber(), Config.HOSTNAME);
                            client.bye(portWhoAsk);

                        } catch (ConnectException e) {
                            e.printStackTrace();
                        }
                    }

                    if(DatabaseConnection.getPeers().containsKey(portWhoAsk)){
                        if(DatabaseConnection.deletePeer(portWhoAsk)){
                            System.out.println("the peer "+ portWhoAsk+ " has been removed " );
                            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main.fxml"));
                            fxmlLoader.load();
                            MainController mainController =  fxmlLoader.getController();
                            if (mainController != null) {
                                mainController.setListViewText("the peer "+ portWhoAsk+ " has been removed ");
                            }
                        }

                    }

                }
                break;
            }


            this.socketClient.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * find a file in a directory
     * @param fileName
     * @param searchDirectory
     * @return
     * @throws IOException
     */
    protected String findFileInDir(String fileName, String searchDirectory) throws IOException {
        return Arrays.stream((new File(searchDirectory)).list(new FilenameFilter() {
            @Override
            public boolean accept(File f, String name) {
                return name.contains(fileName);
            }
        })).map(String::toString).findFirst().orElse(null);
    }
}
