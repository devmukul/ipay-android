package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
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

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRequestMoneyTask = null;

    private ProgressDialog mProgressDialog;

    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private String mDescription;
    private boolean isInContacts;

    private TextView mServiceChargeTextView;
    private TextView mNetAmountTextView;

    private Tracker mTracker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.RECEIVER_MOBILE_NUMBER);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);
        isInContacts = getActivity().getIntent().getBooleanExtra(Constants.IS_IN_CONTACTS, false);

        if (getArguments() != null) {
            mReceiverName = getArguments().getString(Constants.NAME);
            mPhotoUri = getArguments().getString(Constants.PHOTO_URI);
        }

        mTracker = Utilities.getTracker(getActivity());

        mProgressDialog = new ProgressDialog(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_request_money_review));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_request_money_review, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ProfileImageView receiverProfileImageView = findViewById(R.id.receiver_profile_image_view);
        final TextView receiverNameTextView = findViewById(R.id.receiver_name_text_view);
        final TextView receiverMobileNumberTextView = findViewById(R.id.receiver_mobile_number_text_view);

        if (!TextUtils.isEmpty(mPhotoUri)) {
            receiverProfileImageView.setProfilePicture(mPhotoUri, false);
        }
        if (TextUtils.isEmpty(mReceiverName)) {
            receiverNameTextView.setVisibility(View.GONE);
        } else {
            receiverNameTextView.setVisibility(View.VISIBLE);
            receiverNameTextView.setText(mReceiverName);
        }
        receiverMobileNumberTextView.setText(mReceiverMobileNumber);

        final TextView amountTextView = findViewById(R.id.amount_text_view);
        mServiceChargeTextView = findViewById(R.id.service_charge_text_view);
        mNetAmountTextView = findViewById(R.id.net_amount_text_view);

        amountTextView.setText(Utilities.formatTaka(mAmount));
        mServiceChargeTextView.setText(Utilities.formatTaka(new BigDecimal(0.0)));
        mServiceChargeTextView.setText(Utilities.formatTaka(mAmount.subtract(new BigDecimal(0.0))));

        TextView descriptionTextView = findViewById(R.id.description_text_view);
        View descriptionViewHolder = findViewById(R.id.description_view_holder);
        final CheckBox addToContactCheckBox = findViewById(R.id.add_to_contact_check_box);

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

        Button requestMoneyButton = findViewById(R.id.request_money_button);

        requestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRequestMoney();
                if (addToContactCheckBox.isChecked()) {
                    addContact(mReceiverName, mReceiverMobileNumber, null);
                }
            }
        });

        if (!Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(RequestMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRuleWithServiceCharge(Constants.SERVICE_ID_REQUEST_MONEY);
        else
            attemptGetServiceCharge();
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection ConstantConditions, unchecked
        return (T) getView().findViewById(id);
    }

    private void attemptRequestMoney() {
        if (mRequestMoneyTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.requesting_money));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
        RequestMoneyRequest mRequestMoneyRequest = new RequestMoneyRequest(mReceiverMobileNumber,
                mAmount.doubleValue(), mDescription);
        Gson gson = new Gson();
        String json = gson.toJson(mRequestMoneyRequest);
        mRequestMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REQUEST_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY, json, getActivity());
        mRequestMoneyTask.mHttpResponseListener = this;
        mRequestMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @ValidateAccess
    private void addContact(String name, String phoneNumber, String relationship) {
        AddContactRequestBuilder addContactRequestBuilder = new
                AddContactRequestBuilder(name, phoneNumber, relationship);

        new AddContactAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mRequestMoneyTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_REQUEST_MONEY)) {

            try {
                RequestMoneyResponse mRequestMoneyResponse = gson.fromJson(result.getJsonString(), RequestMoneyResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();

                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_LONG);

                    //Google Analytic event
                    Utilities.sendSuccessEventTracker(mTracker, "Request Money", ProfileInfoCacheManager.getAccountId(), mAmount.longValue());
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_SHORT);

                    //Google Analytic event
                    Utilities.sendFailedEventTracker(mTracker, "Request Money", ProfileInfoCacheManager.getAccountId(), mRequestMoneyResponse.getMessage(), mAmount.longValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.failed_request_money, Toast.LENGTH_SHORT);
                Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage());
            }

            mProgressDialog.dismiss();
            mRequestMoneyTask = null;

        }
    }

    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_REQUEST_MONEY;
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

    }
}
