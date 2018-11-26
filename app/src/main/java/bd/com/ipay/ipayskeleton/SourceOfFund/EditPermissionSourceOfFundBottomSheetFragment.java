package bd.com.ipay.ipayskeleton.SourceOfFund;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.GenericResponseWithMessageOnly;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.AcceptOrRejectBeneficiaryRequest;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.Beneficiary;
import bd.com.ipay.ipayskeleton.SourceOfFund.models.UpdateMonthlyLimitRequest;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EditPermissionSourceOfFundBottomSheetFragment extends BottomSheetDialogFragment implements HttpResponseListener {
    private Beneficiary beneficiary;
    private HttpRequestPutAsyncTask updateBeneficiaryAsyncTask;
    private HttpRequestPutAsyncTask updateMonthlyLimitAsyncTask;

    private EditText pinEditText;
    private EditText amountEditText;

    private IpayProgressDialog ipayProgressDialog;
    public BeneficiaryUpdateListener beneficiaryUpdateListener;

    private String action;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.view_bottom_sheet_source_of_fund_edit_permission, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        amountEditText = view.findViewById(R.id.amount);
        pinEditText = view.findViewById(R.id.pin);
        TextView permissionDetailsTextView = view.findViewById(R.id.permission_details);
        Bundle bundle = getArguments();
        beneficiary = (Beneficiary) bundle.getSerializable(Constants.BENEFICIARY);
        action = bundle.getString(Constants.TO_DO);
        String name = beneficiary.getUser().getName();
        String permissionDetailsText = permissionDetailsTextView.getText().toString();
        permissionDetailsText = permissionDetailsText.replace("Sourav Saha", name);
        permissionDetailsTextView.setText(permissionDetailsText);
        Button doneButton = view.findViewById(R.id.done);
        ipayProgressDialog = new IpayProgressDialog(getContext());
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInput()) {
                    if (action.equals(Constants.EDIT_AMOUNT)) {
                        attemptChangeMonthlyLimit();
                    } else {
                        attemptUpdateBeneficiaryStatus(beneficiary.getId());
                    }
                    Utilities.hideKeyboard(getActivity());
                }
            }
        });

    }

    private void attemptChangeMonthlyLimit() {
        if (updateMonthlyLimitAsyncTask != null) {
            return;
        } else {
            ipayProgressDialog.setMessage("Please wait. . .");

            UpdateMonthlyLimitRequest updateMonthlyLimitRequest = new UpdateMonthlyLimitRequest
                    (Long.parseLong(amountEditText.getText().toString()), pinEditText.getText().toString());
            String jsonString = new Gson().toJson(updateMonthlyLimitRequest);
            String mUri = Constants.BASE_URL_MM + Constants.URI_CHANGE_MONTHLY_LIMIT +
                    Long.toString(beneficiary.getId()) + "/" + "monthly-credit-limit";
            updateMonthlyLimitAsyncTask = new HttpRequestPutAsyncTask
                    (Constants.COMMAND_CHANGE_MONTLY_LIMIT, mUri,
                            jsonString, getContext(), this, false);
            updateMonthlyLimitAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ipayProgressDialog.show();
        }
    }

    public void setBeneficiaryUpdateListener(BeneficiaryUpdateListener beneficiaryUpdateListener) {
        this.beneficiaryUpdateListener = beneficiaryUpdateListener;
    }

    public boolean verifyUserInput() {
        Editable pinEditable, amountEditable;
        pinEditable = pinEditText.getText();
        amountEditable = amountEditText.getText();
        if (amountEditable == null) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_LONG).show();
            return false;
        } else if (amountEditable.toString() == null || amountEditable.toString().equals("")) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_LONG).show();
            return false;
        }
        if (pinEditable == null) {
            Toast.makeText(getContext(), "Please enter your pin", Toast.LENGTH_LONG).show();
            return false;
        } else if (pinEditable.toString() == null || pinEditable.toString().equals("")) {
            Toast.makeText(getContext(), "Please enter your pin", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void attemptUpdateBeneficiaryStatus(long id) {
        if (updateBeneficiaryAsyncTask != null) {
            return;
        } else {
            AcceptOrRejectBeneficiaryRequest acceptOrRejectBeneficiaryRequest = new AcceptOrRejectBeneficiaryRequest
                    (Long.parseLong(amountEditText.getText().toString()), pinEditText.getText().toString(), "APPROVED");
            updateBeneficiaryAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_ACCEPT_OR_REJECT_BENEFICIARY,
                    Constants.BASE_URL_MM +
                            Constants.URL_ACCEPT_OR_REJECT_SOURCE_OF_FUND + id,
                    new Gson().toJson(acceptOrRejectBeneficiaryRequest), getContext(), this, false);
            updateBeneficiaryAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            ipayProgressDialog.setMessage("Please wait . . .");
            ipayProgressDialog.show();
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            ipayProgressDialog.dismiss();
            updateBeneficiaryAsyncTask = null;
            updateMonthlyLimitAsyncTask = null;
            return;
        } else {
            ipayProgressDialog.dismiss();
            if (result.getApiCommand().equals(Constants.COMMAND_ACCEPT_OR_REJECT_BENEFICIARY)) {
                try {
                    GenericResponseWithMessageOnly genericResponseWithMessageOnly =
                            new Gson().fromJson(result.getJsonString(), GenericResponseWithMessageOnly.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        beneficiaryUpdateListener.onBeneficiaryStatusUpdated();
                        Toast.makeText(getContext(), genericResponseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), genericResponseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
                }
                updateBeneficiaryAsyncTask = null;
            } else if (result.getApiCommand().equals(Constants.COMMAND_CHANGE_MONTLY_LIMIT)) {
                try {
                    GenericResponseWithMessageOnly genericResponseWithMessageOnly =
                            new Gson().fromJson(result.getJsonString(), GenericResponseWithMessageOnly.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        beneficiaryUpdateListener.onBeneficiaryStatusUpdated();
                        Toast.makeText(getContext(), genericResponseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getContext(), genericResponseWithMessageOnly.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getContext(), getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
                }
                updateMonthlyLimitAsyncTask = null;
            }
        }
    }

    public interface BeneficiaryUpdateListener {
        public void onBeneficiaryStatusUpdated();
    }
}