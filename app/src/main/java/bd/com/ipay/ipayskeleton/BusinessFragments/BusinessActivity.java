package bd.com.ipay.ipayskeleton.BusinessFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.R;

public class BusinessActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);

        switchToBusinessInformationFragment();

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
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BusinessFragment()).commit();
    }

    public void switchToBusinessInformationFragment() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new BusinessInformationFragment()).commit();
    }

    public void switchToEditBusinessInformationFragment(Bundle bundle) {
        Fragment editBusinessInformationFragment = new EditBusinessInformationFragment();
        editBusinessInformationFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, editBusinessInformationFragment).addToBackStack(null).commit();
    }

    public void switchToEmployeeManagementFragment() {

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
