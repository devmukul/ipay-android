package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.Bank.Instant;

import android.os.Bundle;
import android.text.InputType;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.BankTransactionFragments.IPayAbstractBankTransactionAmountInputFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.BankTransactionFragments.IPayAbstractBankTransactionConfirmationFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IPayAddMoneyFromBankInstantlyAmountInputFragment extends IPayAbstractBankTransactionAmountInputFragment {
	@Override
	protected void setupViewProperties() {
		setTransactionDescription(getString(R.string.add_money_from_title));
		setName(bankAccountList.getBankName());
		setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
		setTransactionImageResource(bankAccountList.getBankIcon(getContext()));
	}

	@Override
	protected void performContinueAction() {
		if (getActivity() instanceof IPayTransactionActionActivity) {
			final Bundle bundle = new Bundle();
			bundle.putParcelable(Constants.SELECTED_BANK_ACCOUNT, bankAccountList);
			bundle.putSerializable(IPayAbstractBankTransactionConfirmationFragment.TRANSACTION_AMOUNT_KEY, getAmount());
			((IPayTransactionActionActivity) getActivity()).switchFragment(new IPayAddMoneyFromBankInstantlyConfirmationFragment(), bundle, 2, true);
		}
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
		if (getAmount() == null) {
			errorMessage = getString(R.string.please_enter_amount);
		} else {
			final BigDecimal amount = new BigDecimal(getAmount().doubleValue());
			final BigDecimal minimumAmount = businessRules.getMIN_AMOUNT_PER_PAYMENT();
			final BigDecimal maximumAmount = businessRules.getMAX_AMOUNT_PER_PAYMENT();
			errorMessage = InputValidator.isValidAmount(getActivity(), amount, minimumAmount, maximumAmount);
		}
		if (errorMessage != null) {
			showErrorMessage(errorMessage);
			return false;
		}
		return true;
	}

	@Override
	protected int getServiceId() {
		return ServiceIdConstants.ADD_MONEY_BY_BANK;
	}
}
