package bd.com.ipay.ipayskeleton.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.RequestMoneyFragments.RequestFragments;
import bd.com.ipay.ipayskeleton.RequestMoneyFragments.RequestMoneyFragment;

public class RequestMoneyActivity extends AppCompatActivity {

    private FloatingActionButton mFabRequestMoney;
    private boolean switchedToPendingList = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);
        mFabRequestMoney = (FloatingActionButton) findViewById(R.id.fab_request_money);

        mFabRequestMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToRequestMoneyFragment();
            }
        });

        switchToRequestsFragment();
    }

    @Override
    public void onBackPressed() {
        if (switchedToPendingList) super.onBackPressed();
        else {
            switchToRequestsFragment();
        }
    }

    public void switchToRequestsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestFragments()).commit();
        mFabRequestMoney.setVisibility(View.VISIBLE);
        switchedToPendingList = true;
    }

    public void switchToRequestMoneyFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestMoneyFragment()).commit();
        mFabRequestMoney.setVisibility(View.GONE);
        switchedToPendingList = false;
    }
}


