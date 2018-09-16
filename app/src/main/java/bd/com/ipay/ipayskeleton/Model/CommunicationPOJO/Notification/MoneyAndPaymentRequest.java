package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification;

import android.os.Parcel;

import java.math.BigDecimal;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.InvoiceItem;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MoneyAndPaymentRequest implements Notification {

    private Long id;
    private BigDecimal amount;
    private Long requestTime;
    private String title;
    private int serviceID;
    public String description;
    public UserProfile originatorProfile;
    private UserProfile receiverProfile;
    private BigDecimal vat;
    private List<InvoiceItem> itemList;
    private int status;
    private String transactionID;


    public MoneyAndPaymentRequest() {
    }

    protected MoneyAndPaymentRequest(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readLong();
        }
        amount = (BigDecimal) in.readSerializable();
        if (in.readByte() == 0) {
            requestTime = null;
        } else {
            requestTime = in.readLong();
        }
        title = in.readString();
        serviceID = in.readInt();
        description = in.readString();
        originatorProfile = in.readParcelable(UserProfile.class.getClassLoader());
        receiverProfile = in.readParcelable(UserProfile.class.getClassLoader());
        vat = (BigDecimal) in.readSerializable();
        itemList = in.createTypedArrayList(InvoiceItem.CREATOR);
        status = in.readInt();
        transactionID = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(id);
        }
        dest.writeSerializable(amount);
        if (requestTime == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(requestTime);
        }
        dest.writeString(title);
        dest.writeInt(serviceID);
        dest.writeString(description);
        dest.writeParcelable(originatorProfile, flags);
        dest.writeParcelable(receiverProfile, flags);
        dest.writeSerializable(vat);
        dest.writeTypedList(itemList);
        dest.writeInt(status);
        dest.writeString(transactionID);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MoneyAndPaymentRequest> CREATOR = new Creator<MoneyAndPaymentRequest>() {
        @Override
        public MoneyAndPaymentRequest createFromParcel(Parcel in) {
            return new MoneyAndPaymentRequest(in);
        }

        @Override
        public MoneyAndPaymentRequest[] newArray(int size) {
            return new MoneyAndPaymentRequest[size];
        }
    };

    public List<InvoiceItem> getItemList() {
        return itemList;
    }

    public BigDecimal getVat() {
        return vat;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return getOriginatorProfile().getUserName();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Long getRequestTime() {
        return requestTime;
    }

    public String getNotificationTitle() {
        if (serviceID == Constants.SERVICE_ID_REQUEST_INVOICE
                || serviceID == Constants.SERVICE_ID_REQUEST_PAYMENT)
            return "Payment Request";
        else if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY)
            return "Money Request";
        else return title;
    }

    public String getTitle() {
        return title;
    }

    public UserProfile getOriginatorProfile() {
        return originatorProfile;
    }

    public int getServiceID() {
        return serviceID;
    }

    @Override
    public int getNotificationType() {
        if (serviceID == Constants.SERVICE_ID_REQUEST_MONEY)
            return Constants.NOTIFICATION_TYPE_REQUEST_MONEY;
        else
            return Constants.NOTIFICATION_TYPE_MAKE_PAYMENT;
    }

    public String getDescriptionOfRequest() {
        return description;
    }

    @Override
    public String getImageUrl() {
        return getOriginatorProfile().getUserProfilePicture();
    }

    @Override
    public long getTime() {
        return getRequestTime();
    }

    public int getStatus() {
        return status;
    }
}
