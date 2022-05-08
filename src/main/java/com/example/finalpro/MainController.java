package com.example.finalpro;

import com.example.finalpro.background.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.ConnectException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainController {
    @FXML
    public Button startProgram;
    @FXML
    public Button name;
    @FXML
    public Button knownPeers;
    @FXML
    public TextField theOtherPeerListeningPort;
    @FXML
    public Button itsMeBtn;
    @FXML
    public TextField fileNameField;
    @FXML
    public Button fileBtn;
    @FXML
    public Button downloadBtn;
    @FXML
    public Button byeBtn;
    @FXML
    public ListView listResponse;
    @FXML
    public TextField folderField;
    @FXML
    private TextField username;
    @FXML
    private TextField port;
    @FXML
    public Text errorMessage;

    /**
     * handle startiing server action
     */
    @FXML
    protected void onSettingChange() {



        int portField = Integer.parseInt(port.getText());

        if(portAvailable(portField)){
            errorMessage.setText("");
            startProgram.setDisable(true);
            username.setDisable(true);
            port.setDisable(true);

            name.setDisable(false);
            fileBtn.setDisable(false);
            downloadBtn.setDisable(false);
            itsMeBtn.setDisable(false);
            byeBtn.setDisable(false);
            knownPeers.setDisable(false);

            // needed when you are using gui
            (new Thread(){
                @Override
                public void run() {
                    Main.setupBasicSettings(portField,   username.getText(), folderField.getText());
                }
            }).start();

        }else{
            errorMessage.setText("port is not available");
        }
    }

    /**
     * is available port
     * @param port
     * @return
     */
    public static boolean portAvailable(int port) {

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {

                }
            }
        }

        return false;
    }

    public void setListViewText(String value){
        listResponse.getItems().add(value);
    }
    /**
     * handle the peer name action
     */
    @FXML
    public void searchAvailablePeers() {
        try {
            Client client = new Client(Integer.parseInt(theOtherPeerListeningPort.getText()), Config.HOSTNAME);
            Response<String> response  = (Response<String>) client.askNameToPeer();

            if(response.getStatus() == Config.SUCCESS){
                String message = response.getMessage().toString();
                listResponse.getItems().add("(SUCCESS) "+  message  );
            }
        } catch (ConnectException e) {
            listResponse.getItems().add("(ERROR) peer not found" );
        }

    }

    /**
     * handle the known peer action
     */
    @FXML
    public void getKnownPeers(ActionEvent actionEvent) {
         try {Client client = new Client(Integer.parseInt(theOtherPeerListeningPort.getText()), Config.HOSTNAME );
            Response response = client.knownPeers();
            if(response.getStatus() == Config.SUCCESS)
            {
                HashMap<Integer, Peerr> peers = (HashMap<Integer, Peerr>)response.getMessage();
                listResponse.getItems().add("(SUCCESS) this peer knows "+peers.size()+" other peers" );
                peers.entrySet().stream().forEach(entry -> {
                    Peerr peerr = entry.getValue();
                    String username = peerr.getUsername();
                    listResponse.getItems().add("  this peer knows the peer "+username );
                });
                listResponse.getItems().add("  end of list" );
            }
        } catch (ConnectException e) {
             listResponse.getItems().add("(ERROR) peer not found" );
        }

    }

    /**
     * handle the "it's me" action
     */
    @FXML
    public void greetingPeer(ActionEvent actionEvent) {
        try {
            Client client = new Client(Integer.parseInt(theOtherPeerListeningPort.getText()), Config.HOSTNAME );
            Response response = client.greetingOtherPeer();
            if(response.getStatus() == Config.SUCCESS)
            {
                String message = response.getMessage().toString();
                listResponse.getItems().add("(SUCCESS) "+message );
            }
            else if(response.getStatus() == Config.ERROR){
                String errorMesasge = response.getMessage().toString();
                listResponse.getItems().add("(ERROR) "+errorMesasge );

            }
        } catch (ConnectException e) {
            listResponse.getItems().add("(ERROR) peer not found" );
        }

    }

    /**
     * handle ask file action
     */
    @FXML
    public void askFile(ActionEvent actionEvent) {
        try {
            Client client = new Client(Integer.parseInt(theOtherPeerListeningPort.getText()), Config.HOSTNAME );
            client.doYouHaveThisFile(fileNameField.getText(), Main.myPort);
        } catch (ConnectException e) {
            listResponse.getItems().add("(ERROR) peer not found" );
        }


    }

    /**
     * download action
     * @param actionEvent
     */
    @FXML
    public void downloadFile(ActionEvent actionEvent) {
    }

    /**
     * disconnect action
     * @param actionEvent
     */
    @FXML
    public void disconnectFromNetwork(ActionEvent actionEvent) {
        try {
            Client client = new Client(Integer.parseInt(theOtherPeerListeningPort.getText()), Config.HOSTNAME );
            client.bye(Main.myPort);
        } catch (ConnectException e) {
            listResponse.getItems().add("(ERROR) peer not found" );
        }
    }
}
