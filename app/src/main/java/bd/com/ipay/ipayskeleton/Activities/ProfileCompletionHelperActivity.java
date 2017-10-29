package bd.com.ipay.ipayskeleton.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.LoginAndSignUp.LogoutResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionStatusResponse;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBankFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBasicInfoFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBankHelperFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAskForIntroductionHelperFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardConsentAgreementForBankFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardContactsFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardPhotoIdUploadFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardAddBasicInfiHelperFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardPhotoIdUploadHelperFragment;
import bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments.OnBoardProfilePictureUploadHelperFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ProfileCompletionHelperActivity extends BaseActivity implements HttpResponseListener {
    private HttpRequestPostAsyncTask mLogoutTask = null;
    private LogoutResponse mLogOutResponse;
    private ProgressDialog mProgressDialog;
    public Uri mProfilePhotoUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_completion_helper);
        SharedPrefManager.setFirstLaunch(false);
        mProgressDialog = new ProgressDialog(ProfileCompletionHelperActivity.this);
        if(ProfileInfoCacheManager.isSwitchedFromSignup()){
            switchToProfilePictureFragment();
        }else {
            if(!ProfileInfoCacheManager.isProfilePictureUploaded()){
                switchToProfilePictureFragment();
            }else if(!ProfileInfoCacheManager.isIdentificationDocumentUploaded()){
                switchToPhotoIdUploadHelperFragment();
            }else if(!ProfileInfoCacheManager.isBasicInfoAdded()){
                switchToBasicInfoEditHelperFragment();
            }else {
                switchToHomeActivity();
            }
        }
    }

    @Override
    public void onBackPressed() {

            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStackImmediate();
            } else {
                new AlertDialog.Builder(ProfileCompletionHelperActivity.this)
                        .setMessage(R.string.are_you_sure_to_exit)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (Utilities.isConnectionAvailable(ProfileCompletionHelperActivity.this)) {
                                    attemptLogout();
                                } else {
                                    ProfileInfoCacheManager.setLoggedInStatus(false);
                                    ((MyApplication) ProfileCompletionHelperActivity.this.getApplication()).clearTokenAndTimer();
                                    finish();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();
            }
    }

    private void attemptLogout() {
        if (mLogoutTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_signing_out));
        mProgressDialog.show();
        LogoutRequest mLogoutModel = new LogoutRequest(ProfileInfoCacheManager.getMobileNumber());
        Gson gson = new Gson();
        String json = gson.toJson(mLogoutModel);

        mLogoutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_LOG_OUT,
                Constants.BASE_URL_MM + Constants.URL_LOG_OUT, json, ProfileCompletionHelperActivity.this);
        mLogoutTask.mHttpResponseListener = this;
        mLogoutTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    public void switchToHomeActivity() {
        Utilities.hideKeyboard(this);
        Intent intent = new Intent(ProfileCompletionHelperActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    public void switchToProfilePictureFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardProfilePictureUploadHelperFragment()).addToBackStack(null).commit();
    }

    public void switchToPhotoIdUploadHelperFragment() {
        OnBoardPhotoIdUploadHelperFragment onBoardIcdentificationFragment = new OnBoardPhotoIdUploadHelperFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, onBoardIcdentificationFragment).addToBackStack(null).commit();
    }

    public void switchToPhotoIdUploadFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardPhotoIdUploadFragment()).addToBackStack(null).commit();
    }

    public void switchToBasicInfoEditHelperFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardAddBasicInfiHelperFragment()).addToBackStack(null).commit();
    }

    public void switchToBasicInfoEditFragment() {
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

    public void switchToAskedIntroductionHelperFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardAskForIntroductionHelperFragment()).commit();
    }

    public void switchToContactFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new OnBoardContactsFragment()).commit();
    }

    public void switchToAddBankAgreementFragment(Bundle bundle) {
        OnBoardConsentAgreementForBankFragment consentAgreementForBankFragment = new OnBoardConsentAgreementForBankFragment();
        consentAgreementForBankFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, consentAgreementForBankFragment).addToBackStack(null).commit();
    }

    private void getAvailableBankList() {
        GetAvailableBankAsyncTask getAvailableBanksTask = new GetAvailableBankAsyncTask(this);
        getAvailableBanksTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mLogoutTask = null;
            Toast.makeText(ProfileCompletionHelperActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        switch (result.getApiCommand()) {
            case Constants.COMMAND_LOG_OUT:
                try {
                    mLogOutResponse = gson.fromJson(result.getJsonString(), LogoutResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        ((MyApplication) this.getApplication()).clearTokenAndTimer();
                        finish();
                    } else {
                        Toast.makeText(ProfileCompletionHelperActivity.this, mLogOutResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ProfileCompletionHelperActivity.this, R.string.could_not_sign_out, Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    protected Context setContext() {
        return ProfileCompletionHelperActivity.this;
    }
}
