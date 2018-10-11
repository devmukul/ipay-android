package bd.com.ipay.ipayskeleton.Activities.UtilityBillPayActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.IPayAbstractTransactionSuccessFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.LankaBangla.Card.LankaBanglaCardNumberInputFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public final class IPayUtilityBillPayActionActivity extends BaseActivity {

	public static final String BILL_PAY_LANKABANGLA_CARD = "LANKABANGLA_CARD";
	public static final String BILL_PAY_PARTY_NAME_KEY = "BILL_PAY_PARTY_NAME";


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ipay_utility_bill_pay_action);
		final String billPayPartyName = getIntent().getStringExtra(BILL_PAY_PARTY_NAME_KEY);
		BusinessRuleCacheManager.fetchBusinessRule(this, ServiceIdConstants.UTILITY_BILL_PAYMENT);
		switch (billPayPartyName) {
			case BILL_PAY_LANKABANGLA_CARD:
				final Bundle bundle = new Bundle();
				switchFragment(new LankaBanglaCardNumberInputFragment(), bundle, 1, false);
				break;
			default:
				finish();
		}
	}

	public void switchFragment(@NonNull Fragment fragment, @NonNull Bundle bundle, int maxBackStackEntryCount, boolean shouldAnimate) {
		if (getSupportFragmentManager().getBackStackEntryCount() > maxBackStackEntryCount) {
			getSupportFragmentManager().popBackStackImmediate();
		}

		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		if (shouldAnimate) {
			if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
				fragmentTransaction.setCustomAnimations(R.anim.right_to_left_enter,
						R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit);
			}
		}
		fragment.setArguments(bundle);
		fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(fragment.getTag());
		fragmentTransaction.commit();
	}

	@Override
	public void onBackPressed() {
		Utilities.hideKeyboard(this);
		for (Fragment fragment : getSupportFragmentManager().getFragments()) {
			if (fragment instanceof IPayAbstractTransactionSuccessFragment) {
				finish();
				return;
			}
		}
		if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
			getSupportFragmentManager().popBackStackImmediate();
		} else {
			finish();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected Context setContext() {
		return IPayUtilityBillPayActionActivity.this;
	}
}
