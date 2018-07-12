package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.BanglalionBillPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments.UtilityProviderListFragment;
import bd.com.ipay.ipayskeleton.R;

public class UtilityBillPaymentActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utility_bill_payment);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToBillProviderListFragment();

        //switchToBanglalionBillPayFragment();
    }

    public void switchToBillProviderListFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new UtilityProviderListFragment()).commit();
    }



    public void switchToBanglalionBillPayFragment() {
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fragment_container, new BanglalionBillPayFragment()).commit();
    }

    @Override
    public Context setContext() {
        return UtilityBillPaymentActivity.this;
    }
}
