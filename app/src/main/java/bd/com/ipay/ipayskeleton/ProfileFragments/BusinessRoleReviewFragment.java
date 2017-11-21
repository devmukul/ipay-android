package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import bd.com.ipay.ipayskeleton.Activities.NotificationActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessManagerInvitationDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessService;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.UpdateBusinessRoleInvitationRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.UpdateInvitationRequestResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.BusinessRoleManagerInvitation;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class BusinessRoleReviewFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetDetailsOfInviteRoleTask;

    private ProfileImageView mProfileImageView;
    private Button mAcceptButton;
    private Button mRejectButton;
    private TextView mNameTextView;
    private TextView mMobileNumberTextView;
    private TextView mRoleNameTextView;

    private RecyclerView mPrevilegesRecyclerView;
    private EmployeeDetailsAdapter mEmployeeDetailsAdapter;

    private String mImageUri;
    private Long mID;
    private List<String> mServiceList;

    private ProgressDialog mProgressDialog;
    private Bundle mBundle;
    private BusinessRoleManagerInvitation mBusinessRoleManagerInvitation;

    private HttpRequestPutAsyncTask mAcceptOrCancelBusinessAsynctask = null;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_role_review, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();
        setUpViews(v);
        return v;
    }

    public void setUpViews(View v) {
        mAcceptButton = (Button) v.findViewById(R.id.button_accept);
        mRejectButton = (Button) v.findViewById(R.id.button_reject);
        mNameTextView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberTextView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mRoleNameTextView = (TextView) v.findViewById(R.id.role);
        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mPrevilegesRecyclerView = (RecyclerView) v.findViewById(R.id.privilege_list);

        if (getArguments() != null) {
            mBundle = getArguments();
            String jsonString = mBundle.getString(Constants.BUSINESS_ROLE_REQUEST);
            Gson gson = new Gson();
            mBusinessRoleManagerInvitation = gson.fromJson(jsonString, BusinessRoleManagerInvitation.class);

            mImageUri = Constants.BASE_URL_FTP_SERVER + mBusinessRoleManagerInvitation.getImageUrl();
            mProfileImageView.setProfilePicture(mImageUri, false);

            mNameTextView.setText(mBusinessRoleManagerInvitation.getBusinessName());
            mRoleNameTextView.setText(mBusinessRoleManagerInvitation.getRoleName());

            mID = mBusinessRoleManagerInvitation.getId();
            getDetailsOfInvitedRole();
        }
        setButtonActions();
    }

    private void getDetailsOfInvitedRole() {
        if (mGetDetailsOfInviteRoleTask != null) return;
        else {
            mProgressDialog.setMessage(getString(R.string.progress_dialog_fetching_details));
            mProgressDialog.show();
            mGetDetailsOfInviteRoleTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DETAILS_OF_INVITED_BUSINESS_ROLE,
                    Constants.BASE_URL_MM + Constants.URL_GET_DETAILS_OF_INVITED_BUSINESS_ROLE + mID, getActivity());
            mGetDetailsOfInviteRoleTask.mHttpResponseListener = this;
            mGetDetailsOfInviteRoleTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void setButtonActions() {
        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage(getString(R.string.accepting_business_role_manager_request));
                mProgressDialog.show();
                updateBusinessRoleRequest(Constants.ACCEPTED);
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage(getString(R.string.rejecting_business_role_manager_request));
                mProgressDialog.show();
                updateBusinessRoleRequest(Constants.REJECTED);
            }
        });
    }

    private void switchToNotificationFragment() {
        ((NotificationActivity) getActivity()).switchToNotificationFragment();
    }

    private List<String> getServiceNamesFromBusinessServices(List<BusinessService> businessServicesList) {
        List<String> serviceNames = new ArrayList<>();
        for (BusinessService businessService : businessServicesList) {
            serviceNames.add(businessService.getServiceName());
        }
        return serviceNames;
    }

    private void updateBusinessRoleRequest(String status) {
        if (mAcceptOrCancelBusinessAsynctask != null) return;
        else {
            UpdateBusinessRoleInvitationRequest updateBusinessRoleInvitationRequest = new UpdateBusinessRoleInvitationRequest(mID,
                    status);
            String jsonString = new Gson().toJson(updateBusinessRoleInvitationRequest);
            mAcceptOrCancelBusinessAsynctask = new HttpRequestPutAsyncTask(Constants.COMMAND_UPDATE_BUSINESS_ROLE_INVITATION,
                    Constants.BASE_URL_MM + Constants.URL_CREATE_EMPLOYEE, jsonString, getActivity());
            mAcceptOrCancelBusinessAsynctask.mHttpResponseListener = this;
            mAcceptOrCancelBusinessAsynctask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (result.getApiCommand().equals(Constants.COMMAND_GET_DETAILS_OF_INVITED_BUSINESS_ROLE)) {
                Gson gson = new Gson();
                String jsonString = result.getJsonString();
                BusinessManagerInvitationDetailsResponse businessManagerInvitationDetailsResponse = null;
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
                        businessManagerInvitationDetailsResponse = gson.fromJson(jsonString,
                                BusinessManagerInvitationDetailsResponse.class);
                        mServiceList = getServiceNamesFromBusinessServices(businessManagerInvitationDetailsResponse.getServiceList());
                        mEmployeeDetailsAdapter = new EmployeeDetailsAdapter();
                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                        mPrevilegesRecyclerView.setLayoutManager(linearLayoutManager);
                        mPrevilegesRecyclerView.setAdapter(mEmployeeDetailsAdapter);
                        break;
                    default:
                        Toaster.makeText(getActivity(), businessManagerInvitationDetailsResponse.getMessage(), Toast.LENGTH_LONG);
                }
            } else if (result.getApiCommand().equals(Constants.COMMAND_UPDATE_BUSINESS_ROLE_INVITATION)) {
                Gson gson = new Gson();
                String jsonString = result.getJsonString();
                UpdateInvitationRequestResponse updateInvitationRequestResponse = gson.fromJson(jsonString,
                        UpdateInvitationRequestResponse.class);
                switch (result.getStatus()) {
                    case Constants.HTTP_RESPONSE_STATUS_OK:
                        Toast.makeText(getActivity(), updateInvitationRequestResponse.getMessage(), Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        Intent intent = new Intent(this.getContext(), NotificationActivity.class);
                        intent.putExtra(Constants.TAG, Constants.RELOAD);
                        startActivity(intent);
                        break;
                    default:
                        Toast.makeText(getActivity(), updateInvitationRequestResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private class EmployeeDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class EmployeePrivilegeViewHolder extends RecyclerView.ViewHolder {

            private final CheckBox mPrivilegeCheckBox;

            public EmployeePrivilegeViewHolder(View itemView) {
                super(itemView);

                mPrivilegeCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_privilege);
            }

            public void bindView(final int pos) {
                mPrivilegeCheckBox.setText(mServiceList.get(pos));
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_privilege, parent, false);
            return new EmployeePrivilegeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            EmployeePrivilegeViewHolder vh = (EmployeePrivilegeViewHolder) holder;
            vh.bindView(position);
        }

        @Override
        public int getItemCount() {
            if (mServiceList == null) return 0;
            else return mServiceList.size();
        }
    }
}
