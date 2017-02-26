package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education;

public class InvoicePayableAccountRelation {
    public Integer payableAccountHeadId;
    public Double fee;

    public Integer getPayableAccountHeadId() {
        return payableAccountHeadId;
    }

    public Double getFee() {
        return fee;
    }

    public void setPayableAccountHeadId(Integer payableAccountHeadId) {
        this.payableAccountHeadId = payableAccountHeadId;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public InvoicePayableAccountRelation() {
    }
}
