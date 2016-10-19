package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;


import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.AddMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.ReceivedMoneRequestsHistoryFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.ReceivedMoneyRequestsFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.SentMoneyRequestsHistoryFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyHistoryActivity extends BaseActivity {

    public static int requestType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);

        requestType = getIntent().getIntExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_RECEIVED_REQUEST);


        if (requestType == Constants.REQUEST_TYPE_RECEIVED_REQUEST)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new ReceivedMoneRequestsHistoryFragment()).commit();
        else
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new SentMoneyRequestsHistoryFragment()).commit();

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

    @Override
    public Context setContext() {
        return RequestMoneyHistoryActivity.this;
    }
}