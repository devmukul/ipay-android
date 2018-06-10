package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

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
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyResponse;
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
    private Button mRequestMoneyButton;
    private Bundle bundle;
    private TextInputLayout pinLayout;

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
        ((RequestMoneyActivity) getActivity()).toolbar.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        Drawable mBackButtonIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_arrow_back);
        mBackButtonIcon.setColorFilter(new
                PorterDuffColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY));
        ((RequestMoneyActivity) getActivity()).backButton.setVisibility(View.VISIBLE);
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
        ((RequestMoneyActivity) getActivity()).toolbar.setBackgroundColor(Color.WHITE);
        mRequestMoneyButton = (Button) view.findViewById(R.id.request_money_button);
        mPinEditText.setVisibility(View.GONE);
        getDataFromBundle();

        mRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utilities.hideKeyboard(getActivity());
                if (verifyInput()) {
                    attemptRequestMoney();
                }
            }
        });
    }

    private boolean verifyInput() {
        if (mNoteEditText.getText() == null || mNoteEditText.getText().toString().equals("")) {
            mNoteEditText.setError("Please enter a note");
            return false;
        } else {
            return true;
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
        String setString = "YOU ARE REQUESTING TK " + mAmount + " FROM";
        mDescriptionTextView.setText(setString, TextView.BufferType.SPANNABLE);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney));
        ((Spannable) mDescriptionTextView.getText()).setSpan(span, 16, 16 + 3 + mAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private void attemptRequestMoney() {
        if (mRequestMoneyTask != null) {
            return;
        }

        mCustomProgressDialog.setLoadingMessage(getString(R.string.requesting_money));
        mCustomProgressDialog.showDialog();
        if (mNoteEditText.getText() != null) {
            mNote = mNoteEditText.getText().toString();
        } else {
            mNote = null;
        }

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
                    RequestMoneyResponse mRequestMoneyResponse = new Gson().fromJson(result.getJsonString(), RequestMoneyResponse.class);
                    switch (result.getStatus()) {
                        case Constants.HTTP_RESPONSE_STATUS_OK:
                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                            } else {
                                mCustomProgressDialog.showSuccessAnimationAndMessage(mRequestMoneyResponse.getMessage());
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
                                    ((RequestMoneyActivity) getActivity()).switchToRequestMoneySuccessFragment(bundle);
                                }
                            }, 2000);

                            //Google Analytic event
                            Utilities.sendSuccessEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                            break;
                        case Constants.HTTP_RESPONSE_STATUS_ACCEPTED:
                        case Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED:
                            mCustomProgressDialog.dismissDialog();
                            Toast.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            SecuritySettingsActivity.otpDuration = mRequestMoneyResponse.getOtpValidFor();
                            launchOTPVerification();
                            break;
                        case Constants.HTTP_RESPONSE_STATUS_BLOCKED:
                            if (getActivity() != null) {
                                mCustomProgressDialog.showFailureAnimationAndMessage(mRequestMoneyResponse.getMessage());
                                ((MyApplication) getActivity().getApplication()).launchLoginPage("");

                                Utilities.sendBlockedEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), new BigDecimal(mAmount).longValue());
                            }
                            break;
                        default:
                            if (getActivity() != null) {
                                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog == null) {
                                    mCustomProgressDialog.showFailureAnimationAndMessage(mRequestMoneyResponse.getMessage());
                                } else {
                                    Toast.makeText(getContext(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                if (mRequestMoneyResponse.getMessage().toLowerCase().contains(TwoFactorAuthConstants.WRONG_OTP)) {
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
                                        mRequestMoneyResponse.getMessage(), new BigDecimal(mAmount).longValue());
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
