package com.example.finalpro;

import java.io.Serializable;
import java.util.ArrayList;

public class Response<O> implements Serializable {

    private int status; // 200 mean success, 500 mean error
    private O message; // the response message

    /**
     * init the response
     * @param status
     * @param message
     */
    public Response(int status, O message) {
        this.status = status;
        this.message = message;
    }

    /**
     *
     * @return the status of the response
     */
    public int getStatus() {
        return status;
    }

    /**
     * set status value
     * @param status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     *
     * @return the object message
     */
    public O getMessage() {
        return message;
    }

    /**
     * set the message
     * @param message
     */
    public void setMessage(O message) {
        this.message = message;
    }
}
