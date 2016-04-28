package bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.SendInviteRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class InviteFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;

    private ProgressDialog mProgressDialog;

    private TextView mMobileNumberView;
    private Button mInviteToIpayButton;

    private String mMobileNumber;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_invite, container, false);

        mMobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);

        mProgressDialog = new ProgressDialog(getActivity());
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mInviteToIpayButton = (Button) v.findViewById(R.id.button_invite_to_ipay);

        mMobileNumberView.setText(mMobileNumber);
        mInviteToIpayButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                sendInvite(mMobileNumber);
            }
        });

        return v;
    }

    private void sendInvite(String phoneNumber) {

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_sending_invite));
        mProgressDialog.show();

        SendInviteRequest sendInviteRequest = new SendInviteRequest(phoneNumber);
        Gson gson = new Gson();
        String json = gson.toJson(sendInviteRequest);
        mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                Constants.BASE_URL + Constants.URL_SEND_INVITE, json, getActivity(), this);
        mSendInviteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mSendInviteTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_request, Toast.LENGTH_SHORT).show();

            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SEND_INVITE)) {
            try {
                if (resultList.size() > 2) {
                    mSendInviteResponse = gson.fromJson(resultList.get(2), SendInviteResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.invitation_sent, Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }

                    } else if (getActivity() != null) {
                        Toast.makeText(getActivity(), mSendInviteResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mSendInviteTask = null;

        }
    }
}
