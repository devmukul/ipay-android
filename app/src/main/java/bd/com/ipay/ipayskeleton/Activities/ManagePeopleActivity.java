package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.ManagePeopleFragments.EmployeeManagementFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ManagePeopleActivity extends BaseActivity implements HttpResponseListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        getAllRoles();

        switchToEmployeeManagementFragment();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void switchToEmployeeManagementFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new EmployeeManagementFragment()).commit();

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

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

    }

    @Override
    protected Context setContext() {
        return this;
    }
}