package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.MoneyRequestListHolderFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyActivity extends BaseActivity {

    private FloatingActionButton mFabRequestMoney;
    private boolean switchedToPendingList = true;

    /**
     * If this value is set in the intent extras,
     * you would be taken directly to the new request page
     */
    public static final String LAUNCH_NEW_REQUEST = "LAUNCH_NEW_REQUEST";

    public static final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();

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
        }
        return super.onOptionsItemSelected(item);

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

        setTitle(R.string.request_money);
        mFabRequestMoney.setVisibility(View.VISIBLE);
        switchedToPendingList = true;
    }

    public void switchToRequestMoneyFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RequestMoneyFragment()).commit();

        setTitle(R.string.request_money);
        mFabRequestMoney.setVisibility(View.GONE);
        switchedToPendingList = false;
    }

    @Override
    public Context setContext() {
        return RequestMoneyActivity.this;
    }
}


