package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSendMoneyTask = null;

    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private SendMoneyRequest mSendMoneyRequest;

    private ProgressDialog mProgressDialog;

    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mSenderMobileNumber;
    private String mPhotoUri;
    private String mDescription;
    private boolean isInContacts;

    private TextView mServiceChargeTextView;
    private TextView mNetAmountTextView;

    private Tracker mTracker;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSenderMobileNumber = ProfileInfoCacheManager.getMobileNumber();

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.RECEIVER_MOBILE_NUMBER);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);

        mReceiverName = getArguments().getString(Constants.NAME);
        mPhotoUri = getArguments().getString(Constants.PHOTO_URI);

        isInContacts = getActivity().getIntent().getBooleanExtra(Constants.IS_IN_CONTACTS, false);

        mProgressDialog = new ProgressDialog(getContext());

        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_send_money_review));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_send_money_review, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProfileImageView receiverProfileImageView = findViewById(R.id.receiver_profile_image_view);
        final TextView receiverNameTextView = findViewById(R.id.receiver_name_text_view);
        final TextView receiverMobileNumberTextView = findViewById(R.id.receiver_mobile_number_text_view);
        final TextView amountTextView = findViewById(R.id.amount_text_view);
        mServiceChargeTextView = findViewById(R.id.service_charge_text_view);
        mNetAmountTextView = findViewById(R.id.net_amount_text_view);
        TextView descriptionTextView = findViewById(R.id.description_text_view);
        View descriptionViewHolder = findViewById(R.id.description_view_holder);
        final CheckBox addToContactCheckBox = findViewById(R.id.add_to_contact_check_box);
        Button sendMoneyButton = findViewById(R.id.send_money_button);

        if (!TextUtils.isEmpty(mPhotoUri)) {
            receiverProfileImageView.setProfilePicture(mPhotoUri, false);
        }
        if (TextUtils.isEmpty(mReceiverName)) {
            receiverNameTextView.setVisibility(View.GONE);
        } else {
            receiverNameTextView.setVisibility(View.VISIBLE);
            receiverNameTextView.setText(mReceiverName);
        }
        if (getActivity().getIntent() != null) {
            if (getActivity().getIntent().getBooleanExtra(Constants.FROM_QR_SCAN, false)) {
                receiverMobileNumberTextView.setVisibility(View.GONE);
            } else {
                receiverMobileNumberTextView.setText(mReceiverMobileNumber);
            }
        } else {
            receiverMobileNumberTextView.setText(mReceiverMobileNumber);
        }

        amountTextView.setText(Utilities.formatTaka(mAmount));
        mServiceChargeTextView.setText(Utilities.formatTaka(new BigDecimal(0.0)));
        mServiceChargeTextView.setText(Utilities.formatTaka(mAmount.subtract(new BigDecimal(0.0))));

        if (TextUtils.isEmpty(mDescription)) {
            descriptionViewHolder.setVisibility(View.GONE);
        } else {
            descriptionViewHolder.setVisibility(View.VISIBLE);
            descriptionTextView.setText(mDescription);
        }
        if (!isInContacts) {
            addToContactCheckBox.setVisibility(View.VISIBLE);
            addToContactCheckBox.setChecked(true);
        }

        sendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    final String errorMessage = InputValidator.isValidAmount(getActivity(), getAmount(),
                            SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (errorMessage == null) {
                        attemptSendMoneyWithPinCheck();
                    } else {
                        showErrorDialog(errorMessage);
                    }
                } else {
                    attemptSendMoneyWithPinCheck();
                }

                if (addToContactCheckBox.isChecked()) {
                    addContact(mReceiverName, mReceiverMobileNumber, null);
                }
            }
        });

        if (!Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRuleWithServiceCharge(Constants.SERVICE_ID_SEND_MONEY);
        else
            attemptGetServiceCharge();

    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection ConstantConditions, unchecked
        return (T) getView().findViewById(id);
    }

    private void attemptSendMoneyWithPinCheck() {

        if (SendMoneyActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    attemptSendMoney(pin);
                }
            });
        } else {
            attemptSendMoney(null);
        }
    }

    private void launchHomeActivity() {
        Intent intent = new Intent(((Activity) getActivity()), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    private void launchOTPVerification() {
        String jsonString = new Gson().toJson(mSendMoneyRequest);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), jsonString, Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, Constants.METHOD_POST);
        mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;

    }

    @ValidateAccess
    private void addContact(String name, String phoneNumber, String relationship) {
        AddContactRequestBuilder addContactRequestBuilder = new
                AddContactRequestBuilder(name, phoneNumber, relationship);

        new AddContactAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptSendMoney(String pin) {
        if (mSendMoneyTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_money));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        mSendMoneyRequest = new SendMoneyRequest(
                mSenderMobileNumber, ContactEngine.formatMobileNumberBD(mReceiverMobileNumber),
                mAmount.toString(), mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mSendMoneyRequest);
        mSendMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL_SM + Constants.URL_SEND_MONEY, json, getActivity());
        mSendMoneyTask.mHttpResponseListener = this;
        mSendMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void showErrorDialog(final String errorMessage) {
        new AlertDialog.Builder(getContext())
                .setMessage(errorMessage)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_SEND_MONEY;
    }

    @Override
    public BigDecimal getAmount() {
        return mAmount;
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mServiceChargeTextView.setText(Utilities.formatTaka(serviceCharge));
        mNetAmountTextView.setText(Utilities.formatTaka(mAmount.subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
        SendMoneyActivity.mMandatoryBusinessRules.setIS_PIN_REQUIRED(isPinRequired);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);
        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mSendMoneyTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT);
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SEND_MONEY)) {

            try {
                SendMoneyResponse mSendMoneyResponse = gson.fromJson(result.getJsonString(), SendMoneyResponse.class);
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), mSendMoneyResponse.getMessage(), Toast.LENGTH_LONG);
                        launchHomeActivity();
                        //Google Analytic event
                        Utilities.sendSuccessEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), mAmount.longValue());
                        break;
                    case Constants.HTTP_RESPONSE_STATUS_ACCEPTED:
                    case Constants.HTTP_RESPONSE_STATUS_NOT_EXPIRED:
                        Toast.makeText(getActivity(), mSendMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        SecuritySettingsActivity.otpDuration = mSendMoneyResponse.getOtpValidFor();
                        launchOTPVerification();
                        break;
                    case Constants.HTTP_RESPONSE_STATUS_BLOCKED:
                        if (getActivity() != null)
                            ((MyApplication) getActivity().getApplication()).launchLoginPage(mSendMoneyResponse.getMessage());
                        Utilities.sendBlockedEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(), mAmount.longValue());
                        break;
                    default:
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), mSendMoneyResponse.getMessage(), Toast.LENGTH_LONG);

                        //Google Analytic event
                        Utilities.sendFailedEventTracker(mTracker, "Send Money", ProfileInfoCacheManager.getAccountId(),
                                mSendMoneyResponse.getMessage(), mAmount.longValue());
                        break;

                }
            } catch (Exception e) {
                e.printStackTrace();
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
            }
            mProgressDialog.dismiss();
            mSendMoneyTask = null;

        }

    }
}
