package bd.com.ipay.ipayskeleton.Model.SqLiteDatabase;

public class SubscriberEntry {

    private String mobileNumber;
    private String name;

    public SubscriberEntry(String mobileNumber, String name) {
        this.mobileNumber = mobileNumber;
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getName() {
        return name;
    }

}
