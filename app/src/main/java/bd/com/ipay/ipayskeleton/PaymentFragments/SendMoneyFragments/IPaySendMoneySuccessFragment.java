package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPaySendMoneySuccessFragment extends IPayAbstractTransactionSuccessFragment {
	private String name;
	private String mobileNumber;
	private String profilePicture;
	private Number transactionAmount;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			name = getArguments().getString(Constants.NAME);
			mobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);
			profilePicture = getArguments().getString(Constants.PHOTO_URI);
			transactionAmount = (Number) getArguments().getSerializable(IPaySendMoneyAmountInputFragment.TRANSACTION_AMOUNT_KEY);
		}
	}

	@Override
	protected void setupViewProperties() {
		setTransactionSuccessMessage(getStyledTransactionDescription(R.string.send_money_success_message, transactionAmount));
		setSuccessDescription(getString(R.string.send_money_success_description));
		setName(name);
		setUserName(mobileNumber);
		setSenderImage(ProfileInfoCacheManager.getProfileImageUrl());
		setReceiverImage(profilePicture);
	}
}
