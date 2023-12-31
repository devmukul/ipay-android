package bd.com.ipay.ipayskeleton.Utilities;

import android.os.Parcel;
import android.os.Parcelable;

public class DeepLinkAction implements Parcelable {
    private String action;
    private String orderId;

    private String actionType;
    private String queryParameter;

    public DeepLinkAction() {
        this("", "", "","");
    }


    public DeepLinkAction(String action, String orderId, String actionType, String queryParameter) {
        this.action = action;
        this.orderId = orderId;
        this.actionType = actionType;
        this.queryParameter = queryParameter;
    }

    protected DeepLinkAction(Parcel in) {
        action = in.readString();
        orderId = in.readString();
        actionType = in.readString();
        queryParameter = in.readString();
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

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getQueryParameter() {
        return queryParameter;
    }

    public void setQueryParameter(String queryParameter) {
        this.queryParameter = queryParameter;
    }

    @Override
    public String toString() {
        return "DeepLinkAction{" +
                "action='" + action + '\'' +
                ", orderId='" + orderId + '\'' +
                ", actionType='" + actionType + '\'' +
                ", queryParameter='" + queryParameter + '\'' +
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
        dest.writeString(actionType);
        dest.writeString(queryParameter);
    }
}
