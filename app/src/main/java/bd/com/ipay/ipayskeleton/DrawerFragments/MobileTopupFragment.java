package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TopUp.TopupResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MobileTopupFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mTopupTask = null;
    private TopupResponse mTopupResponse;

    private EditText mMobileNumberEditText;
    private EditText mAmountEditText;
    private Spinner mSelectOperator;
    private Spinner mSelectType;
    private Button mRechargeButton;

    private ProgressDialog mProgressDialog;
    private SharedPreferences pref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mobile_topup, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        mSelectOperator = (Spinner) v.findViewById(R.id.operator_list_spinner);
        mSelectType = (Spinner) v.findViewById(R.id.recharge_type);
        mRechargeButton = (Button) v.findViewById(R.id.button_recharge);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.recharging_balance));

        ArrayAdapter<CharSequence> mAdapterMobileOperators = ArrayAdapter.createFromResource(getActivity(),
                R.array.mobile_operators, android.R.layout.simple_dropdown_item_1line);
        mSelectOperator.setAdapter(mAdapterMobileOperators);

        ArrayAdapter<CharSequence> mAdapterRechargeTypes = ArrayAdapter.createFromResource(getActivity(),
                R.array.recharge_types, android.R.layout.simple_dropdown_item_1line);
        mSelectType.setAdapter(mAdapterRechargeTypes);

        mRechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptTopUp();
            }
        });

        return v;
    }

    private void attemptTopUp() {
        if (mTopupTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (mMobileNumberEditText.getText().toString().trim().length() != 10) {
            mMobileNumberEditText.setError(getString(R.string.error_invalid_mobile_number));
            focusView = mMobileNumberEditText;
            cancel = true;
        }

        if (mAmountEditText.getText().toString().trim().length() == 0) {
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            focusView = mAmountEditText;
            cancel = true;
        }

        long senderAccountID = Long.parseLong(pref.getString(Constants.USERID, "").replaceAll("[^0-9]", ""));
        String receiverMobileNumber = "+880" + mMobileNumberEditText.getText().toString().trim();
        int mobileNumberType = mSelectType.getSelectedItemPosition() + 1;
        int operatorCode = mSelectOperator.getSelectedItemPosition() + 1;
        double amount = Double.parseDouble(mAmountEditText.getText().toString().trim());
        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, 1);
        String countryCode = "+88"; // TODO: For now Bangladesh Only

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            mProgressDialog.show();
            // TODO: token theke userclass and userType ashbe
            TopupRequest mTopupRequestModel = new TopupRequest(senderAccountID, receiverMobileNumber,
                    mobileNumberType, operatorCode, amount, countryCode, accountType, 1);
            Gson gson = new Gson();
            String json = gson.toJson(mTopupRequestModel);
            mTopupTask = new HttpRequestPostAsyncTask(Constants.COMMAND_TOPUP_REQUEST,
                    Constants.BASE_URL_SM + Constants.URL_TOPUP_REQUEST, json, getActivity());
            mTopupTask.mHttpResponseListener = this;
            mTopupTask.execute((Void) null);
        }
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
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
                        //if(getActivity() != null)Toast.makeText(getActivity(), mTopupResponse.getStatusDescription(), Toast.LENGTH_LONG).show();
                        //((HomeActivity) getActivity()).switchToHomeFragment();
                        getActivity().finish();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.progress_dialog_processing, Toast.LENGTH_LONG).show();
                    } else if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        // TODO: Save transaction in database
                        //if(getActivity() != null)Toast.makeText(getActivity(), mTopupResponse.getStatusDescription(), Toast.LENGTH_LONG).show();
//                        ((HomeActivity) getActivity()).switchToHomeFragment();
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
