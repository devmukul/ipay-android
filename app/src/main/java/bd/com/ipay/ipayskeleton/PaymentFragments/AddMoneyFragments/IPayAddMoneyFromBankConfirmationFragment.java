package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.os.Bundle;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.AddOrWithdrawMoney.AddMoneyByBankRequestRequest;
import bd.com.ipay.ipayskeleton.PaymentFragments.BankTransactionFragments.IPayAbstractBankTransactionConfirmationFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPayAddMoneyFromBankConfirmationFragment extends IPayAbstractBankTransactionConfirmationFragment {

	@Override
	protected String getApiCommand() {
		return Constants.COMMAND_ADD_MONEY_FROM_BANK;
	}

	@Override
	protected String getRequestJson() {
		return gson.toJson(new AddMoneyByBankRequestRequest(bankAccountList.getBankAccountId(), transactionAmount.doubleValue(), getNote(), getPin()));
	}

	@Override
	protected String getUrl() {
		return Constants.BASE_URL_SM + Constants.URL_ADD_MONEY_FROM_BANK;
	}

	@Override
	protected void bankTransactionSuccess(final Bundle bundle) {
		if (getActivity() instanceof IPayTransactionActionActivity)
			((IPayTransactionActionActivity) getActivity()).switchFragment(new IPayAddMoneyFromBankSuccessFragment(), bundle, 3, true);
	}

	@Override
	protected void setupViewProperties() {
		setTransactionDescription(getStyledTransactionDescription(R.string.add_money_confirmation_message, transactionAmount));
		setName(bankAccountList.getBankName());
		setTransactionImageResource(bankAccountList.getBankIcon(getContext()));
		setNoteEditTextHint(getString(R.string.short_note_optional_hint));
	}

	@Override
	protected String getTrackerCategory() {
		return "Add Money from Bank";
	}

}
