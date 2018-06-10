package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.MoneyRequestListHolderFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyConfirmFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyHelperFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyRecheckFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneySuccessFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.TransactionContactFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyActivity extends BaseActivity {

    /**
     * If this value is set in the intent extras,
     * you would be taken directly to the new request page
     */
    public static final String LAUNCH_NEW_REQUEST = "LAUNCH_NEW_REQUEST";
    public static MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules(Constants.REQUEST_MONEY);
    private FloatingActionButton mFabRequestMoney;
    private boolean switchedToPendingList = true;

    public Toolbar toolbar;
    public TextView mToolbarHelpText;
    public TextView mTitle;
    public ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarHelpText = (TextView) toolbar.findViewById(R.id.help_text_view);
        mTitle = (TextView) toolbar.findViewById(R.id.title);
        backButton = (ImageView) toolbar.findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        mToolbarHelpText.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        switchToRequestMoneyHelperFragment();
    }

    public void switchToRequestMoneyHelperFragment() {
        switchToRequestMoneyHelperFragment(false);
    }

    public void showTitle() {
        mTitle.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        mTitle.setVisibility(View.GONE);
    }

    public void switchToRequestMoneyHelperFragment(boolean isBackPresent) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        if (isBackPresent) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isBackPresent", isBackPresent);
            RequestMoneyHelperFragment requestMoneyHelperFragment = new RequestMoneyHelperFragment();
            requestMoneyHelperFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, requestMoneyHelperFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new RequestMoneyHelperFragment()).commit();
        }
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
        } else {
            super.onBackPressed();
        }
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

    public void switchToRequestMoneyContactFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        TransactionContactFragment transactionContactFragment = new TransactionContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SOURCE, Constants.REQUEST_MONEY);
        transactionContactFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, transactionContactFragment).addToBackStack(null).commit();
    }

    public void switchToRequestMoneyRecheckFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        RequestMoneyRecheckFragment requestMoneyRecheckFragment = new RequestMoneyRecheckFragment();
        requestMoneyRecheckFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, requestMoneyRecheckFragment).addToBackStack(null).commit();
    }

    public void switchToSendMoneyConfirmFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        RequestMoneyConfirmFragment requestMoneyConfirmFragment = new RequestMoneyConfirmFragment();
        requestMoneyConfirmFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, requestMoneyConfirmFragment).addToBackStack(null).commit();
    }

    public void switchToRequestMoneySuccessFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 4) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        RequestMoneySuccessFragment requestMoneyFragment = new RequestMoneySuccessFragment();
        requestMoneyFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, requestMoneyFragment).addToBackStack(null).commit();
    }

    @Override
    public Context setContext() {
        return RequestMoneyActivity.this;
    }
}

