package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.BanglalionBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.BrilliantBillPayFragment;
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
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void switchToBillProviderListFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UtilityProviderListFragment()).commit();
    }

    public void switchToBrilliantRechargeFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BrilliantBillPayFragment()).commit();
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
    public Context setContext() {
        return UtilityBillPaymentActivity.this;
    }
}
