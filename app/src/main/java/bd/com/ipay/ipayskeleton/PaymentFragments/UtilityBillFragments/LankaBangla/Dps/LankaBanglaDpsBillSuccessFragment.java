package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla.Dps;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;

public class LankaBanglaDpsBillSuccessFragment extends IPayAbstractTransactionSuccessFragment {

	private Number billAmount;
	private String cardNumber;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			billAmount = (Number) getArguments().getSerializable(LankaBanglaDpsBillConfirmationFragment.BILL_AMOUNT_KEY);
			cardNumber = getArguments().getString(LankaBanglaDpsAmountInputFragment.CARD_NUMBER_KEY, "");
		}
	}

	@Override
	protected void setupViewProperties() {
		setTransactionSuccessMessage(getStyledTransactionDescription(R.string.pay_bill_success_message, billAmount));
		setSuccessDescription(getString(R.string.pay_bill_success_description));
		setName(CardNumberValidator.deSanitizeEntry(cardNumber, ' '));
		setReceiverImage(R.drawable.ic_lankabd2);
	}
}
