package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.EducationFragments.SelectInstitutionFragment;
import bd.com.ipay.ipayskeleton.R;

public class EducationActivity extends BaseActivity {

    private boolean switchedToSelectInstituteFragment = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchToSelectInstituteFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (switchedToSelectInstituteFragment) super.onBackPressed();
        else {
            switchToSelectInstituteFragment();
        }
    }

    public void switchToSelectInstituteFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SelectInstitutionFragment()).commit();
        switchedToSelectInstituteFragment = true;
    }

    @Override
    public Context setContext() {
        return EducationActivity.this;
    }
}





