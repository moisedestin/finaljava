package com.example.finalpro;

import java.io.Serializable;

public class Peerr implements Serializable {
    private int portNumber;
    private String hostName;
    private String username;

    /**
     * init peer
     * @param portNumber
     * @param hostName
     * @param username
     */
    public Peerr(int portNumber, String hostName, String username) {
        this.portNumber = portNumber;
        this.hostName = hostName;
        this.username = username;
    }

    /**
     * get port number
     * @return
     */
    public int getPortNumber() {
        return portNumber;
    }

    /**
     * get username
     * @return
     */
    public String getUsername() {
        return username;
    }

    /**
     * get hostname
     * @return
     */
    public String getHostName() {
        return hostName;
    }
}
