package bd.com.ipay.ipayskeleton.PaymentFragments.WithdrawMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.BankSelectorView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class WithdrawMoneyFragment extends BaseFragment implements HttpResponseListener {

    private static final int WITHDRAW_MONEY_REVIEW_REQUEST = 101;

    private HttpRequestGetAsyncTask mGetBankTask = null;
    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private BankSelectorView mBankSelectorView;
    private EditText mNoteEditText;
    private EditText mAmountEditText;

    private List<UserBankClass> mListUserBankClasses;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_WITHDRAW_MONEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_withdraw_money, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mBankSelectorView = findViewById(R.id.bank_selector_view);
        mAmountEditText = findViewById(R.id.amount);
        mNoteEditText = findViewById(R.id.description);
        Button buttonWithdrawMoney = findViewById(R.id.button_cash_out);

        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_BANK_ACCOUNTS)) {
            getBankInformation();
        }

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        buttonWithdrawMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.WITHDRAW_MONEY)
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });
    }

    private <T extends View> T findViewById(@IdRes int viewId) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(viewId);
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
        mBankSelectorView.setSelectable(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_withdraw_money));
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
        final boolean shouldProceed;
        final View focusView;
        clearAllErrorMessage();

        if (!isValidAmount()) {
            focusView = mAmountEditText;
            shouldProceed = false;
        } else if (mBankSelectorView.getSelectedItemPosition() == -1) {
            focusView = null;
            mBankSelectorView.setError(R.string.select_a_bank);
            shouldProceed = false;
        } else if (TextUtils.isEmpty(mNoteEditText.getText().toString().trim())) {
            focusView = mNoteEditText;
            mNoteEditText.setError(getString(R.string.please_write_note));
            shouldProceed = false;
        } else {
            focusView = null;
            shouldProceed = true;
        }

        if (focusView != null) {
            focusView.requestFocus();
        }
        return shouldProceed;
    }

    private boolean isValidAmount() {
        final boolean isValidAmount;
        final BigDecimal amount = new BigDecimal(SharedPrefManager.getUserBalance());
        if (TextUtils.isEmpty(mAmountEditText.getText())) {
            isValidAmount = false;
            mAmountEditText.setError(getString(R.string.please_enter_amount));
        } else if (new BigDecimal(mAmountEditText.getText().toString()).compareTo(amount) > 0) {
            isValidAmount = false;
            mAmountEditText.setError(getString(R.string.insufficient_balance));
        } else if (Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                && Utilities.isValueAvailable(WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            final String errorMessage = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    WithdrawMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                    WithdrawMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());
            if (errorMessage != null) {
                isValidAmount = false;
                mAmountEditText.setError(errorMessage);
            } else {
                isValidAmount = true;
            }
        } else {
            isValidAmount = true;
        }
        return isValidAmount;
    }

    private void clearAllErrorMessage() {
        mAmountEditText.setError(null);
        mBankSelectorView.setError(null);
        mNoteEditText.setError(null);
    }

    private void launchReviewPage() {
        final String amount = mAmountEditText.getText().toString().trim();
        final String description = mNoteEditText.getText().toString().trim();

        Intent intent = new Intent(getActivity(), WithdrawMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, Double.parseDouble(amount));
        intent.putExtra(Constants.DESCRIPTION_TAG, description);

        UserBankClass selectedBankAccount = mListUserBankClasses.get(mBankSelectorView.getSelectedItemPosition());
        intent.putExtra(Constants.SELECTED_BANK_ACCOUNT, selectedBankAccount);
        startActivityForResult(intent, WITHDRAW_MONEY_REVIEW_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == WITHDRAW_MONEY_REVIEW_REQUEST) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.cancel();
            mGetBankTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_BANK_LIST:
                mProgressDialog.cancel();
                GetBankListResponse mBankListResponse;
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
                        try {
                            mBankListResponse = gson.fromJson(result.getJsonString(), GetBankListResponse.class);
                            mListUserBankClasses = new ArrayList<>();

                            for (UserBankClass bank : mBankListResponse.getBanks()) {
                                if (bank.getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED)) {
                                    mListUserBankClasses.add(bank);
                                }
                            }
                            mBankSelectorView.setItems(mListUserBankClasses);
                            mBankSelectorView.setSelectable(true);
                            if (!mBankSelectorView.isBankAdded()) {
                                mBankSelectorView.showAddBankDialog(true);
                            } else if (!mBankSelectorView.isVerifiedBankAdded()) {
                                mBankSelectorView.showVerifyBankDialog(true);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;

                    default:
                        break;
                }
                break;
            case Constants.COMMAND_GET_BUSINESS_RULE:
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
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
                        }
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}