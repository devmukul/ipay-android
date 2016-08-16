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
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
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

public class ManagePeopleActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetRolesAsyncTask;
    private GetRolesResponse mGetRolesResponse;

    public static ArrayList<Role> mAllRoleList;
    public static HashMap<Integer, String[]> mRolePrivilegeMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

            mAllRoleList = new ArrayList<>();
            mRolePrivilegeMap = new HashMap<>();
            getAllRoles();

        switchToEmployeeManagementFragment();

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

    private void getAllRoles() {
        if (mGetRolesAsyncTask != null) {
            return;
        }

        mGetRolesAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ALL_ROLES,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_ROLES, ManagePeopleActivity.this, this);
        mGetRolesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void switchToEmployeeManagementFragment() {

        Fragment employeeManagementFragment = new EmployeeManagementFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeManagementFragment).commit();
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

    @Override
    public Context setContext() {
        return ManagePeopleActivity.this;
    }

    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetRolesAsyncTask = null;
            Toast.makeText(ManagePeopleActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
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

                    } else {
                        finish();
                        Toast.makeText(ManagePeopleActivity.this, mGetRolesResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                    Toast.makeText(ManagePeopleActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

            default:
                break;
        }
    }
}
