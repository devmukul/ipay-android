package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.CardPaymentWebViewActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TransactionDetailsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.AbstractSelectorView;
import bd.com.ipay.ipayskeleton.CustomView.BankSelectorView;
import bd.com.ipay.ipayskeleton.CustomView.SelectorView;
import bd.com.ipay.ipayskeleton.CustomView.ServiceSelectorView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.Service.IpayService;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

/**
 * AddMoneyFragment will serve the purpose to Add some money to users ipay account from different
 * source(e.g. Bank,Cards like credit,voucher etc).
 */
public class AddMoneyFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetBankTask = null;
    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private EditText mNoteEditText;
    private EditText mAmountEditText;

    private ServiceSelectorView mAddMoneyOptionSelectorView;

    private View mBankSelectorViewHolder;
    private BankSelectorView mBankSelectorView;

    private TextView mMessageTextView;

    private List<UserBankClass> mListUserBankClasses;

    private ProgressDialog mProgressDialog;
    boolean isOnlyByCard;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_money, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAmountEditText = findViewById(R.id.amount_edit_text);
        mAddMoneyOptionSelectorView = findViewById(R.id.add_money_option_selector_view);
        mBankSelectorViewHolder = findViewById(R.id.bank_selector_view_holder);
        mBankSelectorView = findViewById(R.id.bank_selector_view);
        mNoteEditText = findViewById(R.id.note_edit_text);
        mMessageTextView = findViewById(R.id.message_text_view);
        final View mAddMoneyOptionSelectorViewHolder = findViewById(R.id.add_money_option_selector_view_holder);
        final Button mAddMoneyProceedButton = findViewById(R.id.add_money_proceed_button);

        if (getActivity().getIntent().getStringExtra(Constants.TAG) != null && getActivity().getIntent().getStringExtra(Constants.TAG).equalsIgnoreCase("CARD"))
            isOnlyByCard = true;
        else
            isOnlyByCard = false;
        final List<IpayService> availableAddMoneyOptions = Utilities.getAvailableAddMoneyOptions(isOnlyByCard);

        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        mAddMoneyProceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs()) {
                        launchReviewPage(mAddMoneyOptionSelectorView.getSelectedItem().getServiceId());
                    }
                } else
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mAddMoneyOptionSelectorView.setSelectorDialogTitle(getString(R.string.add_money_from));
        mAddMoneyOptionSelectorView.setOnItemAccessValidation(new SelectorView.OnItemAccessValidation() {
            @Override
            public boolean hasItemAccessAbility(int id, String name) {
                switch (name) {
                    case Constants.ADD_MONEY_BY_BANK_TITLE:
                        return ACLManager.hasServicesAccessibility(ServiceIdConstants.ADD_MONEY_BY_BANK);
                    case Constants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_TITLE:
                        return true;
                    default:
                        return false;
                }
            }
        });

        mAddMoneyOptionSelectorView.setItems(availableAddMoneyOptions);
        mAddMoneyOptionSelectorView.setOnItemSelectListener(new AbstractSelectorView.OnItemSelectListener() {

            @Override
            public boolean onItemSelected(int selectedItemPosition) {
                switch (availableAddMoneyOptions.get(selectedItemPosition).getServiceId()) {
                    case ServiceIdConstants.ADD_MONEY_BY_BANK:
                        setupAddMoneyFromBank();
                        break;
                    case ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD:
                        setupAddMoneyFromCreditOrDebitCard();
                        break;
                }
                return true;
            }
        });
        if (availableAddMoneyOptions.size() == 1) {
            mAddMoneyOptionSelectorView.selectedItem(0);
            mAddMoneyOptionSelectorViewHolder.setVisibility(View.GONE);
        } else {
            mAddMoneyOptionSelectorViewHolder.setVisibility(View.VISIBLE);
        }
    }

    private <T extends View> T findViewById(@IdRes int viewId) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(viewId);
    }

    private void showGetVerifiedDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .content(R.string.get_verified)
                .cancelable(false)
                .content(getString(R.string.can_not_add_money_from_bank_if_not_verified))
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        dialog.show();
    }

    @ValidateAccess(ServiceIdConstants.ADD_MONEY_BY_BANK)
    private void setupAddMoneyFromBank() {
        mMessageTextView.setText(R.string.add_money_from_bank_info);
        mBankSelectorViewHolder.setVisibility(View.VISIBLE);
        if (mListUserBankClasses == null || mListUserBankClasses.isEmpty()) {
            if (ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_BANK_ACCOUNTS)) {
                getBankInformation();
            }
        } else {
            attemptGetBusinessRule(Constants.SERVICE_ID_ADD_MONEY_BY_BANK);
        }
    }

    //@ValidateAccess(ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD)
    private void setupAddMoneyFromCreditOrDebitCard() {
        mMessageTextView.setText(R.string.add_money_from_credit_or_debit_card_info);
        mBankSelectorViewHolder.setVisibility(View.GONE);
        attemptGetBusinessRule(Constants.SERVICE_ID_ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD);
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
                            Toaster.makeText(getActivity(), R.string.failed_available_bank_list_loading, Toast.LENGTH_LONG);
                            getActivity().finish();
                        }
                    }
                });
        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_fetching_bank_list));
        mProgressDialog.show();
        mGetAvailableBankAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void attemptGetBusinessRule(int serviceID) {
        if (mGetBusinessRuleTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {
        final boolean shouldProceed;
        final View focusView;
        clearAllErrorMessage();

        if (mAddMoneyOptionSelectorView.getSelectedItemPosition() == -1) {
            focusView = null;
            mAddMoneyOptionSelectorView.setError(R.string.choose_add_money_option);
            shouldProceed = false;
        } else if (!Utilities.isValueAvailable(AddMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(AddMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        } else if (AddMoneyActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        } else if (mAddMoneyOptionSelectorView.getSelectedItem().getServiceId() == ServiceIdConstants.ADD_MONEY_BY_BANK && mBankSelectorView.getSelectedItemPosition() == -1) {
            focusView = null;
            mBankSelectorView.setError(R.string.select_a_bank);
            shouldProceed = false;
        } else if (!isValidAmount()) {
            focusView = mAmountEditText;
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
        boolean isValidAmount;
        String errorMessage;

        if (TextUtils.isEmpty(mAmountEditText.getText())) {
            errorMessage = getString(R.string.please_enter_amount);
        } else {
            errorMessage = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    AddMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                    AddMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());
        }

        if (errorMessage != null) {
            isValidAmount = false;
            mAmountEditText.setError(errorMessage);
        } else {
            isValidAmount = true;
        }
        return isValidAmount;
    }

    private void clearAllErrorMessage() {
        mAmountEditText.setError(null);
        mAddMoneyOptionSelectorView.setError(null);
        mBankSelectorView.setError(null);
        mNoteEditText.setError(null);
    }

    private void launchReviewPage(final int addMonerServiceId) {
        final String amount = mAmountEditText.getText().toString().trim();
        final String description = mNoteEditText.getText().toString().trim();

        Intent intent = new Intent(getActivity(), AddMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, Double.parseDouble(amount));
        intent.putExtra(Constants.DESCRIPTION_TAG, description);


        switch (addMonerServiceId) {
            case ServiceIdConstants.ADD_MONEY_BY_BANK:
                // Adding the type is by bank
                intent.putExtra(Constants.ADD_MONEY_TYPE, Constants.ADD_MONEY_TYPE_BY_BANK);

                // Adding the info of the selected bank
                UserBankClass selectedBankAccount = mListUserBankClasses.get(mBankSelectorView.getSelectedItemPosition());
                intent.putExtra(Constants.SELECTED_BANK_ACCOUNT, selectedBankAccount);
                break;
            case ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD:
                // Adding the type is by credit/debit card
                intent.putExtra(Constants.ADD_MONEY_TYPE, Constants.ADD_MONEY_TYPE_BY_CREDIT_OR_DEBIT_CARD);
                break;
        }
        startActivityForResult(intent, AddMoneyActivity.ADD_MONEY_REVIEW_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == AddMoneyActivity.ADD_MONEY_REVIEW_REQUEST) {
            if (data != null && data.hasExtra(Constants.CARD_TRANSACTION_DATA)) {
                final int addMoneyByCreditOrDebitCardStatus = data.getBundleExtra(Constants.CARD_TRANSACTION_DATA).getInt(Constants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_STATUS, 0);
                final Intent intent;
                if (addMoneyByCreditOrDebitCardStatus == CardPaymentWebViewActivity.CARD_TRANSACTION_SUCCESSFUL) {
                    if (((AddMoneyActivity) getActivity()).FROM_ON_BOARD) {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.money_added_successfully), Toast.LENGTH_LONG).show();
                        intent = new Intent(getActivity(), HomeActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        final String transactionId = data.getBundleExtra(Constants.CARD_TRANSACTION_DATA).getString(Constants.TRANSACTION_ID);
                        intent = new Intent(getActivity(), TransactionDetailsActivity.class);
                        intent.putExtra(Constants.STATUS, Constants.PAYMENT_REQUEST_STATUS_ALL);
                        intent.putExtra(Constants.MONEY_REQUEST_ID, transactionId);
                        startActivity(intent);
                    }
                }

            }
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetBankTask = null;
            mGetBusinessRuleTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }

        if (isAdded()) mProgressDialog.dismiss();

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_BANK_LIST:
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
                            if (mBankSelectorView.isBankAdded() && mBankSelectorView.isVerifiedBankAdded()) {
                                attemptGetBusinessRule(Constants.SERVICE_ID_ADD_MONEY_BY_BANK);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            if (getActivity() != null)
                                Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
                        }
                        break;
                    default:
                        if (getActivity() != null)
                            Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
                        break;
                }
                break;
            case Constants.COMMAND_GET_BUSINESS_RULE:
                mGetBusinessRuleTask = null;
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
                        try {
                            gson = new Gson();

                            BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                            for (BusinessRule rule : businessRuleArray) {
                                switch (rule.getRuleID()) {
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_MAX_AMOUNT_PER_PAYMENT:
                                        AddMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_MIN_AMOUNT_PER_PAYMENT:
                                        AddMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_VERIFICATION_REQUIRED:
                                        AddMoneyActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                                        break;
                                    // For add money by card
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_CARDMONEY_MAX_AMOUNT_SINGLE:
                                        AddMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_CARDMONEY_MIN_AMOUNT_SINGLE:
                                        AddMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                                        break;
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_CARDMONEY_VERIFICATION_REQUIRED:
                                        AddMoneyActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                                        break;
                                    // For all types of add money
                                    case BusinessRuleConstants.SERVICE_RULE_ADD_MONEY_PIN_REQUIRED:
                                        AddMoneyActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                                        break;
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            if (getActivity() != null)
                                DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
                        }
                        break;
                    default:
                        if (getActivity() != null)
                            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
                        break;
                }
                break;
            default:
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
                break;
        }
    }
}