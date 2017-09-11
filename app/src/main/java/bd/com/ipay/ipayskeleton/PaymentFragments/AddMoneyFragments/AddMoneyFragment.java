package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.ResourceApi.GetAvailableBankAsyncTask;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragmentV4;
import bd.com.ipay.ipayskeleton.CustomView.BankListValidator;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.GetBankListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AddMoneyFragment extends BaseFragmentV4 implements HttpResponseListener {

    private static final int ADD_MONEY_REVIEW_REQUEST = 101;

    private HttpRequestGetAsyncTask mGetBankTask = null;
    private GetBankListResponse mBankListResponse;
    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private Button buttonAddMoney;
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

    private ProgressDialog mProgressDialog;
    private AddMoneyHistoryFragment mAddMoneyHistoryFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_money, container, false);
        mBankNameTextView = (TextView) v.findViewById(R.id.bank_name);
        mBankBranchTextView = (TextView) v.findViewById(R.id.bank_branch);
        mBankAccountTextView = (TextView) v.findViewById(R.id.bank_account_number);
        mBankAccountNumberHintTextView = (TextView) v.findViewById(R.id.bank_account_number_hint);
        mDescriptionEditText = (EditText) v.findViewById(R.id.description);
        mAmountEditText = (EditText) v.findViewById(R.id.amount);
        buttonAddMoney = (Button) v.findViewById(R.id.button_cash_in);
        mLinkABankNoteTextView = (TextView) v.findViewById(R.id.link_a_bank_note);
        mBankIcon = (ImageView) v.findViewById(R.id.portrait);

        // Allow user to write not more than two digits after decimal point for an input of an amount
        mAmountEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter()});

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.progress_dialog_add_money_in_progress));
        mUserBankNameList = new ArrayList<>();
        mUserBankAccountNumberList = new ArrayList<>();
        mUserBankList = new ArrayList<>();

        // Block from adding bank if an user is not verified
        if (ProfileInfoCacheManager.getVerificationStatus().equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
            if (ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_BANK_ACCOUNTS)) {
                getBankInformation();
            }
        } else {
            showGetVerifiedDialog();
        }

        buttonAddMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.ADD_MONEY)
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        mBankNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_BANK_ACCOUNTS)
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });
        mBankIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_BANK_ACCOUNTS)
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });

        mBankBranchTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_BANK_ACCOUNTS)
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });
        mBankAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEE_BANK_ACCOUNTS)
            public void onClick(View v) {
                showBankListAlertDialogue();
            }
        });

        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_ADD_MONEY);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_add_money) );
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.activity_add_money_history, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Remove search action of contacts
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_history:
                switchToAddMoneyHistoryFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showGetVerifiedDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .content(R.string.get_verified)
                .cancelable(false)
                .content(getString(R.string.can_not_add_money_if_not_verified))
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (!ACLManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_BANK_ACCOUNTS)) {
                            DialogUtils.showServiceNotAllowedDialog(getActivity());
                            return;
                        }
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

    private void attemptGetBusinessRule(int serviceID) {

        if (mGetBusinessRuleTask != null) {
            return;
        }

        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {

        boolean cancel = false;
        View focusView = null;

        if (!(mAmountEditText.getText().toString().trim().length() > 0)) {
            focusView = mAmountEditText;
            mAmountEditText.setError(getString(R.string.please_enter_amount));
            cancel = true;
        } else if ((mAmountEditText.getText().toString().trim().length() > 0)
                && Utilities.isValueAvailable(((AddMoneyActivity) getActivity()).mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                && Utilities.isValueAvailable(((AddMoneyActivity) getActivity()).mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

            String error_message = InputValidator.isValidAmount(getActivity(), new BigDecimal(mAmountEditText.getText().toString()),
                    ((AddMoneyActivity) getActivity()).mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                    ((AddMoneyActivity) getActivity()).mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

            if (error_message != null) {
                focusView = mAmountEditText;
                mAmountEditText.setError(error_message);
                cancel = true;
            }
        }
        if (!(mBankNameTextView.getText().toString().trim().length() > 0)) {
            focusView = mBankNameTextView;
            mBankNameTextView.setError(getString(R.string.select_a_bank));
            cancel = true;
        }

        if (!(mDescriptionEditText.getText().toString().trim().length() > 0)) {
            focusView = mDescriptionEditText;
            mDescriptionEditText.setError(getString(R.string.please_write_note));
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

        Intent intent = new Intent(getActivity(), AddMoneyReviewActivity.class);
        intent.putExtra(Constants.AMOUNT, Double.parseDouble(amount));
        intent.putExtra(Constants.BANK_NAME, bankName);
        intent.putExtra(Constants.BANK_ACCOUNT_ID, bankAccountId);
        intent.putExtra(Constants.BANK_ACCOUNT_NUMBER, accountNumber);
        intent.putExtra(Constants.DESCRIPTION_TAG, description);
        intent.putExtra(Constants.BANK_CODE, bankCode);

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
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.show();
            mGetBankTask = null;
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
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
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
                }

            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.fetch_info_failed, Toast.LENGTH_LONG);
            }

            mProgressDialog.dismiss();
            mGetBankTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    for (BusinessRule rule : businessRuleArray) {
                        if (rule.getRuleID().equals(Constants.SERVICE_RULE_ADD_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                            ((AddMoneyActivity) getActivity()).mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());

                        } else if (rule.getRuleID().equals(Constants.SERVICE_RULE_ADD_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                            ((AddMoneyActivity) getActivity()).mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
                }

            } else {
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
            }

            mGetBusinessRuleTask = null;
        }
    }

    public void switchToAddMoneyHistoryFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new AddMoneyHistoryFragment()).addToBackStack(null).commit();
    }
}
