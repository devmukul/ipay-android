package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractBankTransactionOptionFragment;
import bd.com.ipay.ipayskeleton.R;

public class IPayWithdrawMoneyFromBankOptionFragment extends IPayAbstractBankTransactionOptionFragment {

	@Override
	protected void setupViewProperties() {
		setHeaderText(getText(R.string.withdraw_money_anytime));
		setMessageText(getText(R.string.withdraw_money_bank_option_message));
	}
}
