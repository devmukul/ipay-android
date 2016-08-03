package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.InvoiceDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.CreateInvoiceFragmentStepOne;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.RequestPaymentFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.InvoiceFragment.SentInvoicesFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InvoiceActivity extends BaseActivity {

    private FloatingActionButton mFabCreateInvoice;
    private boolean switchedToInvoicesList = true;

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
        if (switchedToInvoicesList)
            super.onBackPressed();
        else {
            switchToInvoicesSentFragment();
        }
    }

    public void switchToInvoicesSentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SentInvoicesFragment()).commit();
        mFabCreateInvoice.setVisibility(View.VISIBLE);
        switchedToInvoicesList = true;
    }

    private void switchToCreateInvoiceFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new CreateInvoiceFragmentStepOne()).commit();
        mFabCreateInvoice.setVisibility(View.GONE);
        switchedToInvoicesList = false;
    }

    private void switchToRequestPaymentFragment() {

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestPaymentFragment()).commit();
        mFabCreateInvoice.setVisibility(View.GONE);
        switchedToInvoicesList = false;
    }

    public void switchToInvoiceDetailsFragment(Bundle bundle) {
        InvoiceDetailsFragment invoiceDetailsFragment = new InvoiceDetailsFragment();
        invoiceDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, invoiceDetailsFragment).commit();
        mFabCreateInvoice.setVisibility(View.GONE);
        switchedToInvoicesList = false;
    }

    @Override
    public Context setContext() {
        return InvoiceActivity.this;
    }
}





