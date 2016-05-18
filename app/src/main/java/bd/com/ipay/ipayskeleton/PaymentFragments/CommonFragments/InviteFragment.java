package bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.RecommendationAndInvite.SendInviteRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.RecommendationAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class InviteFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;

    private ProgressDialog mProgressDialog;

    private TextView mMobileNumberView;
    private Button mInviteToIpayButton;

    private View mInviteContainer;
    private View mUnverifiedContainer;

    private String mMobileNumber;

    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invite, container, false);

        mMobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);

        mProgressDialog = new ProgressDialog(getActivity());
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mInviteToIpayButton = (Button) v.findViewById(R.id.button_invite_to_ipay);

        mInviteContainer = v.findViewById(R.id.invite_container);
        mUnverifiedContainer = v.findViewById(R.id.unverified_container);

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mMobileNumberView.setText(mMobileNumber);
        mInviteToIpayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendInvite(mMobileNumber);
            }
        });

        getProfileInfo(pref.getString(Constants.USERID, ""));

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getActivity(), this);

        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendInvite(String phoneNumber) {

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_sending_invite));
        mProgressDialog.show();

        SendInviteRequest sendInviteRequest = new SendInviteRequest(phoneNumber);
        Gson gson = new Gson();
        String json = gson.toJson(sendInviteRequest);
        mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                Constants.BASE_URL_MM + Constants.URL_SEND_INVITE, json, getActivity(), this);
        mSendInviteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mSendInviteTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_request, Toast.LENGTH_SHORT).show();

            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SEND_INVITE)) {
            try {
                mSendInviteResponse = gson.fromJson(result.getJsonString(), SendInviteResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.invitation_sent, Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    }

                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), mSendInviteResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mSendInviteTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {

            try {
                setContentShown(true);
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (mGetUserInfoResponse.getAccountStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                        mInviteContainer.setVisibility(View.VISIBLE);
                    } else {
                        mUnverifiedContainer.setVisibility(View.VISIBLE);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mGetProfileInfoTask = null;
        }
    }
}
