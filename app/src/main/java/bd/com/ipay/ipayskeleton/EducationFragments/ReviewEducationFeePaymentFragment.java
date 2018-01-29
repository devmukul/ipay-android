package bd.com.ipay.ipayskeleton.EducationFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;
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

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.EducationPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.PinInputDialogBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.EducationInvoice;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.InvoicePayableAccountRelation;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.MakeEducationPaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.MakeEducationPaymentResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Education.PayableItem;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ReviewEducationFeePaymentFragment extends ReviewFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mEducationPaymentTask = null;
    private MakeEducationPaymentResponse mPaymentResponse;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private ProgressDialog mProgressDialog;

    private String mError_message;

    private TextView mInstituteNameView;
    private TextView mSessionNameView;
    private TextView mDiscountView;
    private TextView mNetPayableView;
    private TextView mVatView;
    private EditText mWriteANoteEditText;
    private TextInputLayout mWriteANoteLayout;
    private EditText mDiscountEditText;
    private TextView mAmountView;
    private Button mPaymentButton;

    private BigDecimal mAmount = new BigDecimal(0);
    private BigDecimal mNetPayableAmount = new BigDecimal(0);
    private BigDecimal mDiscount = new BigDecimal(0);
    private BigDecimal mVat = new BigDecimal(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_education_payment_review, container, false);

        mInstituteNameView = (TextView) v.findViewById(R.id.textview_institute_name);
        mSessionNameView = (TextView) v.findViewById(R.id.textview_session_name);

        mAmountView = (TextView) v.findViewById(R.id.textview_amount);
        mDiscountView = (TextView) v.findViewById(R.id.textview_discount);
        mVatView = (TextView) v.findViewById(R.id.textview_vat);
        mNetPayableView = (TextView) v.findViewById(R.id.textview_net_payable);

        mDiscountEditText = (EditText) v.findViewById(R.id.discount);
        mWriteANoteEditText = (EditText) v.findViewById(R.id.description);
        mWriteANoteLayout = (TextInputLayout) v.findViewById(R.id.write_a_note_layout);
        mPaymentButton = (Button) v.findViewById(R.id.button_payment);

        mProgressDialog = new ProgressDialog(getActivity());

        setTransactionDetails();
        setTextWatcherOnDiscountEditText();

        mPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT())
                        && Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())) {
                    mError_message = InputValidator.isValidAmount(getActivity(), mAmount,
                            PaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT(),
                            PaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT());

                    if (mError_message == null) {
                        if (validateInputs())
                            attemptPaymentWithPinCheck();

                    } else {
                        showErrorDialog();
                    }
                } else {
                    if (validateInputs())
                        attemptPaymentWithPinCheck();
                }
            }
        });

        // Check if Min or max amount is available
        if (!Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMAX_AMOUNT_PER_PAYMENT())
                && !Utilities.isValueAvailable(PaymentActivity.mMandatoryBusinessRules.getMIN_AMOUNT_PER_PAYMENT()))
            attemptGetBusinessRuleWithServiceCharge(Constants.SERVICE_ID_MAKE_PAYMENT);
        else
            attemptGetServiceCharge();
        return v;
    }

    private void calculateNetPayable() {
        mNetPayableAmount = mAmount.subtract(mDiscount);
        mNetPayableAmount = mNetPayableAmount.add(mVat);
        mNetPayableView.setText(mNetPayableAmount + " " + getString(R.string.bdt));
    }

    private void setTransactionDetails() {
        mInstituteNameView.setText(EducationPaymentActivity.institutionName);
        mSessionNameView.setText(EducationPaymentActivity.sessionName);

        for (PayableItem mPayableItem : EducationPaymentActivity.mMyPayableItems) {
            mAmount = mAmount.add(mPayableItem.getInstituteFee());
        }

        mVat = Utilities.bigDecimalPercentage(mNetPayableAmount, new BigDecimal(EducationPaymentActivity.selectedInstitution.getVat()));
        mVatView.setText(EducationPaymentActivity.selectedInstitution.getVat() + "%");
        mAmountView.setText(mAmount + " " + getString(R.string.bdt));
        calculateNetPayable();
    }

    private void setTextWatcherOnDiscountEditText() {
        TextWatcher discountTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO: Fix this garish code
                if (s.toString().equals("")) {
                    mDiscountView.setText(R.string.zero_value);
                    mDiscount = new BigDecimal(0);
                    calculateNetPayable();

                } else {
                    if (Double.parseDouble(s.toString()) == 0) {
                        mDiscountView.setText(R.string.zero_value);
                        mDiscount = new BigDecimal(0);
                        calculateNetPayable();
                    } else {
                        if (mAmount.compareTo(BigDecimal.valueOf(Double.parseDouble(s.toString()))) < 0) {
                            mDiscountView.setText(R.string.zero_value);
                            mDiscount = new BigDecimal(0);
                            calculateNetPayable();

                        } else {
                            mDiscountView.setText(s.toString() + " " + getString(R.string.bdt));
                            mDiscount = new BigDecimal(Double.parseDouble(s.toString()));
                            calculateNetPayable();
                        }
                    }
                }
            }
        };

        mDiscountEditText.addTextChangedListener(discountTextWatcher);
    }

    private boolean validateInputs() {
        mWriteANoteLayout.setError(null);

        if (mWriteANoteEditText.getText().toString().trim().length() == 0) {
            mWriteANoteLayout.setError(getString(R.string.please_write_note));
            mWriteANoteEditText.requestFocus();
            return false;
        }

        return true;
    }

    private void attemptPaymentWithPinCheck() {
        if (PaymentActivity.mMandatoryBusinessRules.IS_PIN_REQUIRED()) {
            final PinInputDialogBuilder pinInputDialogBuilder = new PinInputDialogBuilder(getActivity());

            pinInputDialogBuilder.onSubmit(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    attemptEducationalPayment(pinInputDialogBuilder.getPin());
                }
            });
            pinInputDialogBuilder.build().show();
        } else {
            attemptEducationalPayment(null);
        }
    }

    private void attemptEducationalPayment(String pin) {
        if (mEducationPaymentTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_text_payment));
        mProgressDialog.show();
        EducationInvoice mEducationInvoice = prepareEducationInvoice();
        InvoicePayableAccountRelation[] mInvoicePayableAccountRelations = prepareInvoicePayableAccountRelationArray();
        String mDescription = mWriteANoteEditText.getText().toString().trim();
        if (mDescription.length() == 0) mDescription = null;

        MakeEducationPaymentRequest makeEducationPaymentRequest = new MakeEducationPaymentRequest(mDescription, pin, mEducationInvoice, mInvoicePayableAccountRelations);
        Gson gson = new Gson();
        String json = gson.toJson(makeEducationPaymentRequest);
        mEducationPaymentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_MAKE_PAYMENT_EDUCATION,
                Constants.BASE_URL_EDU + Constants.URL_MAKE_PAYMENT_EDUCATION, json, getActivity());
        mEducationPaymentTask.mHttpResponseListener = this;
        mEducationPaymentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private EducationInvoice prepareEducationInvoice() {
        EducationInvoice mEducationInvoice = new EducationInvoice();
        mEducationInvoice.setUpdateTime(null);
        mEducationInvoice.setCreationTime(null);
        mEducationInvoice.setSessionId(EducationPaymentActivity.selectedSession.getId());
        mEducationInvoice.setDepartmentId(EducationPaymentActivity.selectedStudent.getDepartmentId());
        mEducationInvoice.setEventParticipantId(EducationPaymentActivity.selectedStudent.getId());
        mEducationInvoice.setDiscount(mDiscount);
        mEducationInvoice.setVat(mVat);
        mEducationInvoice.setInstituteId(EducationPaymentActivity.selectedInstitution.getId());
        mEducationInvoice.setTotalFee(mNetPayableAmount);

        return mEducationInvoice;
    }

    private InvoicePayableAccountRelation[] prepareInvoicePayableAccountRelationArray() {

        ArrayList<InvoicePayableAccountRelation> mInvoicePayableAccountRelations = new ArrayList<>();
        for (PayableItem payableItem : EducationPaymentActivity.mMyPayableItems) {
            InvoicePayableAccountRelation mInvoicePayableAccountRelation = new InvoicePayableAccountRelation();
            mInvoicePayableAccountRelation.setFee(payableItem.getInstituteFee().doubleValue());
            mInvoicePayableAccountRelation.setPayableAccountHeadId(payableItem.getPayableAccountHead().getId());
            mInvoicePayableAccountRelations.add(mInvoicePayableAccountRelation);
        }

        InvoicePayableAccountRelation[] mInvoicePayableAccountRelationArray = new InvoicePayableAccountRelation[mInvoicePayableAccountRelations.size()];
        return mInvoicePayableAccountRelations.toArray(mInvoicePayableAccountRelationArray);
    }

    private void showErrorDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage(mError_message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public int getServiceID() {
        return Constants.SERVICE_ID_MAKE_PAYMENT;
    }

    @Override
    public BigDecimal getAmount() {
        return mAmount;
    }

    @Override
    public void onServiceChargeLoadFinished(BigDecimal serviceCharge) {
        mNetPayableView.setText(Utilities.formatTaka(mAmount.subtract(serviceCharge)));
    }

    @Override
    public void onPinLoadFinished(boolean isPinRequired) {
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        super.httpResponseReceiver(result);

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mEducationPaymentTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.payment_failed_due_to_server_down, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_MAKE_PAYMENT_EDUCATION)) {

            try {
                mPaymentResponse = gson.fromJson(result.getJsonString(), MakeEducationPaymentResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mPaymentResponse.getStatusMessage(), Toast.LENGTH_LONG).show();
                    getActivity().setResult(Activity.RESULT_OK);
                    getActivity().finish();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mPaymentResponse.getStatusMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            mProgressDialog.dismiss();
            mEducationPaymentTask = null;
        }
    }
}
