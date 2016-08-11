package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.GetBusinessTypesAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.GetBusinessInformationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.GetUserAddressResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.District;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessInformationFragment extends ProgressFragment implements HttpResponseListener {

    private TextView mBusinessNameView;
    private TextView mBusinessMobileNumberView;
    private TextView mBusinessEmailView;
    private TextView mBusinessTypeView;

    private HttpRequestGetAsyncTask mGetUserAddressTask = null;
    private GetUserAddressResponse mGetUserAddressResponse = null;

    private HttpRequestGetAsyncTask mGetThanaListAsyncTask = null;
    private GetThanaResponse mGetThanaResponse;

    private HttpRequestGetAsyncTask mGetDistrictListAsyncTask = null;
    private GetDistrictResponse mGetDistrictResponse;

    private AddressClass mOfficeAddress;

    private TextView mOfficeAddressView;

    private View mOfficeAddressHolder;

    private ImageButton mOfficeAddressEditButton;
    private ImageButton mOfficeInfoEditButton;

    private List<Thana> mThanaList;
    private List<District> mDistrictList;


    private HttpRequestGetAsyncTask mGetBusinessInformationAsyncTask;
    private GetBusinessInformationResponse mGetBusinessInformationResponse;

    private GetBusinessTypesAsyncTask mGetBusinessTypesAsyncTask;

    private List<BusinessType> mBusinessTypes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_information, container, false);
        getActivity().setTitle(R.string.business_information);

        mBusinessNameView = (TextView) v.findViewById(R.id.textview_business_name);
        mBusinessMobileNumberView = (TextView) v.findViewById(R.id.textview_business_mobile_number);
        mBusinessEmailView = (TextView) v.findViewById(R.id.textview_business_email);
        mBusinessTypeView = (TextView) v.findViewById(R.id.textview_business_type);

        mOfficeAddressView = (TextView) v.findViewById(R.id.textview_office_address);

        mOfficeAddressEditButton = (ImageButton) v.findViewById(R.id.button_edit_office_address);
        mOfficeInfoEditButton = (ImageButton) v.findViewById(R.id.button_edit_office_information);

        mOfficeAddressHolder = v.findViewById(R.id.office_address_holder);

        if ( ProfileInfoCacheManager.isAccountVerified()) {
            mOfficeAddressEditButton.setVisibility(View.GONE);
            mOfficeInfoEditButton.setVisibility(View.GONE);
        } else {
            mOfficeAddressEditButton.setVisibility(View.VISIBLE);
            mOfficeInfoEditButton.setVisibility(View.VISIBLE);
        }

        getDistrictList();

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);

        getBusinessInformation();
    }

    private void loadAddresses() {

        if (mOfficeAddress == null) {
            mOfficeAddressView.setVisibility(View.GONE);
        } else {
            mOfficeAddressHolder.setVisibility(View.VISIBLE);
            mOfficeAddressView.setText(mOfficeAddress.toString(mThanaList, mDistrictList));
        }

        final Bundle officeAddressBundle = new Bundle();
        officeAddressBundle.putString(Constants.ADDRESS_TYPE, Constants.ADDRESS_TYPE_OFFICE);
        if (mOfficeAddress != null)
            officeAddressBundle.putSerializable(Constants.ADDRESS, mOfficeAddress);

        mOfficeAddressEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToEditAddressFragment(officeAddressBundle);
            }
        });

        mOfficeInfoEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchEditBusinessInformationFragment();
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

    private void launchEditBusinessInformationFragment() {
        Bundle bundle = new Bundle();

        if (mGetBusinessInformationResponse == null || mBusinessTypes == null) {
            Toast.makeText(getActivity(), R.string.please_wait_until_information_loading, Toast.LENGTH_LONG).show();
            return;
        }

        bundle.putString(Constants.BUSINESS_NAME, mGetBusinessInformationResponse.getBusinessName());
        bundle.putString(Constants.BUSINESS_MOBILE_NUMBER, mGetBusinessInformationResponse.getMobileNumber());
        bundle.putString(Constants.BUSINESS_EMAIL, mGetBusinessInformationResponse.getEmail());
        bundle.putInt(Constants.BUSINESS_TYPE, mGetBusinessInformationResponse.getBusinessType());
        bundle.putParcelableArrayList(Constants.BUSINESS_TYPE_LIST, new ArrayList<>(mBusinessTypes));

        ((ProfileActivity) getActivity()).switchToEditBusinessInformationFragment(bundle);
    }

    private void getBusinessInformation() {
        if (mGetBusinessInformationAsyncTask != null)
            return;

        mGetBusinessInformationAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_BUSINESS_INFORMATION,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_INFORMATION, getActivity(), this);
        mGetBusinessInformationAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void processBusinessInformationResponse() {

        mBusinessNameView.setText(mGetBusinessInformationResponse.getBusinessName());
        mBusinessMobileNumberView.setText(mGetBusinessInformationResponse.getMobileNumber());
        mBusinessEmailView.setText(mGetBusinessInformationResponse.getEmail());
        mBusinessTypeView.setText(R.string.loading);

        setContentShown(true);

        // Load business types, then extract the name of the business type from businessTypeId
        mGetBusinessTypesAsyncTask = new GetBusinessTypesAsyncTask(getActivity(), new GetBusinessTypesAsyncTask.BusinessTypeLoadListener() {
            @Override
            public void onLoadSuccess(List<BusinessType> businessTypes) {
                mBusinessTypes = businessTypes;

                for (BusinessType businessType : businessTypes) {
                    if (businessType.getId() == mGetBusinessInformationResponse.getBusinessType())
                        mBusinessTypeView.setText(businessType.getName());
                }
            }

            @Override
            public void onLoadFailed() {
                mBusinessTypeView.setText(R.string.failed_loading_business_type);
            }
        });
        mGetBusinessTypesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetBusinessInformationAsyncTask = null;
            mGetUserAddressTask = null;
            mGetDistrictListAsyncTask = null;
            mGetThanaListAsyncTask = null;

            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                ((ProfileActivity) getActivity()).switchToBusinessBasicInfoHolderFragment();
                ;
            }

            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_BUSINESS_INFORMATION:
                try {
                    mGetBusinessInformationResponse = gson.fromJson(result.getJsonString(), GetBusinessInformationResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processBusinessInformationResponse();
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_business_information, Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_business_information, Toast.LENGTH_LONG).show();
                    }
                }

                mGetBusinessInformationAsyncTask = null;
                break;

            case Constants.COMMAND_GET_USER_ADDRESS_REQUEST:
                try {
                    mGetUserAddressResponse = gson.fromJson(result.getJsonString(), GetUserAddressResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
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
