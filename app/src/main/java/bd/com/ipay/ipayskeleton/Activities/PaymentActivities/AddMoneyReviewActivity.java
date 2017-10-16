package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.AddMoneyByBankReviewFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.AddMoneyByCreditOrDebitCardReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddMoneyReviewActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_review);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String addMoneyReviewType = getIntent().getStringExtra(Constants.ADD_MONEY_TYPE);

        switch (addMoneyReviewType) {
            case Constants.ADD_MONEY_TYPE_BY_BANK:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new AddMoneyByBankReviewFragment()).commit();
                break;
            case Constants.ADD_MONEY_TYPE_BY_CREDIT_OR_DEBIT_CARD:
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container, new AddMoneyByCreditOrDebitCardReviewFragment()).commit();
                break;
        }
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

    @Override
    public Context setContext() {
        return AddMoneyReviewActivity.this;
    }
}

