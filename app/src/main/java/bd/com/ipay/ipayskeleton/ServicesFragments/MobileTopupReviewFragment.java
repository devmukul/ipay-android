package bd.com.ipay.ipayskeleton.ServicesFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MobileTopupReviewFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTopupTask = null;
    private TopupResponse mTopupResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private double mAmount;
    private BigDecimal mServiceCharge;
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
        mServiceCharge = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.SERVICE_CHARGE);
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

        mAmountView.setText(String.format("\u09F3 %.2f", mAmount));
        mServiceChargeView.setText(String.format("\u09F3 %.2f", mServiceCharge));
        mTotalView.setText(String.format("\u09F3 %.2f", new BigDecimal(mAmount).add(mServiceCharge)));

        mTopupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptTopUp();
            }
        });

        return v;
    }

    private void attemptTopUp() {
        TopupRequest mTopupRequestModel = new TopupRequest(Long.parseLong(mMobileNumber.replaceAll("[^0-9]", "")),
                mMobileNumber, mMobileNumberType, mOperatorCode, mAmount,
                mCountryCode, mAccountType, Constants.DEFAULT_USER_CLASS);
        Gson gson = new Gson();
        String json = gson.toJson(mTopupRequestModel);
        mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, json, getActivity());
        mTopupTask.mHttpResponseListener = this;
        mTopupTask.execute();
    }


    @Override
    public void httpResponseReceiver(String result) {
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
