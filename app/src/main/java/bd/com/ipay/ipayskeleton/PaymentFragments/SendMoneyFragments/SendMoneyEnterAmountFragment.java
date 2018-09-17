package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyConfirmActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.BusinessRule;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.GetBusinessRuleRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleConstants;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SendMoneyEnterAmountFragment extends Fragment implements HttpResponseListener {
    private Button mContinueButton;
    private TextView mNameTextView;
    private ProfileImageView mProfileImageView;
    private TextView mIpayBalanceTextView;
    private TextView mAmountEditText;
    private EditText mDummy;
    private View mParentLayout;
    public ImageView mBackButton;

    private HttpRequestGetAsyncTask mGetBusinessRuleTask = null;

    private Bundle bundle;

    private String imageUrl;
    private String name;
    private String mMobileNumber;

    private String before;
    private String after;
    int l = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_money_enter_amount, container, false);
        attemptGetBusinessRule(ServiceIdConstants.SEND_MONEY);
        SendMoneyActivity.mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(Constants.SEND_MONEY);
        ((SendMoneyActivity) getActivity()).toolbar.setVisibility(View.GONE);
        setUpViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void setUpViews(View view) {
        final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
        numberFormat.setMinimumIntegerDigits(2);
        numberFormat.setMinimumFractionDigits(2);
        before = "00.00";
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mBackButton = (ImageView) view.findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBackButton.setVisibility(View.GONE);
                getActivity().onBackPressed();
            }
        });
        mIpayBalanceTextView = (TextView) view.findViewById(R.id.ipay_balance_text_view);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_image_view);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);
        mAmountEditText = (TextView) view.findViewById(R.id.amount_edit_text);
        mDummy = (EditText) view.findViewById(R.id.amount_dummy_edit_text);
        mDummy.requestFocus();
        mParentLayout = view.findViewById(R.id.parent_layout);
        ((SendMoneyActivity) getActivity()).toolbar.setVisibility(View.GONE);
        mAmountEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDummy.setSelection(mDummy.getText().length());
            }
        });

        mDummy.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                double result = 0;
                if (charSequence != null && charSequence.length() > 0) {
                    result = new Double(charSequence.toString());
                    result /= 100;
                }
                mAmountEditText.setText(numberFormat.format(result));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        if (getArguments() != null) {
            bundle = getArguments();
            name = bundle.getString("name");
            mNameTextView.setText(name);
            imageUrl = bundle.getString("imageUrl");
            mMobileNumber = bundle.getString("number");
            mProfileImageView.setProfilePicture(bundle.getString("imageUrl"), false);
        }
        mIpayBalanceTextView.setText("Tk. " + SharedPrefManager.getUserBalance());
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (verifyUserInputs()) {
                    String amount = Utilities.formatTakaFromString(mAmountEditText.getText().toString());
                    amount = amount.replaceAll("[^\\d.]", "");
                    Intent intent = new Intent(getActivity(), SendMoneyConfirmActivity.class);
                    intent.putExtra("name", mNameTextView.getText().toString());
                    intent.putExtra("imageUrl", imageUrl);
                    intent.putExtra("amount", amount);
                    intent.putExtra("number", mMobileNumber);
                    startActivity(intent);
                }
            }
        });
    }

    private void attemptGetBusinessRule(int serviceID) {
        if (mGetBusinessRuleTask != null) {
            return;
        }
        String mUri = new GetBusinessRuleRequestBuilder(serviceID).getGeneratedUri();
        mGetBusinessRuleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_RULE,
                mUri, getActivity(), this, true);

        mGetBusinessRuleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {
        mDummy.setError(null);
        boolean cancel = false;
        String errorMessage;

        if (!Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        } else if (SendMoneyActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        }

        if (SharedPrefManager.ifContainsUserBalance()) {
            final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());

            if (TextUtils.isEmpty(mAmountEditText.getText())) {
                errorMessage = getString(R.string.please_enter_amount);

            } else if (!InputValidator.isValidDigit(mAmountEditText.getText().toString().trim())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else {
                String amount = mAmountEditText.getText().toString();
                 amount = amount.replaceAll("[^\\d.]", "");
                final BigDecimal sendMoneyAmount = new BigDecimal(amount);
                if (sendMoneyAmount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                } else {
                    final BigDecimal minimumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumSendMoneyAmount = SendMoneyActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);
                    errorMessage = InputValidator.isValidAmount(getActivity(), sendMoneyAmount, minimumSendMoneyAmount, maximumSendMoneyAmount);
                }
            }
        } else {
            errorMessage = getString(R.string.balance_not_available);
        }

        if (errorMessage != null) {
            Snackbar snackbar= Snackbar.make(mParentLayout, errorMessage, Snackbar.LENGTH_LONG);
            View view = snackbar.getView();
            view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            TextView textView = (TextView)view.findViewById(android.support.design.R.id.snackbar_text);
            LinearLayout.LayoutParams layoutParams =new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_cancel_black_24dp, 0, 0, 0);
            textView.setCompoundDrawablePadding(getResources().getDimensionPixelOffset(R.dimen.value4));
            snackbar.show();
            cancel = true;
        }
        if (cancel) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetBusinessRuleTask = null;
            return;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_RULE)) {
            if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                try {

                    BusinessRule[] businessRuleArray = new Gson().fromJson(result.getJsonString(), BusinessRule[].class);

                    if (businessRuleArray != null) {

                        for (BusinessRule rule : businessRuleArray) {
                            if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MAX_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMAX_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_MIN_AMOUNT_PER_PAYMENT)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setMIN_AMOUNT_PER_PAYMENT(rule.getRuleValue());
                            } else if (rule.getRuleID().contains(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_VERIFICATION_REQUIRED)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setVERIFICATION_REQUIRED(rule.getRuleValue());
                            } else if (rule.getRuleID().equals(BusinessRuleConstants.SERVICE_RULE_SEND_MONEY_PIN_REQUIRED)) {
                                SendMoneyActivity.mMandatoryBusinessRules.setPIN_REQUIRED(rule.getRuleValue());
                            }
                        }
                        BusinessRuleCacheManager.setBusinessRules(Constants.SEND_MONEY, SendMoneyActivity.mMandatoryBusinessRules);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
                }

            } else {
                if (getActivity() != null)
                    DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            }
        }
    }
}
