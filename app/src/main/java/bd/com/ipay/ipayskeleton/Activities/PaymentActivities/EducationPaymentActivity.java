package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.EducationFragments.AddPayAbleFragment;
import bd.com.ipay.ipayskeleton.EducationFragments.PayEducationFeesFragment;
import bd.com.ipay.ipayskeleton.EducationFragments.ReviewEducationFeePaymentFragment;
import bd.com.ipay.ipayskeleton.EducationFragments.SelectInstitutionFragment;
import bd.com.ipay.ipayskeleton.EducationFragments.ShowStudentInfoFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Education.Institution;
import bd.com.ipay.ipayskeleton.Model.MMModule.Education.PayableItem;
import bd.com.ipay.ipayskeleton.Model.MMModule.Education.SemesterOrSession;
import bd.com.ipay.ipayskeleton.Model.MMModule.Education.Student;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EducationPaymentActivity extends BaseActivity {

    private boolean switchedToSelectInstituteFragment = true;
    private boolean switchedToPaymentReviewFragment = false;

    public static String studentID = "";
    public static int institutionID = -1;
    public static String institutionName = "";
    public static int sessionID = -1;
    public static String sessionName = "";
    public static Institution selectedInstitution = new Institution();
    public static SemesterOrSession selectedSession = new SemesterOrSession();
    public static Student selectedStudent = new Student();

    public static ArrayList<PayableItem> mMyPayableItems;
    public static final String ARGS_ENABLED_PAYABLE_ITEMS = "ARGS_ENABLED_PAYABLE_ITEMS";

    public static final String STUDENT_NAME = "STUDENT_NAME";
    public static final String STUDENT_DEPARTMENT = "STUDENT_DEPARTMENT";
    public static final String STUDENT_MOBILE_NUMBER = "STUDENT_MOBILE_NUMBER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_education);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mMyPayableItems = new ArrayList<PayableItem>();

        switchToSelectInstituteFragment();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (switchedToPaymentReviewFragment) switchToPayEducationFeesFragment();
        else if (switchedToSelectInstituteFragment) super.onBackPressed();
        else {
            switchToSelectInstituteFragment();
        }
    }

    private void resetElements() {
        studentID = "";
        institutionID = -1;
        sessionID = -1;
        mMyPayableItems.clear();
    }

    public void switchToSelectInstituteFragment() {
        resetElements();
        Utilities.hideKeyboard(EducationPaymentActivity.this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SelectInstitutionFragment()).commit();
        switchedToSelectInstituteFragment = true;
        switchedToPaymentReviewFragment = false;
    }

    public void switchToStudentInfoFragment(Bundle args) {

        Utilities.hideKeyboard(EducationPaymentActivity.this);
        ShowStudentInfoFragment mShowStudentInfoFragment = new ShowStudentInfoFragment();
        mShowStudentInfoFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mShowStudentInfoFragment).commit();
        switchedToSelectInstituteFragment = false;
        switchedToPaymentReviewFragment = false;
    }

    public void switchToAddPayableFragment(Bundle args) {

        Utilities.hideKeyboard(EducationPaymentActivity.this);
        AddPayAbleFragment mAddPayAbleFragment = new AddPayAbleFragment();
        mAddPayAbleFragment.setArguments(args);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mAddPayAbleFragment).commit();
        switchedToSelectInstituteFragment = false;
        switchedToPaymentReviewFragment = false;
    }

    public void switchToPayEducationFeesFragment() {
        Utilities.hideKeyboard(EducationPaymentActivity.this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new PayEducationFeesFragment()).commit();
        switchedToSelectInstituteFragment = false;
        switchedToPaymentReviewFragment = false;
    }

    public void switchToPaymentReviewFragment() {
        Utilities.hideKeyboard(EducationPaymentActivity.this);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ReviewEducationFeePaymentFragment()).commit();
        switchedToSelectInstituteFragment = false;
        switchedToPaymentReviewFragment = true;
    }

    @Override
    public Context setContext() {
        return EducationPaymentActivity.this;
    }
}





