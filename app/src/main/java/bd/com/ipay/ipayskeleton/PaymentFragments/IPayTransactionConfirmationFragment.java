package bd.com.ipay.ipayskeleton.PaymentFragments;

import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.IPayTransactionResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TwoFactorAuthConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IPayTransactionConfirmationFragment extends Fragment implements HttpResponseListener {

    private MandatoryBusinessRules mandatoryBusinessRules;
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private int transactionType;
    private String name;
    private BigDecimal amount;
    private String mobileNumber;
    private String profilePicture;

    private EditText mNoteEditText;
    private EditText mPinEditText;

    private CustomProgressDialog mCustomProgressDialog;

    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    protected Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
            name = getArguments().getString(Constants.NAME);
            mobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);
            profilePicture = getArguments().getString(Constants.PHOTO_URI);
            amount = (BigDecimal) getArguments().getSerializable(Constants.AMOUNT);
        }
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumIntegerDigits(1);
        mandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(BusinessRuleCacheManager.getTag(transactionType));

        if (getActivity() != null) {
            mTracker = Utilities.getTracker(getActivity());
            mCustomProgressDialog = new CustomProgressDialog(getActivity());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ipay_transaction_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNoteEditText = view.findViewById(R.id.note_edit_text);
        mPinEditText = view.findViewById(R.id.pin_edit_text);

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        final TextView transactionDescriptionTextView = view.findViewById(R.id.transaction_description_text_view);
        final TextView nameTextView = view.findViewById(R.id.name_text_view);
        final View pinLayoutHolder = view.findViewById(R.id.pin_layout_holder);
        final RoundedImageView profileImageView = view.findViewById(R.id.profile_image_view);
        final Button transactionConfirmationButton = view.findViewById(R.id.transaction_confirmation_button);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            getActivity().setTitle(R.string.empty_string);
        }

        if (mandatoryBusinessRules != null)
            pinLayoutHolder.setVisibility(mandatoryBusinessRules.IS_PIN_REQUIRED() ? View.VISIBLE : View.GONE);

        final String amountValue = getString(R.string.balance_holder, numberFormat.format(amount));
        final Spannable spannableAmount;
        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                spannableAmount = new SpannableString(getString(R.string.send_money_confirmation_message, amountValue));
                spannableAmount.setSpan(new StyleSpan(Typeface.BOLD), 16, 16 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableAmount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney)), 16, 16 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                transactionDescriptionTextView.setText(spannableAmount, TextView.BufferType.SPANNABLE);
                mNoteEditText.setHint(R.string.short_note_optional_hint);
                transactionConfirmationButton.setText(R.string.send_money);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_TOP_UP:
                spannableAmount = new SpannableString(getString(R.string.top_up_confirmation_message, amountValue));
                spannableAmount.setSpan(new StyleSpan(Typeface.BOLD), 14, 14 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableAmount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney)), 14, 14 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                transactionDescriptionTextView.setText(spannableAmount, TextView.BufferType.SPANNABLE);
                mNoteEditText.setHint(R.string.short_note_optional_hint);
                transactionConfirmationButton.setText(R.string.top_up);
                break;


            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                pinLayoutHolder.setVisibility(View.GONE);
                spannableAmount = new SpannableString(getString(R.string.request_money_confirmation_message, amountValue));
                spannableAmount.setSpan(new StyleSpan(Typeface.BOLD), 19, 19 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableAmount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney)), 19, 19 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                transactionDescriptionTextView.setText(spannableAmount, TextView.BufferType.SPANNABLE);
                mNoteEditText.setHint(R.string.short_note_hint);
                transactionConfirmationButton.setText(R.string.request_money);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                transactionDescriptionTextView.setText(R.string.empty_string);
                mNoteEditText.setHint(R.string.empty_string);
                break;
        }
        if (getContext() != null) {
            if (pinLayoutHolder.getVisibility() == View.VISIBLE) {
                mPinEditText.requestFocus();
                Utilities.showKeyboard(getContext(), mPinEditText);
            } else {
                mNoteEditText.requestFocus();
                Utilities.showKeyboard(getContext(), mNoteEditText);
            }
        }

        nameTextView.setText(name);
        Glide.with(this)
                .load(profilePicture)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .transform(new CircleTransform(getContext()))
                .into(profileImageView);

        transactionConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pinLayoutHolder.getVisibility() == View.VISIBLE) {
                    Editable pin = mPinEditText.getText();
                    if (TextUtils.isEmpty(pin)) {
                        showErrorMessage(getString(R.string.please_enter_a_pin));
                        return;
                    } else if (pin.length() != 4) {
                        showErrorMessage(getString(R.string.minimum_pin_length_message));
                        return;
                    }
                }
                if (transactionType == IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY) {
                    Editable noteEditTextText = mNoteEditText.getText();
                    if (TextUtils.isEmpty(noteEditTextText)) {
                        showErrorMessage(getString(R.string.please_write_note));
                        return;
                    }
                }
                Utilities.hideKeyboard(getContext(), view);
                confirmTransaction();
            }
        });
    }

    private void showErrorMessage(String errorMessage) {
        if (getActivity() != null && getView() != null) {
            Snackbar snackbar = Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.value50);
            snackbarView.setLayoutParams(layoutParams);
            TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ActivityCompat.getColor(getActivity(), android.R.color.white));
            snackbar.show();
        }
    }

    private final Gson gson = new GsonBuilder().create();
    private String requestJson = "{}";
    private HttpRequestPostAsyncTask httpRequestPostAsyncTask;

    private void confirmTransaction() {
        if (!Utilities.isConnectionAvailable(getContext())) {
            Toaster.makeText(getContext(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
            return;
        }
        if (httpRequestPostAsyncTask != null)
            return;
        final String apiCommand;
        final String url;
        final String note = mNoteEditText.getText().toString();
        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                apiCommand = Constants.COMMAND_REQUEST_MONEY;
                requestJson = gson.toJson(new SendMoneyRequest(ContactEngine.formatMobileNumberBD(ProfileInfoCacheManager.getMobileNumber()), ContactEngine.formatMobileNumberBD(mobileNumber),
                        amount.toString(), note, mPinEditText.getText().toString()));
                url = Constants.BASE_URL_SM + Constants.URL_SEND_MONEY;
                mCustomProgressDialog.setMessage(getString(R.string.sending_money));
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                apiCommand = Constants.COMMAND_REQUEST_MONEY;
                requestJson = gson.toJson(new RequestMoneyRequest(ContactEngine.formatMobileNumberBD(mobileNumber),
                        Double.valueOf(amount.toString()), note));
                url = Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY;
                mCustomProgressDialog.setMessage(getString(R.string.requesting_money));
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                return;
        }
        httpRequestPostAsyncTask = new HttpRequestPostAsyncTask(apiCommand, url, requestJson, getContext(), this, false);
        httpRequestPostAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mCustomProgressDialog.setTitle(R.string.please_wait_no_ellipsis);
        mCustomProgressDialog.showDialog();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), mCustomProgressDialog)) {
            httpRequestPostAsyncTask = null;
            mCustomProgressDialog.dismissDialog();
        } else {
            switch (result.getApiCommand()) {
                case Constants.COMMAND_SEND_MONEY:
                case Constants.COMMAND_REQUEST_MONEY:
                    httpRequestPostAsyncTask = null;
                    IPayTransactionResponse iPayTransactionResponse = new Gson().fromJson(result.getJsonString(), IPayTransactionResponse.class);
                    switch (result.getStatus()) {
                        case Constants.HTTP_RESPONSE_STATUS_OK:
                            if (mOTPVerificationForTwoFactorAuthenticationServicesDialog != null) {
                                mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismissDialog();
                            } else {
                                mCustomProgressDialog.setTitle(R.string.success);
                                mCustomProgressDialog.showSuccessAnimationAndMessage(iPayTransactionResponse.getMessage());
                            }
                            Utilities.sendSuccessEventTracker(mTracker, getTrackerCategory(), ProfileInfoCacheManager.getAccountId(), amount.longValue());
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mCustomProgressDialog.hide();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(Constants.NAME, name);
                                    bundle.putString(Constants.RECEIVER_IMAGE_URL, profilePicture);
                                    bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                                    bundle.putString(Constants.SENDER_IMAGE_URL, Constants.BASE_URL_FTP_SERVER + ProfileInfoCacheManager.getProfileImageUrl());
                                    bundle.putSerializable(Constants.AMOUNT, amount);
                                    if (getActivity() instanceof IPayTransactionActionActivity)
                                        ((IPayTransactionActionActivity) getActivity()).switchToTransactionSuccessFragment(bundle);
                                }
                            }, 2000);
                            break;
                        case Constants.HTTP_RESPONSE_STATUS_ACCEPTED:
                        case Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED:
                            mCustomProgressDialog.dismissDialog();
                            Toast.makeText(getActivity(), iPayTransactionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                            launchOTPVerification(iPayTransactionResponse.getOtpValidFor());
                            break;
                        case Constants.HTTP_RESPONSE_STATUS_BLOCKED:
                            if (getActivity() != null) {
                                mCustomProgressDialog.showFailureAnimationAndMessage(iPayTransactionResponse.getMessage());
                                ((MyApplication) getActivity().getApplication()).launchLoginPage("");
                                Utilities.sendBlockedEventTracker(mTracker, getTrackerCategory(), ProfileInfoCacheManager.getAccountId(), amount.longValue());
                            }
                            break;
                        default:
                            if (getActivity() != null) {
                                if (mOTPVerificationForTwoFactorAuthenticationServicesDialog == null) {
                                    mCustomProgressDialog.showFailureAnimationAndMessage(iPayTransactionResponse.getMessage());
                                } else {
                                    Toast.makeText(getContext(), iPayTransactionResponse.getMessage(), Toast.LENGTH_LONG).show();
                                }

                                if (iPayTransactionResponse.getMessage().toLowerCase().contains(TwoFactorAuthConstants.WRONG_OTP)) {
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
                                Utilities.sendFailedEventTracker(mTracker, getTrackerCategory(), ProfileInfoCacheManager.getAccountId(),
                                        iPayTransactionResponse.getMessage(), amount.longValue());
                                break;
                            }
                    }
                    break;
            }
        }
    }

    private String getTrackerCategory() {
        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                return "Send Money";
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                return "Request Money";
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                return "";
        }
    }

    private void launchOTPVerification(long otpValidFor) {
        if (getActivity() != null) {
            switch (transactionType) {
                case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), requestJson, Constants.COMMAND_SEND_MONEY,
                            Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, Constants.METHOD_POST, otpValidFor);
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.setOtpValidFor(otpValidFor);
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
                    break;
                case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), requestJson, Constants.COMMAND_REQUEST_MONEY,
                            Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY, Constants.METHOD_POST, otpValidFor);
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.setOtpValidFor(otpValidFor);
                    mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
                    break;
            }
        }
    }
}
