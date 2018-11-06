package bd.com.ipay.ipayskeleton.SourceOfFund.models;


public class AddSponsorRequest {

    private String mobileNumber;
    private String relation;

    public AddSponsorRequest(String mobileNumber, String relation) {
        this.mobileNumber = mobileNumber;
        this.relation = relation;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

    public String getMobileNumber() {

        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
