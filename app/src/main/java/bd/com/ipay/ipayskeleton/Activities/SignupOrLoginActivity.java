package bd.com.ipay.ipayskeleton.Activities;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.OTPVerificationBusinessFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.SignupBusinessStepOneFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.SignupBusinessStepThreeFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments.SignupBusinessStepTwoFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragments.LoginFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.LoginFragments.OTPVerificationTrustFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments.OTPVerificationPersonalFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.PersonalSignUpFragments.SignupPersonalStepOneFragment;
import bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.SelectAccountTypeFragment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBankFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBankHelperFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBasicInfoHelperFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardConsentAgreementForBankFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardPhotoIdUploadHelperFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardProfilePictureUploadHelperFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupOrLoginActivity extends AppCompatActivity {

    private final int OVERLAY_REQUEST_CODE = 1234;

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
    public static long otpDuration;

    public static AddressClass mAddressBusiness;
    public static AddressClass mAddressBusinessHolder;

    private MaterialDialog.Builder mOverlayPermissionDialogBuilder;
    private MaterialDialog mOverlayPermissionDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_or_login);


        if (SharedPrefManager.ifContainsUserID()) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, new LoginFragment()).commit();
        } else {
            Utilities.hideKeyboard(this);
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

        // Initialize the dialog
        initializeErrorDialogForOverlayPermission();

        // Check for overlay permission
        if (checkDrawOverlayPermission()) {
            // Do nothing
        } else showErrorDialogOnOverlayPermission();

    }

    private void initializeErrorDialogForOverlayPermission() {
        mOverlayPermissionDialogBuilder = new MaterialDialog.Builder(SignupOrLoginActivity.this)
                .title(R.string.attention)
                .content(R.string.allow_overlay_permission)
                .cancelable(false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        checkDrawOverlayPermission();
                    }
                });
        mOverlayPermissionDialog = mOverlayPermissionDialogBuilder.build();
    }

    private void showErrorDialogOnOverlayPermission() {
        mOverlayPermissionDialog.show();
    }

    public boolean checkDrawOverlayPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if (!Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, OVERLAY_REQUEST_CODE);
            return false;
        } else {
            return true;
        }
    }

    public void switchToLoginFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new LoginFragment()).commit();
    }

    public void switchToOTPVerificationPersonalFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationPersonalFragment()).addToBackStack(null).commit();
    }

    public void switchToSignupPersonalStepOneFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupPersonalStepOneFragment()).addToBackStack(null).commit();
    }

    public void switchToOTPVerificationBusinessFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationBusinessFragment()).addToBackStack(null).commit();
    }

    public void switchToOTPVerificationTrustedFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OTPVerificationTrustFragment()).addToBackStack(null).commit();
    }

    public void switchToAccountSelectionFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SelectAccountTypeFragment()).commit();
    }

    public void switchToHomeActivity() {
        Intent intent = new Intent(SignupOrLoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    public void switchToDeviceTrustActivity() {
        Intent intent = new Intent(SignupOrLoginActivity.this, DeviceTrustActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    public void switchToTourActivity() {
        Utilities.hideKeyboard(this);
        Intent intent = new Intent(SignupOrLoginActivity.this, TourActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }

    public void switchToBusinessStepOneFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupBusinessStepOneFragment()).addToBackStack(null).commit();
    }

    public void switchToBusinessStepTwoFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 2) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupBusinessStepTwoFragment()).addToBackStack(null).commit();
    }

    public void switchToBusinessStepThreeFragment() {
        while (getSupportFragmentManager().getBackStackEntryCount() > 3) {
            getSupportFragmentManager().popBackStackImmediate();
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new SignupBusinessStepThreeFragment()).addToBackStack(null).commit();
    }

    public void switchToProfileCompletionHelperActivity() {
        Intent intent = new Intent(SignupOrLoginActivity.this, ProfileCompletionHelperActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        this.finish();
    }


    public void switchToProfilePictureFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardProfilePictureUploadHelperFragment()).commit();
    }

    public void switchToIdentificationDocumentFragment(String profileImagePath) {
        System.out.println("Test Path "+profileImagePath);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.PROFILE_PHOTO_PATH, profileImagePath);

        OnBoardPhotoIdUploadHelperFragment onBoardIcdentificationFragment = new OnBoardPhotoIdUploadHelperFragment();
        onBoardIcdentificationFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, onBoardIcdentificationFragment).addToBackStack(null).commit();
    }

    public void switchToAddressFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardAddBasicInfoHelperFragment()).addToBackStack(null).commit();
    }

    public void switchToEditBasicFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardAddBasicInfoFragment()).addToBackStack(null).commit();
    }

    public void switchToAddNewBankHelperFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardAddBankHelperFragment()).commit();
    }

    public void switchToAddNewBankFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardAddBankFragment()).commit();
    }

    public void switchToAddBankAgreementFragment(Bundle bundle) {
        OnBoardConsentAgreementForBankFragment consentAgreementForBankFragment = new OnBoardConsentAgreementForBankFragment();
        consentAgreementForBankFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, consentAgreementForBankFragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        Utilities.hideKeyboard(this);
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            if (!SharedPrefManager.ifContainsUserID()) {
                Intent intent = new Intent(SignupOrLoginActivity.this, TourActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                this.finish();
            } else
                this.finish();
        }
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_REQUEST_CODE) {

            // Dismiss the dialog
            mOverlayPermissionDialog.dismiss();

            if (Settings.canDrawOverlays(this)) {
                // Do nothing
            } else
                showErrorDialogOnOverlayPermission();  // Request the permission again
        }
    }

}

