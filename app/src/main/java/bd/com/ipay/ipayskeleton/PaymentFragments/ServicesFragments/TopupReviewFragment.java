package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TopupReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTopupTask = null;
    private TopupResponse mTopupResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private double mAmount;
    private String mMobileNumber;
    private int mAccountType;
    private int mMobileNumberType;
    private String mCountryCode;
    private int mOperatorCode;

    private TextView mMobileNumberView;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mTotalView;
    private Button mTopupButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_mobile_topup_review, container, false);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mMobileNumberType = getActivity().getIntent().getIntExtra(Constants.MOBILE_NUMBER_TYPE, 1);
        mOperatorCode = getActivity().getIntent().getIntExtra(Constants.OPERATOR_CODE, 0);
        mCountryCode = getActivity().getIntent().getStringExtra(Constants.COUNTRY_CODE);

        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mTopupButton = (Button) v.findViewById(R.id.button_topup);

        mProgressDialog = new ProgressDialog(getActivity());

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mMobileNumber = pref.getString(Constants.USERID, "");
        mAccountType = pref.getInt(Constants.ACCOUNT_TYPE, 1);

        mMobileNumberView.setText(mMobileNumber);

        mAmountView.setText(Utilities.formatTaka(mAmount));

        mTopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        attemptTopUp(pinInputDialogBuilder.getPin());
                    }
                });

                pinInputDialogBuilder.build().show();
            }
        });

        attemptGetServiceCharge();

        return v;
    }

    private void attemptTopUp(String pin) {
        TopupRequest mTopupRequestModel = new TopupRequest(Long.parseLong(mMobileNumber.replaceAll("[^0-9]", "")),
                mMobileNumber, mMobileNumberType, mOperatorCode, mAmount,
                mCountryCode, mAccountType, Constants.DEFAULT_USER_CLASS, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mTopupRequestModel);
        mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL + Constants.URL_TOPUP_REQUEST, json, getActivity());
        mTopupTask.mHttpResponseListener = this;
        mTopupTask.execute();
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
        mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
        mTotalView.setText(Utilities.formatTaka(getAmount().add(serviceCharge)));
    }

    @Override
    public void httpResponseReceiver(String result) {
        super.httpResponseReceiver(result);

        if (result == null) {
            mProgressDialog.show();
            mTopupTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_TOPUP_REQUEST)) {

            if (resultList.size() > 2) {
                try {
                    mTopupResponse = gson.fromJson(resultList.get(2), TopupResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_PROCESSING)) {
                        // TODO: Save transaction in database
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                    } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        // TODO: Save transaction in database
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
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.recharge_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mTopupTask = null;

        }
    }
}
