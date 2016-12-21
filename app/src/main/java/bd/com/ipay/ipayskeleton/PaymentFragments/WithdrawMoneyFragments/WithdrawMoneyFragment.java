package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.BankListValidator;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.MMModule.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class WithdrawMoneyFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;

    private static final int WITHDRAW_MONEY_REVIEW_REQUEST = 101;

    private Button buttonWithdrawMoney;
    private TextView mBankNameTextView;
    private TextView mBankBranchTextView;
    private TextView mBankAccountTextView;
    private TextView mBankAccountNumberHintTextView;
    private EditText mDescriptionEditText;
    private EditText mAmountEditText;
    private TextView mLinkABankNoteTextView;
    private ImageView mBankIcon;
    private List<UserBankClass> mListUserBankClasses;
    private ArrayList<String> mUserBankNameList;
    private ArrayList<String> mUserBankAccountNumberList;
    private ArrayList<String> mUserBankList;
    private int[] mBankIconArray;
    private int selectedBankPosition = 0;

    private SharedPreferences pref;
    private ProgressDialog mProgressDialog;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_withdraw_money, container, false);
        mBankNameTextView = (TextView) v.findViewById(R.id.bank_name);
        mBankBranchTextView = (TextView) v.findViewById(R.id.bank_branch);
        mBankAccountTextView = (TextView) v.findViewById(R.id.bank_account_number);
        mBankAccountNumberHintTextView = (TextView) v.findViewById(R.id.bank_account_number_hint);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonWithdrawMoney = (Button) v.findViewById(R.id.button_cash_out);
        mLinkABankNoteTextView = (TextView) v.findViewById(R.id.link_a_bank_note);
        mBankIcon = (ImageView) v.findViewById(R.id.portrait);

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));
        mUserBankNameList = new ArrayList<>();
        mUserBankAccountNumberList = new ArrayList<>();
        mUserBankList = new ArrayList<>();

        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        // Block from adding bank if an user is not verified
        if (ProfileInfoCacheManager.getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
            getBankInformation();
        } else {
            showGetVerifiedDialog();
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

        mBankNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });
        mBankIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });

        mBankBranchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });
        mBankAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_WITHDRAW_MONEY);

        return v;
    }

    private void showGetVerifiedDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .content(R.string.get_verified)
                .cancelable(false)
                .content(getString(R.string.can_not_add_bank_if_not_verified))
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getActivity().onBackPressed();
                    }
                });

        dialog.show();
    }

    private void getBankInformation() {
        // It might be possible that we have failed to load the available bank list during
        // application startup. In that case first try to load the available bank list first, and
        // then load user bank details. Otherwise directly load the bank list.
        if (CommonData.isAvailableBankListLoaded()) {
            getBankList();
        } else {
            attemptRefreshAvailableBankNames();
        }
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

        if (!(mDescriptionEditText.getText().toString().trim().length() > 0)) {
            focusView = mDescriptionEditText;
            mDescriptionEditText.setError(getString(R.string.please_write_note));
            cancel = true;

        }

        if (!(mBankNameTextView.getText().toString().trim().length() > 0)) {
            focusView = mBankNameTextView;
            mBankNameTextView.setError(getString(R.string.select_a_bank));
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
        CustomSelectorDialogWithIcon bankSelectorDialogWithIcon = new CustomSelectorDialogWithIcon(getActivity(), getString(R.string.select_a_bank), mUserBankList, mBankIconArray);
        bankSelectorDialogWithIcon.setOnResourceSelectedListener(new CustomSelectorDialogWithIcon.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                selectedBankPosition = id;
                mBankNameTextView.setError(null);
                mBankBranchTextView.setVisibility(View.VISIBLE);
                mBankAccountTextView.setVisibility(View.VISIBLE);
                mBankAccountNumberHintTextView.setVisibility(View.VISIBLE);
                mBankIcon.setVisibility(View.VISIBLE);
                Drawable icon = getResources().getDrawable(mListUserBankClasses.get(selectedBankPosition).getBankIcon(getActivity()));
                mBankIcon.setImageDrawable(icon);

                mBankNameTextView.setText(mListUserBankClasses.get(selectedBankPosition).getBankName());
                mBankBranchTextView.setText(mListUserBankClasses.get(selectedBankPosition).getBranchName());
                mBankAccountTextView.setText(mListUserBankClasses.get(selectedBankPosition).getAccountNumber());

            }
        });

        bankSelectorDialogWithIcon.show();
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
                        bankListValidator.showVerifyBankDialog(getActivity());
                    } else {
                        mLinkABankNoteTextView.setVisibility(View.GONE);
                        mBankIconArray = new int[mListUserBankClasses.size()];

                        for (int i = 0; i < mListUserBankClasses.size(); i++) {
                            mUserBankNameList.add(mListUserBankClasses.get(i).getBankName());
                            mUserBankAccountNumberList.add(mListUserBankClasses.get(i).getAccountNumber());
                            mUserBankList.add(mListUserBankClasses.get(i).getBankName() + "\n" + mListUserBankClasses.get(i).getBranchName() + "\n" + mListUserBankClasses.get(i).getAccountNumber());
                            int icon = mListUserBankClasses.get(i).getBankIcon(getActivity());
                            mBankIconArray[i] = icon;
                        }
                    }

                    if (mUserBankNameList.size() == 1) {
                        mBankBranchTextView.setVisibility(View.VISIBLE);
                        mBankAccountTextView.setVisibility(View.VISIBLE);
                        mBankAccountNumberHintTextView.setVisibility(View.VISIBLE);
                        mBankIcon.setVisibility(View.VISIBLE);
                        Drawable icon = getResources().getDrawable(mListUserBankClasses.get(selectedBankPosition).getBankIcon(getActivity()));
                        mBankIcon.setImageDrawable(icon);

                        mBankNameTextView.setText(mListUserBankClasses.get(selectedBankPosition).getBankName());
                        mBankBranchTextView.setText(mListUserBankClasses.get(selectedBankPosition).getBranchName());
                        mBankAccountTextView.setText(mListUserBankClasses.get(selectedBankPosition).getAccountNumber());
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
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            }

            mGetBusinessRuleTask = null;

        }
    }
}
