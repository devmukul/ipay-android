package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.GetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.District;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

@SuppressWarnings("IfCanBeSwitch")
public class AddressFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetUserAddressTask = null;
    private GetUserAddressResponse mGetUserAddressResponse = null;

    private HttpRequestGetAsyncTask mGetThanaListAsyncTask = null;
    private GetThanaResponse mGetThanaResponse;

    private HttpRequestGetAsyncTask mGetDistrictListAsyncTask = null;
    private GetDistrictResponse mGetDistrictResponse;

    private AddressClass mPresentAddress;
    private AddressClass mPermanentAddress;
    private AddressClass mOfficeAddress;

    private TextView mPresentAddressView;
    private TextView mPermanentAddressView;
    private TextView mOfficeAddressView;

    private View mPermanentAddressviewHolder;
    private View mOfficeAddressviewHolder;
    private View mPresentAddressHolder;
    private View mPermanentAddressHolder;
    private View mOfficeAddressHolder;

    private ImageButton mPresentAddressEditButton;
    private ImageButton mPermanentAddressEditButton;
    private ImageButton mOfficeAddressEditButton;

    private List<Thana> mThanaList;
    private List<District> mDistrictList;

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

        getActivity().setTitle(getString(R.string.address));

        mPresentAddressView = (TextView) v.findViewById(R.id.textview_present_address);
        mPermanentAddressView = (TextView) v.findViewById(R.id.textview_permanent_address);
        mOfficeAddressView = (TextView) v.findViewById(R.id.textview_office_address);

        mPresentAddressEditButton = (ImageButton) v.findViewById(R.id.button_edit_present_address);
        mPermanentAddressEditButton = (ImageButton) v.findViewById(R.id.button_edit_permanent_address);
        mOfficeAddressEditButton = (ImageButton) v.findViewById(R.id.button_edit_office_address);

        mPermanentAddressviewHolder = v.findViewById(R.id.permanent_address_view_holder);
        mOfficeAddressviewHolder = v.findViewById(R.id.office_address_view_holder);

        mPresentAddressHolder = v.findViewById(R.id.present_address_holder);
        mPermanentAddressHolder = v.findViewById(R.id.permanent_address_holder);
        mOfficeAddressHolder = v.findViewById(R.id.office_address_holder);
        /**
         * Get district list first.
         * Then get all thanas within that district.
         * Then load addresses.
         */
        getDistrictList();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);
    }

    private void loadAddresses() {
        if (ProfileInfoCacheManager.getAccountType() == Constants.PERSONAL_ACCOUNT_TYPE) {
            mPermanentAddressviewHolder.setVisibility(View.VISIBLE);
            mOfficeAddressviewHolder.setVisibility(View.GONE);

            if (mPermanentAddress == null) {
                mPermanentAddressView.setVisibility(View.GONE);
            } else {
                mPermanentAddressHolder.setVisibility(View.VISIBLE);
                mPermanentAddressView.setText(mPermanentAddress.toString(mThanaList, mDistrictList));
            }
        } else if (ProfileInfoCacheManager.getAccountType() == Constants.BUSINESS_ACCOUNT_TYPE) {
            mPermanentAddressviewHolder.setVisibility(View.GONE);
            mOfficeAddressviewHolder.setVisibility(View.VISIBLE);

            if (mOfficeAddress == null) {
                mOfficeAddressView.setVisibility(View.GONE);
            } else {
                mOfficeAddressHolder.setVisibility(View.VISIBLE);
                mOfficeAddressView.setText(mOfficeAddress.toString(mThanaList, mDistrictList));
            }
        }

        if (mPresentAddress == null) {
            mPresentAddressView.setVisibility(View.GONE);
        } else {
            mPresentAddressHolder.setVisibility(View.VISIBLE);
            mPresentAddressView.setText(mPresentAddress.toString(mThanaList, mDistrictList));
        }

        final Bundle presentAddressBundle = new Bundle();
        presentAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_PRESENT);
        if (mPresentAddress != null)
            presentAddressBundle.putSerializable(Constants.ADDRESS, mPresentAddress);

        final Bundle permanentAddressBundle = new Bundle();
        permanentAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_PERMANENT);
        if (mPermanentAddress != null)
            permanentAddressBundle.putSerializable(Constants.ADDRESS, mPermanentAddress);
        if (mPresentAddress != null)
            permanentAddressBundle.putSerializable(Constants.PRESENT_ADDRESS, mPresentAddress);

        final Bundle officeAddressBundle = new Bundle();
        officeAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_OFFICE);
        if (mOfficeAddress != null)
            officeAddressBundle.putSerializable(Constants.ADDRESS, mOfficeAddress);

        mPresentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToEditAddressFragment(presentAddressBundle);
            }
        });

        mPermanentAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToEditAddressFragment(permanentAddressBundle);
            }
        });

        mOfficeAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToEditAddressFragment(officeAddressBundle);
            }
        });
    }

    private void getThanaList() {
        mGetThanaListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_THANA_LIST,
                new ThanaRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetThanaListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getDistrictList() {
        mGetDistrictListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DISTRICT_LIST,
                new DistrictRequestBuilder().getGeneratedUri(), getActivity(), this);
        mGetDistrictListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getUserAddress() {
        if (mGetUserAddressTask != null) {
            return;
        }

        mGetUserAddressTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_ADDRESS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_USER_ADDRESS_REQUEST, getActivity(), this);
        mGetUserAddressTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetUserAddressTask = null;
            mGetDistrictListAsyncTask = null;
            mGetThanaListAsyncTask = null;

            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }

            return;
        }


        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_USER_ADDRESS_REQUEST:
                try {
                    mGetUserAddressResponse = gson.fromJson(result.getJsonString(), GetUserAddressResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mPresentAddress = mGetUserAddressResponse.getPresentAddress();
                        mPermanentAddress = mGetUserAddressResponse.getPermanentAddress();
                        mOfficeAddress = mGetUserAddressResponse.getOfficeAddress();

                        loadAddresses();
                        setContentShown(true);
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetUserAddressTask = null;

                break;
            case Constants.COMMAND_GET_THANA_LIST:
                try {
                    mGetThanaResponse = gson.fromJson(result.getJsonString(), GetThanaResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mThanaList = mGetThanaResponse.getThanas();
                        getUserAddress();

                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_thana_list, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetThanaListAsyncTask = null;
                break;
            case Constants.COMMAND_GET_DISTRICT_LIST:
                try {
                    mGetDistrictResponse = gson.fromJson(result.getJsonString(), GetDistrictResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mDistrictList = mGetDistrictResponse.getDistricts();
                        getThanaList();

                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_district_list, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_district_list, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetDistrictListAsyncTask = null;
                break;
        }
    }
}
