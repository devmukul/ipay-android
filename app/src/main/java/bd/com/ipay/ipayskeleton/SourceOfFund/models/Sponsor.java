package bd.com.ipay.ipayskeleton.SourceOfFund.models;


import java.io.Serializable;

public class Sponsor implements Serializable {
    private long id;
    private String initiatedBy;
    private long monthlyCreditLimit;
    private String relationship;
    private String status;
    private String updatedAt;
    private User user;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(String initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public long getMonthlyCreditLimit() {
        return monthlyCreditLimit;
    }

    public void setMonthlyCreditLimit(long monthlyCreditLimit) {
        this.monthlyCreditLimit = monthlyCreditLimit;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}