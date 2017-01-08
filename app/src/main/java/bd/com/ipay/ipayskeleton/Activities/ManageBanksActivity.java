package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Map;

import bd.com.ipay.ipayskeleton.ManageBanksFragments.AddBankAgreementFragment;
import bd.com.ipay.ipayskeleton.ManageBanksFragments.LinkBankFragment;
import bd.com.ipay.ipayskeleton.ManageBanksFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.BankBranch;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ManageBanksActivity extends BaseActivity {

    public FloatingActionButton mFabAddNewBank;

    public ArrayList<String> mDistrictNames;
    public ArrayList<BankBranch> mBranches;
    public ArrayList<String> mBranchNames;
    public ArrayList<Bank> bankNames;

    public int mSelectedBranchId = -1;
    public int mSelectedBankId = -1;
    public int mSelectedDistrictId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_banks);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFabAddNewBank = (FloatingActionButton) findViewById(R.id.fab_add_new_bank);

        mDistrictNames = new ArrayList<>();
        mBranches = new ArrayList<>();
        mBranchNames = new ArrayList<>();
        bankNames = new ArrayList<>();

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
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        // If back to bank account fragment then set the visibility of add bank button
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            // Check if the account is verified before adding a bank account.
            if (ProfileInfoCacheManager.getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED))
                mFabAddNewBank.setVisibility(View.VISIBLE);
            else mFabAddNewBank.setVisibility(View.GONE);
        }

        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    public void switchToBankAccountsFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BankAccountsFragment()).commit();

        // Check if the account is verified before adding a bank account.
        if (ProfileInfoCacheManager.getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED))
            mFabAddNewBank.setVisibility(View.VISIBLE);
        else mFabAddNewBank.setVisibility(View.GONE);
    }

    private void switchToAddNewBankFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LinkBankFragment()).addToBackStack(null).commit();
        mFabAddNewBank.setVisibility(View.GONE);
    }

    public void switchToAddBankAgreementFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
            getSupportFragmentManager().popBackStackImmediate();

        AddBankAgreementFragment addBankAgreementFragment = new AddBankAgreementFragment();
        addBankAgreementFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, addBankAgreementFragment).addToBackStack(null).commit();
    }

    @Override
    public Context setContext() {
        return ManageBanksActivity.this;
    }
}



