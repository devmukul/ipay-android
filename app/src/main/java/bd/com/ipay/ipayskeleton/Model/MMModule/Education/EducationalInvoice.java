package bd.com.ipay.ipayskeleton.Model.MMModule.Education;

import android.os.Parcel;
import android.os.Parcelable;

public class EducationalInvoice implements Parcelable {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.educationalInvoiceId);
        dest.writeValue(this.totalFee);
        dest.writeValue(this.status);
        dest.writeString(this.creatorIpayAccountID);
        dest.writeString(this.description);
        dest.writeParcelable(this.institute, flags);
        dest.writeParcelable(this.eventParticipant, flags);
        dest.writeParcelable(this.session, flags);
        dest.writeValue(this.creationTime);
        dest.writeValue(this.updateTime);
    }

    public EducationalInvoice() {
    }

    protected EducationalInvoice(Parcel in) {
        this.educationalInvoiceId = (Integer) in.readValue(Integer.class.getClassLoader());
        this.totalFee = (Double) in.readValue(Double.class.getClassLoader());
        this.status = (Integer) in.readValue(Integer.class.getClassLoader());
        this.creatorIpayAccountID = in.readString();
        this.description = in.readString();
        this.institute = in.readParcelable(Institution.class.getClassLoader());
        this.eventParticipant = in.readParcelable(EventParticipant.class.getClassLoader());
        this.session = in.readParcelable(Session.class.getClassLoader());
        this.creationTime = (Long) in.readValue(Long.class.getClassLoader());
        this.updateTime = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Parcelable.Creator<EducationalInvoice> CREATOR = new Parcelable.Creator<EducationalInvoice>() {
        @Override
        public EducationalInvoice createFromParcel(Parcel source) {
            return new EducationalInvoice(source);
        }

        @Override
        public EducationalInvoice[] newArray(int size) {
            return new EducationalInvoice[size];
        }
    };
}