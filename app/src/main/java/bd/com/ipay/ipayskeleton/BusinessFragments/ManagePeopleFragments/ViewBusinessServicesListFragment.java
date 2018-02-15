package bd.com.ipay.ipayskeleton.BusinessFragments.ManagePeopleFragments;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ManagedBusinessAccountSettingsActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.RemoveEmployeeResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessAccountDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessService;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.GetBusinessDetailsRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.LeaveOrRemoveBusinessAccountRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;


public class ViewBusinessServicesListFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetDetailsOfRoleTask;
    private BusinessAccountDetails mBusinessAccoutnDetails;

    private HttpRequestDeleteAsyncTask mResignFromBusinessAsyncTask;
    private RemoveEmployeeResponse mResignFromBusinessResponse;

    private List<String> privilegeList;
    private long mID;
    private long mBusinessAccountID;
    private String uriLeaveAccount;
    private String uriGetDetailsOfBusiness;

    private ProgressDialog mProgressDialog;

    private Button mBackButton;
    private RecyclerView mAccessListRecyclerView;

    private ProfileImageView mBusinessLogo;
    private TextView mBusinessNameTextView;
    private TextView mRoleTextView;
    private View mainView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_view_business_services_list, container, false);
        mBusinessLogo = (ProfileImageView) mainView.findViewById(R.id.business_profile_image_view);
        mBusinessNameTextView = (TextView) mainView.findViewById(R.id.business_name_text_view);
        mRoleTextView = (TextView) mainView.findViewById(R.id.role_name);
        mProgressDialog = new ProgressDialog(getContext());
        getAssociatedBusinessAccountID();
        getAssociatedID();
        mBackButton = (Button) mainView.findViewById(R.id.back_button);
        mAccessListRecyclerView = (RecyclerView) mainView.findViewById(R.id.access_list_recycler_view);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(getActivity()).
                        setMessage(getString(R.string.do_you_want_to_resign))
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                resignFromBusiness(mID);
                            }
                        }).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });
        mainView.setVisibility(View.GONE);
        return mainView;
    }

    private void getAssociatedID() {
        try {
            mID = ((ManagedBusinessAccountSettingsActivity) (getActivity())).mId;
            if (mID == Constants.BUSINESS_ID_DEFAULT) {
                getActivity().finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resignFromBusiness(long id) {
        if (mResignFromBusinessAsyncTask != null) {
            return;
        }
        uriLeaveAccount = new LeaveOrRemoveBusinessAccountRequestBuilder(id).getGeneratedUri();
        mResignFromBusinessAsyncTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_LEAVE_ACCOUNT, uriLeaveAccount, getActivity(), this);
        mResignFromBusinessAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.show();
        mProgressDialog.setCancelable(false);
    }

    private void getAssociatedBusinessAccountID() {
        try {
            mBusinessAccountID = ((ManagedBusinessAccountSettingsActivity) (getActivity())).mBusinessAccountId;
            if (mBusinessAccountID == Constants.BUSINESS_ID_DEFAULT) {
                getActivity().finish();
            } else {
                getDetailsOfAssociatedRole();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDetailsOfAssociatedRole() {
        if (mGetDetailsOfRoleTask != null) return;
        else {
            mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching_details));
            mProgressDialog.show();
            mProgressDialog.setCancelable(false);
            GetBusinessDetailsRequestBuilder getBusinessDetailsRequestBuilder = new GetBusinessDetailsRequestBuilder(mBusinessAccountID);
            uriGetDetailsOfBusiness = getBusinessDetailsRequestBuilder.getGeneratedUri();
            mGetDetailsOfRoleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DETAILS_OF_BUSINESS_ROLE,
                    uriGetDetailsOfBusiness, getActivity());
            mGetDetailsOfRoleTask.mHttpResponseListener = this;
            mGetDetailsOfRoleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private List<String> getServiceNamesFromBusinessServices(List<BusinessService> businessServicesList) {
        List<String> serviceNames = new ArrayList<>();
        for (BusinessService businessService : businessServicesList) {
            serviceNames.add(businessService.getServiceName());
        }
        return serviceNames;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            Toast.makeText(getContext(), getActivity().getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
        } else {
            if (result.getApiCommand().equals(Constants.COMMAND_GET_DETAILS_OF_BUSINESS_ROLE)) {
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mBusinessAccoutnDetails = new Gson().fromJson(result.getJsonString(),
                                BusinessAccountDetails.class);
                        privilegeList = getServiceNamesFromBusinessServices(mBusinessAccoutnDetails.getServiceList());
                        mAccessListRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        mAccessListRecyclerView.setAdapter(new AccessListAdapter());
                        mBusinessNameTextView.setText(mBusinessAccoutnDetails.getBusinessName());
                        mRoleTextView.setText(mBusinessAccoutnDetails.getRoleName());
                        mBusinessLogo.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER +
                                mBusinessAccoutnDetails.getBusinessProfilePictureUrlHigh(), false);
                        mainView.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(getActivity(), getActivity().getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();

                    }

                } catch (Exception e) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.service_not_available), Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
                mGetDetailsOfRoleTask = null;
                mProgressDialog.dismiss();
            } else if (result.getApiCommand().equals(Constants.COMMAND_LEAVE_ACCOUNT)) {
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mResignFromBusinessResponse = new Gson().fromJson(result.getJsonString(), RemoveEmployeeResponse.class);
                        Toast.makeText(getActivity(), mResignFromBusinessResponse.getMessage(), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), mResignFromBusinessResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), mResignFromBusinessResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
                mResignFromBusinessAsyncTask = null;
                mProgressDialog.dismiss();
            }

        }
    }

    public class AccessListAdapter extends RecyclerView.Adapter<AccessListViewHolder> {
        @Override
        public AccessListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_role_privileges, parent, false);
            return new AccessListViewHolder(view);
        }

        @Override
        public void onBindViewHolder(AccessListViewHolder holder, int position) {
            holder.privilege.setText(privilegeList.get(position));

        }

        @Override
        public int getItemCount() {
            return privilegeList.size();
        }
    }

    public class AccessListViewHolder extends RecyclerView.ViewHolder {
        private CheckBox privilege;

        public AccessListViewHolder(View itemView) {
            super(itemView);
            privilege = (CheckBox) (itemView.findViewById(R.id.checkbox_privilege));
        }
    }

}
