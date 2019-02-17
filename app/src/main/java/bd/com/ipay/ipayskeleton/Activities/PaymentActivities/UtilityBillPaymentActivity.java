package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.AmberITBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.BanglalionBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.BrilliantBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.DescoBillConfirmationFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.DescoBillInfoFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.DescoBillSuccessFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.DescoEnterBillNumberFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.DpdcBillPaymentFragment;
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
                } else if (service.equals(Constants.BRILLIANT)) {
                    switchToBrilliantRechargeFragment();
                } else if (service.equals(Constants.WESTZONE)) {
                    switchToWestZoneBillPayFragment();
                } else if (service.equals(Constants.DESCO)) {
                    switchToDescoBillPayFragment();
                } else if (service.equals(Constants.DPDC)) {
                    switchToDpdcBillPaymentFragment();
                } else if (service.equals(Constants.AMBERIT)) {
                    switchToAmberITBillPaymentFragment();
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
                replace(R.id.fragment_container, new DescoEnterBillNumberFragment()).commit();
    }

    public void switchToDescoBillInfoFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
        DescoBillInfoFragment descoBillInfoFragment = new DescoBillInfoFragment();
        descoBillInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit).replace(R.id.fragment_container, descoBillInfoFragment).addToBackStack(null).commit();
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

    @Override
    public Context setContext() {
        return UtilityBillPaymentActivity.this;
    }

    public void switchToDescoBillConfirmationFragment(Bundle bundle) {

        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStack();
        }
        DescoBillConfirmationFragment descoBillConfirmationFragment = new DescoBillConfirmationFragment();
        descoBillConfirmationFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit).replace
                (R.id.fragment_container, descoBillConfirmationFragment).addToBackStack(null).commit();
    }

    public void switchToDescoBillSuccessFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3) {
            getSupportFragmentManager().popBackStack();
        }
        DescoBillSuccessFragment descoBillSuccessFragment = new DescoBillSuccessFragment();
        descoBillSuccessFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter,
                R.anim.right_to_left_exit, R.anim.left_to_right_enter, R.anim.left_to_right_exit).replace
                (R.id.fragment_container, descoBillSuccessFragment).addToBackStack(null).commit();
    }
}
