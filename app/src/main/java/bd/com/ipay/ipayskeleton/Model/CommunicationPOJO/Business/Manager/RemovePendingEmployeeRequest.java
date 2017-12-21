package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager;


public class RemovePendingEmployeeRequest {

    private String status;
    private long id;

    public RemovePendingEmployeeRequest(long id, String status) {
        this.status = status;
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
