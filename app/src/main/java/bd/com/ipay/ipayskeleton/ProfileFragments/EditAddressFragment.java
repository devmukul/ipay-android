package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.AddressInputView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.SetUserAddressRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.SetUserAddressResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class EditAddressFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSetUserAddressTask = null;
    private SetUserAddressResponse mSetUserAddressResponse;

    private AddressClass mAddress;
    private AddressClass mPresentAddress;
    private String mAddressType;

    private AddressInputView mAddressInputView;

    private View mSameasPresentAddressCheckboxHolder;
    private CheckBox mSameasPresentAddressCheckbox;

    private Button mSaveButton;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_edit_address, container, false);

        getActivity().setTitle(getString(R.string.edit_address));

        mProgressDialog = new ProgressDialog(getActivity());
        mAddressInputView = (AddressInputView) v.findViewById(R.id.input_address);
        mSameasPresentAddressCheckboxHolder = v.findViewById(R.id.holder_same_as_present_address);
        mSameasPresentAddressCheckbox = (CheckBox) v.findViewById(R.id.checkbox_same_as_present_address);
        mSaveButton = (Button) v.findViewById(R.id.button_save);

        mAddress = (AddressClass) getArguments().getSerializable(Constants.ADDRESS);
        mAddressType = getArguments().getString(Constants.ADDRESS_TYPE);

        if (mAddressType.equals(Constants.ADDRESS_TYPE_PERMANENT)) {
            mSameasPresentAddressCheckboxHolder.setVisibility(View.VISIBLE);
            mPresentAddress = (AddressClass) getArguments().getSerializable(Constants.PRESENT_ADDRESS);
        }

        if (mAddress != null)
            mAddressInputView.setInformation(mAddress);

        mSameasPresentAddressCheckbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSameasPresentAddressCheckbox.isChecked()) {
                    if (mPresentAddress != null)
                        mAddressInputView.setInformation(mPresentAddress);
                }

            }
        });

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAddressInputView.verifyUserInputs()) {
                    mAddress = mAddressInputView.getInformation();
                    setUserAddress();
                }
            }
        });

        return v;
    }

    private void setUserAddress() {
        mProgressDialog.setMessage(getString(R.string.progress_dialog_saving_address));
        mProgressDialog.show();

        SetUserAddressRequest userAddressRequest = new SetUserAddressRequest(mAddressType, mAddress);

        Gson gson = new Gson();
        String addressJson = gson.toJson(userAddressRequest, SetUserAddressRequest.class);
        mSetUserAddressTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_USER_ADDRESS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_SET_USER_ADDRESS_REQUEST, addressJson, getActivity(), this);
        mSetUserAddressTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSetUserAddressTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_USER_ADDRESS_REQUEST)) {

            try {
                mSetUserAddressResponse = gson.fromJson(result.getJsonString(), SetUserAddressResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    Toast.makeText(getActivity(), mSetUserAddressResponse.getMessage(), Toast.LENGTH_LONG).show();
                    if (ProfileInfoCacheManager.isBusinessAccount()) {
                        if (getArguments().getString(Constants.EDIT_ADDRESS_SOURCE).equals("BUSINESS_PRESENT"))
                           ((ProfileActivity) getActivity()).switchToBusinessInfoFragment();
                    } else {
                        ((ProfileActivity) getActivity()).switchToAddressFragment();
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetUserAddressResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_save_failed, Toast.LENGTH_SHORT).show();
            }

            mSetUserAddressTask = null;
            mProgressDialog.dismiss();
        }
    }
}