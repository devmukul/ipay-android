package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.CreateInvoiceReviewActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.InvoiceActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateInvoiceFragmentStepTwo extends Fragment {
    private static final int REQUEST_CREATE_INVOICE_REVIEW = 101;
    private EditText mRateEditText;
    private EditText mVatEditText;
    private TextView mTotalTextView;
    private TextView mQuantityTextView;
    private Button buttonCreateInvoice;
    private ProgressDialog mProgressDialog;
    private String mMobileNumber;
    private String mItemName;
    private String mDescription;
    private BigDecimal mQuantity;
    private BigDecimal mRate;
    private BigDecimal mAmount;
    private BigDecimal mVat;
    private BigDecimal mTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_invoice_step_two, container, false);
        mRateEditText = (EditText) v.findViewById(R.id.rate);
        mVatEditText = (EditText) v.findViewById(R.id.vat);
        buttonCreateInvoice = (Button) v.findViewById(R.id.button_request_money);
        mTotalTextView = (TextView) v.findViewById(R.id.total);
        mQuantityTextView = (TextView) v.findViewById(R.id.quantity);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.submitting_request_money));

        Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.INVOICE_RECEIVER_TAG)) {
            mMobileNumber = args.getString(Constants.INVOICE_RECEIVER_TAG);
        }
        if (args != null && args.containsKey(Constants.INVOICE_ITEM_NAME_TAG)) {
            mItemName = args.getString(Constants.INVOICE_ITEM_NAME_TAG);
        }
        if (args != null && args.containsKey(Constants.INVOICE_DESCRIPTION_TAG)) {
            mDescription = args.getString(Constants.INVOICE_DESCRIPTION_TAG);
        }
        if (args != null && args.containsKey(Constants.INVOICE_QUANTITY_TAG)) {
            mQuantity = new BigDecimal(args.getString(Constants.INVOICE_QUANTITY_TAG));
            mQuantityTextView.setText(" * " + mQuantity);
        }

        mRateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() > 0) {
                    try {
                        mRate = new BigDecimal(s.toString().replace(".", "").replace(',', '.'));
                    } catch (Exception e) {
                        mRate = new BigDecimal(0);
                    }
                    if (mRate != null) {
                        mAmount = mQuantity.multiply(mRate);
                        mTotal = mAmount;
                    }
                    if (mVat != null) {
                        mTotal = mAmount.add(mTotal.multiply(mVat.divide(new BigDecimal(100))));
                    }
                    mTotalTextView.setText(Utilities.formatTaka(mTotal));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    try {
                        mRate = new BigDecimal(s.toString().replace(".", "").replace(',', '.'));
                    } catch (Exception e) {
                        mRate = new BigDecimal(0);
                    }
                    if (mRate != null) {
                        mAmount = mQuantity.multiply(mRate);
                        mTotal = mAmount;
                    }
                    if (mVat != null) {
                        mTotal = mAmount.add(mTotal.multiply(mVat.divide(new BigDecimal(100))));
                    }
                    mTotalTextView.setText(Utilities.formatTaka(mTotal));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    try {
                        mRate = new BigDecimal(s.toString().replace(".", "").replace(',', '.'));
                    } catch (Exception e) {
                        mRate = new BigDecimal(0);
                    }
                    if (mRate != null) {
                        mAmount = mQuantity.multiply(mRate);
                        mTotal = mAmount;
                    }
                    if (mVat != null) {
                        mTotal = mAmount.add(mTotal.multiply(mVat.divide(new BigDecimal(100))));
                    }
                    mTotalTextView.setText(Utilities.formatTaka(mTotal));
                }
            }
        });

        mVatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().length() > 0) {
                    try {
                        mVat = new BigDecimal(s.toString().replace(".", "").replace(',', '.'));
                    } catch (Exception e) {
                        mVat = new BigDecimal(0);
                    }
                    if (mRate != null) {
                        mAmount = mQuantity.multiply(mRate);
                        mTotal = mAmount;
                    }
                    if (mVat != null) {
                        mTotal = mAmount.add(mTotal.multiply(mVat.divide(new BigDecimal(100))));
                    }
                    mTotalTextView.setText(Utilities.formatTaka(mTotal));
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() > 0) {
                    try {
                        mVat = new BigDecimal(s.toString().replace(".", "").replace(',', '.'));
                    } catch (Exception e) {
                        mVat = new BigDecimal(0);
                    }
                    if (mRate != null) {
                        mAmount = mQuantity.multiply(mRate);
                        mTotal = mAmount;
                    }
                    if (mVat != null) {
                        mTotal = mAmount.add(mTotal.multiply(mVat.divide(new BigDecimal(100))));
                    }
                    mTotalTextView.setText(Utilities.formatTaka(mTotal));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    try {
                        mVat = new BigDecimal(s.toString().replace(".", "").replace(',', '.'));
                    } catch (Exception e) {
                        mVat = new BigDecimal(0);
                    }
                    if (mRate != null) {
                        mAmount = mQuantity.multiply(mRate);
                        mTotal = mAmount;
                    }
                    if (mVat != null) {
                        mTotal = mAmount.add(mTotal.multiply(mVat.divide(new BigDecimal(100))));
                    }
                    mTotalTextView.setText(Utilities.formatTaka(mTotal));
                }
            }
        });

        buttonCreateInvoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    if (verifyUserInputs()) launchReviewPage();
                } else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        return v;
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        String rate = mRateEditText.getText().toString();

        // Check for a validation
        if (!(rate.length() > 0 && Double.parseDouble(rate) > 0)) {
            mRateEditText.setError(getString(R.string.please_enter_amount));
            focusView = mRateEditText;
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
        String rate = mRateEditText.getText().toString();
        String vat = mVatEditText.getText().toString();

        Intent intent = new Intent(getActivity(), CreateInvoiceReviewActivity.class);
        intent.putExtra(Constants.INVOICE_ITEM_NAME_TAG, mItemName);
        intent.putExtra(Constants.INVOICE_RECEIVER_TAG, ContactEngine.formatMobileNumberBD(mMobileNumber));
        intent.putExtra(Constants.INVOICE_DESCRIPTION_TAG, mDescription);
        intent.putExtra(Constants.INVOICE_QUANTITY_TAG, mQuantity.toString());
        intent.putExtra(Constants.INVOICE_RATE_TAG, rate);

        if (vat != null) intent.putExtra(Constants.VAT, vat);
        else intent.putExtra(Constants.VAT, "0");
        intent.putExtra(Constants.TOTAL, mTotal+"");

        startActivityForResult(intent, REQUEST_CREATE_INVOICE_REVIEW);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (getActivity() != null)
            ((InvoiceActivity) getActivity()).switchToInvoicesSentFragment();
    }
}

