package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import java.util.ArrayList;
import java.util.List;

public class EducationPaymentDetails {
    private Integer id;
    private Long creationTime;
    private Long deliveryTime;
    private String receiverAccountId;
    private Double receiverAmount;
    private Double discount;
    private String requestIp;
    private Integer requestPort;
    private Long requestTime;
    private String senderAccountId;
    private Double senderAmount;
    private Object senderInformation;
    private Integer status;
    private String transactionId;
    private Long updateTime;
    private EducationalInvoice educationalInvoice;
    private List<InvoicePayableAccountRelation> invoicePayableAccountRelations = new ArrayList<InvoicePayableAccountRelation>();

    public Integer getId() {
        return id;
    }

    public Long getCreationTime() {
        return creationTime;
    }

    public Long getDeliveryTime() {
        return deliveryTime;
    }

    public String getReceiverAccountId() {
        return receiverAccountId;
    }

    public Double getReceiverAmount() {
        return receiverAmount;
    }

    public Double getDiscount() {
        return discount;
    }

    public String getRequestIp() {
        return requestIp;
    }

    public Integer getRequestPort() {
        return requestPort;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public String getSenderAccountId() {
        return senderAccountId;
    }

    public Double getSenderAmount() {
        return senderAmount;
    }

    public Object getSenderInformation() {
        return senderInformation;
    }

    public Integer getStatus() {
        return status;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public EducationalInvoice getEducationalInvoice() {
        return educationalInvoice;
    }

    public List<InvoicePayableAccountRelation> getInvoicePayableAccountRelations() {
        return invoicePayableAccountRelations;
    }
}
