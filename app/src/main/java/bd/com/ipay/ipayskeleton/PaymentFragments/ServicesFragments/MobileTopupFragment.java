package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpReviewActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.CustomContactsSearchView;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class MobileTopupFragment extends BaseFragment implements HttpResponseListener {

    private static final int MOBILE_TOPUP_REVIEW_REQUEST = 101;
    private final int PICK_CONTACT_REQUEST = 100;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private CustomContactsSearchView mMobileNumberEditText;
    private EditText mAmountEditText;
    private EditText mPackageEditText;
    private EditText mOperatorEditText;
    private ImageView mSelectReceiverButton;
    private Button mRechargeButton;
    private TextView mMobileTopUpInfoTextView;
    private ProgressDialog mProgressDialog;

    private List<String> mPackageList;
    private List<String> mOperatorList;
    private CustomSelectorDialog mPackageSelectorDialog;
    private CustomSelectorDialogWithIcon mOperatorSelectorDialog;
    private int mSelectedPackageTypeId = -1;
    private int mSelectedOperatorTypeId = 0;
    private String mUserMobileNumber;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mobile_topup, container, false);

        mMobileNumberEditText = (CustomContactsSearchView) view.findViewById(R.id.mobile_number);
        mAmountEditText = (EditText) view.findViewById(R.id.amount);
        mPackageEditText = (EditText) view.findViewById(R.id.package_type);
        mOperatorEditText = (EditText) view.findViewById(R.id.operator);
        mSelectReceiverButton = (ImageView) view.findViewById(R.id.select_receiver_from_contacts);
        mRechargeButton = (Button) view.findViewById(R.id.button_recharge);
        mMobileTopUpInfoTextView = (TextView) view.findViewById(R.id.text_view_mobile_restriction_info);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.recharging_balance));

        mUserMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        setOperatorAndPackageAdapter();

        int mobileNumberType = SharedPrefManager.getMobileNumberType(Constants.MOBILE_TYPE_PREPAID);
        if (mobileNumberType == Constants.MOBILE_TYPE_PREPAID) {
            mPackageEditText.setText(mPackageList.get(Constants.MOBILE_TYPE_PREPAID - 1));
            mSelectedPackageTypeId = Constants.MOBILE_TYPE_PREPAID - 1;
        } else {
            mPackageEditText.setText(mPackageList.get(Constants.MOBILE_TYPE_POSTPAID - 1));
            mSelectedPackageTypeId = Constants.MOBILE_TYPE_POSTPAID - 1;
        }

        mPackageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mPackageSelectorDialog.show();
            }
        });

        mOperatorEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setMobileNumber();
        setOperator(mUserMobileNumber);

        mRechargeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    // For now, we are directly sending the money without going through any send money query
                    // sendMoneyQuery();
                    if (verifyUserInputs()) {
                        launchReviewPage();
                    }
                } else if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG);
            }
        });

        if (!ProfileInfoCacheManager.isAccountVerified()) {
            mMobileNumberEditText.setEnabledStatus(false);
            mMobileNumberEditText.setFocusableStatus(false);

            mOperatorEditText.setEnabled(false);
            mSelectReceiverButton.setVisibility(View.GONE);
            mAmountEditText.requestFocus();
            mMobileTopUpInfoTextView.setVisibility(View.VISIBLE);

        } else {
            mSelectReceiverButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @ValidateAccess(ServiceIdConstants.GET_CONTACTS)
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            });

            mMobileNumberEditText.requestFocus();
        }
        // Get business rule
        attemptGetBusinessRule(Constants.SERVICE_ID_TOP_UP);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_mobile_topup));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.activity_topup_money_history, menu);

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
                switchToTopupHistoryFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    private void setMobileNumber() {
        mMobileNumberEditText.setCurrentFragmentTag(Constants.TOP_UP);
        mMobileNumberEditText.setCustomTextChangeListener(new ContactsSearchView.CustomTextChangeListener() {
            @Override
            public void onTextChange(String inputText) {
                setOperator(inputText);
            }
        });
        mMobileNumberEditText.setText(mUserMobileNumber);
    }

    private void setOperatorAndPackageAdapter() {

        int[] mIconList = getOperatorIcons();

        mPackageList = Arrays.asList(getResources().getStringArray(R.array.package_type));
        mPackageSelectorDialog = new CustomSelectorDialog(getActivity(), getString(R.string.select_a_package), mPackageList);
        mPackageSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int selectedIndex, String mPackage) {
                mPackageEditText.setText(mPackage);
                mSelectedPackageTypeId = mPackageList.indexOf(mPackage);
            }
        });

        mOperatorList = Arrays.asList(getResources().getStringArray(R.array.mobile_operators));
        mOperatorSelectorDialog = new CustomSelectorDialogWithIcon(getActivity(), getString(R.string.select_an_operator), mOperatorList, mIconList);
        mOperatorSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialogWithIcon.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String mOperator) {
                mOperatorEditText.setText(mOperator);
                mSelectedOperatorTypeId = mOperatorList.indexOf(mOperator);
            }
        });
    }

    private void setOperator(String phoneNumber) {
        phoneNumber = phoneNumber.trim();
        final String[] OPERATOR_PREFIXES = getResources().getStringArray(R.array.operator_prefix);
        for (int i = 0; i < OPERATOR_PREFIXES.length; i++) {
            if (phoneNumber.startsWith("+880" + OPERATOR_PREFIXES[i]) ||
                    phoneNumber.startsWith("0" + OPERATOR_PREFIXES[i]) ||
                    phoneNumber.startsWith("880" + OPERATOR_PREFIXES[i]) ||
                    phoneNumber.startsWith(OPERATOR_PREFIXES[i])) {
                mOperatorEditText.setText(mOperatorList.get(i));
                mSelectedOperatorTypeId = i;
                break;
            } else {
                mOperatorEditText.setText(getString(R.string.invalid_operator));
            }
        }
    }

    private boolean verifyUserInputs() {
        mAmountEditText.setError(null);
        mMobileNumberEditText.setError(null);

        boolean cancel = false;
        View focusView = null;
        String errorMessage = null;

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            //validation check of amount
            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                errorMessage = getString(R.string.please_enter_amount);
                focusView = mAmountEditText;
                cancel = true;
            } else {
                final BigDecimal topUpAmount = new BigDecimal(mAmountEditText.getText().toString());
                if (topUpAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                }
                if (Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {

                    final BigDecimal minimumTopupAmount = TopUpActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumTopupAmount = TopUpActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);

                    errorMessage = InputValidator.isValidAmount(getActivity(), topUpAmount, minimumTopupAmount, maximumTopupAmount);
                }
            }
        } else {
            focusView = mAmountEditText;
            errorMessage = getString(R.string.balance_not_available);
            cancel = true;
        }

        if (errorMessage != null) {
            focusView = mAmountEditText;
            mAmountEditText.setError(errorMessage);
            cancel = true;
        }

        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        if (!mobileNumber.matches(Constants.MOBILE_NUMBER_REGEX)) {
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            focusView = mMobileNumberEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                if (mobileNumber != null)
                    mMobileNumberEditText.setText(mobileNumber);
            }
        } else if (requestCode == MOBILE_TOPUP_REVIEW_REQUEST && resultCode == Activity.RESULT_OK) {
            if (getActivity() != null)
                getActivity().finish();
        }
    }

    private void launchReviewPage() {

        // TODO remove this once gateway problem is fixed. We are doing this now because topup
        // gateway only accepts integer amount
        double amount = Math.floor(Double.parseDouble(mAmountEditText.getText().toString().trim()));
        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        int mobileNumberType;
        if (mSelectedPackageTypeId > 0)
            mobileNumberType = Constants.MOBILE_TYPE_POSTPAID;
        else
            mobileNumberType = Constants.MOBILE_TYPE_PREPAID;
        SharedPrefManager.setMobileNumberType(mobileNumberType);

        int operatorCode = mSelectedOperatorTypeId + 1;
        String countryCode = "+88"; // TODO: For now Bangladesh Only

        Intent intent = new Intent(getActivity(), TopUpReviewActivity.class);
        intent.putExtra(Constants.MOBILE_NUMBER, ContactEngine.formatMobileNumberBD(mobileNumber));
        intent.putExtra(Constants.AMOUNT, amount);
        intent.putExtra(SharedPrefConstants.MOBILE_NUMBER_TYPE, mobileNumberType);
        intent.putExtra(Constants.OPERATOR_CODE, operatorCode);
        intent.putExtra(Constants.COUNTRY_CODE, countryCode);

        startActivityForResult(intent, MOBILE_TOPUP_REVIEW_REQUEST);
    }

    private int[] getOperatorIcons() {
        //Setting the correct image based on Operator
        return new int[]{
                R.drawable.ic_gp2,
                R.drawable.ic_gp2,
                R.drawable.ic_robi2,
                R.drawable.ic_airtel2,
                R.drawable.ic_banglalink2,
                R.drawable.ic_teletalk2,
        };

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

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {

            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {
                    Gson gson = new Gson();

                    BusinessRule[] businessRuleArray = gson.fromJson(result.getJsonString(), BusinessRule[].class);

                    for (BusinessRule rule : businessRuleArray) {
                        if (rule.getRuleID().equals(Constants.SERVICE_RULE_TOP_UP_MAX_AMOUNT_PER_PAYMENT)) {
                            TopUpActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());

                        } else if (rule.getRuleID().equals(Constants.SERVICE_RULE_TOP_UP_MIN_AMOUNT_PER_PAYMENT)) {
                            TopUpActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
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

    public void switchToTopupHistoryFragment() {
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MobileTopupHistoryFragment()).addToBackStack(null).commit();
    }

}
