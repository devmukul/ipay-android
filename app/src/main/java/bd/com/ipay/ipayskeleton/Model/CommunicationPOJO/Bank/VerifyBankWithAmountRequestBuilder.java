package bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class VerifyBankWithAmountRequestBuilder {

    private Long userBankID;

    public VerifyBankWithAmountRequestBuilder(Long userBankID) {
        this.userBankID = userBankID;
    }

    public String getGeneratedUrl() {
        return Constants.BASE_URL_MM + Constants.URL_VERIFY_WITH_AMOUNT_A_BANK +
                userBankID + "/" + Constants.URL_BANK_VERIFICATION_WITH_AMOUNT;
    }
}

