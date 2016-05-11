package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import bd.com.ipay.ipayskeleton.DrawerFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.AddressFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.BasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.DocumentListFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.EmailFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.FragmentEditAddress;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.ProfileCompletionFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.RecommendationRequestsFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.PropertyConstants;
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
            if (targetFragment.equals(BASIC_PROFILE))
                switchToBasicInfoFragment();
            else if (targetFragment.equals(ADDRESS))
                switchToAddressFragment();
            else if (targetFragment.equals(VERIFICATION_DOCUMENT))
                switchToDocumentListFragment();
            else if (targetFragment.equals(VERIFIED_EMAIL))
                switchToEmailFragment();
            else if (targetFragment.equals(TRUSTED_NETWORK))
                switchToTrustedNetworkFragment();
            else
                switchToProfileCompletionFragment();
        } else {
            switchToProfileCompletionFragment();
        }
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
        FragmentEditAddress fragmentEditAddress = new FragmentEditAddress();
        fragmentEditAddress.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragmentEditAddress).addToBackStack(null).commit();
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
