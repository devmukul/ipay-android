package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.InvoiceHistoryFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.ReceivedRequestReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        String tag = getIntent().getStringExtra(Constants.TAG);

        if (tag != null && tag.equals(Constants.INVOICE))
            switchToInvoiceHistoryFragment(getIntent().getExtras());
        else if (tag != null && tag.equals(Constants.REQUEST))
            switchToReceivedRequestReviewFragment();
        else
            switchToNotificationFragment();

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

    public void switchToNotificationFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, HomeActivity.mNotificationFragment).commit();
    }

    public void switchToInvoiceHistoryFragment(Bundle bundle) {

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

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else {
            super.onBackPressed();
        }
    }
}




