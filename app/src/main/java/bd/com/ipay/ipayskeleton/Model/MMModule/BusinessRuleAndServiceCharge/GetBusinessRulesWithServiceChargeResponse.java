package bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.ServiceCharge.FeeCharge;

public class GetBusinessRulesWithServiceChargeResponse {
    private List<BusinessRule> businessRules = new ArrayList<>();
    private FeeCharge feeCharge = new FeeCharge();
    private Boolean isPinRequired = false;

    public GetBusinessRulesWithServiceChargeResponse() {
    }

    public List<BusinessRule> getBusinessRules() {
        return businessRules;
    }

    public void setBusinessRules(List<BusinessRule> businessRules) {
        this.businessRules = businessRules;
    }

    public FeeCharge getFeeCharge() {
        return feeCharge;
    }

    public void setFeeCharge(FeeCharge feeCharge) {
        this.feeCharge = feeCharge;
    }

    public Boolean getPinRequired() {
        if (isPinRequired == null)
            return true;
        return isPinRequired;
    }

    public void setPinRequired(Boolean pinRequired) {
        isPinRequired = pinRequired;
    }
}
