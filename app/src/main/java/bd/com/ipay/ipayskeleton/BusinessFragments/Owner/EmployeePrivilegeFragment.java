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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessRoleDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessService;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.CreateEmployeeRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.CreateEmployeeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class EmployeePrivilegeFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateEmployeeAsyncTask;
    // private CreateEmployeeResponse mCreateEmployeeResponse;

    private HttpRequestPutAsyncTask mEditEmployeeAsyncTask;
    //private UpdateEmployeeResponse mEditEmployeeResponse;

    private HttpRequestGetAsyncTask mRoleDetailsAsyncTask;

    private HttpRequestGetAsyncTask mEmployeeDetailsAsyncTask;
    // private GetEmployeeDetailsResponse mGetEmployeeDetailsResponse;

    // private EmployeeDetails mEmployeeDetails;

    private List<String> mPrivilegeList;
    private EmployeePrivilegeAdapter mEmployeePrivilegeAdapter;

    private String mProfilePicture;
    private String mName;
    private String mMobileNumber;
    private String mRoleName;
    private long mRoleID;

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mRoleView;

    private Button mAddEmployeeOrSavePermissionsButton;
    private long mAssociationId;

    private RecyclerView mPrivilegeListView;
    private ProgressDialog mProgressDialog;
    private EditText roleSelection;
    private int mSelectedRoleId = -1;
    private String mSelectedRoleName = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_privileges, container, false);

       /* if (getArguments() != null && getArguments().containsKey(Constants.ASSOCIATION_ID)) {
            mAssociationId = getArguments().getLong(Constants.ASSOCIATION_ID);
        }*/

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mRoleView = (TextView) v.findViewById(R.id.textview_role);
        mPrivilegeListView = (RecyclerView) v.findViewById(R.id.privilege_list);
        mAddEmployeeOrSavePermissionsButton = (Button) v.findViewById(R.id.button_add_employee_or_save_permissions);

        if (mAssociationId != 0)
            mAddEmployeeOrSavePermissionsButton.setText(R.string.edit_employee);

        mProgressDialog = new ProgressDialog(getActivity());

        if (getArguments() != null) {
            mProfilePicture = getArguments().getString(Constants.PROFILE_PICTURE);
            mName = getArguments().getString(Constants.NAME);
            mMobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);
            mRoleName = getArguments().getString(Constants.ROLENAME);
            mRoleID = getArguments().getLong(Constants.ROLEID);
        }

        mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mProfilePicture, false);
        mNameView.setText(mName);
        mMobileNumberView.setText(mMobileNumber);
        if (!mRoleName.equals("")) mRoleView.setText(mRoleName);
        else mRoleView.setVisibility(View.GONE);
        getDetailsOfSelectedRole();

        mAddEmployeeOrSavePermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmployee(getString(R.string.create_new_employee));
            }
        });


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mPrivilegeListView.setLayoutManager(layoutManager);

        mEmployeePrivilegeAdapter = new EmployeePrivilegeAdapter();

        mPrivilegeListView.setAdapter(mEmployeePrivilegeAdapter);

        return v;
    }

    private void setUpEmployeeDetails() {
        getEmployeeDetails(mAssociationId);
    }

    private void createEmployee(String progressMessage) {
        if (mCreateEmployeeAsyncTask != null)
            return;

        mProgressDialog.setMessage(progressMessage);
        mProgressDialog.show();

        CreateEmployeeRequest createEmployeeRequest = new CreateEmployeeRequest(mMobileNumber, mRoleID);
        Gson gson = new Gson();
        String json = gson.toJson(createEmployeeRequest);

        mCreateEmployeeAsyncTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CREATE_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_CREATE_EMPLOYEE, json, getActivity(), this);
        mCreateEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /*
        private void editEmployee(String progressMessage) {
            if (mEditEmployeeAsyncTask != null)
                return;

            mProgressDialog.setMessage(progressMessage);
            mProgressDialog.show();

            UpdateEmployeeRequest editEmployeeRequest = new UpdateEmployeeRequest(mRole, mAssociationId, mSelectedRoleId);
            Gson gson = new Gson();
            String json = gson.toJson(editEmployeeRequest);

            mEditEmployeeAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_UPDATE_EMPLOYEE,
                    Constants.BASE_URL_MM + Constants.URL_UPDATE_EMPLOYEE, json, getActivity(), this);
            mEditEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }*/
    private void getDetailsOfSelectedRole() {

        if (mRoleDetailsAsyncTask != null) return;
        else {
            mProgressDialog.setMessage(getString(R.string.preparing));
            mProgressDialog.show();
            mRoleDetailsAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DETAILS_OF_BUSINESS_ROLE,
                    Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_ROLES_DETAILS + mRoleID, getActivity());
            mRoleDetailsAsyncTask.mHttpResponseListener = this;
            mRoleDetailsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
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

    private List<String> getServiceNamesFromBusinessServices(List<BusinessService> businessServicesList) {
        List<String> serviceNames = new ArrayList<>();
        for (BusinessService businessService : businessServicesList) {
            serviceNames.add(businessService.getServiceName());
        }
        return serviceNames;
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mCreateEmployeeAsyncTask = null;
            Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
            return;
        }

        Gson gson = new Gson();
        switch (result.getApiCommand()) {
            case Constants.COMMAND_CREATE_EMPLOYEE:
                try {
                    CreateEmployeeResponse createEmployeeResponse = gson.fromJson(result.getJsonString(), CreateEmployeeResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), createEmployeeResponse.getMessage(), Toast.LENGTH_LONG);
                            ((ManagePeopleActivity) getActivity()).switchToEmployeeManagementFragment();
                        }
                    } else {
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), createEmployeeResponse.getMessage(), Toast.LENGTH_LONG);
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.new_employee_creation_failed, Toast.LENGTH_LONG);
                }
                break;
            case Constants.COMMAND_GET_DETAILS_OF_BUSINESS_ROLE:
                try {
                    BusinessRoleDetailsResponse businessRoleDetailsResponse = gson.fromJson(result.getJsonString(),
                            BusinessRoleDetailsResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mPrivilegeList = getServiceNamesFromBusinessServices(businessRoleDetailsResponse.getServiceList());
                        int size = mPrivilegeList.size();
                        mEmployeePrivilegeAdapter.notifyDataSetChanged();
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), businessRoleDetailsResponse.getMessage(), Toast.LENGTH_LONG);
                        }
                    } else {
                        int i = 0;
                    }
                } catch (Exception e) {
                    Toaster.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
                }

           /* case Constants.COMMAND_UPDATE_EMPLOYEE:
                try {
                    mEditEmployeeResponse = gson.fromJson(result.getJsonString(), UpdateEmployeeResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), mEditEmployeeResponse.getMessage(), Toast.LENGTH_LONG);
                            ((ManagePeopleActivity) getActivity()).switchToEmployeeManagementFragment();
                        }
                    } else {
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), mEditEmployeeResponse.getMessage(), Toast.LENGTH_LONG);
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.edit_employee_details_failed, Toast.LENGTH_LONG);
                }
                break;
                */

         /*   case Constants.COMMAND_GET_EMPLOYEE_DETAILS:
                try {
                    mGetEmployeeDetailsResponse = gson.fromJson(result.getJsonString(), GetEmployeeDetailsResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            //Toast.makeText(getActivity(), mGetEmployeeDetailsResponse.getMessage(), Toast.LENGTH_LONG).show();

                            mEmployeeDetails = mGetEmployeeDetailsResponse.getInfo();

                            mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mProfilePicture, false);
                            mNameView.setText(mEmployeeDetails.getName());
                            mMobileNumberView.setText(mEmployeeDetails.getMobileNumber());

                            mSelectedRoleId = mEmployeeDetails.getRoleId();

                            // Get the name of the Role
                            for (Role role : ManagePeopleActivity.mAllRoleList) {
                                if (role.getId() == mSelectedRoleId) {
                                    mSelectedRoleName = role.getName();
                                    break;
                                }
                            }

                            // Set the role selector
                            roleSelection.setText(mSelectedRoleName);

                            mPrivilegeList = Arrays.asList(ManagePeopleActivity.mRolePrivilegeMap.get(mEmployeeDetails.getRoleId()));
                            mEmployeePrivilegeAdapter.notifyDataSetChanged();

                        }
                    } else {
                        if (getActivity() != null) {
                            Toaster.makeText(getActivity(), mGetEmployeeDetailsResponse.getMessage(), Toast.LENGTH_LONG);
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.fetching_employee_details_failed, Toast.LENGTH_LONG);
                }
                break;
            default:
                break;*/
        }
    }

    private class EmployeePrivilegeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class EmployeePrivilegeViewHolder extends RecyclerView.ViewHolder {

            private final CheckBox mPrivilegeCheckBox;

            public EmployeePrivilegeViewHolder(View itemView) {
                super(itemView);

                mPrivilegeCheckBox = (CheckBox) itemView.findViewById(R.id.checkbox_privilege);
            }

            public void bindView(final int pos) {
                mPrivilegeCheckBox.setText(mPrivilegeList.get(pos));
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
            if (mPrivilegeList == null)
                return 0;
            else
                return mPrivilegeList.size();
        }
    }
}
