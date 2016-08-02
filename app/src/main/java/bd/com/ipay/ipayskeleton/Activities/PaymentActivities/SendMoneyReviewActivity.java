package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.InviteToiPayFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.SendMoneyReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyReviewActivity extends BaseActivity implements HttpResponseListener {
    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private ProgressBar mProgressBar;

    private String mReceiverMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_review);

        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReceiverMobileNumber = getIntent().getStringExtra(Constants.INVOICE_RECEIVER_TAG);

        getProfileInfo(mReceiverMobileNumber);
    }

    private void switchToSendInviteFragment() {

        InviteToiPayFragment inviteToiPayFragment = new InviteToiPayFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MOBILE_NUMBER, mReceiverMobileNumber);
        inviteToiPayFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, inviteToiPayFragment).commit();

    }

    private void switchToRequestReviewFragment(String name, String profilePictureUrl) {
        SendMoneyReviewFragment sendMoneyReviewFragment = new SendMoneyReviewFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.NAME, name);
        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_FTP_SERVER + profilePictureUrl);
        sendMoneyReviewFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, sendMoneyReviewFragment).commit();

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
        return SendMoneyReviewActivity.this;
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetProfileInfoTask = null;
            Toast.makeText(this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String name = mGetUserInfoResponse.getName();
                    String profilePicture = null;
                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        profilePicture = Utilities.getImage(mGetUserInfoResponse.getProfilePictures(), Constants.IMAGE_QUALITY_MEDIUM);
                    }

                    switchToRequestReviewFragment(name, profilePicture);
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    switchToSendInviteFragment();
                } else {
                    Toast.makeText(this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                    finish();
                }


            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                finish();
            }

            mGetProfileInfoTask = null;
            mProgressBar.setVisibility(View.GONE);
        }
    }
}
