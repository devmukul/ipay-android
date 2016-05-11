package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

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
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Customview.AddressInputView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.SetUserAddressRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.SetUserAddressResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class FragmentEditAddress extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSetUserAddressTask = null;
    private SetUserAddressResponse mSetUserAddressResponse;

    private AddressClass mAddress;
    private String mAddressType;

    private AddressInputView mAddressInputView;
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

        mProgressDialog = new ProgressDialog(getActivity());
        mAddressInputView = (AddressInputView) v.findViewById(R.id.input_address);
        mSaveButton = (Button) v.findViewById(R.id.button_save);

        mAddress = (AddressClass) getArguments().getSerializable(Constants.ADDRESS);
        mAddressType = getArguments().getString(Constants.ADDRESS_TYPE);

        if (mAddress != null)
            mAddressInputView.setInformation(mAddress);

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
                Constants.BASE_URL + Constants.URL_SET_USER_ADDRESS_REQUEST, addressJson, getActivity(), this);
        mSetUserAddressTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mSetUserAddressTask = null;
            Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SET_USER_ADDRESS_REQUEST)) {

            try {
                mSetUserAddressResponse = gson.fromJson(resultList.get(2), SetUserAddressResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    Toast.makeText(getActivity(), mSetUserAddressResponse.getMessage(), Toast.LENGTH_LONG).show();
                    ((ProfileActivity) getActivity()).switchToAddressFragment();

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