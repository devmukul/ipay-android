package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.SetBusinessInformationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.SetBusinessInformationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EditBusinessInformationFragment extends Fragment implements HttpResponseListener {

    private EditText mBusinessNameEditText;
    private EditText mBusinessMobileNumberEditText;
    private EditText mBusinessEmailEditText;
    private EditText mBusinessTypeEditText;
    private Button mInfoSaveButton;

    private String mBusinessName;
    private String mBusinessMobileNumber;
    private String mBusinessEmail;
    private int mBusinessTypeId;
    private ArrayList<BusinessType> mBusinessTypes;

    private ResourceSelectorDialog<BusinessType> businessTypeResourceSelectorDialog;

    private HttpRequestPostAsyncTask mSetBusinessInformationRequestAsyncTask;
    private SetBusinessInformationResponse mSetBusinessInformationResponse;

    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_business_information, container, false);
        getActivity().setTitle(R.string.edit_business_information);

        mBusinessNameEditText = (EditText) v.findViewById(R.id.business_name);
        mBusinessMobileNumberEditText = (EditText) v.findViewById(R.id.business_mobile_number);
        mBusinessEmailEditText = (EditText) v.findViewById(R.id.business_email);
        mBusinessTypeEditText = (EditText) v.findViewById(R.id.business_type);
        mInfoSaveButton = (Button) v.findViewById(R.id.button_save);

        mBusinessName = getArguments().getString(Constants.BUSINESS_NAME);
        mBusinessMobileNumber = getArguments().getString(Constants.BUSINESS_MOBILE_NUMBER);
        mBusinessEmail = getArguments().getString(Constants.BUSINESS_EMAIL);
        mBusinessTypeId = getArguments().getInt(Constants.BUSINESS_TYPE);
        mBusinessTypes = getArguments().getParcelableArrayList(Constants.BUSINESS_TYPE_LIST);

        businessTypeResourceSelectorDialog = new ResourceSelectorDialog<>(getActivity(), getString(R.string.business_type), mBusinessTypes, mBusinessTypeId);
        businessTypeResourceSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mBusinessTypeEditText.setError(null);
                mBusinessTypeEditText.setText(name);
                mBusinessTypeId = id;
            }
        });

        mBusinessTypeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessTypeResourceSelectorDialog.show();
            }
        });

        mBusinessNameEditText.setText(mBusinessName);
        mBusinessMobileNumberEditText.setText(mBusinessMobileNumber);
        mBusinessEmailEditText.setText(mBusinessEmail);
        for (BusinessType businessType : mBusinessTypes) {
            if (businessType.getId() == mBusinessTypeId) {
                mBusinessTypeEditText.setText(businessType.getName());
                break;
            }
        }

        mProgressDialog = new ProgressDialog(getActivity());

        mInfoSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    Utilities.hideKeyboard(getActivity());
                    attemptSaveBusinessInformation();
                }
            }
        });

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void attemptSaveBusinessInformation() {
        if (mSetBusinessInformationRequestAsyncTask != null)
            return;

        mProgressDialog.setMessage(getString(R.string.saving_business_information));
        mProgressDialog.dismiss();

        Gson gson = new Gson();
        SetBusinessInformationRequest setBusinessInformationRequest = new SetBusinessInformationRequest(
                mBusinessName, mBusinessTypeId, mBusinessEmail, mBusinessMobileNumber);
        String json = gson.toJson(setBusinessInformationRequest);

        mSetBusinessInformationRequestAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_BUSINESS_INFORMATION,
                Constants.BASE_URL_MM + Constants.URL_SET_BUSINESS_INFORMATION, json, getActivity(), this);
        mSetBusinessInformationRequestAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        mBusinessName = mBusinessNameEditText.getText().toString();
        mBusinessMobileNumber = ContactEngine.formatMobileNumberBD(mBusinessMobileNumberEditText.getText().toString());
        mBusinessEmail = mBusinessEmailEditText.getText().toString();
        /** mBusinessTypeId has already been selected when the user picked an item from the dialog **/

        if (mBusinessName.isEmpty()) {
            mBusinessNameEditText.setError(getString(R.string.error_invalid_name));
            cancel = true;
            focusView = mBusinessNameEditText;
        }

        if (!ContactEngine.isValidNumber(mBusinessMobileNumber)) {
            mBusinessMobileNumberEditText.setError(getString(R.string.error_invalid_mobile_number));
            cancel = true;
            focusView = mBusinessMobileNumberEditText;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mSetBusinessInformationRequestAsyncTask = null;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_BUSINESS_INFORMATION)) {
            try {
                mSetBusinessInformationResponse = gson.fromJson(result.getJsonString(), SetBusinessInformationResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mSetBusinessInformationResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((ProfileActivity) getActivity()).switchToBusinessInfoFragment();
                    }
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetBusinessInformationResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.business_information_saving_failed, Toast.LENGTH_LONG).show();
            }

            mSetBusinessInformationRequestAsyncTask = null;
        }
    }
}
