package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;

public class CardDetails {

    private String cardType;
    private String cardInfo;
    private String cardStatus;

    public CardDetails(String cardInfo, String cardStatus, String cardType) {
        this.cardType = cardType;
        this.cardInfo = cardInfo;
        this.cardStatus = cardStatus;
    }


    public String getCardInfo() {
        return cardInfo;
    }

    public String getCardType() {
        return cardType;
    }

    public String getCardStatus() {
        return cardStatus;

    }
}


