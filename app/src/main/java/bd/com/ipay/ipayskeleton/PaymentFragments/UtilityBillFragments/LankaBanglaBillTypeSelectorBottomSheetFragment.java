package bd.com.ipay.ipayskeleton.PaymentFragments.UtilityBillFragments;


import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.UtilityBillPaymentActivity;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class LankaBanglaBillTypeSelectorBottomSheetFragment extends BottomSheetDialogFragment {

    private Button mPayBillButton;
    private TextView mCreditAmountTextView;
    private TextView mOtherAmontTextView;
    private TextView mMinAmountTextView;

    private int selectedRadioID;

    private String mMinAmount;
    private String mCreditAmount;
    private String otherAmount;

    private EditText mOtherAmountEditText;

    private RadioGroup mSelectAmountTypeRadioGroup;

    private TextInputLayout mAmountLayout;

    private RadioButton mOtherRadioButton;
    private RadioButton mCreditAmountRadioButton;
    private RadioButton mMinimumAmountRadioButton;

    public PinInputListener pinInputListener;

    private String selectedAmount;
    private String selectedBillType;

    public static LankaBanglaBillTypeSelectorBottomSheetFragment getInstance() {
        return new LankaBanglaBillTypeSelectorBottomSheetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_bottom_sheet_lanka_bangla_card_bill_pay, container, false);
        if (getDialog() != null && getDialog().getWindow() != null)
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPayBillButton = view.findViewById(R.id.pay_bill_button);
        mAmountLayout = (TextInputLayout) view.findViewById(R.id.amount_layout);
        mSelectAmountTypeRadioGroup = (RadioGroup) view.findViewById(R.id.radio_group);
        mOtherRadioButton = (RadioButton) view.findViewById(R.id.other_amount);
        mMinimumAmountRadioButton = (RadioButton) view.findViewById(R.id.minimum_amount);
        mCreditAmountRadioButton = (RadioButton) view.findViewById(R.id.credit_amount);
        mSelectAmountTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.credit_amount) {
                    mAmountLayout.setVisibility(View.GONE);
                    selectedRadioID = R.id.credit_amount;
                } else if (i == R.id.minimum_amount) {
                    mAmountLayout.setVisibility(View.GONE);
                    selectedRadioID = R.id.minimum_amount;
                } else if (i == R.id.other_amount) {
                    mAmountLayout.setVisibility(View.VISIBLE);
                    selectedRadioID = R.id.other_amount;
                }
            }
        });
        mPayBillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedRadioID == R.id.credit_amount) {
                    if (Long.parseLong(mCreditAmount) != 0) {
                        selectedAmount = mCreditAmount;
                        selectedBillType = Constants.credit_balance;
                        attemptBillPayWithPinCheck();
                    } else {
                        selectedBillType = "";
                        selectedAmount = "";
                        Toast.makeText(getContext(), "You can't pay 0 tk", Toast.LENGTH_LONG).show();
                    }
                } else if (selectedRadioID == R.id.minimum_amount) {
                    if (Long.parseLong(mMinAmount) != 0) {
                        selectedAmount = mMinAmount;
                        selectedBillType = Constants.minimun_pay;
                        attemptBillPayWithPinCheck();
                    } else {
                        selectedAmount = "";
                        selectedBillType = "";
                        Toast.makeText(getContext(), "You can't pay 0 tk", Toast.LENGTH_LONG).show();
                    }
                } else if (selectedRadioID == R.id.other_amount) {
                    if (ifUserEligibleToPay()) {
                        selectedBillType = Constants.others;
                        selectedAmount = otherAmount;
                        attemptBillPayWithPinCheck();
                    }
                }
            }
        });
        mOtherAmountEditText = (EditText) view.findViewById(R.id.amount_edit_text);

        getDataFromBundle(getArguments());
    }

    private void attemptBillPayWithPinCheck() {
        if (UtilityBillPaymentActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            new CustomPinCheckerWithInputDialog(getActivity(), new CustomPinCheckerWithInputDialog.PinCheckAndSetListener() {
                @Override
                public void ifPinCheckedAndAdded(String pin) {
                    pinInputListener.onPinInput(pin, selectedAmount, selectedBillType);
                }
            });
        } else {
            pinInputListener.onPinInput(null, selectedAmount, selectedBillType);
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    private void getDataFromBundle(Bundle bundle) {
        mCreditAmount = bundle.getString("creditAmount");
        mMinAmount = bundle.getString("minimumAmount");
        mCreditAmountRadioButton.setText("Credit Amount ( " + mCreditAmount + " TK ) ");
        mMinimumAmountRadioButton.setText("Minimum Amount ( " + mMinAmount + " TK )");
    }

    private boolean ifUserEligibleToPay() {
        Editable amountEditable;
        amountEditable = mOtherAmountEditText.getText();
        if (amountEditable == null) {
            mOtherAmountEditText.setError("Please provide an amount to pay");
            return false;
        } else {
            otherAmount = amountEditable.toString();
            if (otherAmount == null || otherAmount.equals("")) {
                mOtherAmountEditText.setError("Please provide an amount to pay");
                return false;
            } else {
                if (!Utilities.isValueAvailable(UtilityBillPaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        || !Utilities.isValueAvailable(UtilityBillPaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    DialogUtils.showDialogForBusinessRuleNotAvailable(getActivity());
                    return false;
                }

                if (UtilityBillPaymentActivity.mMandatoryBusinessRules.isVERIFICATION_REQUIRED() && !ProfileInfoCacheManager.isAccountVerified()) {
                    DialogUtils.showDialogVerificationRequired(getActivity());
                    return false;
                }
                if (SharedPrefManager.ifContainsUserBalance()) {
                    final BigDecimal balance = new BigDecimal(SharedPrefManager.getUserBalance());
                    final BigDecimal topUpAmount = new BigDecimal(otherAmount);
                    if (topUpAmount.compareTo(balance) > 0) {
                        Toast.makeText(getContext(), getString(R.string.insufficient_balance), Toast.LENGTH_LONG).show();
                        return false;
                    }

                } else {
                    mOtherAmountEditText.setError(getString(R.string.balance_not_available));
                    return false;
                }
            }
        }
        return true;
    }

    public interface PinInputListener {
        public void onPinInput(String pin, String amount, String selectedBillType);
    }
}