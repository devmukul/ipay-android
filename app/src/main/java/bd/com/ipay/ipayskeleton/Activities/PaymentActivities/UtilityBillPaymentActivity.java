package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.Calendar;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.AmberITBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.BanglalionBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.BrilliantBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.CarnivalBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.DescoBillPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.DpdcBillPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.Link3BillPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.UtilityProviderListFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.WestzoneBillPaymentFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class UtilityBillPaymentActivity extends BaseActivity {

	public static MandatoryBusinessRules mMandatoryBusinessRules;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_utility_bill_payment);
		if (getSupportActionBar() != null)
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		try {
			if (getIntent().hasExtra(Constants.SERVICE)) {
				String service = getIntent().getStringExtra(Constants.SERVICE);
				if (service.equals(Constants.BANGLALION)) {
					switchToBanglalionBillPayFragment();
				} else if (service.equals(Constants.LINK3)) {
					switchToLink3BillPayment();
				} else if (service.equals(Constants.BRILLIANT)) {
					switchToBrilliantRechargeFragment();
				} else if (service.equals(Constants.WESTZONE)) {
					switchToWestZoneBillPayFragment();
				} else if (service.equals(Constants.DESCO)) {
					switchToDescoBillPayFragment();
				} else if (service.equals(Constants.DPDC)) {
					switchToDpdcBillPaymentFragment();
				} else if (service.equals(Constants.CARNIVAL)) {
					switchToDozeBillPaymentFragment();
				} else if (service.equals(Constants.AMBERIT)) {
					switchToAmberITBillPaymentFragment();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void switchToDozeBillPaymentFragment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new CarnivalBillPayFragment()).commit();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			Utilities.hideKeyboard(this);
			onBackPressed();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	public void switchToBillProviderListFragment() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.fragment_container, new UtilityProviderListFragment()).commit();
	}

	public void switchToDescoBillPayFragment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new DescoBillPaymentFragment()).commit();
	}

	private void switchToDpdcBillPaymentFragment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new DpdcBillPaymentFragment()).commit();
	}

	private void switchToAmberITBillPaymentFragment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new AmberITBillPayFragment()).commit();
	}

	public void switchToBrilliantRechargeFragment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new BrilliantBillPayFragment()).commit();
	}

	public void switchToLink3BillPayment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new Link3BillPaymentFragment()).commit();
	}

	public void switchToBanglalionBillPayFragment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new BanglalionBillPayFragment()).commit();
	}

	public void switchToWestZoneBillPayFragment() {
		getSupportFragmentManager().beginTransaction().
				replace(R.id.fragment_container, new WestzoneBillPaymentFragment()).commit();
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
			getSupportFragmentManager().popBackStack();
		} else {
			super.onBackPressed();
		}
	}

	public String getPreviousMonth() {
		Calendar cal = Calendar.getInstance();
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		switch (month) {
			case 1:
				return "January " + Integer.toString(year);

			case 2:
				return "February " + Integer.toString(year);

			case 3:
				return "March " + Integer.toString(year);

			case 4:
				return "April " + Integer.toString(year);

			case 5:
				return "May " + Integer.toString(year);

			case 6:
				return "June " + Integer.toString(year);

			case 7:
				return "July " + Integer.toString(year);

			case 8:
				return "August " + Integer.toString(year);

			case 9:
				return "September " + Integer.toString(year);

			case 10:
				return "October " + Integer.toString(year);
			case 11:
				return "November " + Integer.toString(year);
			case 0:
				return "December " + Integer.toString(year - 1);
			default:
				return "";
		}
	}

	@Override
	public Context setContext() {
		return UtilityBillPaymentActivity.this;
	}
}
