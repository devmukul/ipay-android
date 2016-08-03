package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.BankListValidator;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class WithdrawMoneyFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;

    private static final int WITHDRAW_MONEY_REVIEW_REQUEST = 101;

    private Button buttonWithdrawMoney;
    private EditText mBankAccountNumberEditText;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private TextView mLinkABankNoteTextView;
    private List<UserBankClass> mListUserBankClasses;
    private ArrayList<String> mUserBankNameList;
    private ArrayList<String> mUserBankAccountNumberList;
    private ArrayList<String> mUserBankList;
    private int selectedBankPosition = 0;

    private SharedPreferences pref;
    private ProgressDialog mProgressDialog;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_withdraw_money, container, false);
        mBankAccountNumberEditText = (EditText) v.findViewById(R.id.bank_account_number);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonWithdrawMoney = (Button) v.findViewById(R.id.button_cash_out);
        mLinkABankNoteTextView = (TextView) v.findViewById(R.id.link_a_bank_note);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));
        mUserBankNameList = new ArrayList<>();
        mUserBankAccountNumberList = new ArrayList<>();
        mUserBankList = new ArrayList<>();

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

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
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mBankAccountNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });


        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_WITHDRAW_MONEY);

        return v;
    }

    private void attemptGetBusinessRule(int serviceID) {

        if (mGetBusinessRuleTask != null) {
            return;
        }

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private void attemptRefreshAvailableBankNames() {
        GetAvailableBankAsyncTask mGetAvailableBankAsyncTask = new GetAvailableBankAsyncTask(getActivity(),
                new GetAvailableBankAsyncTask.BankLoadListener() {
                    @Override
                    public void onLoadSuccess() {
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
        mGetBankTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BANK_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_BANK, getActivity());
        mGetBankTask.mHttpResponseListener = this;
        mGetBankTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        String balance = null;
        if (pref.contains(Constants.USER_BALANCE)) {
            balance = pref.getString(Constants.USER_BALANCE, null);
        }

        if (!(mAmountEditText.getText().toString().trim().length() > 0)) {
            focusView = mAmountEditText;
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            cancel = true;
        } else if ((mAmountEditText.getText().toString().trim().length() > 0)
                && Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                && Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

            BigDecimal maxAmount = WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min((new BigDecimal(balance)));

            String error_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(), maxAmount);

            if (error_message != null) {
                focusView = mAmountEditText;
                mAmountEditText.setError(error_message);
                cancel = true;
            }
        }

        if (! (mDescriptionEditText.getText().toString().trim().length() > 0)) {
            focusView = mDescriptionEditText;
            mDescriptionEditText.setError(getString(R.string.please_write_note));
            cancel = true;

        }

        if (!(mBankAccountNumberEditText.getText().toString().trim().length() > 0)) {
            focusView = mBankAccountNumberEditText;
            mBankAccountNumberEditText.setError(getString(R.string.select_a_bank));
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
        int bankCode = selectedBankAccount.getBankIcon(getActivity());

        Intent intent = new Intent(getActivity(), WithdrawMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, Double.parseDouble(amount));
        intent.putExtra(Constants.BANK_NAME, bankName);
        intent.putExtra(Constants.BANK_ACCOUNT_ID, bankAccountId);
        intent.putExtra(Constants.BANK_ACCOUNT_NUMBER, accountNumber);
        intent.putExtra(Constants.INVOICE_DESCRIPTION_TAG, description);
        intent.putExtra(Constants.BANK_CODE, bankCode);

        startActivityForResult(intent, WITHDRAW_MONEY_REVIEW_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == WITHDRAW_MONEY_REVIEW_REQUEST) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }


    private void showBankListAlertDialogue() {
        CustomSelectorDialog bankSelectorDialog = new CustomSelectorDialog(getActivity(), getString(R.string.select_a_bank), mUserBankList);
        bankSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mBankAccountNumberEditText.setError(null);
                mBankAccountNumberEditText.setText(name);

            }
        });

        bankSelectorDialog.show();
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.show();
            mGetBankTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_BANK_LIST)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    mBankListResponse = gson.fromJson(result.getJsonString(), GetBankListResponse.class);

                    mListUserBankClasses = new ArrayList<>();

                    for (UserBankClass bank : mBankListResponse.getBanks()) {
                        if (bank.getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED)) {
                            mListUserBankClasses.add(bank);
                        }
                    }

                    BankListValidator bankListValidator = new BankListValidator(mBankListResponse.getBanks());
                    if (!bankListValidator.isBankAdded()) {
                        bankListValidator.showAddBankDialog(getActivity());
                    } else if (!bankListValidator.isVerifiedBankAdded()) {
                        bankListValidator.showVerifiedBankDialog(getActivity());
                    } else {
                        mLinkABankNoteTextView.setVisibility(View.GONE);
                        for (int i = 0; i < mListUserBankClasses.size(); i++) {
                            mUserBankNameList.add(mListUserBankClasses.get(i).getBankName());
                            mUserBankAccountNumberList.add(mListUserBankClasses.get(i).getAccountNumber());
                            mUserBankList.add(mListUserBankClasses.get(i).getBankName() + "\n" + mListUserBankClasses.get(i).getAccountNumber());
                        }
                    }

                    if (mUserBankNameList.size() == 1) {
                        mBankAccountNumberEditText.setText(mUserBankAccountNumberList.get(0) + "," + mUserBankNameList.get(0));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                mProgressDialog.dismiss();
                mGetBankTask = null;

            }
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    for (BusinessRule rule : businessRuleArray) {
                        if (rule.getRuleID().equals(Constants.SERVICE_RULE_WITHDRAW_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                            WithdrawMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());

                        } else if (rule.getRuleID().equals(Constants.SERVICE_RULE_WITHDRAW_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                            WithdrawMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
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

            mGetBusinessRuleTask = null;

        }
    }
}
