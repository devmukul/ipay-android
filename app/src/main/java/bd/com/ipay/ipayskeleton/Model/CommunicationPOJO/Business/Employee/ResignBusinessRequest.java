package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Employee;

class ResignBusinessRequest {
    private final long associationId;
    private final String status;

    public ResignBusinessRequest(long associationId, String status) {
        this.associationId = associationId;
        this.status = status;
    }
}
