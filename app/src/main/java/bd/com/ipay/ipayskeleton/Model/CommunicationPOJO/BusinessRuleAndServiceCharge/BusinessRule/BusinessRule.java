package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule;

import java.math.BigDecimal;

public class BusinessRule {
    private int serviceID;
    private int ruleCode;
    private int userType;
    private int userClass;
    private int status;
    private int ruleCreatorAccountID;
    private int lastUpdateAdminAccountID;
    private long createTime;
    private long lastUpdateTime;
    private String serviceName;
    private String ruleID;
    private String ruleName;
    private String alarmValueGreater;
    private BigDecimal ruleValue;
    private int id;

    public int getServiceID() {
        return serviceID;
    }

    public void setServiceID(int serviceID) {
        this.serviceID = serviceID;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getRuleID() {
        return ruleID;
    }

    public void setRuleID(String ruleID) {
        this.ruleID = ruleID;
    }

    public int getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(int ruleCode) {
        this.ruleCode = ruleCode;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public int getUserClass() {
        return userClass;
    }

    public void setUserClass(int userClass) {
        this.userClass = userClass;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(int lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public int getRuleCreatorAccountID() {
        return ruleCreatorAccountID;
    }

    public void setRuleCreatorAccountID(int ruleCreatorAccountID) {
        this.ruleCreatorAccountID = ruleCreatorAccountID;
    }

    public int getLastUpdateAdminAccountID() {
        return lastUpdateAdminAccountID;
    }

    public void setLastUpdateAdminAccountID(int lastUpdateAdminAccountID) {
        this.lastUpdateAdminAccountID = lastUpdateAdminAccountID;
    }

    public String isAlarmValueGreater() {
        return alarmValueGreater;
    }

    public void setAlarmValueGreater(String alarmValueGreater) {
        this.alarmValueGreater = alarmValueGreater;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigDecimal getRuleValue() {

        if (ruleValue == null)
            return BigDecimal.ZERO;
        return ruleValue;
    }

    public void setRuleValue(BigDecimal ruleValue) {
        this.ruleValue = ruleValue;
    }

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}

