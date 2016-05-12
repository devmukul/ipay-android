package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.AddMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.AddMoneyResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Bank;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ServiceCharge.GetServiceChargeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyFragment extends Fragment implements HttpResponseListener {

    private static final int ADD_MONEY_REVIEW_REQUEST = 101;

    private HttpRequestPostAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;

    private Button buttonAddMoney;
    private EditText mBankAccountNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private TextView mLinkABankNoteTextView;
    private ImageView mBankPicker;
    private List<UserBankClass> mListUserBankClasses;
    private ArrayList<String> mUserBankNameList;
    private ArrayList<String> mUserBankAccountNumberList;
    private int selectedBankPosition = 0;

    private ProgressDialog mProgressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_money, container, false);
        mBankAccountNumberEditText = (EditText) v.findViewById(R.id.bank_account_number);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonAddMoney = (Button) v.findViewById(R.id.button_cash_in);
        mBankPicker = (ImageView) v.findViewById(R.id.accountPicker);
        mLinkABankNoteTextView = (TextView) v.findViewById(R.id.link_a_bank_note);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));
        mUserBankNameList = new ArrayList<String>();
        mUserBankAccountNumberList = new ArrayList<String>();

        // It might be possible that we have failed to load the available bank list during
        // application startup. In that case first try to load the available bank list first, and
        // then load user bank details. Otherwise directly load the bank list.
        if (CommonData.isAvailableBankListLoaded()) {
            getBankList();
        } else {
            attemptRefreshAvailableBankNames();
        }

        buttonAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mBankPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });

        return v;
    }

    private void attemptRefreshAvailableBankNames() {
        GetAvailableBankAsyncTask mGetAvailableBankAsyncTask = new GetAvailableBankAsyncTask(getActivity(),
                new GetAvailableBankAsyncTask.BankLoadListener() {
                    @Override
                    public void onLoadSuccess(List<Bank> banks) {
                        mProgressDialog.dismiss();
                        getBankList();
                    }

                    @Override
                    public void onLoadFailed() {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_available_bank_list_loading, Toast.LENGTH_LONG).show();
                            getActivity().finish();
                        }
                    }
                });
        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_fetching_bank_list));
        mProgressDialog.show();
        mGetAvailableBankAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getBankList() {
        if (mGetBankTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching_bank_info));
        mProgressDialog.show();
        GetBankListRequest mGetBankListRequest = new GetBankListRequest(Constants.DUMMY);
        Gson gson = new Gson();
        String json = gson.toJson(mGetBankListRequest);
        mGetBankTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_BANK_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_BANK, json, getActivity());
        mGetBankTask.mHttpResponseListener = this;

        mGetBankTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {

        boolean cancel = false;
        View focusView = null;

        if (!(mAmountEditText.getText().toString().trim().length() > 0)) {
            focusView = mAmountEditText;
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            cancel = true;
        }
        if (!(mBankAccountNumberEditText.getText().toString().trim().length() > 0)) {
            focusView = mBankAccountNumberEditText;
            mBankAccountNumberEditText.setError(getString(R.string.enter_bank_account_number));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void launchReviewPage() {
        final String amount = mAmountEditText.getText().toString().trim();
        final String description = mDescriptionEditText.getText().toString().trim();

        UserBankClass selectedBankAccount = mListUserBankClasses.get(selectedBankPosition);
        long bankAccountId = selectedBankAccount.getBankAccountId();
        String bankName = selectedBankAccount.getBankName();
        String accountNumber = selectedBankAccount.getAccountNumber();

        Intent intent = new Intent(getActivity(), AddMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, Double.parseDouble(amount));
        intent.putExtra(Constants.BANK_NAME, bankName);
        intent.putExtra(Constants.BANK_ACCOUNT_ID, bankAccountId);
        intent.putExtra(Constants.BANK_ACCOUNT_NUMBER, accountNumber);
        intent.putExtra(Constants.DESCRIPTION, description);

        startActivityForResult(intent, ADD_MONEY_REVIEW_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == ADD_MONEY_REVIEW_REQUEST) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    private void showBankListAlertDialogue() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
        builderSingle.setIcon(R.drawable.ic_account_balance_black_24dp);
        builderSingle.setTitle(getString(R.string.select_a_bank));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.select_dialog_singlechoice);

        for (int i = 0; i < mUserBankNameList.size(); i++) {
            arrayAdapter.add(mUserBankAccountNumberList.get(i) + "," + mUserBankNameList.get(i));
        }

        builderSingle.setNegativeButton(
                R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builderSingle.setAdapter(
                arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String strName = arrayAdapter.getItem(which);
                        selectedBankPosition = which;
                        mBankAccountNumberEditText.setText(strName);
                    }
                });

        builderSingle.show();
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.show();
            mGetBankTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_BANK_LIST)) {
            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                if (resultList.size() > 2) {
                    try {
                        mBankListResponse = gson.fromJson(resultList.get(2), GetBankListResponse.class);

                        mListUserBankClasses = new ArrayList<>();

                        for (UserBankClass bank : mBankListResponse.getBanks()) {
                            if (bank.getAccountStatus() == Constants.BANK_ACCOUNT_STATUS_ACTIVE &&
                                    bank.getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED)) {
                                mListUserBankClasses.add(bank);
                            }
                        }

                        if (mListUserBankClasses == null) {
                            mBankPicker.setEnabled(false);
                            buttonAddMoney.setEnabled(false);
                            if (getActivity() != null)
                                Toast.makeText(getActivity(), R.string.no_linked_bank_found, Toast.LENGTH_LONG).show();
                            mLinkABankNoteTextView.setVisibility(View.VISIBLE);

                        } else if (mListUserBankClasses.size() == 0) {
                            mBankPicker.setEnabled(false);
                            buttonAddMoney.setEnabled(false);
                            if (getActivity() != null)
                                Toast.makeText(getActivity(), R.string.no_linked_bank_found, Toast.LENGTH_LONG).show();
                            mLinkABankNoteTextView.setVisibility(View.VISIBLE);

                        } else {
                            mLinkABankNoteTextView.setVisibility(View.GONE);
                            for (int i = 0; i < mListUserBankClasses.size(); i++) {
                                mUserBankNameList.add(mListUserBankClasses.get(i).getBankName());
                                mUserBankAccountNumberList.add(mListUserBankClasses.get(i).getAccountNumber());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mGetBankTask = null;

        }
    }
}
