package bd.com.ipay.ipayskeleton.PaymentFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DecimalDigitsInputFilter;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IPayTransactionAmountInputFragment extends Fragment {

    public MandatoryBusinessRules mMandatoryBusinessRules;
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private TextView mAmountTextView;

    private int transactionType;
    private String name;
    private String mobileNumber;
    private String profilePicture;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
            name = getArguments().getString(Constants.NAME);
            mobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);
            profilePicture = getArguments().getString(Constants.PHOTO_URI);
        }
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumIntegerDigits(1);
        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBusinessRuleUpdateBroadcastReceiver, new IntentFilter(Constants.BUSINESS_RULE_UPDATE_BROADCAST));
        mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(BusinessRuleCacheManager.getTag(transactionType));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ipay_transaction_amount_input, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAmountTextView = view.findViewById(R.id.amount_text_view);

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        final TextView transactionDescriptionTextView = view.findViewById(R.id.transaction_description_text_view);
        final TextView nameTextView = view.findViewById(R.id.name_text_view);
        final RoundedImageView profileImageView = view.findViewById(R.id.profile_image_view);
        final EditText amountDummyEditText = view.findViewById(R.id.amount_dummy_edit_text);
        final TextView ipayBalanceTextView = view.findViewById(R.id.ipay_balance_text_view);
        final Button continueButton = view.findViewById(R.id.continue_button);
        final View balanceInfoLayout = view.findViewById(R.id.balance_info_layout);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            getActivity().setTitle(R.string.empty_string);
        }

        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                transactionDescriptionTextView.setText(R.string.request_money_from);
                balanceInfoLayout.setVisibility(View.GONE);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                transactionDescriptionTextView.setText(R.string.send_money_to);
                balanceInfoLayout.setVisibility(View.VISIBLE);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                transactionDescriptionTextView.setText(R.string.empty_string);
                break;
        }
        nameTextView.setText(name);
        Glide.with(this)
                .load(profilePicture)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .transform(new CircleTransform(getContext()))
                .into(profileImageView);
        ipayBalanceTextView.setText(getString(R.string.balance_holder, numberFormat.format(Double.valueOf(SharedPrefManager.getUserBalance()))));

        amountDummyEditText.setFilters(new InputFilter[]{new DecimalDigitsInputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (source != null) {
                    try {
                        String formattedSource = source.subSequence(start, end).toString();

                        String destPrefix = dest.subSequence(0, dstart).toString();

                        String destSuffix = dest.subSequence(dend, dest.length()).toString();

                        String resultString = destPrefix + formattedSource + destSuffix;

                        resultString = resultString.replace(",", ".");

                        double result = Double.valueOf(resultString);
                        if (result > 999999.99)
                            return "";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return super.filter(source, start, end, dest, dstart, dend);
            }
        }});
        amountDummyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountDummyEditText.setSelection(amountDummyEditText.getText().length());
            }
        });
        amountDummyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                double result = 0;
                String addSuffix = "";
                final String resultString;
                if (charSequence != null)
                    resultString = charSequence.toString();
                else
                    resultString = null;
                if (resultString != null && resultString.length() > 0) {
                    if (resultString.matches("[0]+")) {
                        amountDummyEditText.setText("");
                    }

                    if (resultString.charAt(0) != '.' || resultString.length() > 1)
                        result = Double.valueOf(resultString);
                    if (resultString.endsWith(".") || resultString.endsWith(".0") || resultString.endsWith(".00"))
                        addSuffix = resultString.substring(resultString.indexOf('.'), resultString.length());
                    else if (resultString.matches("[0-9]*.[1-9]0"))
                        addSuffix = "0";
                }
                mAmountTextView.setText(String.format("%s%s", numberFormat.format(result), addSuffix));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isValidInputs()) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                    bundle.putString(Constants.NAME, name);
                    bundle.putString(Constants.MOBILE_NUMBER, mobileNumber);
                    bundle.putString(Constants.PHOTO_URI, profilePicture);
                    final BigDecimal amount = new BigDecimal(mAmountTextView.getText().toString().replaceAll("[^\\d.]", ""));
                    bundle.putSerializable(Constants.AMOUNT, amount);
                    if (getActivity() instanceof IPayTransactionActionActivity) {
                        ((IPayTransactionActionActivity) getActivity()).switchToTransactionConfirmationFragment(bundle);
                    }
                }
            }
        });

        if (getActivity() != null) {
            amountDummyEditText.requestFocus();
            Utilities.showKeyboard(getActivity(), amountDummyEditText);
        }
    }

    private boolean isValidInputs() {
        if (!Utilities.isValueAvailable(mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                || !Utilities.isValueAvailable(mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
            DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
            return false;
        } else if (mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
            DialogUtils.showDialogVerificationRequired(getActivity());
            return false;
        }

        final String errorMessage;
        if (SharedPrefManager.ifContainsUserBalance()) {
            if (TextUtils.isEmpty(mAmountTextView.getText())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else if (!InputValidator.isValidDigit(mAmountTextView.getText().toString().trim())) {
                errorMessage = getString(R.string.please_enter_amount);
            } else {
                final BigDecimal amount = new BigDecimal(mAmountTextView.getText().toString().replaceAll("[^\\d.]", ""));
                final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());
                if (transactionType == IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY && amount.compareTo(balance) > 0) {
                    errorMessage = getString(R.string.insufficient_balance);
                } else {
                    final BigDecimal minimumAmount = mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT();
                    final BigDecimal maximumAmount;
                    if (transactionType == IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY) {
                        maximumAmount = mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT().min(balance);
                    } else {
                        maximumAmount = mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT();
                    }
                    errorMessage = InputValidator.isValidAmount(getActivity(), amount, minimumAmount, maximumAmount);
                }
            }
        } else {
            errorMessage = getString(R.string.balance_not_available);
        }
        if (errorMessage != null) {
            if (getActivity() != null && getView() != null) {
                Snackbar snackbar = Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_LONG);
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
                ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
                layoutParams.height = getResources().getDimensionPixelSize(R.dimen.value50);
                snackbarView.setLayoutParams(layoutParams);
                TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(ActivityCompat.getColor(getActivity(), android.R.color.white));
                snackbar.show();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBusinessRuleUpdateBroadcastReceiver);
    }

    private final BroadcastReceiver mBusinessRuleUpdateBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(BusinessRuleCacheManager.SERVICE_ID_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID) == transactionType)
                mMandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(BusinessRuleCacheManager.getTag(transactionType));
        }
    };
}
