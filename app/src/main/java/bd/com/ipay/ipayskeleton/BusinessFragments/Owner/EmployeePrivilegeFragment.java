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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.CreateEmployeeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.CreateEmployeeResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.EmployeeDetails;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.GetEmployeeDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.Privilege;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.PrivilegeConstants;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.UpdateEmployeeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.UpdateEmployeeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Contained in {@link BusinessActivity}
 * Previous Fragment: {@link CreateEmployeeInformationFragment}
 */
public class EmployeePrivilegeFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateEmployeeAsyncTask;
    private CreateEmployeeResponse mCreateEmployeeResponse;

    private HttpRequestPutAsyncTask mEditEmployeeAsyncTask;
    private UpdateEmployeeResponse mEditEmployeeResponse;

    private HttpRequestGetAsyncTask mEmployeeDetailsAsyncTask;
    private GetEmployeeDetailsResponse mGetEmployeeDetailsResponse;

    private EmployeeDetails mEmployeeDetails;

    private List<Privilege> mPrivilegeList;
    private EmployeePrivilegeAdapter mEmployeePrivilegeAdapter;

    private String mProfilePicture;
    private String mName;
    private String mMobileNumber;
    private String mDesignation;

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDesignationView;

    private Button mAddEmployeeOrSavePermissionsButton;
    private long mAssociationId;

    private RecyclerView mPrivilegeListView;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_privileges, container, false);

        if (getArguments() != null && getArguments().containsKey(Constants.ASSOCIATION_ID)) {
            mAssociationId = getArguments().getLong(Constants.ASSOCIATION_ID);
        }

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDesignationView = (TextView) v.findViewById(R.id.textview_designation);
        mPrivilegeListView = (RecyclerView) v.findViewById(R.id.privilege_list);
        mAddEmployeeOrSavePermissionsButton = (Button) v.findViewById(R.id.button_add_employee_or_save_permissions);

        if(mAssociationId != 0) mAddEmployeeOrSavePermissionsButton.setText(R.string.edit_employee);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfilePicture = getArguments().getString(Constants.PROFILE_PICTURE);
        mName = getArguments().getString(Constants.NAME);
        mMobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);
        mDesignation = getArguments().getString(Constants.DESIGNATION);

        mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mProfilePicture, false);
        mNameView.setText(mName);
        mMobileNumberView.setText(mMobileNumber);
        if(!mDesignation.equals(""))mDesignationView.setText(mDesignation);
        else mDesignationView.setVisibility(View.GONE);

        mAddEmployeeOrSavePermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAssociationId==0) createEmployee(getString(R.string.create_new_employee));
                else editEmployee(getString(R.string.edit_employee_details));
            }
        });

        if(mAssociationId == 0) {
            if (getArguments().containsKey(Constants.EMPLOYEE_PRIVILEGE)) {
                mPrivilegeList = getArguments().getParcelableArrayList(Constants.EMPLOYEE_PRIVILEGE);
            } else {
                mPrivilegeList = PrivilegeConstants.ALL_PRIVILEGES;
            }
        } else getEmployeeDetails(mAssociationId);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mPrivilegeListView.setLayoutManager(layoutManager);

        mEmployeePrivilegeAdapter = new EmployeePrivilegeAdapter();

        mPrivilegeListView.setAdapter(mEmployeePrivilegeAdapter);

        return v;
    }

    private void createEmployee(String progressMessage) {
        if (mCreateEmployeeAsyncTask != null)
            return;

        mProgressDialog.setMessage(progressMessage);
        mProgressDialog.show();

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(mMobileNumber, mDesignation, mPrivilegeList);
        Gson gson = new Gson();
        String json = gson.toJson(createEmployeeRequest);

        mCreateEmployeeAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CREATE_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_CREATE_EMPLOYEE, json, getActivity(), this);
        mCreateEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void editEmployee(String progressMessage) {
        if (mEditEmployeeAsyncTask != null)
            return;

        mProgressDialog.setMessage(progressMessage);
        mProgressDialog.show();

        UpdateEmployeeRequest editEmployeeRequest = new UpdateEmployeeRequest(mDesignation, mAssociationId, mPrivilegeList);
        Gson gson = new Gson();
        String json = gson.toJson(editEmployeeRequest);

        mEditEmployeeAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_UPDATE_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_UPDATE_EMPLOYEE, json, getActivity(), this);
        mEditEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getEmployeeDetails(long assotiationId){
        if(mEmployeeDetailsAsyncTask != null){
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
            mCreateEmployeeAsyncTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_CREATE_EMPLOYEE)) {
            try {
                mCreateEmployeeResponse = gson.fromJson(result.getJsonString(), CreateEmployeeResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mCreateEmployeeResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((BusinessActivity) getActivity()).switchToEmployeeManagementFragment();
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mCreateEmployeeResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.new_employee_creation_failed, Toast.LENGTH_LONG).show();
            }
        } else if (result.getApiCommand().equals(Constants.COMMAND_UPDATE_EMPLOYEE)) {
            try {
                mEditEmployeeResponse = gson.fromJson(result.getJsonString(), UpdateEmployeeResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mEditEmployeeResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((BusinessActivity) getActivity()).switchToEmployeeManagementFragment();
                    }
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mEditEmployeeResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.edit_employee_details_failed, Toast.LENGTH_LONG).show();
            }
        }  else if (result.getApiCommand().equals(Constants.COMMAND_GET_EMPLOYEE_DETAILS)) {
            try {
                mGetEmployeeDetailsResponse = gson.fromJson(result.getJsonString(), GetEmployeeDetailsResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mGetEmployeeDetailsResponse.getMessage(), Toast.LENGTH_LONG).show();

                        mEmployeeDetails = mGetEmployeeDetailsResponse.getInfo();

                        mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mProfilePicture, false);
                        mNameView.setText(mEmployeeDetails.getName());
                        mMobileNumberView.setText(mEmployeeDetails.getMobileNumber());

                        if (!mEmployeeDetails.getDesignation().equals("")) mDesignationView.setText(mEmployeeDetails.getDesignation());
                        else mDesignationView.setVisibility(View.GONE);

                        mPrivilegeList = mEmployeeDetails.getPrivilegeList();
                        mEmployeePrivilegeAdapter.notifyDataSetChanged();

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

    private class EmployeePrivilegeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class EmployeePrivilegeViewHolder extends RecyclerView.ViewHolder {

            private CheckBox mPrivilegeCheckBox;

            public EmployeePrivilegeViewHolder(View itemView) {
                super(itemView);

                mPrivilegeCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_privilege);
            }

            public void bindView(final int pos) {
                Privilege privilege = mPrivilegeList.get(pos);

                mPrivilegeCheckBox.setText(PrivilegeConstants.PRIVILEGE_NAME_MAP.get(privilege.getName()));
                mPrivilegeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mPrivilegeList.get(pos).setHasAuthority(isChecked);
                    }
                });
                mPrivilegeCheckBox.setChecked(privilege.hasAuthority());
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_privilege, parent, false);
            EmployeePrivilegeViewHolder vh = new EmployeePrivilegeViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            EmployeePrivilegeViewHolder vh = (EmployeePrivilegeViewHolder) holder;
            vh.bindView(position);
        }

        @Override
        public int getItemCount() {
            if (mPrivilegeList == null)
                return 0;
            else
                return mPrivilegeList.size();
        }
    }
}
