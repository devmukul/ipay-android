package bd.com.ipay.ipayskeleton.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyReviewFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyReviewFragment;
import bd.com.ipay.ipayskeleton.R;

public class RequestMoneyReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money_review);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new RequestMoneyReviewFragment()).commit();

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
}
