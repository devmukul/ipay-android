package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.InvoiceDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.CreateInvoiceFragmentStepOne;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.SentInvoicesFragment;
import bd.com.ipay.ipayskeleton.R;

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
                switchToCreateInvoiceFragment();
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
        if (switchedToInvoicesList) super.onBackPressed();
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





