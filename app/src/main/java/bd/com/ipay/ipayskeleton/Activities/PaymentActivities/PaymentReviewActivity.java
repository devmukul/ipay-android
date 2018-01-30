package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.UserAddress;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.InviteToiPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments.PaymentReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentReviewActivity extends BaseActivity implements HttpResponseListener {
    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private ProgressBar mProgressBar;

    private String mReceiverMobileNumber;
    private String mReceiverName;
    private String mReceiverPhotoUri;
    private String mAddressString;
    private String mDistrict;
    private String mCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_review);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReceiverMobileNumber = getIntent().getStringExtra(Constants.RECEIVER_MOBILE_NUMBER);

        if (getIntent().hasExtra(Constants.NAME)) {
            mReceiverName = getIntent().getStringExtra(Constants.NAME);
            mReceiverPhotoUri = getIntent().getStringExtra(Constants.PHOTO_URI);
            mAddressString = getIntent().getStringExtra(Constants.ADDRESS);
            mDistrict = getIntent().getStringExtra(Constants.DISTRICT);
            mCountry = getIntent().getStringExtra(Constants.COUNTRY);
            switchToPaymentReviewFragment(mReceiverName, mReceiverPhotoUri);

        } else {
            getProfileInfo(mReceiverMobileNumber);
        }
    }

    private void switchToSendInviteFragment() {

        InviteToiPayFragment inviteToiPayFragment = new InviteToiPayFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MOBILE_NUMBER, mReceiverMobileNumber);
        inviteToiPayFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, inviteToiPayFragment).commit();

    }

    private void switchToPaymentReviewFragment(String name, String profilePictureUrl) {
        PaymentReviewFragment paymentReviewFragment = new PaymentReviewFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.NAME, name);
        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + profilePictureUrl);
        bundle.putString(Constants.ADDRESS, mAddressString);
        bundle.putString(Constants.DISTRICT, mDistrict);
        bundle.putString(Constants.COUNTRY, mCountry);
        paymentReviewFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, paymentReviewFragment).commit();

    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, this, this);

        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
    public Context setContext() {
        return PaymentReviewActivity.this;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetProfileInfoTask = null;
            Toaster.makeText(this, R.string.service_not_available, Toast.LENGTH_LONG);
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String name = mGetUserInfoResponse.getName();
                    int accountType = mGetUserInfoResponse.getAccountType();
                    List<UserAddress> office = mGetUserInfoResponse.getAddressList().getOFFICE();
                    if (office != null) {
                        mAddressString = office.get(0).getAddressLine1();
                        mDistrict = office.get(0).getDistrict();
                        mCountry = office.get(0).getCountry();
                    } else {
                        mAddressString = "";
                        mDistrict = "";
                        mCountry = "";
                    }

                    if (accountType != Constants.BUSINESS_ACCOUNT_TYPE) {
                        new AlertDialog.Builder(PaymentReviewActivity.this)
                                .setMessage(R.string.not_a_business_user)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .setCancelable(false)
                                .show();
                    }

                    String profilePicture = null;
                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        profilePicture = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                    }

                    switchToPaymentReviewFragment(name, profilePicture);
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    switchToSendInviteFragment();
                } else {
                    Toaster.makeText(this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                    finish();
                }


            } catch (Exception e) {
                e.printStackTrace();

                Toaster.makeText(this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                finish();
            }

            mGetProfileInfoTask = null;
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
