package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Customview.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.AddMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.AddMoneyResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyReviewFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mAddMoneyTask = null;
    private AddMoneyResponse mAddMoneyResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private double mAmount;
    private String mDescription;
    private long mBankAccountId;
    private String mBankName;
    private String mBankAccountNumber;

    private TextView mBankNameView;
    private TextView mBankAccountNumberView;
    private TextView mDescriptionView;
    private View mDescriptionHolder;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mTotalView;
    private Button mAddMoneyButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_money_review, container, false);

        mAmount = getActivity().getIntent().getDoubleExtra(Constants.AMOUNT, 0);
        mDescription = getActivity().getIntent().getStringExtra(Constants.INVOICE_DESCRIPTION_TAG);
        mBankAccountId = getActivity().getIntent().getLongExtra(Constants.BANK_ACCOUNT_ID, -1);
        mBankName = getActivity().getIntent().getStringExtra(Constants.BANK_NAME);
        mBankAccountNumber = getActivity().getIntent().getStringExtra(Constants.BANK_ACCOUNT_NUMBER);

        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mDescriptionHolder = v.findViewById(R.id.description_holder);
        mBankNameView = (TextView) v.findViewById(R.id.textview_bank_name);
        mBankAccountNumberView = (TextView) v.findViewById(R.id.textview_account_number);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mTotalView = (TextView) v.findViewById(R.id.textview_total);
        mAddMoneyButton = (Button) v.findViewById(R.id.button_add_money);

        mProgressDialog = new ProgressDialog(getActivity());

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mBankNameView.setText(mBankName);
        mBankAccountNumberView.setText(mBankAccountNumber);
        mAmountView.setText(Utilities.formatTaka(mAmount));

        if (mDescription == null || mDescription.isEmpty()) {
            mDescriptionHolder.setVisibility(View.GONE);
        } else {
            mDescriptionView.setText(mDescription);
        }

        mAddMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        attemptAddMoney(pinInputDialogBuilder.getPin());
                    }
                });

                pinInputDialogBuilder.build().show();
            }
        });

        attemptGetServiceCharge();

        return v;
    }

    private void attemptAddMoney(String pin) {
        if (mAddMoneyTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_money));
        mProgressDialog.show();

        AddMoneyRequest mAddMoneyRequest = new AddMoneyRequest(mBankAccountId, mAmount, mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mAddMoneyRequest);
        mAddMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_MONEY,
                Constants.BASE_URL_SM + Constants.URL_ADD_MONEY, json, getActivity());
        mAddMoneyTask.mHttpResponseListener = this;

        mAddMoneyTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_ADD_MONEY;
    }

    @Override
    public BigDecimal getAmount() {
        return new BigDecimal(mAmount);
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mServiceChargeView.setText(Utilities.formatTaka(serviceCharge));
        mTotalView.setText(Utilities.formatTaka(getAmount().subtract(serviceCharge)));
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        super.httpResponseReceiver(result);

        if (result == null) {
            mProgressDialog.show();
            mAddMoneyTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_ADD_MONEY)) {

            try {
                mAddMoneyResponse = gson.fromJson(result.getJsonString(), AddMoneyResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mAddMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    // Exit the Add money activity and return to HomeActivity
                    getActivity().finish();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mAddMoneyResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.add_money_failed, Toast.LENGTH_LONG).show();
            }


            mProgressDialog.dismiss();
            mAddMoneyTask = null;

        }
    }
}