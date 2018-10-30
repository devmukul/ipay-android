package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.Carnival;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;

public class CarnivalBillSuccessFragment extends IPayAbstractTransactionSuccessFragment {

	private Number billAmount;
	private String userName;
	private String carnivalId;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			billAmount = (Number) getArguments().getSerializable(CarnivalBillConfirmationFragment.BILL_AMOUNT_KEY);
			userName = getArguments().getString(CarnivalBillAmountInputFragment.USER_NAME_KEY, "");
			carnivalId = getArguments().getString(CarnivalBillAmountInputFragment.CARNIVAL_ID_KEY, "");
		}
	}

	@Override
	protected void setupViewProperties() {
		setTransactionSuccessMessage(getStyledTransactionDescription(R.string.pay_bill_success_message, billAmount));
		setSuccessDescription(getString(R.string.pay_bill_success_description));
		setName(carnivalId);
		setUserName(userName);
		setReceiverImage(R.drawable.ic_carnival);
	}
}
