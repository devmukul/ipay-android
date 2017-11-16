package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentRequestReceivedDetailsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.SentReceivedRequestReviewFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.BusinessRoleReviewFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.RecommendationReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class NotificationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        String tag = getIntent().getStringExtra(Constants.TAG);

        if (tag != null && tag.equals(Constants.REQUEST_PAYMENT))
            switchToReceivedPaymentRequestDetailsFragment(getIntent().getExtras());
        else if (tag != null && tag.equals(Constants.REQUEST))
            switchToReceivedRequestReviewFragment();
        else if (tag != null && tag.equals(Constants.RECOMMENDATION))
            switchToRecommendationReviewFragment(getIntent().getExtras());
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

    public void switchToReceivedPaymentRequestDetailsFragment(Bundle bundle) {

        PaymentRequestReceivedDetailsFragment paymentRequestReceivedDetailsFragment = new PaymentRequestReceivedDetailsFragment();
        paymentRequestReceivedDetailsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, paymentRequestReceivedDetailsFragment).commit();
    }

    private void switchToReceivedRequestReviewFragment() {

        SentReceivedRequestReviewFragment receivedRequestReviewFragment = new SentReceivedRequestReviewFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, receivedRequestReviewFragment).commit();

    }

    public void switchToRecommendationReviewFragment(Bundle bundle) {
        RecommendationReviewFragment recommendationReviewFragment = new RecommendationReviewFragment();
        recommendationReviewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, recommendationReviewFragment).commit();
    }

    public void switchToBusinessRoleReviewFragment(Bundle bundle){
        BusinessRoleReviewFragment businessRoleReviewFragment=new BusinessRoleReviewFragment();
        businessRoleReviewFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,businessRoleReviewFragment).commit();
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




