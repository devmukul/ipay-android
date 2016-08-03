package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepOne;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepTwo;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.InvoiceDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepOne;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.RequestPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.SentInvoicesFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoiceActivity extends BaseActivity {

    public FloatingActionButton mFabCreateInvoice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        mFabCreateInvoice = (FloatingActionButton) findViewById(R.id.fab_create_invoice);

        mFabCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToRequestPaymentFragment();
                //switchToCreateInvoiceFragment();
            }
        });

        switchToInvoicesSentFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        Utilities.hideKeyboard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else
            super.onBackPressed();

    }

    public void switchToInvoicesSentFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SentInvoicesFragment())
                .commit();
        mFabCreateInvoice.setVisibility(View.VISIBLE);

    }

    private void switchToCreateInvoiceStepOneFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateInvoiceFragmentStepOne())
                .addToBackStack(null)
                .commit();

        mFabCreateInvoice.setVisibility(View.GONE);
    }

    public void switchToCreateInvoiceStepTwoFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        CreateInvoiceFragmentStepTwo frag = new CreateInvoiceFragmentStepTwo();
        frag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, frag)
                .addToBackStack(null)
                .commit();

        mFabCreateInvoice.setVisibility(View.GONE);
    }

    private void switchToRequestPaymentFragment() {

        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestPaymentFragment())
                .addToBackStack(null)
                .commit();
        mFabCreateInvoice.setVisibility(View.GONE);
    }

    public void switchToInvoiceDetailsFragment(Bundle bundle) {
        InvoiceDetailsFragment invoiceDetailsFragment = new InvoiceDetailsFragment();
        if (bundle != null) {
            invoiceDetailsFragment.setArguments(bundle);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, invoiceDetailsFragment)
                .addToBackStack(null)
                .commit();

        mFabCreateInvoice.setVisibility(View.GONE);
    }

    @Override
    public Context setContext() {
        return InvoiceActivity.this;
    }
}





