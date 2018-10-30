package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;

import java.util.ArrayList;


public class GetSavedCardsList {
    private String message;
    private ArrayList<MyCard>cardList;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<MyCard> getCardList() {
        return cardList;
    }

    public void setCardList(ArrayList<MyCard> cardList) {
        this.cardList = cardList;
    }
}
