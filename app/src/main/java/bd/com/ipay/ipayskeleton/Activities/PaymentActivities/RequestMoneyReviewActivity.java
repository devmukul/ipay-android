package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.BaseActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.InviteFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments.RequestMoneyReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class RequestMoneyReviewActivity extends BaseActivity implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private ProgressBar mProgressBar;

    private String mReceiverMobileNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money_review);

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReceiverMobileNumber = getIntent().getStringExtra(Constants.RECEIVER);

        getProfileInfo(mReceiverMobileNumber);
    }

    private void switchToSendInviteFragment() {
        InviteFragment inviteFragment = new InviteFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.MOBILE_NUMBER, mReceiverMobileNumber);
        inviteFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, inviteFragment).commit();

    }

    private void switchToRequestReviewFragment(String name, String profilePictureUrl) {
        RequestMoneyReviewFragment requestMoneyReviewFragment = new RequestMoneyReviewFragment();

        Bundle bundle = new Bundle();
        bundle.putString(Constants.NAME, name);
        bundle.putString(Constants.PHOTO_URI, Constants.BASE_URL_IMAGE_SERVER + profilePictureUrl);
        requestMoneyReviewFragment.setArguments(bundle);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, requestMoneyReviewFragment).commit();

    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, this, this);

        mGetProfileInfoTask.execute();
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
        return RequestMoneyReviewActivity.this;
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mGetProfileInfoTask = null;
            Toast.makeText(this, R.string.logout_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_USER_INFO)) {

            try {
                mGetUserInfoResponse = gson.fromJson(resultList.get(2), GetUserInfoResponse.class);
                if (resultList.get(1) != null) {
                    if (resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        String name = mGetUserInfoResponse.getName();
                        String profilePicture = null;
                        if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                            profilePicture = mGetUserInfoResponse
                                    .getProfilePictures().get(0).getUrl();
                        }

                        switchToRequestReviewFragment(name, profilePicture);
                    } else if (resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_NOT_FOUND)) {
                        switchToSendInviteFragment();
                    } else {
                        Toast.makeText(this, R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
                        finish();
                    }

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
