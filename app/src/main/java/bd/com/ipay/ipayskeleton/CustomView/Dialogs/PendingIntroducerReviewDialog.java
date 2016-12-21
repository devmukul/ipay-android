package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Notification.GetPendingIntroducerRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Introducer.PendingIntroducer;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.IntroduceActionResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PendingIntroducerReviewDialog extends MaterialDialog.Builder implements HttpResponseListener {

    private HttpRequestPostAsyncTask mPendingIntroducerActionTask = null;
    private IntroduceActionResponse mPendingIntroducerActionResponse;

    private ProgressDialog mProgressDialog;

    private Context Context;

    private PendingIntroducer mPendingIntroducer;

    private long mRequestID;
    private String mPendingIntroducerName;
    private String mPendingIntroucerMobileNumber;
    private String mPendingIntroducerPhotoUri;

    private ProfileImageView mProfileImageView;
    private TextView mSenderNameView;
    private TextView mSenderMobileNumberView;

    private Button mRejectButton;
    private Button mAcceptButton;

    private ActionCheckerListener mActionCheckerListener;

    public PendingIntroducerReviewDialog(Context context, PendingIntroducer pendingIntroducer) {
        super(context);

        this.Context = context;
        this.mPendingIntroducer = pendingIntroducer;
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

        mRequestID = mPendingIntroducer.getId();
        mPendingIntroducerName = mPendingIntroducer.getName();
        mPendingIntroucerMobileNumber = mPendingIntroducer.getMobileNumber();
        mPendingIntroducerPhotoUri = mPendingIntroducer.getImageUrl();

        mAcceptButton = (Button) v.findViewById(R.id.button_accept);
        mRejectButton = (Button) v.findViewById(R.id.button_reject);

        mProgressDialog = new ProgressDialog(Context);

        setPendingIntroducerUserInfo();

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
                attemptAcceptRejectPendingIntroducer(mRequestID, Constants.INTRODUCTION_REQUEST_ACTION_APPROVE);
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewDialog.dismiss();
                attemptAcceptRejectPendingIntroducer(mRequestID, Constants.INTRODUCTION_REQUEST_ACTION_REJECT);


            }
        });

    }

    private void setPendingIntroducerUserInfo() {
        if (mPendingIntroducerName == null || mPendingIntroducerName.isEmpty()) {
            mSenderNameView.setVisibility(View.GONE);
        } else {
            mSenderNameView.setText(mPendingIntroducerName);
        }

        mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mPendingIntroducerPhotoUri, false);
        mSenderMobileNumberView.setText(mPendingIntroucerMobileNumber);
    }

    public void setActionCheckerListener(ActionCheckerListener actionCheckerListener) {
        mActionCheckerListener = actionCheckerListener;
    }

    private void attemptAcceptRejectPendingIntroducer(long requestID, String introducerAcceptRejectStatus) {
        if (Context != null)
            Toast.makeText(Context, R.string.service_not_available, Toast.LENGTH_LONG).show();

        if (introducerAcceptRejectStatus.equals(Constants.INTRODUCTION_REQUEST_ACTION_APPROVE))
            mProgressDialog.setMessage(Context.getString(R.string.adding_introducer));
        else
            mProgressDialog.setMessage(Context.getString(R.string.removing_introducer));

        mProgressDialog.show();
        GetPendingIntroducerRequestBuilder getSecurityQuestionBuilder = new GetPendingIntroducerRequestBuilder(requestID, introducerAcceptRejectStatus);
        String url = getSecurityQuestionBuilder.getGeneratedUri();

        mPendingIntroducerActionTask = new HttpRequestPostAsyncTask(Constants.COMMAND_INTRODUCE_ACTION, url, null, Context);
        mPendingIntroducerActionTask.mHttpResponseListener = this;
        mPendingIntroducerActionTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mPendingIntroducerActionTask = null;

            if (Context != null)
                Toast.makeText(Context, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {

            case Constants.COMMAND_INTRODUCE_ACTION:
                try {
                    mPendingIntroducerActionResponse = gson.fromJson(result.getJsonString(), IntroduceActionResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (Context != null)
                            Toast.makeText(Context, mPendingIntroducerActionResponse.getMessage(), Toast.LENGTH_LONG).show();

                        if (mActionCheckerListener != null) {
                            mActionCheckerListener.ifFinishNeeded();
                        }
                    } else {
                        if (Context != null)
                            Toast.makeText(Context, mPendingIntroducerActionResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (Context != null)
                        Toast.makeText(Context, R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mPendingIntroducerActionTask = null;
                break;
        }
    }

    public interface ActionCheckerListener {
        void ifFinishNeeded();
    }
}

