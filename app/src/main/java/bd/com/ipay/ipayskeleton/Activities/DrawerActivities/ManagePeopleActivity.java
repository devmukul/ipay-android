package bd.com.ipay.ipayskeleton.Activities.DrawerActivities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.CreateEmployeeFragment;
import bd.com.ipay.ipayskeleton.BusinessFragments.Owner.EmployeePrivilegeFragment;
import bd.com.ipay.ipayskeleton.ManagePeopleFragments.EmployeeRequestHolderFragment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessRole;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessRoleResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ManagePeopleActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetRolesAsyncTask;
    private BusinessRoleResponse mGetRolesResponse;

    public static ArrayList<BusinessRole> mAllRoleList;
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

    public void switchToEmployeeManagementFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new EmployeeRequestHolderFragment()).commit();

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
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_ROLES_DETAILS, ManagePeopleActivity.this, this);
        mGetRolesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void switchToEmployeeInformationFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CreateEmployeeFragment()).commit();
    }
    public void switchToEmployeePrivilegeFragment(Bundle bundle) {
        EmployeePrivilegeFragment employeePrivilegeFragment=new EmployeePrivilegeFragment();
        employeePrivilegeFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,employeePrivilegeFragment).commit();
    }

    @Override
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
                    System.out.println("Response "+result.toString());
                    mGetRolesResponse = gson.fromJson(result.getJsonString(), BusinessRoleResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        mAllRoleList = (ArrayList<BusinessRole>) mGetRolesResponse.getBusinessRoleList();

//                        // Create a hash map for roleId - Privileges
//                        for (BusinessRole mRole : mAllRoleList)
//                            mRolePrivilegeMap.put(mRole.getId(), mRole.getPrivileges());

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


    @Override
    protected Context setContext() {
        return this;
    }

}