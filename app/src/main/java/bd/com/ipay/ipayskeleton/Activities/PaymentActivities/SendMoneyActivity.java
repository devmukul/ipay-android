package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyConfirmFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyContactFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyHelperFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyRecheckFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyActivity extends BaseActivity {

    public static MandatoryBusinessRules mMandatoryBusinessRules;
    public Toolbar toolbar;
    public TextView mToolbarHelpText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarHelpText = (TextView) toolbar.findViewById(R.id.help_text_view);
        mToolbarHelpText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchToSendMoneyHelperFragment();
            }
        });
        mToolbarHelpText.setVisibility(View.GONE);
        setSupportActionBar(toolbar);
        switchToSendMoneyHelperFragment();
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
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SendMoneyHelperFragment()).commit();
    }

    public void switchToSendMoneyContactFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SendMoneyContactFragment()).addToBackStack(null).commit();
    }

    public void switchToSendMoneyRecheckFragment(ProfileImageView imageView, Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        SendMoneyRecheckFragment sendMoneyRecheckFragment = new SendMoneyRecheckFragment();
        sendMoneyRecheckFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .addSharedElement(imageView, ViewCompat.getTransitionName(imageView))
                .replace(R.id.fragment_container, sendMoneyRecheckFragment).addToBackStack(null).commit();
    }
    public void switchToSendMoneyConfirmFragment(Bundle bundle){
        while(getSupportFragmentManager().getBackStackEntryCount()>3){
            getSupportFragmentManager().popBackStackImmediate();
        }
        SendMoneyConfirmFragment sendMoneyConfirmFragment = new SendMoneyConfirmFragment();
        sendMoneyConfirmFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sendMoneyConfirmFragment).addToBackStack(null).commit();
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
}