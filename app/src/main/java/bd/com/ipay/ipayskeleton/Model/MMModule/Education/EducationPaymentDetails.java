package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class EducationPaymentDetails implements Parcelable {
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
    private Integer status;
    private String transactionId;
    private Long updateTime;
    private EducationalInvoice educationalInvoice;
    private List<InvoicePayableAccountRelation> invoicePayableAccountRelations = new ArrayList<>();

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeValue(this.creationTime);
        dest.writeValue(this.deliveryTime);
        dest.writeString(this.receiverAccountId);
        dest.writeValue(this.receiverAmount);
        dest.writeValue(this.discount);
        dest.writeString(this.requestIp);
        dest.writeValue(this.requestPort);
        dest.writeValue(this.requestTime);
        dest.writeString(this.senderAccountId);
        dest.writeValue(this.senderAmount);
        dest.writeValue(this.status);
        dest.writeString(this.transactionId);
        dest.writeValue(this.updateTime);
        dest.writeParcelable(this.educationalInvoice, flags);
        dest.writeList(this.invoicePayableAccountRelations);
    }

    public EducationPaymentDetails() {
    }

    protected EducationPaymentDetails(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.creationTime = (Long) in.readValue(Long.class.getClassLoader());
        this.deliveryTime = (Long) in.readValue(Long.class.getClassLoader());
        this.receiverAccountId = in.readString();
        this.receiverAmount = (Double) in.readValue(Double.class.getClassLoader());
        this.discount = (Double) in.readValue(Double.class.getClassLoader());
        this.requestIp = in.readString();
        this.requestPort = (Integer) in.readValue(Integer.class.getClassLoader());
        this.requestTime = (Long) in.readValue(Long.class.getClassLoader());
        this.senderAccountId = in.readString();
        this.senderAmount = (Double) in.readValue(Double.class.getClassLoader());
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.transactionId = in.readString();
        this.updateTime = (Long) in.readValue(Long.class.getClassLoader());
        this.educationalInvoice = in.readParcelable(EducationalInvoice.class.getClassLoader());
        this.invoicePayableAccountRelations = new ArrayList<>();
        in.readList(this.invoicePayableAccountRelations, InvoicePayableAccountRelation.class.getClassLoader());
    }

    public static final Parcelable.Creator<EducationPaymentDetails> CREATOR = new Parcelable.Creator<EducationPaymentDetails>() {
        @Override
        public EducationPaymentDetails createFromParcel(Parcel source) {
            return new EducationPaymentDetails(source);
        }

        @Override
        public EducationPaymentDetails[] newArray(int size) {
            return new EducationPaymentDetails[size];
        }
    };
}
