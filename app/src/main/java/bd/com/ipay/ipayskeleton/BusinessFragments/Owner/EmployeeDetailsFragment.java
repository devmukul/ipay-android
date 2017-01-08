package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.EmployeeDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.GetEmployeeDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.PrivilegeConstants;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.Role;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class EmployeeDetailsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mEmployeeDetailsAsyncTask;
    private GetEmployeeDetailsResponse mGetEmployeeDetailsResponse;

    private EmployeeDetails mEmployeeDetails;
    private EmployeeDetailsAdapter mEmployeeDetailsAdapter;

    private List<String> mPrivilegeList;
    private long mAssociationId;

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDesignationView;
    private TextView mRoleView;

    private RecyclerView mPrivilegeListView;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_view_employee_details, container, false);

        mProgressDialog = new ProgressDialog(getActivity());

        if (getArguments() != null)
            mAssociationId = getArguments().getLong(Constants.ASSOCIATION_ID);
        getEmployeeDetails(mAssociationId);

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDesignationView = (TextView) v.findViewById(R.id.textview_designation);
        mPrivilegeListView = (RecyclerView) v.findViewById(R.id.privilege_list);
        mRoleView = (TextView) v.findViewById(R.id.role);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mPrivilegeListView.setLayoutManager(layoutManager);

        return v;
    }

    private void getEmployeeDetails(long assotiationId) {
        if (mEmployeeDetailsAsyncTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.preparing));
        mProgressDialog.show();
        mEmployeeDetailsAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_EMPLOYEE_DETAILS,
                Constants.BASE_URL_MM + Constants.URL_GET_EMPLOYEE_DETAILS + assotiationId, getActivity());
        mEmployeeDetailsAsyncTask.mHttpResponseListener = this;
        mEmployeeDetailsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mEmployeeDetailsAsyncTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_EMPLOYEE_DETAILS)) {
            try {
                mGetEmployeeDetailsResponse = gson.fromJson(result.getJsonString(), GetEmployeeDetailsResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        //Toast.makeText(getActivity(), mGetEmployeeDetailsResponse.getMessage(), Toast.LENGTH_LONG).show();

                        mEmployeeDetails = mGetEmployeeDetailsResponse.getInfo();

                        mProfilePictureView.setProfilePicture(
                                Constants.BASE_URL_FTP_SERVER + mEmployeeDetails.getProfilePictureUrl(), false);
                        mNameView.setText(mEmployeeDetails.getName());
                        mMobileNumberView.setText(mEmployeeDetails.getMobileNumber());

                        if (!mEmployeeDetails.getDesignation().equals(""))
                            mDesignationView.setText(mEmployeeDetails.getDesignation());
                        else mDesignationView.setVisibility(View.GONE);

                        // Get the name of the Role
                        for (Role role : ManagePeopleActivity.mAllRoleList) {
                            if (role.getId() == mEmployeeDetails.getRoleId()) {
                                mRoleView.setText(role.getName());
                                break;
                            }
                        }


                        mPrivilegeList = Arrays.asList(ManagePeopleActivity.mRolePrivilegeMap.get(mEmployeeDetails.getRoleId()));
                        mEmployeeDetailsAdapter = new EmployeeDetailsAdapter();

                        mPrivilegeListView.setAdapter(mEmployeeDetailsAdapter);

                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mGetEmployeeDetailsResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.fetching_employee_details_failed, Toast.LENGTH_LONG).show();
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
                mPrivilegeCheckBox.setText(PrivilegeConstants.PRIVILEGE_NAME_MAP.get(mPrivilegeList.get(pos)));
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
            if (mPrivilegeList == null) return 0;
            else return mPrivilegeList.size();
        }
    }
}
