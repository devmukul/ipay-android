package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.firebase.client.Firebase;

import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.OTPVerificationBusinessFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.OTPVerificationPersonalFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.OTPVerificationTrustFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.SelectAccountTypeFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.SignupBusinessFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.SignupPersonalFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.SignupPersonalStepOneFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.SignupPersonalStepTwoFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class SignupOrLoginActivity extends AppCompatActivity {

    private SharedPreferences pref;
    private Boolean switchedToAccountSelection = false;

    public static String mBirthday;
    public static String mPassword;
    public static String mName;
    public static String mMobileNumber;
    public static String mGender;
    public static int mAccountType;

    public static String mPasswordBusiness;
    public static String mNameBusiness;
    public static String mBusinessName;
    public static String mMobileNumberBusiness;
    public static String mEmailBusiness;
    public static String mBirthdayBusinessHolder;
    public static String mMobileNumberPersonal;
    public static long mTypeofBusiness;
    public static String mPromoCode;
    public static long otpDuration;

    public static AddressClass mAddressBusiness;
    public static AddressClass mAddressBusinessHolder;

    public static final String MESSAGE = "message";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_login);
        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        Firebase.setAndroidContext(this);


        if (pref.contains(Constants.USERID)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SelectAccountTypeFragment()).commit();
            switchedToAccountSelection = true;
        }

        if (getIntent().hasExtra(MESSAGE)) {
            Toast.makeText(this, getIntent().getStringExtra(MESSAGE), Toast.LENGTH_LONG).show();
        }

//        GetDistrictAsyncTask getDistrictAsyncTask = new GetDistrictAsyncTask(this);
//        getDistrictAsyncTask.execute();

//        GetThanaAsyncTask getThanaAsyncTask = new GetThanaAsyncTask(this);
//        getThanaAsyncTask.execute();
    }

    public void switchToPersonalSignUpFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupPersonalFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToBusinessSignUpFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupBusinessFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToOTPVerificationPersonalFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationPersonalFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToSignupPersonalStepOneFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupPersonalStepOneFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToSignupPersonalStepTwoFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupPersonalStepTwoFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToOTPVerificationBusinessFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationBusinessFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToOTPVerificationTrustedFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationTrustFragment()).commit();
        switchedToAccountSelection = false;
    }

    public void switchToAccountSelectionFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SelectAccountTypeFragment()).commit();
        switchedToAccountSelection = true;
    }

    public void switchToHomeActivity() {
        finish();
        Intent intent = new Intent(SignupOrLoginActivity.this, HomeActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        if (switchedToAccountSelection) {
            super.onBackPressed();
        } else switchToAccountSelectionFragment();
    }
}

