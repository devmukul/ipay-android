package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.AddMoneyFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyActivity extends BaseActivity {

    public static final int ADD_MONEY_REVIEW_REQUEST = 101;
    public static boolean FROM_ON_BOARD;

    public static MandatoryBusinessRules mMandatoryBusinessRules;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        if (getIntent().getStringExtra(Constants.INTENDED_FRAGMENT) != null) {
            getIntent().putExtra(Constants.TAG, "CARD");
        }
        if (getIntent() != null) {
            try {
                FROM_ON_BOARD = getIntent().getBooleanExtra(Constants.FROM_ON_BOARD, false);
            } catch (Exception e) {

            }
        }
        changeFragment(new AddMoneyFragment());

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
        return AddMoneyActivity.this;
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