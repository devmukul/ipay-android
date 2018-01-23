package bd.com.ipay.ipayskeleton.Model;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.CardDetails;

public class GetCardResponse {
    private String message;
    private List<CardDetails> userCardList;

    public String getMessage() {
        return message;
    }

    public List<CardDetails> getUserCardList() {
        return userCardList;
    }
}
