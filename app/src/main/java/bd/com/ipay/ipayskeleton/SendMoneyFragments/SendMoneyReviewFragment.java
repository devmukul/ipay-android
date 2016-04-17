package bd.com.ipay.ipayskeleton.SendMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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
import bd.com.ipay.ipayskeleton.Customview.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.SendMoney.SendMoneyResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class SendMoneyReviewFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSendMoneyTask = null;
    private SendMoneyResponse mSendMoneyResponse;

    private ProgressDialog mProgressDialog;

    private SharedPreferences pref;

    private BigDecimal mAmount;
    private BigDecimal mServiceCharge;
    private String mReceiverName;
    private String mReceiverMobileNumber;
    private String mSenderMobileNumber;
    private String mPhotoUri;
    private String mDescription;

    private ProfileImageView mProfileImageView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDescriptionView;
    private LinearLayout mDescriptionHolder;
    private TextView mAmountView;
    private TextView mServiceChargeView;
    private TextView mNetReceivedView;
    private Button mSendMoneyButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_money_review, container, false);

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mServiceCharge = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.SERVICE_CHARGE);
        mReceiverMobileNumber = getActivity().getIntent().getStringExtra(Constants.RECEIVER);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION);
        mReceiverName = getActivity().getIntent().getStringExtra(Constants.NAME);
        mPhotoUri = getActivity().getIntent().getStringExtra(Constants.PHOTO_URI);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDescriptionView = (TextView) v.findViewById(R.id.textview_description);
        mDescriptionHolder = (LinearLayout) v.findViewById(R.id.description_holder);
        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mServiceChargeView = (TextView) v.findViewById(R.id.textview_service_charge);
        mNetReceivedView = (TextView) v.findViewById(R.id.textview_net_received);
        mSendMoneyButton = (Button) v.findViewById(R.id.button_send_money);

        mProgressDialog = new ProgressDialog(getActivity());

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        mSenderMobileNumber = pref.getString(Constants.USERID, "");

        mProfileImageView.setInformation(mPhotoUri, mReceiverName);

        if (mReceiverName == null || mReceiverName.isEmpty()) {
            mNameView.setVisibility(View.GONE);
        } else {
            mNameView.setText(mReceiverName);
        }

        mMobileNumberView.setText(mReceiverMobileNumber);

        if (mDescription == null || mDescription.isEmpty()) {
            mDescriptionHolder.setVisibility(View.GONE);
        } else {
            mDescriptionView.setText(mDescription);
        }

        mAmountView.setText(String.format("\u09F3 %.2f", mAmount));
        mServiceChargeView.setText(String.format("\u09F3 %.2f", mServiceCharge));
        mNetReceivedView.setText(String.format("\u09F3 %.2f", mAmount.subtract(mServiceCharge)));

        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

                pinInputDialogBuilder.onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        attemptSendMoney(pinInputDialogBuilder.getPin());
                    }
                });

                pinInputDialogBuilder.build().show();
            }
        });

        return v;
    }

    private void attemptSendMoney(String pin) {
        if (mSendMoneyTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_sending_money));
        mProgressDialog.show();
        SendMoneyRequest mSendMoneyRequest = new SendMoneyRequest(
                mSenderMobileNumber, ContactEngine.formatMobileNumberBD(mReceiverMobileNumber),
                mAmount.toString(), mDescription, pin);
        Gson gson = new Gson();
        String json = gson.toJson(mSendMoneyRequest);
        mSendMoneyTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_MONEY,
                Constants.BASE_URL + Constants.URL_SEND_MONEY, json, getActivity());
        mSendMoneyTask.mHttpResponseListener = this;
        mSendMoneyTask.execute();
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.show();
            mSendMoneyTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.send_money_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SEND_MONEY)) {

            if (resultList.size() > 2) {

                try {
                    mSendMoneyResponse = gson.fromJson(resultList.get(2), SendMoneyResponse.class);
                    String message = mSendMoneyResponse.getMessage();

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.send_money_failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.send_money_failed, Toast.LENGTH_SHORT).show();
            }

            mProgressDialog.dismiss();
            mSendMoneyTask = null;

        }
    }
}
