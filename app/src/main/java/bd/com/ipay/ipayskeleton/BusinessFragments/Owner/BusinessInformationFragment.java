package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GetBusinessTypesAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.GetBusinessInformationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.BusinessType;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessInformationFragment extends ProgressFragment implements HttpResponseListener {

    private TextView mBusinessNameView;
    private TextView mBusinessMobileNumberView;
    private TextView mBusinessEmailView;
    private TextView mBusinessTypeView;

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

        setHasOptionsMenu(true);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);

        getBusinessInformation();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_edit) {
            launchEditBusinessInformationFragment();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
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

        ((BusinessActivity) getActivity()).switchToEditBusinessInformationFragment(bundle);
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

            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                ((BusinessActivity) getActivity()).switchToBusinessFragment();
            }

            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_BUSINESS_INFORMATION)) {
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
        }
    }
}
