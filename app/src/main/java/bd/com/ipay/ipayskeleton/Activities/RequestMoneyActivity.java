package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.RequestMoneyFragments.MyRequestFragment;
import bd.com.ipay.ipayskeleton.RequestMoneyFragments.RequestMoneyFragment;

public class RequestMoneyActivity extends BaseActivity {

    private FloatingActionButton mFabRequestMoney;
    private boolean switchedToPendingList = true;

    /**
     * If this value is set in the intent extras,
     * you would be taken directly to the new request page
     */
    public static final String LAUNCH_NEW_REQUEST = "LAUNCH_NEW_REQUEST";

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

        if (getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false))
            switchToRequestMoneyFragment();
        else
            switchToMyRequestsFragment();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
            finish();
        } else if (switchedToPendingList) {
            super.onBackPressed();
        } else {
            switchToMyRequestsFragment();
        }
    }

    public void switchToMyRequestsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MyRequestFragment()).commit();
        mFabRequestMoney.setVisibility(View.VISIBLE);
        switchedToPendingList = true;
    }

    public void switchToRequestMoneyFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestMoneyFragment()).commit();
        mFabRequestMoney.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    @Override
    public Context setContext() {
        return RequestMoneyActivity.this;
    }
}


