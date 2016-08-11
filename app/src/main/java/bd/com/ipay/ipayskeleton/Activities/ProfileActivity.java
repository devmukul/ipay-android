package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.BusinessContactFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.BusinessInformationFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EmployeeDetailsFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.BusinessBasicInfoFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.CreateEmployeeFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EditBusinessInformationFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EmployeeManagementFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EmployeePrivilegeFragment;
import bd.com.ipay.ipayskeleton.ManageBanksFragments.LinkBankFragment;
import bd.com.ipay.ipayskeleton.ManageBanksFragments.BankAccountsFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.GetRolesResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.Role;
import bd.com.ipay.ipayskeleton.ProfileFragments.AccountFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.AddressFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.BasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EditAddressFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EditParentInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.EmailFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.IdentificationDocumentListFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.IdentificationHolderFragment;
import bd.com.ipay.ipayskeleton.ProfileFragments.ProfileCompletionFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SecuritySettingsFragments.TrustedNetworkFragment;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_DOCUMENTS;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_INFO;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PERSONAL_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BUSINESS_ADDRESS;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.LINK_AND_VERIFY_BANK;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.BASIC_PROFILE;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.INTRODUCER;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PARENT;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PHOTOID;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_COMPLETENESS;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_INFO;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.PROFILE_PICTURE;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.TRUSTED_NETWORK;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.TRUSTED_NETWORK_AND_PASSWORD_RECOVERY_RULE;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFICATION_DOCUMENT;
import static bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants.VERIFIED_EMAIL;

public class ProfileActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetRolesAsyncTask;
    private GetRolesResponse mGetRolesResponse;

    public static ArrayList<Role> mAllRoleList;
    public static HashMap<Integer, String[]> mRolePrivilegeMap;


    private final String STARTED_FROM_PROFILE_ACTIVITY = "started_from_profile_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        mAllRoleList = new ArrayList<>();
        mRolePrivilegeMap = new HashMap<>();
        getAllRoles();

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
            case LINK_AND_VERIFY_BANK:
                args.putBoolean(STARTED_FROM_PROFILE_ACTIVITY, true);
                break;
            default:
                args = null;
                break;
        }

        return args;
    }

    private void getAllRoles() {
        if (mGetRolesAsyncTask != null) {
            return;
        }

        mGetRolesAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ALL_ROLES,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_ROLES, ProfileActivity.this, this);
        mGetRolesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void switchToFragment(String targetFragment, Bundle bundle, boolean addToBackStack) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment;

        if (targetFragment.equals(LINK_AND_VERIFY_BANK)) {
            Intent intent = new Intent(ProfileActivity.this, ManageBanksActivity.class);
            startActivity(intent);
        } else {
            switch (targetFragment) {
                case Constants.VERIFY_BANK:
                    fragment = new BankAccountsFragment();
                    break;
                case Constants.LINK_BANK:
                    fragment = new LinkBankFragment();
                    break;
                case TRUSTED_NETWORK:
                case TRUSTED_NETWORK_AND_PASSWORD_RECOVERY_RULE:
                    fragment = new TrustedNetworkFragment();
                    break;
                case BASIC_PROFILE:
                    fragment = new BasicInfoFragment();
                    break;
                case BUSINESS_INFO:
                    fragment = new BusinessInformationFragment();
                    break;
                case PROFILE_PICTURE:
                    fragment = new AccountFragment();
                    break;
                case PARENT:
                    fragment = new BasicInfoFragment();
                    break;
                case INTRODUCER:
                    if (bundle == null) bundle = new Bundle();
                    bundle.putString(Constants.INTRODUCER, "introducer");
                    fragment = new IdentificationHolderFragment();
                    break;
                case PERSONAL_ADDRESS:
                case BUSINESS_ADDRESS:
                    fragment = new AddressFragment();
                    break;
                case VERIFIED_EMAIL:
                    fragment = new EmailFragment();
                    break;
                case BUSINESS_DOCUMENTS:
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
    }

    private void switchToProfileFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
    }

    public void switchToBasicInfoFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BasicInfoFragment()).addToBackStack(null).commit();
    }

    public void switchToBusinessContactFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BusinessContactFragment()).addToBackStack(null).commit();
    }

    public void switchToBusinessBasicInfoHolderFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BusinessBasicInfoFragment()).addToBackStack(null).commit();
    }

    public void switchToEmployeeManagementFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        Fragment employeeManagementFragment = new EmployeeManagementFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeManagementFragment).addToBackStack(null).commit();
    }

    public void switchToEmployeeInformationFragment(Bundle bundle) {
        Fragment employeeInformationFragment = new CreateEmployeeFragment();
        if (bundle != null)
            employeeInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeInformationFragment).addToBackStack(null).commit();
    }

    public void switchToEmployeeInformationDetailsFragment(Bundle bundle) {
        Fragment employeeInformationDetailsFragment = new EmployeeDetailsFragment();
        if (bundle != null)
            employeeInformationDetailsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeInformationDetailsFragment).addToBackStack(null).commit();
    }


    public void switchToEditEmployeeInformationFragment(Bundle bundle) {
        Fragment editEmployeeInformationFragment = new CreateEmployeeFragment();
        if (bundle != null)
            editEmployeeInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editEmployeeInformationFragment).addToBackStack(null).commit();
    }

    public void switchToEmployeePrivilegeFragment(Bundle bundle) {
        Fragment employeePrivilegeFragment = new EmployeePrivilegeFragment();
        if (bundle != null)
            employeePrivilegeFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeePrivilegeFragment).addToBackStack(null).commit();
    }

    public void switchToBusinessInfoFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BusinessInformationFragment()).addToBackStack(null).commit();
    }

    public void switchToEditBusinessInformationFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3)
            getSupportFragmentManager().popBackStackImmediate();

        Fragment editBusinessInformationFragment = new EditBusinessInformationFragment();
        if (bundle != null)
            editBusinessInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editBusinessInformationFragment).addToBackStack(null).commit();
    }

    public void switchToProfileCompletionFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileCompletionFragment()).addToBackStack(null).commit();
    }

    public void switchToEditBasicInfoFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
            getSupportFragmentManager().popBackStackImmediate();

        EditBasicInfoFragment editBasicInfoFragment = new EditBasicInfoFragment();
        editBasicInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editBasicInfoFragment).addToBackStack(null).commit();
    }

    public void switchToEditParentInfoFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
            getSupportFragmentManager().popBackStackImmediate();

        EditParentInfoFragment editParentInfoFragment = new EditParentInfoFragment();
        editParentInfoFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, editParentInfoFragment).addToBackStack(null).commit();
    }

    public void switchToAddressFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AddressFragment()).addToBackStack(null).commit();
    }

    public void switchToEditAddressFragment(Bundle bundle) {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
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

    public void switchToIdentificationDocumentListFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IdentificationDocumentListFragment()).addToBackStack(null).commit();
    }

    public void switchToIntroducerFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new IdentificationHolderFragment()).addToBackStack(null).commit();
    }

    @Override
    public Context setContext() {
        return ProfileActivity.this;
    }

    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetRolesAsyncTask = null;
            Toast.makeText(ProfileActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_ALL_ROLES:
                try {
                    mGetRolesResponse = gson.fromJson(result.getJsonString(), GetRolesResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        mAllRoleList = (ArrayList<Role>) mGetRolesResponse.getRoles();

                        // Create a hash map for roleId - Privileges
                        for (Role mRole : mAllRoleList)
                            mRolePrivilegeMap.put(mRole.getId(), mRole.getPrivileges());

                        switchToProfileFragment();

                    } else {
                        finish();
                        Toast.makeText(ProfileActivity.this, mGetRolesResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                    Toast.makeText(ProfileActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

            default:
                break;
        }
    }
}
