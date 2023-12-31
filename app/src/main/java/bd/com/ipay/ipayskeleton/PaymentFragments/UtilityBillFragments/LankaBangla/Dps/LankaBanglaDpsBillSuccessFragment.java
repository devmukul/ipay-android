package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla.Dps;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;

public class LankaBanglaDpsBillSuccessFragment extends IPayAbstractTransactionSuccessFragment {

	private Number billAmount;
	private String accountNumber;
	private String accountUserName;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			billAmount = (Number) getArguments().getSerializable(LankaBanglaDpsAmountInputFragment.INSTALLMENT_AMOUNT_KEY);
			accountNumber = getArguments().getString(LankaBanglaDpsAmountInputFragment.ACCOUNT_NUMBER_KEY, "");
			accountUserName = getArguments().getString(LankaBanglaDpsAmountInputFragment.ACCOUNT_USER_NAME_KEY, "");
		}
	}

	@Override
	protected void setupViewProperties() {
		setTransactionSuccessMessage(getStyledTransactionDescription(R.string.pay_bill_success_message, billAmount));
		setSuccessDescription(getString(R.string.pay_bill_success_description));
		setName(accountNumber);
		setUserName(accountUserName);
		setReceiverImage(R.drawable.ic_lankabd2);
	}
}
