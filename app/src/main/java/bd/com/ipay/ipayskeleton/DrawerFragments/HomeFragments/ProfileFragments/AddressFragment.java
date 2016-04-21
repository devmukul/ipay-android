package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.GetUserAddressResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddressFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetUserAddressTask = null;
    private GetUserAddressResponse mGetUserAddressResponse = null;

    private ProgressDialog mProgressDialog;

    private AddressClass mPresentAddress;
    private AddressClass mPermanentAddress;
    private AddressClass mOfficeAddress;

    private TextView mPresentAddressView;
    private TextView mPermanentAddressView;
    private TextView mOfficeAddressView;

    private View mPresentAddressHolder;
    private View mPermanentAddressHolder;
    private View mOfficeAddressHolder;

    private Button mPresentAddressEditButton;
    private Button mPermanentAddressEditButton;
    private Button mOfficeAddressEditButton;

    private Button mPresentAddressAddButton;
    private Button mPermanentAddressAddButton;
    private Button mOfficeAddressAddButton;

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
        View v = inflater.inflate(R.layout.fragment_profile_address, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        mPresentAddressView = (TextView) v.findViewById(R.id.textview_present_address);
        mPermanentAddressView = (TextView) v.findViewById(R.id.textview_permanent_address);
        mOfficeAddressView = (TextView) v.findViewById(R.id.textview_office_address);

        mPresentAddressEditButton = (Button) v.findViewById(R.id.button_edit_present_address);
        mPermanentAddressEditButton = (Button) v.findViewById(R.id.button_edit_permanent_address);
        mOfficeAddressEditButton = (Button) v.findViewById(R.id.button_edit_office_address);

        mPresentAddressAddButton = (Button) v.findViewById(R.id.button_add_present_address);
        mPermanentAddressAddButton = (Button) v.findViewById(R.id.button_add_permanent_address);
        mOfficeAddressAddButton = (Button) v.findViewById(R.id.button_add_office_address);

        mPresentAddressHolder = v.findViewById(R.id.present_address_holder);
        mPermanentAddressHolder = v.findViewById(R.id.permanent_address_holder);
        mOfficeAddressHolder = v.findViewById(R.id.office_address_holder);

        getUserAddress();

        return v;
    }

    private void loadAddresses() {
        if (mPresentAddress == null) {
            mPresentAddressHolder.setVisibility(View.GONE);
            mPresentAddressAddButton.setVisibility(View.VISIBLE);
        } else {
            mPresentAddressHolder.setVisibility(View.VISIBLE);
            mPresentAddressAddButton.setVisibility(View.GONE);
            mPresentAddressView.setText(mPresentAddress.toString());
        }

        if (mPermanentAddress == null) {
            mPermanentAddressHolder.setVisibility(View.GONE);
            mPermanentAddressAddButton.setVisibility(View.VISIBLE);
        } else {
            mPermanentAddressHolder.setVisibility(View.VISIBLE);
            mPermanentAddressAddButton.setVisibility(View.GONE);
            mPermanentAddressView.setText(mPermanentAddress.toString());
        }

        if (mOfficeAddress == null) {
            mOfficeAddressHolder.setVisibility(View.GONE);
            mOfficeAddressAddButton.setVisibility(View.VISIBLE);
        } else {
            mOfficeAddressHolder.setVisibility(View.VISIBLE);
            mOfficeAddressAddButton.setVisibility(View.GONE);
            mOfficeAddressView.setText(mOfficeAddress.toString());
        }

        final Bundle presentAddressBundle = new Bundle();
        presentAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_PRESENT);
        if (mPresentAddress != null)
            presentAddressBundle.putSerializable(Constants.ADDRESS, mPresentAddress);

        final Bundle permanentAddressBundle = new Bundle();
        permanentAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_PERMANENT);
        if (mPermanentAddress != null)
            permanentAddressBundle.putSerializable(Constants.ADDRESS, mPermanentAddress);

        final Bundle officeAddressBundle = new Bundle();
        officeAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_OFFICE);
        if (mOfficeAddress != null)
            officeAddressBundle.putSerializable(Constants.ADDRESS, mOfficeAddress);

        mPresentAddressAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).switchToEditAddressFragment(presentAddressBundle);
            }
        });

        mPresentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).switchToEditAddressFragment(presentAddressBundle);
            }
        });

        mPermanentAddressAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).switchToEditAddressFragment(permanentAddressBundle);
            }
        });

        mPermanentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).switchToEditAddressFragment(permanentAddressBundle);
            }
        });

        mOfficeAddressAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).switchToEditAddressFragment(officeAddressBundle);
            }
        });

        mOfficeAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((HomeActivity) getActivity()).switchToEditAddressFragment(officeAddressBundle);
            }
        });
    }

    private void getUserAddress() {
        if (mGetUserAddressTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_address));

        mGetUserAddressTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_ADDRESS_REQUEST,
                Constants.BASE_URL + "/" + Constants.URL_GET_USER_ADDRESS_REQUEST, getActivity(), this);
        mGetUserAddressTask.execute();
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetUserAddressTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_USER_ADDRESS_REQUEST)) {
            try {
                mGetUserAddressResponse = gson.fromJson(resultList.get(2), GetUserAddressResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mPresentAddress = mGetUserAddressResponse.getPresentAddress();
                    mPermanentAddress = mGetUserAddressResponse.getPermanentAddress();
                    mOfficeAddress = mGetUserAddressResponse.getOfficeAddress();

                    loadAddresses();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                    ((HomeActivity) getActivity()).switchToDashBoard();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                ((HomeActivity) getActivity()).switchToDashBoard();
            }

            mGetUserAddressTask = null;
        }
    }
}
