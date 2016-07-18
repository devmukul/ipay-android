package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RequestMoney.RequestMoneyResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class RequestMoneyReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mRequestMoneyTask = null;
    private RequestMoneyResponse mRequestMoneyResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private BigDecimal mAmount;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mPhotoUri;
    private String mDescription;
    private String mTitle;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDescriptionView;
    private TextView mTitleView;
    private View mDescriptionHolder;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mNetReceivedView;
    private Button mRequestMoneyButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_request_money_review, container, false);

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.INVOICE_RECEIVER_TAG);
        mDescription = getActivity().getIntent().getStringExtra(Constants.INVOICE_DESCRIPTION_TAG);
        mTitle = getActivity().getIntent().getStringExtra(Constants.INVOICE_TITLE_TAG);

        mReceiverName = getArguments().getString(Constants.NAME);
        mPhotoUri = getArguments().getString(Constants.PHOTO_URI);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mTitleView = (TextView) v.findViewById(R.id.textview_title);
        mDescriptionHolder = v.findViewById(R.id.description_holder);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mNetReceivedView = (TextView) v.findViewById(R.id.textview_net_received);
        mRequestMoneyButton = (Button) v.findViewById(R.id.button_request_money);

        mProgressDialog = new ProgressDialog(getActivity());

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mProfileImageView.setProfilePicture(mPhotoUri, false);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mMobileNumberView.setText(mReceiverMobileNumber);

        if ((mDescription == null || mDescription.isEmpty()) && (mTitle == null || mTitle.isEmpty())) {
            mDescriptionHolder.setVisibility(View.GONE);
        } else {
            if (mDescription == null || mDescription.isEmpty()) {
                mDescriptionView.setVisibility(View.GONE);
            } else {
                mDescriptionView.setText(mDescription);
            }

            if (mTitle == null || mTitle.isEmpty()) {
                mTitleView.setVisibility(View.GONE);
            } else {
                mTitleView.setText(mTitle);
            }
        }

        mAmountView.setText(Utilities.formatTaka(mAmount));

        mRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());
//
//                pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
//                    @Override
//                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                        attemptRequestMoney(pinInputDialogBuilder.getPin());
//                    }
//                });
//
//                pinInputDialogBuilder.show();
                attemptRequestMoney();
            }
        });

        attemptGetServiceCharge();

        return v;
    }

    private void attemptRequestMoney() {
        if (mRequestMoneyTask != null) {
            return;
        }


        mProgressDialog.setMessage(getString(R.string.requesting_money));
        mProgressDialog.show();
        RequestMoneyRequest mRequestMoneyRequest = new RequestMoneyRequest(mReceiverMobileNumber,
                mAmount.doubleValue(), mTitle, mDescription);
        Gson gson = new Gson();
        String json = gson.toJson(mRequestMoneyRequest);
        mRequestMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REQUEST_MONEY,
                Constants.BASE_URL_SM + Constants.URL_REQUEST_MONEY, json, getActivity());
        mRequestMoneyTask.mHttpResponseListener = this;
        mRequestMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
					|| result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mRequestMoneyTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_REQUEST_MONEY)) {

            try {
                mRequestMoneyResponse = gson.fromJson(result.getJsonString(), RequestMoneyResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();

                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mRequestMoneyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_request_money, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mRequestMoneyTask = null;

        }
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
        mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
        mNetReceivedView.setText(Utilities.formatTaka(mAmount.subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {

    }
}
