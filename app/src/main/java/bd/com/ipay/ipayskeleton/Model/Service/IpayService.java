package bd.com.ipay.ipayskeleton.Model.Service;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;

public class IpayService {


    private int serviceId;
    @DrawableRes
    private int serviceIconResId;
    @NonNull
    private String serviceTitle;

    public IpayService(int serviceId) {
        this(serviceId, -1, "");
    }

    public IpayService(int serviceId, int serviceIconResId, @NonNull String serviceTitle) {
        this.serviceId = serviceId;
        this.serviceIconResId = serviceIconResId;
        this.serviceTitle = serviceTitle;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getServiceIconResId() {
        return serviceIconResId;
    }

    public void setServiceIconResId(int serviceIconResId) {
        this.serviceIconResId = serviceIconResId;
    }

    @NonNull
    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(@NonNull String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IpayService that = (IpayService) o;

        return serviceId == that.serviceId;
    }

    @Override
    public String toString() {
        return "IpayService{" +
                "serviceId=" + serviceId +
                ", serviceIconResId=" + serviceIconResId +
                ", serviceTitle='" + serviceTitle + '\'' +
                '}';
    }
}
