package bd.com.ipay.ipayskeleton.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SendMoneyFragments.SendMoneyReviewFragment;
import bd.com.ipay.ipayskeleton.ServicesFragments.MobileTopupReviewFragment;

public class MobileTopUpReviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_top_up_review);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new MobileTopupReviewFragment()).commit();

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
