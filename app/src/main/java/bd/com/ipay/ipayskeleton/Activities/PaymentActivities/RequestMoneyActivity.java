package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.MoneyRequestListHolderFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyActivity extends BaseActivity {

    private FloatingActionButton mFabRequestMoney;
    private boolean switchedToPendingList = true;
    public static boolean switchedToSentRequestFragment = false;
    public static boolean switchedToReceivedRequestFragment = true;

    private Menu mOptionsMenu;

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
            switchToMoneyRequestListFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.action_notification) {
            Intent intent = new Intent(this, RequestMoneyHistoryActivity.class);
            if(switchedToReceivedRequestFragment)
            intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_RECEIVED_REQUEST);
            else intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_SENT_REQUEST);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_activity, menu);
        mOptionsMenu = menu;

        // If the menu is recreated, then restore the previous badge count
        return true;
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra(LAUNCH_NEW_REQUEST, false)) {
            finish();
        } else if (switchedToPendingList) {
            super.onBackPressed();
        } else {
            switchToMoneyRequestListFragment();
        }
    }

    public void switchToMoneyRequestListFragment() {
        switchToMoneyRequestListFragment(false);
    }

    public void switchToMoneyRequestListFragment(boolean switchToSentRequestsFragment) {
        MoneyRequestListHolderFragment moneyRequestListHolderFragment = new MoneyRequestListHolderFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(MoneyRequestListHolderFragment.SWITCH_TO_SENT_REQUESTS, switchToSentRequestsFragment);
        moneyRequestListHolderFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, moneyRequestListHolderFragment).commit();

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


