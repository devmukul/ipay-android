package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.LinkedList;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.AddMoneyFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments.AddMoneyHistoryFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyActivity extends BaseActivity {

    public static final MandatoryBusinessRules mMandatoryBusinessRules = new MandatoryBusinessRules();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);
        switchToAddMoneyFragment();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_add_money_history, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        }else if(item.getItemId() == R.id.action_history) {
            switchToAddMoneyHistoryFragment();
            return true;
        }else {
            return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        Utilities.hideKeyboard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            switchToAddMoneyFragment();
        } else {
            finish();
        }
    }


    @Override
    public Context setContext() {
        return AddMoneyActivity.this;
    }

    public void switchToAddMoneyHistoryFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddMoneyHistoryFragment fragment = new AddMoneyHistoryFragment();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void switchToAddMoneyFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddMoneyFragment()).addToBackStack(null).commit();
    }
}