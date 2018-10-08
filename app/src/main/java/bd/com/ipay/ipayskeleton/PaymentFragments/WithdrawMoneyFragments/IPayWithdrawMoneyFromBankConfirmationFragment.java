package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import android.os.Bundle;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.WithdrawMoneyRequest;
import bd.com.ipay.ipayskeleton.PaymentFragments.BankTransactionFragments.IPayAbstractBankTransactionConfirmationFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPayWithdrawMoneyFromBankConfirmationFragment extends IPayAbstractBankTransactionConfirmationFragment {

	@Override
	protected String getApiCommand() {
		return Constants.COMMAND_WITHDRAW_MONEY;
	}

	@Override
	protected String getRequestJson() {
		return gson.toJson(new WithdrawMoneyRequest(bankAccountList.getBankAccountId(), transactionAmount.doubleValue(), getNote(), getPin()));
	}

	@Override
	protected String getUrl() {
		return Constants.BASE_URL_SM + Constants.URL_WITHDRAW_MONEY;
	}

	@Override
	protected void setupViewProperties() {
		setTransactionDescription(getStyledTransactionDescription(R.string.withdraw_money_confirmation_message, transactionAmount));
		setName(bankAccountList.getAccountNumber());
		setTransactionImageResource(bankAccountList.getBankIcon(getContext()));
		setNoteEditTextHint(getString(R.string.short_note_optional_hint));
	}

	@Override
	protected void bankTransactionSuccess(final Bundle bundle) {
		if (getActivity() instanceof IPayTransactionActionActivity)
			((IPayTransactionActionActivity) getActivity()).switchFragment(new IPayWithdrawMoneyFromBankSuccessFragment(), bundle, 3, true);
	}

	@Override
	protected String getTrackerCategory() {
		return "Withdraw Money from Bank";
	}

	@Override
	protected boolean verifyInput() {
		return false;
	}
}
