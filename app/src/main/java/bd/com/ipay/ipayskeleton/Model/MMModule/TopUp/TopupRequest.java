package bd.com.ipay.ipayskeleton.Model.MMModule.TopUp;

public class TopupRequest {

    private long senderAccountID;
    private String receiverMobileNumber;
    private int mobileNumberType;
    private int operatorCode;
    private double amount;
    private String countryCode;
    private int senderAccountUserType;
    private int senderAccountUserClass;
    private String pin;

    public TopupRequest(long senderAccountID, String receiverMobileNumber,
                        int mobileNumberType, int operatorCode, double amount, String countryCode,
                        int senderAccountUserType, int senderAccountUserClass, String pin) {
        this.senderAccountID = senderAccountID;
        this.receiverMobileNumber = receiverMobileNumber;
        this.mobileNumberType = mobileNumberType;
        this.operatorCode = operatorCode;
        this.amount = amount;
        this.countryCode = countryCode;
        this.senderAccountUserClass = senderAccountUserClass;
        this.senderAccountUserType = senderAccountUserType;
        this.pin = pin;
    }
}
