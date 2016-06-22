package bd.com.ipay.ipayskeleton.Activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;


import bd.com.ipay.ipayskeleton.ForgotPasswordFragments.ForgetPasswordFragment;
import bd.com.ipay.ipayskeleton.ForgotPasswordFragments.OTPVerificationForgotPasswordFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.SignupBusinessStepOneFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.SignupBusinessStepThreeFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.SignupBusinessStepTwoFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragments.LoginFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.OTPVerificationBusinessFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments.OTPVerificationPersonalFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragments.OTPVerificationTrustFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.SelectAccountTypeFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments.SignupPersonalStepOneFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments.SignupPersonalStepTwoFragment;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.RegistrationIntentService;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupOrLoginActivity extends AppCompatActivity {

    private SharedPreferences pref;

    public static String mBirthday;
    public static String mPassword;
    public static String mName;
    public static String mMobileNumber;
    public static String mGender = "M";
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
    public static AddressClass mAddressPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_login);

        pref = getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        if (pref.contains(Constants.USERID)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment()).commit();
        } else {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new SelectAccountTypeFragment()).commit();
        }

        if (getIntent().hasExtra(Constants.MESSAGE)) {
            String message = getIntent().getStringExtra(Constants.MESSAGE);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            switchToLoginFragment();
        } else if (getIntent().hasExtra(Constants.TARGET_FRAGMENT)) {
            String targetFragment = getIntent().getStringExtra(Constants.TARGET_FRAGMENT);
            if (targetFragment.equals(Constants.SIGN_IN)) {
                switchToLoginFragment();
            } else if (targetFragment.equals(Constants.SIGN_UP)) {
                switchToAccountSelectionFragment();
            }
        }

        // Start service for GCM
        if (Utilities.checkPlayServices(SignupOrLoginActivity.this)) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    public void switchToLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment()).commit();
    }

    public void switchToOTPVerificationPersonalFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationPersonalFragment()).commit();
    }

    public void switchToSignupPersonalStepOneFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupPersonalStepOneFragment()).commit();
    }

    public void switchToSignupPersonalStepTwoFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupPersonalStepTwoFragment()).commit();
    }

    public void switchToOTPVerificationBusinessFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationBusinessFragment()).commit();
    }

    public void switchToOTPVerificationTrustedFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationTrustFragment()).commit();
    }

    public void switchToAccountSelectionFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SelectAccountTypeFragment()).commit();
    }

    public void switchToForgetPasswordFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ForgetPasswordFragment()).addToBackStack(null).commit();
    }

    public void switchToHomeActivity() {
        Intent intent = new Intent(SignupOrLoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    public void switchToBusinessStepOneFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupBusinessStepOneFragment()).commit();
    }

    public void switchToBusinessStepTwoFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupBusinessStepTwoFragment()).commit();
    }

    public void switchToBusinessStepThreeFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupBusinessStepThreeFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            Intent intent = new Intent(SignupOrLoginActivity.this, TourActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            this.finish();
        }
    }

}

