package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyConfirmActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;


public class SendMoneyConfirmFragment extends BaseFragment implements HttpResponseListener {
    private EditText mNoteEditText;
    private EditText mPinEditText;
    private TextView mNameTextView;
    private TextView mDescriptionTextView;
    private ProfileImageView mProfileImageView;
    private Button mSendMoneyButton;
    private Bundle bundle;
    private TextInputLayout pinLayout;

    private String mAmount;
    private String mName;
    private String mProfilePictureUrl;

    private CustomProgressDialog mCustomProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog
            mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private HttpRequestPostAsyncTask mSendMoneyTask;
    private SendMoneyRequest mSendMoneyRequest;

    private String mPin;
    private String mMobileNumber;
    private String mNote;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_money_confirm, container, false);
        ((SendMoneyConfirmActivity) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        Drawable mBackButtonIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back);
        mBackButtonIcon.setColorFilter(new
                PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY));
        ((SendMoneyConfirmActivity) getActivity()).backButton.setVisibility(View.VISIBLE);
        setUpViews(view);
        mCustomProgressDialog = new CustomProgressDialog(getContext());
        return view;
    }

    private void setUpViews(View view) {
        mNoteEditText = (EditText) view.findViewById(R.id.note_edit_text);
        mPinEditText = (EditText) view.findViewById(R.id.pin_edit_text);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_image_view);
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mDescriptionTextView = (TextView) view.findViewById(R.id.textview_description);
        ((SendMoneyConfirmActivity) getActivity()).toolbar.setBackgroundColor(Color.WHITE);
        mSendMoneyButton = (Button) view.findViewById(R.id.send_money_button);
        if (!SendMoneyActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            mPinEditText.setVisibility(View.GONE);
        } else {
            mPinEditText.setVisibility(View.VISIBLE);
        }
        getDataFromBundle();

        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(getActivity());
                if (mPinEditText.getVisibility() == View.VISIBLE) {
                    if (verifyInput()) {
                        attemptSendMoney(mPin);
                    }
                } else {
                    attemptSendMoney(null);
                }
            }
        });
    }

    private boolean verifyInput() {
        String errorMessage = null;
        if (mPinEditText.getText() != null) {
            mPin = mPinEditText.getText().toString();
            if (mPin.length() < 4) {
                errorMessage = "Pin must be at least 4 digits";
                mPinEditText.setError(errorMessage);
                return false;
            } else {
                return true;
            }
        } else {
            errorMessage = "Please enter a pin";
            mPinEditText.setError(errorMessage);
            return false;
        }

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
        String setString = "YOU ARE SENDING TK " + mAmount + " TO";
        mDescriptionTextView.setText(setString, TextView.BufferType.SPANNABLE);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney));
        ((Spannable) mDescriptionTextView.getText()).setSpan(span, 16, 16 + 3 + mAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void attemptSendMoney(String pin) {
        if (mSendMoneyTask != null) {
            return;
        }

        mCustomProgressDialog.setLoadingMessage(getString(R.string.progress_dialog_text_sending_money));
        mCustomProgressDialog.showDialog();
        if (mNoteEditText.getText() != null) {
            mNote = mNoteEditText.getText().toString();
        } else {
            mNote = null;
        }

        mSendMoneyRequest = new SendMoneyRequest(
                ProfileInfoCacheManager.getMobileNumber(), ContactEngine.formatMobileNumberBD(mMobileNumber),
                mAmount, mNote, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mSendMoneyRequest);
        mSendMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, json, getActivity(), false);
        mSendMoneyTask.mHttpResponseListener = this;
        mSendMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mSendMoneyRequest);
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
            mSendMoneyTask = null;
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
                                    bundle.putString("senderImageUrl", ProfileInfoCacheManager.getProfileImageUrl());
                                    bundle.putString("amount", mAmount);
                                    ((SendMoneyActivity) getActivity()).switchToSendMoneySuccessFragment(bundle);
                                }
                            }, 2000);

                            //Google Analytic event
                            Utilities.sendSuccessEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
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

                                Utilities.sendBlockedEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
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
                                Utilities.sendFailedEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(),
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
                mSendMoneyTask = null;
            }
        }
    }
}
