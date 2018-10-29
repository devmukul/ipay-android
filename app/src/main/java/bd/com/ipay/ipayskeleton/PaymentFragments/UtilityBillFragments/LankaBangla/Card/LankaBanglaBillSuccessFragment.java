package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla.Card;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;

public class LankaBanglaBillSuccessFragment extends IPayAbstractTransactionSuccessFragment {

	private Number billAmount;
	private String cardNumber;
	private String cardUserName;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			billAmount = (Number) getArguments().getSerializable(LankaBanglaBillConfirmationFragment.BILL_AMOUNT_KEY);
			cardNumber = getArguments().getString(LankaBanglaAmountInputFragment.CARD_NUMBER_KEY, "");
			cardUserName = getArguments().getString(LankaBanglaAmountInputFragment.CARD_USER_NAME_KEY, "");
		}
	}

	@Override
	protected void setupViewProperties() {
		setTransactionSuccessMessage(getStyledTransactionDescription(R.string.pay_bill_success_message, billAmount));
		setSuccessDescription(getString(R.string.pay_bill_success_description));
		setName(CardNumberValidator.deSanitizeEntry(cardNumber, ' '));
		setUserName(cardUserName);
		setReceiverImage(R.drawable.ic_lankabd2);
	}
}
