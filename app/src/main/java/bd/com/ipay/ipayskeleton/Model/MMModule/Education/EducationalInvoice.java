package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

public class EducationalInvoice {
    private Integer educationalInvoiceId;
    private Double totalFee;
    private Integer status;
    private String creatorIpayAccountID;
    private String description;
    private Institution institute;
    private EventParticipant eventParticipant;
    private Session session;
    private Long creationTime;
    private Long updateTime;

    public Integer getEducationalInvoiceId() {
        return educationalInvoiceId;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public Integer getStatus() {
        return status;
    }

    public String getCreatorIpayAccountID() {
        return creatorIpayAccountID;
    }

    public String getDescription() {
        return description;
    }

    public Institution getInstitute() {
        return institute;
    }

    public EventParticipant getEventParticipant() {
        return eventParticipant;
    }

    public Session getSession() {
        return session;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Long getUpdateTime() {
        return updateTime;
    }
}