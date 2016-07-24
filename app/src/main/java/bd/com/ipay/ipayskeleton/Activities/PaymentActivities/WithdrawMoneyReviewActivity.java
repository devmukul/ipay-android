package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments.WithdrawMoneyReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class WithdrawMoneyReviewActivity extends BaseActivity {

    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_money_review);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new WithdrawMoneyReviewFragment()).commit();

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
        return WithdrawMoneyReviewActivity.this;
    }
}

