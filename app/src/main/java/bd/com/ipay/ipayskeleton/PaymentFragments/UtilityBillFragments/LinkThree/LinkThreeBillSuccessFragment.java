package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LinkThree;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;

public class LinkThreeBillSuccessFragment extends IPayAbstractTransactionSuccessFragment {

	private Number billAmount;
	private String userName;
	private String subscriberId;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			billAmount = (Number) getArguments().getSerializable(LinkThreeBillConfirmationFragment.BILL_AMOUNT_KEY);
			userName = getArguments().getString(LinkThreeBillAmountInputFragment.USER_NAME_KEY, "");
			subscriberId = getArguments().getString(LinkThreeBillAmountInputFragment.SUBSCRIBER_ID_KEY, "");
		}
	}

	@Override
	protected void setupViewProperties() {
		setTransactionSuccessMessage(getStyledTransactionDescription(R.string.pay_bill_success_message, billAmount));
		setSuccessDescription(getString(R.string.pay_bill_success_description));
		setName(subscriberId);
		setUserName(userName);
		setReceiverImage(R.drawable.link_three_logo);
	}
}
