package bd.com.ipay.ipayskeleton.WithdrawMoneyFragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.WithdrawMoneyRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.AddOrWithdrawMoney.WithdrawMoneyResponse;
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

public class WithdrawMoneyFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCashOutTask = null;
    private WithdrawMoneyResponse mWithdrawMoneyResponse;

    private HttpRequestPostAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;

    private HttpRequestPostAsyncTask mServiceChargeTask = null;
    private GetServiceChargeResponse mGetServiceChargeResponse;

    private Button buttonWithdrawMoney;
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
        View v = inflater.inflate(R.layout.fragment_withdraw_money, container, false);
        mBankAccountNumberEditText = (EditText) v.findViewById(R.id.bank_account_number);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonWithdrawMoney = (Button) v.findViewById(R.id.button_cash_out);
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

        buttonWithdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    showAlertDialogue();
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mBankPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });

        if (Utilities.isConnectionAvailable(getActivity()))
            attemptGetServiceCharge();
        else
            Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();

        return v;
    }

    private void attemptGetServiceCharge() {
        if (mServiceChargeTask != null) {
            return;
        }

        SharedPreferences pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Context.MODE_PRIVATE);
        int accountType = pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE);
        int accountClass = Constants.DEFAULT_USER_CLASS;

        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
        GetServiceChargeRequest mServiceChargeRequest = new GetServiceChargeRequest(Constants.SERVICE_ID_WITHDRAW_MONEY, accountType, accountClass);
        Gson gson = new Gson();
        String json = gson.toJson(mServiceChargeRequest);
        mServiceChargeTask = new HttpRequestPostAsyncTask(Constants.COMMAND_GET_SERVICE_CHARGE,
                Constants.BASE_URL_SM + Constants.URL_SERVICE_CHARGE, json, getActivity());
        mServiceChargeTask.mHttpResponseListener = this;
        mServiceChargeTask.execute((Void) null);
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
        mGetAvailableBankAsyncTask.execute();
    }

    ;

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
                Constants.BASE_URL_POST_MM + Constants.URL_GET_BANK, json, getActivity());
        mGetBankTask.mHttpResponseListener = this;
        mGetBankTask.execute((Void) null);
    }

    private void attemptWithdrawMoney(String amount, String accountNumber, String description) {
        if (mCashOutTask != null) {
            return;
        }

        if (!accountNumber.equals(mListUserBankClasses.get(selectedBankPosition).getAccountNumber())) {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.invalid_account_number, Toast.LENGTH_LONG).show();
            return;
        }

        mProgressDialog.show();
        long bankAccountId = mListUserBankClasses.get(selectedBankPosition).getBankAccountId();

        WithdrawMoneyRequest mWithdrawMoneyRequest = new WithdrawMoneyRequest(bankAccountId,
                Double.parseDouble(amount), description);
        Gson gson = new Gson();
        String json = gson.toJson(mWithdrawMoneyRequest);
        mCashOutTask = new HttpRequestPostAsyncTask(Constants.COMMAND_WITHDRAW_MONEY,
                Constants.BASE_URL_SM + Constants.URL_WITHDRAW_MONEY, json, getActivity());
        mCashOutTask.mHttpResponseListener = this;
        mCashOutTask.execute((Void) null);

    }

    private void showAlertDialogue() {

        mAmountEditText.setError(null);
        mBankAccountNumberEditText.setError(null);

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
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {

            final String amount = mAmountEditText.getText().toString().trim();
            final String accountNumber = mBankAccountNumberEditText.getText().toString().trim();
            final String description = mDescriptionEditText.getText().toString().trim();
            String serviceChargeDescription = "";

            if (mGetServiceChargeResponse != null) {
                if (mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)).compareTo(new BigDecimal(0)) > 0)
                    serviceChargeDescription = "You'll be charged " + mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)) + " Tk. for this transaction.";
                else if (mGetServiceChargeResponse.getServiceCharge(new BigDecimal(amount)).compareTo(new BigDecimal(0)) == 0)
                    serviceChargeDescription = getString(R.string.no_extra_charges);
                else {
                    Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                    return;
                }

            } else {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                return;
            }

            AlertDialog.Builder alertDialogue = new AlertDialog.Builder(getActivity());
            alertDialogue.setTitle(R.string.confirm_add_money);
            alertDialogue.setMessage("You're going to withdraw " + amount + " BDT from iPay to your Account Number: "
                    + accountNumber
                    + "\n" + serviceChargeDescription
                    + "\nDo you want to continue?");

            alertDialogue.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    attemptWithdrawMoney(amount, accountNumber, description);
                }
            });

            alertDialogue.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Do nothing
                }
            });

            alertDialogue.show();
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
                        String[] accountNumberAndBankName = strName.split(",");
                        final String accountNumber = accountNumberAndBankName[0];

                        AlertDialog.Builder builderInner = new AlertDialog.Builder(
                                getActivity());
                        builderInner.setMessage(strName);
                        builderInner.setTitle(R.string.your_selected_bank_account_is);
                        builderInner.setPositiveButton(
                                R.string.zxing_button_ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        mBankAccountNumberEditText.setText(accountNumber.trim());
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                    }
                });
        builderSingle.show();
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.show();
            mGetBankTask = null;
            mCashOutTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_WITHDRAW_MONEY)) {

            if (resultList.size() > 2) {
                try {
                    mWithdrawMoneyResponse = gson.fromJson(resultList.get(2), WithdrawMoneyResponse.class);
                    String message = mWithdrawMoneyResponse.getMessage();

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
                        // Return to HomeActivity
                        getActivity().finish();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.withdraw_money_failed, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.withdraw_money_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mCashOutTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_BANK_LIST)) {
            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {

                if (resultList.size() > 2) {

                    try {
                        mBankListResponse = gson.fromJson(resultList.get(2), GetBankListResponse.class);

                        mListUserBankClasses = mBankListResponse.getBanks();

                        if (mListUserBankClasses == null) {
                            mBankPicker.setEnabled(false);
                            buttonWithdrawMoney.setEnabled(false);
                            if (getActivity() != null)
                                Toast.makeText(getActivity(), R.string.no_linked_bank_found, Toast.LENGTH_LONG).show();
                            mLinkABankNoteTextView.setVisibility(View.VISIBLE);

                        } else if (mListUserBankClasses.size() == 0) {
                            mBankPicker.setEnabled(false);
                            buttonWithdrawMoney.setEnabled(false);
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
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.pending_get_failed, Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mGetBankTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_GET_SERVICE_CHARGE)) {
            if (resultList.size() > 2) {
                try {
                    mGetServiceChargeResponse = gson.fromJson(resultList.get(2), GetServiceChargeResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        // Do nothing
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();

            mProgressDialog.dismiss();
            mServiceChargeTask = null;
        }
    }
}
