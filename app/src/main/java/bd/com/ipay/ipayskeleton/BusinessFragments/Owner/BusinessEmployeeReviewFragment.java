package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.ConfirmBusinessInvitationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Employee.ConfirmBusinessInvitationResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.EmployeeDetails;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.GetEmployeeDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.GetRolesResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.PrivilegeConstants;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.Role;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessEmployeeReviewFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPutAsyncTask mConfirmBusinessInvitationTask = null;
    private ConfirmBusinessInvitationResponse mConfirmBusinessInvitationResponse;

    private EmployeeDetailsAdapter mEmployeeDetailsAdapter;

    private List<String> mPrivilegeList;

    private HttpRequestGetAsyncTask mGetRolesAsyncTask;
    private GetRolesResponse mGetRolesResponse;

    public static ArrayList<Role> mAllRoleList;
    public static HashMap<Integer, String[]> mRolePrivilegeMap;

    private long mAssociationId;
    private String mSenderName;
    private String mSenderMobileNumber;
    private String mPhotoUri;
    private String mDesignation;
    private int mRoleId;

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private TextView mDesignationView;
    private TextView mRoleView;
    private View mDesignationHolder;

    private Button mRejectButton;
    private Button mAcceptButton;
    private Button mSpamButton;

    private RecyclerView mPrivilegeListView;
    private ProgressDialog mProgressDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_employee_review, container, false);
        getActivity().setTitle(R.string.business_invitation);

        mAllRoleList = new ArrayList<>();
        mRolePrivilegeMap = new HashMap<>();
        getAllRoles();

        Bundle bundle = getArguments();

        mSenderName = bundle.getString(Constants.NAME);
        mSenderMobileNumber = bundle.getString(Constants.MOBILE_NUMBER);
        mPhotoUri = bundle.getString(Constants.PHOTO_URI);
        mDesignation = bundle.getString(Constants.DESIGNATION);
        mAssociationId = bundle.getLong(Constants.ASSOCIATION_ID);
        mRoleId = bundle.getInt(Constants.ROLE_ID);

        mProgressDialog = new ProgressDialog(getActivity());

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mDesignationHolder = v.findViewById(R.id.designation_container);
        mDesignationView = (TextView) v.findViewById(R.id.designation);
        mPrivilegeListView = (RecyclerView) v.findViewById(R.id.privilege_list);
        mRoleView = (TextView) v.findViewById(R.id.role);


        mAcceptButton = (Button) v.findViewById(R.id.button_accept);
        mRejectButton = (Button) v.findViewById(R.id.button_reject);
        mSpamButton = (Button) v.findViewById(R.id.button_spam);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mPrivilegeListView.setLayoutManager(layoutManager);

        mProfilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mPhotoUri, false);
        mNameView.setText(mSenderName);
        mMobileNumberView.setText(mSenderMobileNumber);

        if (!(mDesignation == null || mDesignation.isEmpty())) {
            mDesignationHolder.setVisibility(View.VISIBLE);
            mDesignationView.setText(mDesignation);
        } else {
            mDesignationHolder.setVisibility(View.GONE);
        }

        mAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle(R.string.are_you_sure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                acceptBusinessInvitation(mAssociationId);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                        .show();
            }
        });

        mRejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle(R.string.are_you_sure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                rejectBusinessInvitation(mAssociationId);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                        .show();

            }
        });

        mSpamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new android.app.AlertDialog.Builder(getActivity())
                        .setTitle(R.string.are_you_sure)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                markBusinessInvitationAsSpam(mAssociationId);
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        })
                        .show();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void acceptBusinessInvitation(long id) {
        confirmBusinessInvitationRequest(id, Constants.BUSINESS_INVITATION_ACCEPTED, getString(R.string.loading_accepting_invitation));
    }

    private void rejectBusinessInvitation(long id) {
        confirmBusinessInvitationRequest(id, Constants.BUSINESS_INVITATION_REJECTED, getString(R.string.loading_rejecting_invitation));
    }

    private void markBusinessInvitationAsSpam(long id) {
        confirmBusinessInvitationRequest(id, Constants.BUSINESS_INVITATION_SPAM, getString(R.string.loading_marking_invitation_as_spam));
    }

    private void confirmBusinessInvitationRequest(long id, String status, String message) {
        if (mConfirmBusinessInvitationTask != null) {
            return;
        }

        mProgressDialog.setMessage(message);
        mProgressDialog.show();

        Gson gson = new Gson();
        ConfirmBusinessInvitationRequest confirmBusinessInvitationRequest = new ConfirmBusinessInvitationRequest(id, status);
        String json = gson.toJson(confirmBusinessInvitationRequest);

        mConfirmBusinessInvitationTask = new HttpRequestPutAsyncTask(Constants.COMMAND_CONFIRM_BUSINESS_INVITATION,
                Constants.BASE_URL_MM + Constants.URL_CONFIRM_BUSINESS_INVITATION, json, getActivity(), this);
        mConfirmBusinessInvitationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getAllRoles() {
        if (mGetRolesAsyncTask != null) {
            return;
        }
        mGetRolesAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ALL_ROLES,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_ROLES, getActivity(), this);
        mGetRolesAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetRolesAsyncTask = null;
            mConfirmBusinessInvitationTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
            return;
        }

        Gson gson = new Gson();

        if (this.isAdded()) setContentShown(true);

        switch (result.getApiCommand()) {
            case Constants.COMMAND_CONFIRM_BUSINESS_INVITATION:
                try {
                    mConfirmBusinessInvitationResponse = gson.fromJson(result.getJsonString(), ConfirmBusinessInvitationResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), mConfirmBusinessInvitationResponse.getMessage(), Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mConfirmBusinessInvitationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_confirming_business_invitation, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mConfirmBusinessInvitationTask = null;
                break;
            case Constants.COMMAND_GET_ALL_ROLES:
                try {
                    mGetRolesResponse = gson.fromJson(result.getJsonString(), GetRolesResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        mAllRoleList = (ArrayList<Role>) mGetRolesResponse.getRoles();

                        // Create a hash map for roleId - Privileges
                        for (Role mRole : mAllRoleList)
                            mRolePrivilegeMap.put(mRole.getId(), mRole.getPrivileges());


                        // Get the name of the Role
                        for (Role role : mAllRoleList) {
                            if (role.getId() == mRoleId) {
                                mRoleView.setText(role.getName());
                                break;
                            }
                        }

                        mPrivilegeList = Arrays.asList(mRolePrivilegeMap.get(mRoleId));
                        mEmployeeDetailsAdapter = new EmployeeDetailsAdapter();

                        mPrivilegeListView.setAdapter(mEmployeeDetailsAdapter);

                    } else {
                        Toast.makeText(getActivity(), mGetRolesResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

            default:
                break;
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
