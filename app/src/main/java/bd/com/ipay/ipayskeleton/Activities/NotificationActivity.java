package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.InvoiceHistoryFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.ReceivedRequestReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class NotificationActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle.get(Constants.TAG).equals(Constants.INVOICE))
            switchToInvoiceHistoryFrament(bundle);
        else if (bundle.get(Constants.TAG).equals(Constants.REQUEST))
            switchToReceivedRequestReviewFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
            else {
                finish();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void switchToInvoiceHistoryFrament(Bundle bundle) {

        InvoiceHistoryFragment invoiceHistoryFragment = new InvoiceHistoryFragment();
        invoiceHistoryFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, invoiceHistoryFragment).commit();
    }

    private void switchToReceivedRequestReviewFragment() {

        ReceivedRequestReviewFragment receivedRequestReviewFragment = new ReceivedRequestReviewFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, receivedRequestReviewFragment).commit();

    }

    @Override
    public Context setContext() {
        return NotificationActivity.this;
    }
}




