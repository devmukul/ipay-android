package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.Card;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;

public class IPayAddMoneyFromCardSuccessFragment extends IPayAbstractTransactionSuccessFragment {

	protected Number transactionAmount;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			transactionAmount = (Number) getArguments().getSerializable(IPayAddMoneyFromCardTransactionConfirmationFragment.TRANSACTION_AMOUNT_KEY);
		}
	}

	@Override
	protected void setupViewProperties() {
		setTransactionSuccessMessage(getStyledTransactionDescription(R.string.add_money_card_success_message, transactionAmount));
		setSuccessDescription(getString(R.string.add_money_card_success_description));
		setName(getString(R.string.debit_credit_card));
		setReceiverImage(R.drawable.ic_debit_credit_card_icon);
	}
}
