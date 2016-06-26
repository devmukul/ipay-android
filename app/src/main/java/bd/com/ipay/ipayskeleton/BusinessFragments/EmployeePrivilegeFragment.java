package bd.com.ipay.ipayskeleton.BusinessFragments;

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

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.CreateEmployeeRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.CreateEmployeeResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Privilege;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.PrivilegeConstants;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class EmployeePrivilegeFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateEmployeeAsyncTask;
    private CreateEmployeeResponse mCreateEmployeeResponse;

    private List<Privilege> mPrivilegeList;
    private EmployeePrivilegeAdapter mEmployeePrivilegeAdapter;

    private String mProfilePicture;
    private String mName;
    private String mMobileNumber;
    private String mDesignation;

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;

    private Button mAddEmployeeOrSavePermissionsButton;

    private RecyclerView mPrivilegeListView;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_privileges, container, false);

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mPrivilegeListView = (RecyclerView) v.findViewById(R.id.privilege_list);
        mAddEmployeeOrSavePermissionsButton = (Button) v.findViewById(R.id.button_add_employee_or_save_permissions);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfilePicture = getArguments().getString(Constants.PROFILE_PICTURE);
        mName = getArguments().getString(Constants.NAME);
        mMobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);
        mDesignation = getArguments().getString(Constants.DESIGNATION);

        mProfilePictureView.setInformation(mProfilePicture, mName);
        mNameView.setText(mName);
        mMobileNumberView.setText(mMobileNumber);

        mAddEmployeeOrSavePermissionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEmployee(getString(R.string.create_new_employee));
            }
        });

        if (getArguments().containsKey(Constants.EMPLOYEE_PRIVILEGE)) {
            mPrivilegeList = getArguments().getParcelableArrayList(Constants.EMPLOYEE_PRIVILEGE);
        } else {
            mPrivilegeList = PrivilegeConstants.ALL_PRIVILEGES;
        }

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
                mPrivilegeCheckBox.setChecked(privilege.hasAuthority());
                mPrivilegeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        mPrivilegeList.get(pos).setHasAuthority(isChecked);
                    }
                });
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
