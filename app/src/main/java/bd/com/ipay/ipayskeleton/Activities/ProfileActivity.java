package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.ManageBanksFragments.AddBankFragment;
import bd.com.ipay.ipayskeleton.ManageBanksFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.AddressFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.BasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.IdentificationDocumentListFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EmailFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EditAddressFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.IntroducerFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.IntroducedFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.ProfileCompletionFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.AccountFragment;
import bd.com.ipay.ipayskeleton.Account_Security_Settings_Fragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.*;

public class ProfileActivity extends BaseActivity {

    private final String STARTED_FROM_PROFILE_ACTIVITY = "started_from_profile_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        String targetFragment = getIntent().getStringExtra(Constants.TARGET_FRAGMENT);

        if (targetFragment != null) {
            Bundle args = setBundle(targetFragment);
            switchToFragment(targetFragment, args, false);
        } else {
            switchToProfileFragment();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            if (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStack();
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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    private Bundle setBundle(String targetFragment) {
        Bundle args = new Bundle();
        switch (targetFragment) {
            case ADD_BANK:
                args.putBoolean(STARTED_FROM_PROFILE_ACTIVITY, true);
                break;
            default:
                args = null;
                break;
        }

        return args;
    }

    public void switchToFragment(String targetFragment, Bundle bundle, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        switch (targetFragment) {
            case VERIFY_BANK:
                fragment = new BankAccountsFragment();
                break;
            case ADD_BANK:
                fragment = new AddBankFragment();
                break;
            case TRUSTED_NETWORK:
            case TRUSTED_NETWORK_AND_PASSWORD_RECOVERY_RULE:
                fragment = new TrustedNetworkFragment();
                break;
            case BASIC_PROFILE:
                fragment = new BasicInfoFragment();
                break;
            case PROFILE_PICTURE:
                fragment = new AccountFragment();
                break;
            case PARENT:
                fragment = new BasicInfoFragment();
                break;
            case INTRODUCER:
                fragment = new IntroducerFragment();
                break;
            case INTRODUCED:
                fragment = new IntroducedFragment();
                break;
            case ADDRESS:
                fragment = new AddressFragment();
                break;
            case VERIFIED_EMAIL:
                fragment = new EmailFragment();
                break;
            case VERIFICATION_DOCUMENT:
            case PHOTOID:
                fragment = new IdentificationDocumentListFragment();
                break;
            case PROFILE_COMPLETENESS:
                fragment = new ProfileCompletionFragment();
                break;
            case PROFILE_INFO:
                fragment = new AccountFragment();
            default:
                fragment = new AccountFragment();
        }

        if (bundle != null)
            fragment.setArguments(bundle);

        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack)
            ft.addToBackStack(null);

        ft.commit();
    }

    private void switchToProfileFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
    }

    public void switchToBasicInfoFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BasicInfoFragment()).addToBackStack(null).commit();
    }

    public void switchToProfileCompletionFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileCompletionFragment()).addToBackStack(null).commit();
    }

    public void switchToEditBasicInfoFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        EditBasicInfoFragment editBasicInfoFragment = new EditBasicInfoFragment();
        editBasicInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editBasicInfoFragment).addToBackStack(null).commit();
    }

    public void switchToAddressFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddressFragment()).addToBackStack(null).commit();
    }

    public void switchToEditAddressFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        EditAddressFragment editAddressFragment = new EditAddressFragment();
        editAddressFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editAddressFragment).addToBackStack(null).commit();
    }

    public void switchToEmailFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmailFragment()).addToBackStack(null).commit();
    }

    public void switchToTrustedNetworkFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TrustedNetworkFragment()).addToBackStack(null).commit();
    }

    public void switchToDocumentUploadFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        DocumentUploadFragment documentUploadFragment = new DocumentUploadFragment();
        documentUploadFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, documentUploadFragment).addToBackStack(null).commit();
    }

    public void switchToIdentificationDocumentListFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IdentificationDocumentListFragment()).addToBackStack(null).commit();
    }

    public void switchToBankFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BankAccountsFragment()).addToBackStack(null).commit();
    }

    public void switchToIntroducerFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IntroducerFragment()).addToBackStack(null).commit();
    }

    public void switchToProfileInfoFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).addToBackStack(null).commit();
    }

    @Override
    public Context setContext() {
        return ProfileActivity.this;
    }
}
