package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractBankTransactionOptionFragment;
import bd.com.ipay.ipayskeleton.R;

public class IPayAddMoneyFromBankOptionFragment extends IPayAbstractBankTransactionOptionFragment {

	@Override
	protected void setupViewProperties() {
		setHeaderText(getText(R.string.add_money_securely));
		setMessageText(getText(R.string.add_money_bank_option_message));
	}
}
