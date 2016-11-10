package bd.com.ipay.ipayskeleton.LoginAndSignUpFragments.BusinessSignUpFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.SignupOrLoginActivity;
import bd.com.ipay.ipayskeleton.Api.GetBusinessTypesAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.AddressInputSignUpView;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class SignupBusinessStepTwoFragment extends Fragment {

    private EditText mBusinessType;

    private EditText mBusinessNameView;
    private Button mNextButton;

    private ResourceSelectorDialog<BusinessType> businessTypeResourceSelectorDialog;

    private AddressInputSignUpView mBusinessAddressView;

    private ProgressDialog mProgressDialog;
    private int mSelectedBusinessTypeId = -1;
    public int mSelectedThanaId = -1;
    public int mSelectedDistrictId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_signup_business_step_two, container, false);
        mProgressDialog = new ProgressDialog(getActivity());

        mBusinessNameView = (EditText) v.findViewById(R.id.business_name);
        mBusinessType = (EditText) v.findViewById(R.id.business_type);

        mNextButton = (Button) v.findViewById(R.id.business_again_next_button);

        mBusinessAddressView = (AddressInputSignUpView) v.findViewById(R.id.business_address);

        mBusinessAddressView.setHintAddressInput(getString(R.string.business_address_line_1),
                getString(R.string.business_address_line_2));

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isConnectionAvailable(getActivity())) attemptGoNextPage();
                else if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_LONG).show();
            }
        });

        //business type dialog
        mBusinessType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                businessTypeResourceSelectorDialog.show();
            }
        });


        // Asynchronously load business types into the spinner
        GetBusinessTypesAsyncTask getBusinessTypesAsyncTask =
                new GetBusinessTypesAsyncTask(getActivity(), businessTypeLoadListener);
        mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_fetching_business_types));
        mProgressDialog.show();
        getBusinessTypesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSelectedDistrictId = mBusinessAddressView.mSelectedDistrictId;
        mSelectedThanaId = mBusinessAddressView.mSelectedThanaId;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_signup_business_page);
        mBusinessAddressView.mSelectedDistrictId = mSelectedDistrictId;
        mBusinessAddressView.mSelectedThanaId = mSelectedThanaId;
    }

    private void attemptGoNextPage() {
        // Store values at the time of the login attempt.
        SignupOrLoginActivity.mBusinessName = mBusinessNameView.getText().toString().trim();
        SignupOrLoginActivity.mAccountType = Constants.BUSINESS_ACCOUNT_TYPE;
        SignupOrLoginActivity.mTypeofBusiness = mSelectedBusinessTypeId;

        boolean cancel = false;
        View focusView = null;

        if (mBusinessNameView.getText().toString().trim().length() == 0) {
            mBusinessNameView.setError(getString(R.string.invalid_business_name));
            focusView = mBusinessNameView;
            cancel = true;

        } else if (!InputValidator.isValidName(mBusinessNameView.getText().toString().trim())) {
            mBusinessNameView.setError(getString(R.string.please_enter_valid_name));
            focusView = mBusinessNameView;
            cancel = true;

        } else if (!mBusinessAddressView.verifyUserInputs()) {
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            if (focusView != null) focusView.requestFocus();
        } else {
            SignupOrLoginActivity.mAddressBusiness = mBusinessAddressView.getInformation();
            ((SignupOrLoginActivity) getActivity()).switchToBusinessStepThreeFragment();
        }
    }

    private final GetBusinessTypesAsyncTask.BusinessTypeLoadListener businessTypeLoadListener =
            new GetBusinessTypesAsyncTask.BusinessTypeLoadListener() {
                @Override
                public void onLoadSuccess(List<BusinessType> businessTypes) {
                    //set adapter to load the types
                    setTypeAdapter(businessTypes);
                    mProgressDialog.dismiss();
                }

                @Override
                public void onLoadFailed() {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                        getActivity().finish();
                        mProgressDialog.dismiss();
                    }
                }
            };


    private void setTypeAdapter(List<BusinessType> businessTypeList) {
        businessTypeResourceSelectorDialog = new ResourceSelectorDialog<>(getActivity(), getString(R.string.business_type), businessTypeList, mSelectedBusinessTypeId);
        businessTypeResourceSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mBusinessType.setError(null);
                mBusinessType.setText(name);
                mSelectedBusinessTypeId = id;
            }
        });
    }
}


