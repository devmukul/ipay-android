package bd.com.ipay.ipayskeleton.Model.MMModule.Business;

public class ResignBusinessRequest {
    private int associationId;
    private String status;

    public ResignBusinessRequest(int associationId, String status) {
        this.associationId = associationId;
        this.status = status;
    }
}
