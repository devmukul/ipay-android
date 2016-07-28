package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.ReceivedRequestReviewFragment;
import bd.com.ipay.ipayskeleton.R;


public class ReceivedRequestReviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receieved_request_review);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToReceivedRequestReviewFragment();
    }

    private void switchToReceivedRequestReviewFragment() {

        ReceivedRequestReviewFragment receivedRequestReviewFragment = new ReceivedRequestReviewFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, receivedRequestReviewFragment).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Context setContext() {
        return ReceivedRequestReviewActivity.this;
    }


}
