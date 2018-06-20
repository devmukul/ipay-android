package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyHelperFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyRecheckFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneySuccessFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.TransactionContactFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyActivity extends BaseActivity {

    public static MandatoryBusinessRules mMandatoryBusinessRules;
    public Toolbar toolbar;
    public TextView mToolbarHelpText;
    public TextView mTitle;
    public ImageView backButton;

    public Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
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
        switchToSendMoneyHelperFragment();
    }

    public void showTitle() {
        mTitle.setVisibility(View.VISIBLE);
    }

    public void hideTitle() {
        mTitle.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void switchToSendMoneyFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SendMoneyFragment()).commit();
    }

    public void switchToSendMoneyHelperFragment() {
        switchToSendMoneyHelperFragment(false);
    }

    public void switchToSendMoneyHelperFragment(boolean isBackPresent) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        if (isBackPresent) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("isBackPresent", isBackPresent);
            SendMoneyHelperFragment sendMoneyHelperFragment = new SendMoneyHelperFragment();
            sendMoneyHelperFragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, sendMoneyHelperFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SendMoneyHelperFragment()).commit();
        }
    }

    public void switchToSendMoneyContactFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        TransactionContactFragment transactionContactFragment = new TransactionContactFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SOURCE, Constants.SEND_MONEY);
        transactionContactFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in_enter, R.anim.up_to_down_exit)
                .replace(R.id.fragment_container, transactionContactFragment).addToBackStack(null).commit();
    }

    public void switchToSendMoneyRecheckFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        this.bundle = bundle;
        SendMoneyRecheckFragment sendMoneyRecheckFragment = new SendMoneyRecheckFragment();
        sendMoneyRecheckFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.right_to_left_enter, R.anim.right_to_left_exit)
                .replace(R.id.fragment_container, sendMoneyRecheckFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public Context setContext() {
        return SendMoneyActivity.this;
    }

    public void switchToSendMoneySuccessFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 4) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        SendMoneySuccessFragment sendMoneySuccessFragment = new SendMoneySuccessFragment();
        sendMoneySuccessFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sendMoneySuccessFragment).addToBackStack(null).commit();
    }
}