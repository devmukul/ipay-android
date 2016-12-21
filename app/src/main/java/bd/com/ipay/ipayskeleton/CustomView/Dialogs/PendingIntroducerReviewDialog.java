package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroduceActionResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PendingIntroducerReviewDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private HttpRequestPostAsyncTask mIntroducerActionTask = null;
    private IntroduceActionResponse mIntroduceActionResponse;

    private ProgressDialog mProgressDialog;

    private Context context;
    private long mRequestID;
    private String mSenderName;
    private String mSenderMobileNumber;
    private String mPhotoUri;

    private ProfileImageView mProfileImageView;
    private TextView mSenderNameView;
    private TextView mSenderMobileNumberView;

    private Button mRejectButton;
    private Button mAcceptButton;

    private ActionCheckerListener mActionCheckerListener;

    public PendingIntroducerReviewDialog(Context context, long id, String name, String mobileNumber, String profilePictureUrl) {
        super(context);

        this.mSenderName = name;
        this.mSenderMobileNumber = mobileNumber;
        this.mPhotoUri = profilePictureUrl;
        this.mRequestID = id;
        this.context = context;

        initializeView();
    }

    public void initializeView() {

        final MaterialDialog reviewDialog = new MaterialDialog.Builder(this.getContext())
                .title(R.string.request_to_introduce)
                .customView(R.layout.dialog_pending_introducer_review, true)
                .show();

        View v = reviewDialog.getCustomView();
        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mSenderNameView = (TextView) v.findViewById(R.id.textview_name);
        mSenderMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);

        mAcceptButton = (Button) v.findViewById(R.id.button_accept);
        mRejectButton = (Button) v.findViewById(R.id.button_reject);

        mProgressDialog = new ProgressDialog(context);

        if (mSenderName == null || mSenderName.isEmpty()) {
            mSenderNameView.setVisibility(View.GONE);
        } else {
            mSenderNameView.setText(mSenderName);
        }

        mProfileImageView.setProfilePicture(mPhotoUri, false);
        mSenderMobileNumberView.setText(mSenderMobileNumber);

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
                attemptIntroducerVerificationStatus(mRequestID, Constants.INTRODUCTION_REQUEST_ACTION_APPROVE);
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialDialog.Builder(context)
                        .title(R.string.are_you_sure)
                        .content(R.string.introduction_request_reject_dialog_content)
                        .positiveText(R.string.yes)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                reviewDialog.dismiss();
                                attemptIntroducerVerificationStatus(mRequestID, Constants.INTRODUCTION_REQUEST_ACTION_REJECT);
                            }
                        })
                        .negativeText(R.string.no)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                // Do nothing
                            }
                        })
                        .show();

            }
        });

    }

    public void setActionCheckerListener(ActionCheckerListener actionCheckerListener) {
        mActionCheckerListener = actionCheckerListener;
    }

    private void attemptIntroducerVerificationStatus(long requestID, String recommendationStatus) {

        if (requestID == 0) {
            if (context != null)
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.setMessage(context.getString(R.string.verifying_user));
        mProgressDialog.show();
        mIntroducerActionTask = new HttpRequestPostAsyncTask(Constants.COMMAND_INTRODUCE_ACTION,
                Constants.BASE_URL_MM + Constants.URL_PENDING_INTRODUCER_ACTION + "/" + requestID + "/" + recommendationStatus, null, context);
        mIntroducerActionTask.mHttpResponseListener = this;
        mIntroducerActionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mIntroducerActionTask = null;

            if (context != null)
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {

            case Constants.COMMAND_INTRODUCE_ACTION:

                try {
                    mIntroduceActionResponse = gson.fromJson(result.getJsonString(), IntroduceActionResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (context != null)
                            Toast.makeText(context, mIntroduceActionResponse.getMessage(), Toast.LENGTH_LONG).show();

                        if (mActionCheckerListener != null) {
                            mActionCheckerListener.ifFinishNeeded();
                        }
                    } else {
                        if (context != null)
                            Toast.makeText(context, mIntroduceActionResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (context != null)
                        Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mIntroducerActionTask = null;
                break;
        }
    }

    public interface ActionCheckerListener {
        void ifFinishNeeded();
    }
}

