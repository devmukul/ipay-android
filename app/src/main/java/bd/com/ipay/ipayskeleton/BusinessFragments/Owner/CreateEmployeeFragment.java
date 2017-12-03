package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessRole;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessRoleDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRoles.BusinessService;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateEmployeeFragment extends Fragment implements HttpResponseListener {


    private final int PICK_CONTACT_REQUEST = 100;

    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private GetUserInfoResponse mGetUserInfoResponse;

    private HttpRequestGetAsyncTask mRoleDetailsAsyncTask;

    private EditText mMobileNumberEditText;
    private ImageView mSelectMobileNumberFromContactsButton;
    private EditText mRoleEditText;

    private String mSelectedRoleName;
    private String mSelectedMobileNumber;
    private long mSelectedRoleID = -1;
    private long mPrevSelectedRoleID;
    private List<String> mPrivilegeList;
    private EmployeePrivilegeAdapter mEmployeePrivilegeAdapter;

    private Button mContinueButton;
    private long mAssociationId;

    private ProgressDialog mProgressDialog;
    private ResourceSelectorDialog mRolesSelectorDialog;
    private boolean isReturnedFromContactPicker = false;
    private HashMap<String, Integer> mIDtoRoleNameMap;
    private ArrayList<BusinessRole> mAllRolesList;
    private RecyclerView mRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        /*Bundle args = getArguments();
        if (args != null && args.containsKey(Constants.ASSOCIATION_ID)) {
            mAssociationId = getArguments().getLong(Constants.ASSOCIATION_ID);
        }*/

        View v = inflater.inflate(R.layout.fragment_employee_information, container, false);
        if (mAssociationId == 0) getActivity().setTitle(R.string.create_employee);
        else getActivity().setTitle(R.string.edit_employee);

        mMobileNumberEditText = (EditText) v.findViewById(R.id.mobile_number);
        mRoleEditText = (EditText) v.findViewById(R.id.role);
        mRoleEditText.setFocusable(false);
        mRoleEditText.setClickable(true);
        mSelectMobileNumberFromContactsButton = (ImageView) v.findViewById(R.id.select_mobile_number_from_contacts);
        mContinueButton = (Button) v.findViewById(R.id.button_continue);
        mRecyclerView = (RecyclerView) v.findViewById(R.id.privilege_list);
        mIDtoRoleNameMap = new HashMap<>();
        createRoleNameToIDMap();
        if (mAssociationId == 0) {
            mSelectMobileNumberFromContactsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                    intent.putExtra(Constants.VERIFIED_USERS_ONLY, true);
                    startActivityForResult(intent, PICK_CONTACT_REQUEST);
                }
            });
        }

        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    mSelectedMobileNumber = ContactEngine.formatMobileNumberBD(
                            mMobileNumberEditText.getText().toString().trim());
                    Utilities.hideKeyboard(getActivity());
                    getProfileInfo(mSelectedMobileNumber);
                }
            }
        });
        setRolesSelectorAdapter(mAllRolesList);

        mProgressDialog = new ProgressDialog(getActivity());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        mEmployeePrivilegeAdapter = new EmployeePrivilegeAdapter();

        mRecyclerView.setAdapter(mEmployeePrivilegeAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSelectedRoleID != -1) {
            mRoleEditText.setText(mSelectedRoleName);
            //getDetailsOfSelectedRole();
        }
        if (mSelectedMobileNumber != null)
            mMobileNumberEditText.setText(mSelectedMobileNumber);
    }

    private List<String> getServiceNamesFromBusinessServices(List<BusinessService> businessServicesList) {
        List<String> serviceNames = new ArrayList<>();
        for (BusinessService businessService : businessServicesList) {
            serviceNames.add(businessService.getServiceName());
        }
        return serviceNames;
    }

    private void setRolesSelectorAdapter(ArrayList<BusinessRole> rolesList) {
        mRolesSelectorDialog = new ResourceSelectorDialog(getActivity(), getActivity().getString(R.string.select_a_role), rolesList);
        mRolesSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mRoleEditText.setText(name);
                mSelectedRoleName = name;
                if (mIDtoRoleNameMap.get(name) != mSelectedRoleID) {
                    mSelectedRoleID = mIDtoRoleNameMap.get(name);
                    getDetailsOfSelectedRole();
                }
            }
        });

        mRoleEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRolesSelectorDialog.show();
            }
        });
    }

    private void createRoleNameToIDMap() {
        mAllRolesList = new ArrayList<>();
        mAllRolesList = ((ManagePeopleActivity) (getActivity())).mAllRoleList;
        for (BusinessRole roles : mAllRolesList)
            mIDtoRoleNameMap.put(roles.getName(), roles.getId());
    }

    private void getDetailsOfSelectedRole() {

        if (mRoleDetailsAsyncTask != null) return;
        else {
            mProgressDialog.setMessage(getString(R.string.preparing));
            mProgressDialog.show();
            mRoleDetailsAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DETAILS_OF_BUSINESS_ROLE,
                    Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_ROLES_DETAILS + mSelectedRoleID, getActivity());
            mRoleDetailsAsyncTask.mHttpResponseListener = this;
            mRoleDetailsAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        String mobileNumber = mMobileNumberEditText.getText().toString().trim();

        if (!ContactEngine.isValidNumber(mobileNumber)) {
            focusView = mMobileNumberEditText;
            mMobileNumberEditText.setError(getString(R.string.please_enter_valid_mobile_number));
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.preparing));
        mProgressDialog.show();

        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(mobileNumber);

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getActivity(), this);

        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void launchPrevilegePage(String mobileNumber, String name, String profilePicture, String roleName, long roleID,ArrayList<String>privileges) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.MOBILE_NUMBER, mobileNumber);
        bundle.putString(Constants.ROLENAME, roleName);
        bundle.putString(Constants.NAME, name);
        bundle.putLong(Constants.ROLEID, roleID);
        bundle.putString(Constants.PROFILE_PICTURE, profilePicture);
        bundle.putStringArrayList(Constants.PRIVILEGES,privileges);
        //bundle.putLong(Constants.ASSOCIATION_ID, mAssociationId);

        ((ManagePeopleActivity) getActivity()).switchToEmployeePrivilegeFragment(bundle);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAssociationId == 0) {
            if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
                String mobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                if (mobileNumber != null) {
                    mMobileNumberEditText.setText(mobileNumber);
                    isReturnedFromContactPicker = true;
                }
            }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {

        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR) {
            mGetProfileInfoTask = null;
            Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG);
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
            try {
                mGetUserInfoResponse = gson.fromJson(result.getJsonString(), GetUserInfoResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String name = mGetUserInfoResponse.getName();
                    String profilePicture = null;
                    if (!mGetUserInfoResponse.getProfilePictures().isEmpty()) {
                        profilePicture = mGetUserInfoResponse
                                .getProfilePictures().get(0).getUrl();
                    }

                    String mobileNumber = ContactEngine.formatMobileNumberBD(mMobileNumberEditText.getText().toString());
                    String role = mRoleEditText.getText().toString();
                    launchPrevilegePage(mobileNumber, name, profilePicture, role, mSelectedRoleID,(ArrayList<String>) mPrivilegeList);

                } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.user_has_no_ipay_account, Toast.LENGTH_LONG).show();
                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
                }


            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.profile_info_get_failed, Toast.LENGTH_SHORT);
            }

            mGetProfileInfoTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_DETAILS_OF_BUSINESS_ROLE)) {
            try {
                BusinessRoleDetailsResponse businessRoleDetailsResponse = gson.fromJson(result.getJsonString(),
                        BusinessRoleDetailsResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mPrivilegeList = getServiceNamesFromBusinessServices(businessRoleDetailsResponse.getServiceList());
                    mEmployeePrivilegeAdapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                Toaster.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG);
            }
            mRoleDetailsAsyncTask = null;
        }
    }

    private class EmployeePrivilegeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class EmployeePrivilegeViewHolder extends RecyclerView.ViewHolder {

            private final CheckBox mPrivilegeCheckbox;

            public EmployeePrivilegeViewHolder(View itemView) {
                super(itemView);

                mPrivilegeCheckbox = (CheckBox) itemView.findViewById(R.id.checkbox_privilege);
            }

            public void bindView(final int pos) {
                mPrivilegeCheckbox.setText(mPrivilegeList.get(pos));
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
