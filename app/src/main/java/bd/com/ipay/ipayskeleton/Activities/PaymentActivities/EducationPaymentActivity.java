package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.EducationFragments.EducationPaymentFragment;
import bd.com.ipay.ipayskeleton.EducationFragments.SelectInstitutionFragment;
import bd.com.ipay.ipayskeleton.EducationFragments.ShowStudentInfoFragment;
import bd.com.ipay.ipayskeleton.R;

public class EducationPaymentActivity extends BaseActivity {

    private boolean switchedToSelectInstituteFragment = true;

    public static String studentID = "";
    public static int institutionID = -1;
    public static int sessionID = -1;

    public static final String STUDENT_NAME = "STUDENT_NAME";
    public static final String STUDENT_DEPARTMENT = "STUDENT_DEPARTMENT";
    public static final String STUDENT_MOBILE_NUMBER = "STUDENT_MOBILE_NUMBER";

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

    public void switchToEducationPaymentFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new EducationPaymentFragment()).commit();
        switchedToSelectInstituteFragment = false;
    }

    public void switchToStudentInfoFragment(Bundle args) {

        ShowStudentInfoFragment mShowStudentInfoFragment = new ShowStudentInfoFragment();
        mShowStudentInfoFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mShowStudentInfoFragment).commit();
        switchedToSelectInstituteFragment = false;
    }

    @Override
    public Context setContext() {
        return EducationPaymentActivity.this;
    }
}





