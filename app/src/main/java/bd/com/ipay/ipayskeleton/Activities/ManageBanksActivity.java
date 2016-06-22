package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.View;

import bd.com.ipay.ipayskeleton.ManageBanksFragments.AddBankFragment;
import bd.com.ipay.ipayskeleton.ManageBanksFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.R;

public class ManageBanksActivity extends BaseActivity {

    private FloatingActionButton mFabAddNewBank;
    private boolean switchedToAddBankFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_banks);
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
    public void onBackPressed() {
        if (!switchedToAddBankFragment) super.onBackPressed();
        else {
            switchToBankAccountsFragment();
        }
    }

    public void switchToBankAccountsFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BankAccountsFragment()).commit();
        mFabAddNewBank.setVisibility(View.VISIBLE);
        switchedToAddBankFragment = false;
    }

    public void switchToAddNewBankFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddBankFragment()).commit();
        mFabAddNewBank.setVisibility(View.GONE);
        switchedToAddBankFragment = true;
    }

    @Override
    public Context setContext() {
        return ManageBanksActivity.this;
    }
}




