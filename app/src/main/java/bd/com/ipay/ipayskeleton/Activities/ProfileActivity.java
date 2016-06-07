package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.DrawerFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.AddressFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.BasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.IdentificationDocumentListFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EmailFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EditAddressFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.IntroducerFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.ProfileCompletionFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.*;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        String targetFragment = getIntent().getStringExtra(Constants.TARGET_FRAGMENT);

        if (targetFragment != null) {
            switchToFragment(targetFragment, null, false);
        } else {
            switchToProfileCompletionFragment();
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
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

    public void switchToFragment(String targetFragment, Bundle bundle, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        switch (targetFragment) {
            case VERIFY_BANK:
            case ADD_BANK:
                fragment = new BankAccountsFragment();
                break;
            case TRUSTED_NETWORK:
            case TRUSTED_NETWORK_AND_PASSWORD_RECOVERY_RULE:
                fragment = new TrustedNetworkFragment();
                break;
            case BASIC_PROFILE:
            case PROFILE_PICTURE:
            case PARENT:
                fragment = new BasicInfoFragment();
                break;
            case INTRODUCER:
                fragment = new IntroducerFragment();
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
            default:
                fragment = new ProfileCompletionFragment();
        }

        if (bundle != null)
            fragment.setArguments(bundle);

        ft.replace(R.id.fragment_container, fragment);
        if (addToBackStack)
                ft.addToBackStack(null);

        ft.commit();
    }

    public void switchToBasicInfoFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BasicInfoFragment()).commit();
    }

    public void switchToProfileCompletionFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileCompletionFragment()).commit();
    }

    public void switchToEditBasicInfoFragment(Bundle bundle) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        EditBasicInfoFragment editBasicInfoFragment = new EditBasicInfoFragment();
        editBasicInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editBasicInfoFragment).addToBackStack(null).commit();
    }

    public void switchToAddressFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddressFragment()).commit();
    }

    public void switchToEditAddressFragment(Bundle bundle) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        EditAddressFragment editAddressFragment = new EditAddressFragment();
        editAddressFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editAddressFragment).addToBackStack(null).commit();
    }

    public void switchToEmailFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmailFragment()).commit();
    }

    public void switchToTrustedNetworkFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TrustedNetworkFragment()).commit();
    }

    public void switchToDocumentUploadFragment(Bundle bundle) {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        DocumentUploadFragment documentUploadFragment = new DocumentUploadFragment();
        documentUploadFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, documentUploadFragment).addToBackStack(null).commit();
    }

    public void switchToIdentificationDocumentListFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IdentificationDocumentListFragment()).commit();
    }

    public void switchToBankFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BankAccountsFragment()).commit();
    }

    public void switchToIntroducerFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IntroducerFragment()).commit();
    }

    @Override
    public Context setContext() {
        return ProfileActivity.this;
    }
}
