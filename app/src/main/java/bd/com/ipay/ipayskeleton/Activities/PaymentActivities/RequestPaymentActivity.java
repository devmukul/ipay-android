package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestPaymentFragments.RequestPaymentFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestPaymentActivity extends BaseActivity {

    public FloatingActionButton mFabNewRequestPayment;

    public static MandatoryBusinessRules mMandatoryBusinessRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        mFabNewRequestPayment = (FloatingActionButton) findViewById(R.id.fab_new_request_payment);

        mFabNewRequestPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToRequestPaymentFragment();
            }
        });

        switchToRequestPaymentFragment();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        Utilities.hideKeyboard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else
            super.onBackPressed();

    }

    private void switchToRequestPaymentFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestPaymentFragment())
                .commit();
        mFabNewRequestPayment.setVisibility(View.GONE);
    }

    @Override
    public Context setContext() {
        return RequestPaymentActivity.this;
    }
}