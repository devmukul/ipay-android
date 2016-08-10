package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.Business;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.CreateEmployeeResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.GetEmployeeDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.GetRolesResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.Role;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.UpdateEmployeeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetRolesAsyncTask;
    private GetRolesResponse mGetRolesResponse;

    public static ArrayList<Role> mAllRoleList;
    public static HashMap<Integer, String[]> mRolePrivilegeMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        mAllRoleList = new ArrayList<Role>();
        mRolePrivilegeMap = new HashMap<Integer, String[]>();
        getAllRoles();

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

    private void getAllRoles() {
        if (mGetRolesAsyncTask != null) {
            return;
        }

        mGetRolesAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ALL_ROLES,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_ROLES, BusinessActivity.this, this);
        mGetRolesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void switchToBusinessFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BusinessFragment()).commit();
    }

    // Business -> Business Information
    public void switchToBusinessInformationFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new BusinessInformationFragment()).addToBackStack(null).commit();
    }

    // Business -> Business Information -> Edit Business Information
    public void switchToEditBusinessInformationFragment(Bundle bundle) {
        Fragment editBusinessInformationFragment = new EditBusinessInformationFragment();
        if (bundle != null)
            editBusinessInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editBusinessInformationFragment).addToBackStack(null).commit();
    }

    // Business -> Employee Management
    public void switchToEmployeeManagementFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }

        Fragment employeeManagementFragment = new EmployeeManagementFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeManagementFragment).addToBackStack(null).commit();
    }

    // Business -> Employee Management -> Employee Information
    public void switchToEmployeeInformationFragment(Bundle bundle) {
        Fragment employeeInformationFragment = new CreateEmployeeFragment();
        if (bundle != null)
            employeeInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeInformationFragment).addToBackStack(null).commit();
    }

    // Business -> Employee Management -> Employee Information -> Employee Privilege
    public void switchToEmployeePrivilegeFragment(Bundle bundle) {
        Fragment employeePrivilegeFragment = new EmployeePrivilegeFragment();
        if (bundle != null)
            employeePrivilegeFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeePrivilegeFragment).addToBackStack(null).commit();
    }


    // Business -> Employee Management -> Employee Information details
    public void switchToEmployeeInformationDetailsFragment(Bundle bundle) {
        Fragment employeeInformationDetailsFragment = new EmployeeDetailsFragment();
        if (bundle != null)
            employeeInformationDetailsFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, employeeInformationDetailsFragment).addToBackStack(null).commit();
    }

    // Business -> Employee Management -> Employee Information edit
    public void switchToEditEmployeeInformationFragment(Bundle bundle) {
        Fragment editEmployeeInformationFragment = new CreateEmployeeFragment();
        if (bundle != null)
            editEmployeeInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editEmployeeInformationFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStack();
        else
            super.onBackPressed();
    }

    @Override
    public Context setContext() {
        return BusinessActivity.this;
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetRolesAsyncTask = null;
            Toast.makeText(BusinessActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
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

                        switchToBusinessFragment();

                    } else {
                        finish();
                        Toast.makeText(BusinessActivity.this, mGetRolesResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    finish();
                    Toast.makeText(BusinessActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

            default:
                break;
        }
    }
}
