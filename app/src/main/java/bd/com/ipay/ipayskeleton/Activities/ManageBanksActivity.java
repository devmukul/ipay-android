package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import bd.com.ipay.ipayskeleton.ManageBanksFragments.LinkBankFragment;
import bd.com.ipay.ipayskeleton.ManageBanksFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ManageBanksActivity extends BaseActivity {

    private FloatingActionButton mFabAddNewBank;
    private boolean switchedToAddBankFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_banks);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFabAddNewBank = (FloatingActionButton) findViewById(R.id.fab_add_new_bank);

        mFabAddNewBank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToAddNewBankFragment();
            }
        });

        switchToBankAccountsFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            if (switchedToAddBankFragment) {
                switchToBankAccountsFragment();
            }
            else {
                finish();
            }
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (switchedToAddBankFragment)
            switchToBankAccountsFragment();
        else
            super.onBackPressed();
    }

    public void switchToBankAccountsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BankAccountsFragment()).commit();
        mFabAddNewBank.setVisibility(View.VISIBLE);
        switchedToAddBankFragment = false;
    }

    private void switchToAddNewBankFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LinkBankFragment()).commit();
        mFabAddNewBank.setVisibility(View.GONE);
        switchedToAddBankFragment = true;
    }

    @Override
    public Context setContext() {
        return ManageBanksActivity.this;
    }
}




