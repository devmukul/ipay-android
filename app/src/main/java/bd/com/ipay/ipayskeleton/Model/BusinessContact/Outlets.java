package bd.com.ipay.ipayskeleton.Model.BusinessContact;

import android.text.TextUtils;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserAddress;

public class Outlets
{
    private OutletAddress outletAddress;
    private Long outletId;
    private String outletLogoUrl;
    private String outletName;

    public OutletAddress getOutletAddress() {
        return outletAddress;
    }

    public void setOutletAddress(OutletAddress outletAddress) {
        this.outletAddress = outletAddress;
    }

    public Long getOutletId() {
        return outletId;
    }

    public void setOutletId(Long outletId) {
        this.outletId = outletId;
    }

    public String getOutletLogoUrl() {
        return outletLogoUrl;
    }

    public void setOutletLogoUrl(String outletLogoUrl) {
        this.outletLogoUrl = outletLogoUrl;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }


    public String getAddressString() {
        String addressString = null;
        if (outletAddress != null) {
            addressString = outletAddress.getAddressLine1();

            if (!TextUtils.isEmpty(outletAddress.getAddressLine2()))
                addressString += " "+outletAddress.getAddressLine2();

        }
        return addressString;
    }

}