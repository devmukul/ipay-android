package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CreditCard;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;


public class CreditCardBillSuccessFragment extends IPayAbstractTransactionSuccessFragment {
    private Number billAmount;
    private String cardNumber;
    private String cardUserName;
    private int bankIconId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            billAmount = (Number) getArguments().getSerializable(IPayUtilityBillPayActionActivity.BILL_AMOUNT_KEY);
            cardNumber = getArguments().getString(IPayUtilityBillPayActionActivity.CARD_NUMBER_KEY, "");
            cardUserName = getArguments().getString(IPayUtilityBillPayActionActivity.CARD_USER_NAME_KEY, "");
            bankIconId = getArguments().getInt(IPayUtilityBillPayActionActivity.BANK_ICON, 0);
        }
    }

    @Override
    protected void setupViewProperties() {
        setTransactionSuccessMessage(getStyledTransactionDescription(R.string.pay_bill_success_message, billAmount));
        setSuccessDescription(getString(R.string.pay_bill_success_description));
        setName(CardNumberValidator.deSanitizeEntry(cardNumber, ' '));
        setUserName(cardUserName);
        setReceiverImage(bankIconId);
    }
}
