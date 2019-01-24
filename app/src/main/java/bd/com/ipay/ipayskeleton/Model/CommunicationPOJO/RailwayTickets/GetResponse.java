package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RailwayTickets;

import java.io.Serializable;

public class GetResponse implements Serializable {

    String message;

    public GetResponse() {
    }

    public GetResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "GetResponse{" +
                "message='" + message + '\'' +
                '}';
    }
}
