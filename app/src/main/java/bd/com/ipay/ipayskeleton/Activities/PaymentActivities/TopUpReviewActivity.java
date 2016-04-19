package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments.TopupReviewFragment;

public class TopUpReviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_top_up_review);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new TopupReviewFragment()).commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
        return TopUpReviewActivity.this;
    }
}
