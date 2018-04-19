
package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetBankListResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("bankAccountList")
    @Expose
    private List<BankAccountList> bankAccountList = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public GetBankListResponse() {
    }

    /**
     * 
     * @param message
     * @param bankAccountList
     */
    public GetBankListResponse(String message, List<BankAccountList> bankAccountList) {
        super();
        this.message = message;
        this.bankAccountList = bankAccountList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<BankAccountList> getBankAccountList() {
        return bankAccountList;
    }

    public void setBankAccountList(List<BankAccountList> bankAccountList) {
        this.bankAccountList = bankAccountList;
    }

}
