package bd.com.ipay.ipayskeleton.BusinessFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.R;

public class BusinessActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        switchToBusinessFragment();

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
        Fragment employeeInformationFragment = new CreateEmployeeInformationFragment();
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
}
