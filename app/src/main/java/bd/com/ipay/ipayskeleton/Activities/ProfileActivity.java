package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.DrawerFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.AddressFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.BasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.DocumentListFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EmailFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EditAddressFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.ProfileCompletionFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.RecommendationRequestsFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import static  bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.PropertyConstants.*;

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
            else
                finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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
                fragment = new RecommendationRequestsFragment();
                break;
            case ADDRESS:
                fragment = new AddressFragment();
                break;
            case VERIFIED_EMAIL:
                fragment = new EmailFragment();
                break;
            case VERIFICATION_DOCUMENT:
            case PHOTOID:
                fragment = new DocumentListFragment();
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
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BasicInfoFragment()).commit();
    }

    public void switchToProfileCompletionFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileCompletionFragment()).commit();
    }

    public void switchToEditBasicInfoFragment(Bundle bundle) {
        EditBasicInfoFragment editBasicInfoFragment = new EditBasicInfoFragment();
        editBasicInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editBasicInfoFragment).addToBackStack(null).commit();
    }

    public void switchToAddressFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddressFragment()).commit();
    }

    public void switchToEditAddressFragment(Bundle bundle) {
        EditAddressFragment editAddressFragment = new EditAddressFragment();
        editAddressFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editAddressFragment).addToBackStack(null).commit();
    }

    public void switchToEmailFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EmailFragment()).commit();
    }

    public void switchToTrustedNetworkFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TrustedNetworkFragment()).commit();
    }

    public void switchToDocumentUploadFragment(Bundle bundle) {
        DocumentUploadFragment documentUploadFragment = new DocumentUploadFragment();
        documentUploadFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, documentUploadFragment).addToBackStack(null).commit();
    }

    public void switchToDocumentListFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new DocumentListFragment()).commit();
    }

    public void switchToBankFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BankAccountsFragment()).commit();
    }

    public void switchToIntroducerFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecommendationRequestsFragment()).commit();
    }

    @Override
    public Context setContext() {
        return ProfileActivity.this;
    }
}
