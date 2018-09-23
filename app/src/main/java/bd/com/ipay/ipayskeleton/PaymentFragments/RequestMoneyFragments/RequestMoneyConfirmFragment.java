package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyConfirmActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class RequestMoneyConfirmFragment extends BaseFragment implements HttpResponseListener {
    private EditText mNoteEditText;
    private EditText mPinEditText;
    private TextView mNameTextView;
    private TextView mDescriptionTextView;
    private ProfileImageView mProfileImageView;
    private Button mSendMoneyButton;
    private Bundle bundle;
    private TextInputLayout pinLayout;
    private CoordinatorLayout parentLayout;

    private String mAmount;
    private String mName;
    private String mProfilePictureUrl;

    private CustomProgressDialog mCustomProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog
            mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private HttpRequestPostAsyncTask mRequestMoneyTask;
    private RequestMoneyRequest mRequestMoneyRequest;

    private String mPin;
    private String mMobileNumber;
    private String mNote;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_money_confirm, container, false);
        ((RequestMoneyConfirmActivity) getActivity()).backButton.setVisibility(View.VISIBLE);
        setUpViews(view);
        mCustomProgressDialog = new CustomProgressDialog(getContext());
        parentLayout = view.findViewById(R.id.parent_layout);
        return view;
    }

    private void setUpViews(View view) {
        mNoteEditText = (EditText) view.findViewById(R.id.note_edit_text);
        mPinEditText = (EditText) view.findViewById(R.id.pin_edit_text);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_image_view);
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mDescriptionTextView = (TextView) view.findViewById(R.id.textview_description);
        mSendMoneyButton = (Button) view.findViewById(R.id.request_money_button);
        getDataFromBundle();

        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verifyInput()){
                    Utilities.hideKeyboard(getActivity());
                    attemptRequestMoney();
                }
            }
        });
    }

    private boolean verifyInput() {
        String errorMessage = null;
        if (mNoteEditText.getText() != null) {
            mNote = mNoteEditText.getText().toString();
            if (mNote == null || mNote.equals("")) {
                errorMessage = "Please enter a note";
            } else {
                errorMessage = null;
            }

        } else {
            errorMessage = "Please enter a note";
        }
        if (errorMessage != null) {
            showSnackBar(errorMessage);
            return false;
        } else {
            return true;
        }
    }

    private void showSnackBar(String errorMessage) {
        Snackbar snackbar = Snackbar.make(parentLayout, errorMessage, Snackbar.LENGTH_SHORT);
        View view = snackbar.getView();
        view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
        TextView textView = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alert, 0, 0, 0);
        textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.value10));
        snackbar.show();
    }

    private void getDataFromBundle() {
        if (getArguments() != null) {
            bundle = getArguments();
            mAmount = bundle.getString("amount");
            mName = bundle.getString("name");
            mProfilePictureUrl = bundle.getString("imageUrl");
            mMobileNumber = bundle.getString("number");
            mNameTextView.setText(mName);
            mProfileImageView.setProfilePicture(mProfilePictureUrl, false);
            setUpTextViews();
        }
    }

    private void setUpTextViews() {
        String setString = "YOU ARE REQUESTING TK " + mAmount + " FROM";
        mDescriptionTextView.setText(setString, TextView.BufferType.SPANNABLE);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney));
        ((Spannable) mDescriptionTextView.getText()).setSpan(span, 18, 18 + 4 + mAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void attemptRequestMoney() {
        if (mRequestMoneyTask != null) {
            return;
        }

        mCustomProgressDialog.setLoadingMessage(getString(R.string.requesting_money));
        mCustomProgressDialog.showDialog();

        mRequestMoneyRequest = new RequestMoneyRequest(ContactEngine.formatMobileNumberBD(mMobileNumber),
                Double.parseDouble(mAmount), mNote);
        Gson gson = new Gson();
        String json = gson.toJson(mRequestMoneyRequest);
        mRequestMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY, json, getActivity(), false);
        mRequestMoneyTask.mHttpResponseListener = this;
        mRequestMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mRequestMoneyRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCustomProgressDialog.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        mCustomProgressDialog.dismiss();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), mCustomProgressDialog)) {
            mRequestMoneyTask = null;
            return;
        } else {
            if (result.getApiCommand().equals(Constants.COMMAND_SEND_MONEY)) {

                try {
                    SendMoneyResponse mSendMoneyResponse = new Gson().fromJson(result.getJsonString(), SendMoneyResponse.class);
                    switch (result.getStatus()) {
                        case Constants.HTTP_RESPONSE_STATUS_OK:
                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                            } else {
                                mCustomProgressDialog.showSuccessAnimationAndMessage(mSendMoneyResponse.getMessage());
                            }
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mCustomProgressDialog.hide();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("name", mName);
                                    bundle.putString("receiverImageUrl", mProfilePictureUrl);
                                    bundle.putString("senderImageUrl", Constants.BASE_URL_FTP_SERVER + ProfileInfoCacheManager.getProfileImageUrl());
                                    bundle.putString("amount", mAmount);
                                    ((RequestMoneyConfirmActivity) getActivity()).switchToRequestMoneySuccessFragment(bundle);
                                }
                            }, 2000);

                            //Google Analytic event
                            Utilities.sendSuccessEventTracker(mTracker, "Request Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                            break;
                        case Constants.HTTP_RESPONSE_STATUS_ACCEPTED:
                        case Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED:
                            mCustomProgressDialog.dismissDialog();
                            Toast.makeText(getActivity(), mSendMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            SecuritySettingsActivity.otpDuration = mSendMoneyResponse.getOtpValidFor();
                            launchOTPVerification();
                            break;
                        case Constants.HTTP_RESPONSE_STATUS_BLOCKED:
                            if (getActivity() != null) {
                                mCustomProgressDialog.showFailureAnimationAndMessage(mSendMoneyResponse.getMessage());
                                ((MyApplication) getActivity().getApplication()).launchLoginPage("");

                                Utilities.sendBlockedEventTracker(mTracker, "Request Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                            }
                            break;
                        default:
                            if (getActivity() != null) {
                                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog == null) {
                                    mCustomProgressDialog.showFailureAnimationAndMessage(mSendMoneyResponse.getMessage());
                                } else {
                                    Toast.makeText(getContext(), mSendMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                if (mSendMoneyResponse.getMessage().toLowerCase().contains(TwoFactorAuthConstants.WRONG_OTP)) {
                                    if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                        mOTPVerificationForTwoFactorAuthenticationServicesDialog.showOtpDialog();
                                        mCustomProgressDialog.dismissDialog();
                                    }
                                } else {
                                    if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                        mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                                    }
                                }
                                //Google Analytic event
                                Utilities.sendFailedEventTracker(mTracker, "Request", ProfileInfoCacheManager.getAccountId(),
                                        mSendMoneyResponse.getMessage(), new BigDecimal(mAmount).longValue());
                                break;
                            }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                        mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                    }
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
                    mCustomProgressDialog.showFailureAnimationAndMessage(getResources().getString(R.string.service_not_available));
                }
                mRequestMoneyTask = null;
            }
        }
    }
}
