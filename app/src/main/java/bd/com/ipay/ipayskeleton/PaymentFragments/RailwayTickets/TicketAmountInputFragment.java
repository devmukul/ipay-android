package bd.com.ipay.ipayskeleton.PaymentFragments.RailwayTickets;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.View;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities.IPayUtilityBillPayActionActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractAmountFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla.Dps.LankaBanglaDpsBillConfirmationFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TicketAmountInputFragment extends IPayAbstractAmountFragment {

	private String mSelectedStationFrom = null;
	private String mSelectedStationTo = null;
	private String mSelectedGender = null;
	private int mSelectedDate;
	private int mSelectedAdult;
	private int mSelectedChild;

	private String mSelectedTrain = null;
	private String mSelectedClass = null;
	private String mSelectedTicketId = null;
	private String mSelectedMessage = null;
	private int mSelectedTrainNo;
	private double mFareAmount;
	private double mVatAmount;
	private double mTotalAmount;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments() != null) {
			mSelectedClass = getArguments().getString(IPayUtilityBillPayActionActivity.KEY_TICKET_CLASS_NAME, "");
			mSelectedGender = getArguments().getString(IPayUtilityBillPayActionActivity.KEY_TICKET_GENDER, "");
			mSelectedDate = getArguments().getInt(IPayUtilityBillPayActionActivity.KEY_TICKET_DATE, 0);
			mSelectedAdult = getArguments().getInt(IPayUtilityBillPayActionActivity.KEY_TICKET_ADULTS, 0);
			mSelectedChild = getArguments().getInt(IPayUtilityBillPayActionActivity.KEY_TICKET_CHILD, 0);
			mSelectedStationFrom = getArguments().getString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_FROM, "");
			mSelectedStationTo = getArguments().getString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_TO, "");
			mSelectedTrain = getArguments().getString(IPayUtilityBillPayActionActivity.KEY_TICKET_TRAIN_NAME, "");
			mSelectedTicketId = getArguments().getString(IPayUtilityBillPayActionActivity.KEY_TICKET_TICKET_ID, "");
			mSelectedMessage = getArguments().getString(IPayUtilityBillPayActionActivity.KEY_TICKET_MESSAGE_ID, "");
			mSelectedTrainNo = getArguments().getInt(IPayUtilityBillPayActionActivity.KEY_TICKET_TRAIN_NO, 0);
			mFareAmount = getArguments().getDouble(IPayUtilityBillPayActionActivity.KEY_TICKET_FARE_AMOUNT, 0);
			mVatAmount = getArguments().getDouble(IPayUtilityBillPayActionActivity.KEY_TICKET_VAT_AMOUNT, 0);
			mTotalAmount = getArguments().getDouble(IPayUtilityBillPayActionActivity.KEY_TICKET_TOTAL_AMOUNT, 0);
		}
	}

	@Override
	protected void setupViewProperties() {
		setBalanceInfoLayoutVisibility(View.VISIBLE);
		setTransactionDescription(getString(R.string.paying_money_for));
		setInputType(InputType.TYPE_CLASS_NUMBER);
		setTransactionImageResource(R.drawable.bd_railway);
		setName(getString(R.string.railway_ticket_name));
		setUserName(Utilities.formatJourneyInfoText(mSelectedTrain +" - "+mSelectedTrainNo, mSelectedAdult, mSelectedChild));
		setAmount(String.valueOf( mTotalAmount ));
		setAmountFieldEnabled(false);
	}

	@Override
	protected InputFilter getInputFilter() {
		return new InputFilter() {
			@Override
			public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
				if (source != null) {
					try {
						String formattedSource = source.subSequence(start, end).toString();

						String destPrefix = dest.subSequence(0, dstart).toString();

						String destSuffix = dest.subSequence(dend, dest.length()).toString();

						String resultString = destPrefix + formattedSource + destSuffix;

						resultString = resultString.replace(",", ".");

						double result = Double.valueOf(resultString);
						if (result > Integer.MAX_VALUE)
							return "";
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		};
	}

	@Override
	protected boolean verifyInput() {
		if (!Utilities.isValueAvailable(businessRules.getMIN_AMOUNT_PER_PAYMENT())
				|| !Utilities.isValueAvailable(businessRules.getMAX_AMOUNT_PER_PAYMENT())) {
			DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
			return false;
		} else if (businessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
			DialogUtils.showDialogVerificationRequired(getActivity());
			return false;
		}

		final String errorMessage;
		if (SharedPrefManager.ifContainsUserBalance()) {
			if (getAmount() == null) {
				errorMessage = getString(R.string.please_enter_amount);
			} else {
				final BigDecimal amount = new BigDecimal(getAmount().doubleValue());
				final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

				if (amount.compareTo(balance) > 0) {
					errorMessage = getString(R.string.insufficient_balance);
				} else {
					final BigDecimal minimumAmount = businessRules.getMIN_AMOUNT_PER_PAYMENT();
					final BigDecimal maximumAmount = businessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);
					errorMessage = InputValidator.isValidAmount(getActivity(), amount, minimumAmount, maximumAmount);
				}
			}
		} else {
			errorMessage = getString(R.string.balance_not_available);
		}
		if (errorMessage != null) {
			showErrorMessage(errorMessage);
			return false;
		}
		return true;
	}

	@Override
	protected void performContinueAction() {
		Bundle bundle = new Bundle();
		bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_TRAIN_NAME, mSelectedTrain);
		bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_CLASS_NAME, mSelectedClass);
		bundle.putDouble(IPayUtilityBillPayActionActivity.KEY_TICKET_FARE_AMOUNT, mFareAmount);
		bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_GENDER, mSelectedGender);
		bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_DATE, mSelectedDate);
		bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_ADULTS, Integer.valueOf(mSelectedAdult));
		bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_CHILD, Integer.valueOf(mSelectedChild));
		bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_FROM, mSelectedStationFrom);
		bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_STATION_TO, mSelectedStationTo);
		bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_TICKET_ID, mSelectedTicketId);
		bundle.putDouble(IPayUtilityBillPayActionActivity.KEY_TICKET_TOTAL_AMOUNT, mTotalAmount);
		bundle.putString(IPayUtilityBillPayActionActivity.KEY_TICKET_MESSAGE_ID, mSelectedMessage);
		bundle.putInt(IPayUtilityBillPayActionActivity.KEY_TICKET_TRAIN_NO, mSelectedTrainNo);
		bundle.putDouble(IPayUtilityBillPayActionActivity.KEY_TICKET_VAT_AMOUNT, mVatAmount);

		if (getActivity() instanceof IPayUtilityBillPayActionActivity) {
			((IPayUtilityBillPayActionActivity) getActivity()).switchFragment(new TicketConfirmationFragment(), bundle, 2, true);
		}
	}

	@Override
	protected int getServiceId() {
		return ServiceIdConstants.UTILITY_BILL_PAYMENT;
	}
}
