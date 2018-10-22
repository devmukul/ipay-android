package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.Bank.Instant;

import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractBankTransactionOptionFragment;
import bd.com.ipay.ipayskeleton.R;

public class IPayAddMoneyFromBankInstantlyOptionFragment extends IPayAbstractBankTransactionOptionFragment {

	@Override
	protected void setupViewProperties() {
		setHeaderText(getText(R.string.add_money_securely));
		setMessageText(getText(R.string.add_money_instant_option_message));
	}
}
