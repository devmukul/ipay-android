package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTopupTask = null;
    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;
    private GetUserInfoResponse mGetUserInfoResponse;
    private TopupResponse mTopupResponse;

    private ProgressDialog mProgressDialog;

    private double mAmount;
    private String mMobileNumber;
    private int mMobileNumberType;
    private String mCountryCode;
    private int mOperatorCode;
    private String mError_message;

    public TextView mReceiverView;
    private TextView mMobileNumberView;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mTotalView;
    private TextView mPackageView;
    private TextView mOperatorView;
    public ProfileImageView mProfileImageView;
    private ImageView mOperatorImageView;
    public LinearLayout mInvitationLayout;
    public CheckBox mInvitationCheckBox;
    private Button mTopupButton;

    private View mServiceChargeHolder;
    private View mTopUpHolder;
    private List<String> mArraypackages;
    private List<String> mArrayoperators;

    private View v;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mobile_topup_review, container, false);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mMobileNumberType = getActivity().getIntent().getIntExtra(Constants.MOBILE_NUMBER_TYPE, 1);
        mOperatorCode = getActivity().getIntent().getIntExtra(Constants.OPERATOR_CODE, 0);
        mCountryCode = getActivity().getIntent().getStringExtra(Constants.COUNTRY_CODE);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.image_view_profile);
        mReceiverView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mPackageView = (TextView) v.findViewById(R.id.textview_package);
        mOperatorView = (TextView) v.findViewById(R.id.textview_operator);
        mOperatorImageView = (ImageView) v.findViewById(R.id.imageView_operator);
        mInvitationLayout = (LinearLayout) v.findViewById(R.id.layout_checkbox_invitation);
        mInvitationCheckBox = (CheckBox) v.findViewById(R.id.checkbox_topup_invite);
        mTopupButton = (Button) v.findViewById(R.id.button_topup);
        mServiceChargeHolder = v.findViewById(R.id.service_charge_holder);
        mTopUpHolder = v.findViewById(R.id.topup_holder);
        mProgressDialog = new ProgressDialog(getActivity());
        mMobileNumber = getActivity().getIntent().getStringExtra(Constants.MOBILE_NUMBER);

        mMobileNumberView.setText(mMobileNumber);
        mAmountView.setText(Utilities.formatTaka(mAmount));
        getProfileInfo(mMobileNumber);

        mArraypackages = Arrays.asList(getResources().getStringArray(R.array.package_type));
        mArrayoperators = Arrays.asList(getResources().getStringArray(R.array.mobile_operators));
        mPackageView.setText(mArraypackages.get(mMobileNumberType - 1));
        mOperatorView.setText(mArrayoperators.get(mOperatorCode - 1));
        setOperatorIcon();

        mTopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mError_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmount),
                        TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                        TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                if (mError_message == null) {
                    if (TopUpActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
                        final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                        pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                attemptTopUp(pinInputDialogBuilder.getPin());
                            }
                        });

                        pinInputDialogBuilder.build().show();
                    } else {
                        attemptTopUp(null);
                    }

                    if (mInvitationCheckBox.isChecked())
                        sendInvite(mMobileNumber);

                } else {
                    new AlertDialog.Builder(getContext())
                            .setMessage(mError_message)
                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    getActivity().finish();
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        });

        if (!Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRulewithServiceCharge(Constants.SERVICE_ID_TOP_UP);
        else
            attemptGetServiceCharge();

        return v;
    }

    private void attemptTopUp(String pin) {
        TopupRequest mTopupRequestModel = new TopupRequest(Long.parseLong(mMobileNumber.replaceAll("[^0-9]", "")),
                mMobileNumber, mMobileNumberType, mOperatorCode, mAmount,
                mCountryCode, mMobileNumberType, Constants.DEFAULT_USER_CLASS, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mTopupRequestModel);
        mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, json, getActivity());
        mTopupTask.mHttpResponseListener = this;
        mTopupTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_TOP_UP;
    }

    @Override
    public BigDecimal getAmount() {
        return new BigDecimal(mAmount);
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        if (serviceCharge == null || serviceCharge.equals(BigDecimal.ZERO)) {
            mServiceChargeHolder.setVisibility(View.GONE);
            mTopUpHolder.setVisibility(View.GONE);
        } else {
            mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
            mTotalView.setText(Utilities.formatTaka(getAmount().subtract(serviceCharge)));
        }
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
        TopUpActivity.mMandatoryBusinessRules.setIS_PIN_REQUIRED(isPinRequired);

    }

    private void setOperatorIcon() {
        //Setting the correct image based on Operator
        int[] images = {
                R.drawable.ic_gp,
                R.drawable.ic_robi,
                R.drawable.ic_airtel,
                R.drawable.ic_banglalink,
                R.drawable.ic_teletalk,
        };

        mOperatorImageView.setImageResource(images[mOperatorCode - 1]);
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

    private String getUserNameFromContacts(String mobileNumber) {
        return ContactEngine.getContactNameFromNumber(getActivity(), mobileNumber);
    }

    private void sendInvite(String phoneNumber) {

        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_sending_invite));
        mProgressDialog.show();

        mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                Constants.BASE_URL_MM + Constants.URL_SEND_INVITE + phoneNumber, null, getActivity(), this);
        mSendInviteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;
            mTopupTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_TOPUP_REQUEST)) {

            try {
                mTopupResponse = gson.fromJson(result.getJsonString(), TopupResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mTopupTask = null;

        }
        if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mInvitationLayout.setVisibility(View.GONE);
                    mInvitationCheckBox.setChecked(false);
                    String name = mGetUserInfoResponse.getName();
                    String profilePicture = null;

                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        profilePicture = mGetUserInfoResponse
                                .getProfilePictures().get(0).getUrl();
                    }
                    mReceiverView.setText(name);
                    mProfileImageView.setInformation(mMobileNumber, Constants.BASE_URL_FTP_SERVER + profilePicture, name, false);

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {

                } else {
                    mInvitationLayout.setVisibility(View.VISIBLE);
                    String name = ContactEngine.getContactNameFromNumber(getActivity(), mMobileNumber);
                    if (name != null)
                        mReceiverView.setText(name);
                    Uri photoUri = ContactEngine.getPhotoUri(getActivity(), mMobileNumber);
                    mProfileImageView.setInformation(mMobileNumber, photoUri.toString(), name, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;

        }
        if (result.getApiCommand().equals(Constants.COMMAND_SEND_INVITE)) {
            try {
                mSendInviteResponse = gson.fromJson(result.getJsonString(), SendInviteResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.invitation_sent, Toast.LENGTH_LONG).show();
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

        }
    }
}
