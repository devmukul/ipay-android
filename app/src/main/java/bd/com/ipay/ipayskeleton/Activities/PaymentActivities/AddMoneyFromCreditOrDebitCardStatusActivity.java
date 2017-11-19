package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.AddMoneyFromCreditOrDebitCardStatusFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyFromCreditOrDebitCardStatusActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_by_credit_or_debit_card_satus);

        AddMoneyFromCreditOrDebitCardStatusFragment addMoneyFromCreditOrDebitCardStatusFragment = new AddMoneyFromCreditOrDebitCardStatusFragment();
        if (getIntent().hasExtra(Constants.CARD_TRANSACTION_DATA)) {
            addMoneyFromCreditOrDebitCardStatusFragment.setArguments(getIntent().getBundleExtra(Constants.CARD_TRANSACTION_DATA));
        }
        changeFragment(addMoneyFromCreditOrDebitCardStatusFragment);

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

    @Override
    public Context setContext() {
        return AddMoneyFromCreditOrDebitCardStatusActivity.this;
    }

    public void changeFragment(Fragment fragment) {
        changeFragment(fragment, null);
    }

    public void changeFragment(Fragment fragment, @Nullable Bundle bundle) {
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment).commit();

    }
}
