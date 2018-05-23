package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyContactFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyHelperFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyActivity extends BaseActivity {

    public static MandatoryBusinessRules mMandatoryBusinessRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SendMoneyHelperFragment()).commit();

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                .add(R.id.fragment_container, new SendMoneyFragment()).commit();
    }

    public void switchToSendMoneyHelperFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SendMoneyHelperFragment()).commit();
    }

    public void switchToSendMoneyContactFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStack();
        }
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new SendMoneyContactFragment()).commit();
    }

    @Override
    public Context setContext() {
        return SendMoneyActivity.this;
    }
}