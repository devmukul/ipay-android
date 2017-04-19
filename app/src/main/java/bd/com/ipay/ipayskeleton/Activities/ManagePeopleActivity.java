package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EmployeeDetailsFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.CreateEmployeeFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EmployeeManagementFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EmployeePrivilegeFragment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.GetRolesResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.Role;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

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

        while (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();

        Fragment employeeManagementFragment = new EmployeeManagementFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeManagementFragment).commit();
    }


    public void switchToEmployeeInformationFragment(Bundle bundle) {

        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        Fragment employeeInformationFragment = new CreateEmployeeFragment();
        if (bundle != null)
            employeeInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeInformationFragment).addToBackStack(null).commit();
    }

    public void switchToEmployeeInformationDetailsFragment(Bundle bundle) {

        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
            getSupportFragmentManager().popBackStackImmediate();

        Fragment employeeInformationDetailsFragment = new EmployeeDetailsFragment();
        if (bundle != null)
            employeeInformationDetailsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeInformationDetailsFragment).addToBackStack(null).commit();
    }


    public void switchToEditEmployeeInformationFragment(Bundle bundle) {

        while (getSupportFragmentManager().getBackStackEntryCount() > 1)
            getSupportFragmentManager().popBackStackImmediate();

        Fragment editEmployeeInformationFragment = new CreateEmployeeFragment();
        if (bundle != null)
            editEmployeeInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editEmployeeInformationFragment).addToBackStack(null).commit();
    }

    public void switchToEmployeePrivilegeFragment(Bundle bundle) {

        while (getSupportFragmentManager().getBackStackEntryCount() > 2)
            getSupportFragmentManager().popBackStackImmediate();

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

    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetRolesAsyncTask = null;
            Toaster.makeText(ManagePeopleActivity.this, R.string.service_not_available, Toast.LENGTH_LONG);
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
                        Toaster.makeText(ManagePeopleActivity.this, mGetRolesResponse.getMessage(), Toast.LENGTH_LONG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                    Toaster.makeText(ManagePeopleActivity.this, R.string.service_not_available, Toast.LENGTH_LONG);
                }

            default:
                break;
        }
    }
}
