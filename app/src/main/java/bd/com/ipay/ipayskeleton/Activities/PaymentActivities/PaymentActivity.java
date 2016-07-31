package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.InvoiceHistoryFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.InvoicePaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.MakePaymentFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class PaymentActivity extends BaseActivity {

    private FloatingActionButton mFabMakingPayment;
    private boolean switchedToPendingList = true;
    public static final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        mFabMakingPayment = (FloatingActionButton) findViewById(R.id.fab_payment_making);

        switchToInvoicePaymentFragment();

        mFabMakingPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToMakePaymentFragment();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Utilities.hideKeyboard(this);
        if (item.getItemId() == android.R.id.home) {
            if (switchedToPendingList) {
                super.onBackPressed();
            } else {
                switchToInvoicePaymentFragment();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void onBackPressed() {
        if (switchedToPendingList) {
            super.onBackPressed();
        } else {
            switchToInvoicePaymentFragment();
        }
    }

    private void switchToMakePaymentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MakePaymentFragment()).commit();
        mFabMakingPayment.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    private void switchToInvoicePaymentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new InvoicePaymentFragment()).commit();
        mFabMakingPayment.setVisibility(View.VISIBLE);
        switchedToPendingList = true;
    }

    public void switchToInvoiceHistoryFragment(Bundle bundle) {
        InvoiceHistoryFragment invoiceHistoryFragment = new InvoiceHistoryFragment();
        invoiceHistoryFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, invoiceHistoryFragment).commit();
        mFabMakingPayment.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    @Override
    public Context setContext() {
        return PaymentActivity.this;
    }
}



