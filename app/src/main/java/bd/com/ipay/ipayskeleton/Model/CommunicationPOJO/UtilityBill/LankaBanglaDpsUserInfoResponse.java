package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.UtilityBill;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LankaBanglaDpsUserInfoResponse {

    @SerializedName("message")
    @Expose
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SerializedName("branch_id")
    @Expose
    private String branchId;

    @SerializedName("account_number")
    @Expose
    private String accountNumber;

    @SerializedName("account_title")
    @Expose
    private String accountTitle;

    @SerializedName("account_maturity_date")
    @Expose
    private String accountMaturityDate;

    @SerializedName("installment_amount")
    @Expose
    private long installmentAmount;

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountTitle() {
        return accountTitle;
    }

    public void setAccountTitle(String accountTitle) {
        this.accountTitle = accountTitle;
    }

    public String getAccountMaturityDate() {
        return accountMaturityDate;
    }

    public void setAccountMaturityDate(String accountMaturityDate) {
        this.accountMaturityDate = accountMaturityDate;
    }

    public long getInstallmentAmount() {
        return installmentAmount;
    }

    public void setInstallmentAmount(long installmentAmount) {
        this.installmentAmount = installmentAmount;
    }
}
