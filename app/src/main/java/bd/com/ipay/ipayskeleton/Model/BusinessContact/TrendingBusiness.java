package bd.com.ipay.ipayskeleton.Model.BusinessContact;


import java.util.List;

import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;

public class TrendingBusiness {
    private String businessType;
    private List<BusinessAccountEntry> businessProfile;

    public String getBusinessType() {
        return businessType;
    }

    public List<BusinessAccountEntry> getBusinessProfile() {
        return businessProfile;
    }
}
