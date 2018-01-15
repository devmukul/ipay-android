package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;

import java.util.List;

public class AddCardResponse {

    private String message;
    private List<CardDetails> userCardList;

    public String getMessage() {
        return message;
    }

    public List<CardDetails> getUserCardList() {
        return userCardList;
    }
}