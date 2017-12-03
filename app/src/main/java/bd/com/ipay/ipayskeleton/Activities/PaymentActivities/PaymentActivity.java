package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.MakePaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentRequestReceivedDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentRequestsReceivedFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentActivity extends BaseActivity {

    private FloatingActionButton mFabMakingPayment;
    private boolean switchedToPendingList = true;
    public static final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();

    /**
     * If this value is set in the intent extras,
     * you would be taken directly to the new request page
     */
    public static final String LAUNCH_NEW_REQUEST = "LAUNCH_NEW_REQUEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mFabMakingPayment = (FloatingActionButton) findViewById(R.id.fab_payment_making);

        if (getIntent().hasExtra(Constants.MOBILE_NUMBER) || getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
            switchToMakePaymentFragment();
        } else
            switchToReceivedPaymentRequestsFragment();

        mFabMakingPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.MAKE_PAYMENT)
            public void onClick(View v) {
                switchToMakePaymentFragment();
            }
        });

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utilities.hideKeyboard(this);
        if (item.getItemId() == android.R.id.home) {
            if (switchedToPendingList) {
                super.onBackPressed();
            } else if (getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
                super.onBackPressed();
            } else {
                switchToReceivedPaymentRequestsFragment();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
            finish();
        } else if (switchedToPendingList) {
            super.onBackPressed();
        } else {
            switchToReceivedPaymentRequestsFragment();
        }
    }

    public void switchToMakePaymentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MakePaymentFragment()).commit();
        mFabMakingPayment.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    public void switchToReceivedPaymentRequestsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PaymentRequestsReceivedFragment()).commit();
        mFabMakingPayment.setVisibility(View.VISIBLE);
        switchedToPendingList = true;
    }

    public void switchToReceivedPaymentRequestDetailsFragment(Bundle bundle) {
        PaymentRequestReceivedDetailsFragment paymentRequestReceivedDetailsFragment = new PaymentRequestReceivedDetailsFragment();
        paymentRequestReceivedDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, paymentRequestReceivedDetailsFragment).commit();
        mFabMakingPayment.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    @Override
    public Context setContext() {
        return PaymentActivity.this;
    }
}
