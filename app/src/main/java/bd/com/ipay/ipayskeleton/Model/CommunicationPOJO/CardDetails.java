package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO;

public class CardDetails {

    private  long accountId;
    private long id;
    private String cardInfo;
    private String cardStatus;

    public CardDetails(long accountId, long id, String cardInfo, String cardStatus) {
        this.accountId = accountId;
        this.id = id;
        this.cardInfo = cardInfo;
        this.cardStatus = cardStatus;
    }

    public long getAccountId() {
        return accountId;
    }

    public long getId() {
        return id;
    }

    public String getCardInfo() {
        return cardInfo;
    }

    public String getCardStatus() {
        return cardStatus;
    }
}


