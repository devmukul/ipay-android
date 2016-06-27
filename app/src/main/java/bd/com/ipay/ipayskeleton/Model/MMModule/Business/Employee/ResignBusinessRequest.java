package bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee;

public class ResignBusinessRequest {
    private long associationId;
    private String status;

    public ResignBusinessRequest(long associationId, String status) {
        this.associationId = associationId;
        this.status = status;
    }
}
