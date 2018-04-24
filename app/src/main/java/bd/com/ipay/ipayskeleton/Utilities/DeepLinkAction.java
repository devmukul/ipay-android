package bd.com.ipay.ipayskeleton.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class DeepLinkAction implements Parcelable {
    private String action;
    private String orderId;

    public DeepLinkAction() {
        this("", "");
    }

    public DeepLinkAction(String action, String orderId) {

        this.action = action;
        this.orderId = orderId;
    }

    protected DeepLinkAction(Parcel in) {
        action = in.readString();
        orderId = in.readString();
    }

    public static final Creator<DeepLinkAction> CREATOR = new Creator<DeepLinkAction>() {
        @Override
        public DeepLinkAction createFromParcel(Parcel in) {
            return new DeepLinkAction(in);
        }

        @Override
        public DeepLinkAction[] newArray(int size) {
            return new DeepLinkAction[size];
        }
    };

    public String getAction() {

        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "DeepLinkAction{" +
                "action='" + action + '\'' +
                ", orderId=" + orderId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(orderId);
    }
}
