package bd.com.ipay.ipayskeleton.Model;

import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.CardDetails;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class GetCardResponse {
    private String message;
    private List<CardDetails> userCardList;

    public String getMessage() {
        return message;
    }

    public List<CardDetails> getUserCardList() {
        return userCardList;
    }

    public boolean isAnyCardVerified() {
        if (!userCardList.isEmpty()) {
            for (CardDetails cardDetail : userCardList) {
                if (cardDetail.getCardStatus().equals(Constants.VERIFIED))
                    return true;
            }
        }
        return false;
    }
}
