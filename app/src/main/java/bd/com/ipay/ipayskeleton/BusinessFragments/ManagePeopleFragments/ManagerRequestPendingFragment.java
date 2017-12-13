package bd.com.ipay.ipayskeleton.BusinessFragments.ManagePeopleFragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.PendingInvitationList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.PendingManagerListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.RemoveEmployeeResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.RemovePendingEmployeeRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ManagerRequestPendingFragment extends BaseFragment implements HttpResponseListener{

    private HttpRequestGetAsyncTask mGetAllManagerAsyncTask;
    private PendingManagerListResponse mGetAllManagerResponse;

    private HttpRequestPutAsyncTask mRemoveAnManagerAsyncTask;
    private RemoveEmployeeResponse mRemoveAnManagerResponse;

    private EmployeeListAdapter adapter;
    private LinearLayoutManager layoutManager;

    private RecyclerView mManagerListView;
    private TextView mEmptyListTextView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_management, container, false);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mManagerListView = (RecyclerView) v.findViewById(R.id.list_manager);
        layoutManager = new LinearLayoutManager(getActivity());
        mManagerListView.setLayoutManager(layoutManager);
        adapter = new EmployeeListAdapter();
        mManagerListView.setAdapter(adapter);

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();
        Utilities.hideKeyboard(getContext(), getView());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter.notifyDataSetChanged();
        if (ManagerRequestHolderFragment.mPendingEmployeeList != null && ManagerRequestHolderFragment.mPendingEmployeeList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    private void showDeleteEmployeeConfirmationDialog(final PendingInvitationList employee) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                .setMessage(R.string.are_you_sure_to_remove_employee)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAnEmployee(employee.getId());
                    }
                })
                .setNegativeButton(android.R.string.no, null);

        dialog.show();
    }


    private void getEmployeeList() {
        if (mGetAllManagerAsyncTask != null)
            return;

        mGetAllManagerAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PENDING_EMPLOYEE_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_PENDING_EMPLOYEE_LIST, getActivity(), this);
        mGetAllManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeAnEmployee(long associationId) {
        if (mRemoveAnManagerAsyncTask != null)
            return;

        RemovePendingEmployeeRequest createEmployeeRequest = new RemovePendingEmployeeRequest(associationId, "CANCELED");
        Gson gson = new Gson();
        String json = gson.toJson(createEmployeeRequest);

        mRemoveAnManagerAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_REMOVE_AN_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_PENDING_EMPLOYEE , json, getContext(), this);
        mRemoveAnManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetAllManagerAsyncTask = null;

            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_EMPLOYEE_LIST)) {
            try {
                mGetAllManagerResponse = gson.fromJson(result.getJsonString(), PendingManagerListResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    ManagerRequestHolderFragment.mPendingEmployeeList = mGetAllManagerResponse.getPendingInvitationList();
                    adapter.notifyDataSetChanged();
                } else {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), R.string.failed_loading_manager_list, Toast.LENGTH_LONG);
                        getActivity().onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toaster.makeText(getActivity(), R.string.failed_loading_manager_list, Toast.LENGTH_LONG);
                    getActivity().onBackPressed();
                }
            }

            mGetAllManagerAsyncTask = null;
        }
        else if (result.getApiCommand().equals(Constants.COMMAND_REMOVE_AN_EMPLOYEE)) {

            try {
                mRemoveAnManagerResponse = gson.fromJson(result.getJsonString(), RemoveEmployeeResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRemoveAnManagerResponse.getMessage();

                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), message, Toast.LENGTH_LONG);
                        getEmployeeList();
                    }

                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRemoveAnManagerResponse.getMessage(), Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.could_not_remove_employee, Toast.LENGTH_LONG);
            }

            mRemoveAnManagerAsyncTask = null;
        }

        if (ManagerRequestHolderFragment.mPendingEmployeeList != null && ManagerRequestHolderFragment.mPendingEmployeeList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }


    public class EmployeeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private class EmployeeViewHolder extends RecyclerView.ViewHolder {
            private final ProfileImageView mProfileImageView;
            private final TextView mNameView;
            private final TextView mMobileNumberView;
            private final TextView mDesignationView;
            private final ImageView mDeleteView;
            private final ImageView mEditView;
            private final View divider;

            private CustomSelectorDialog mCustomSelectorDialog;
            private List<String> mEmployee_manage_ActionList;

            public EmployeeViewHolder(View itemView) {
                super(itemView);

                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mDesignationView = (TextView) itemView.findViewById(R.id.textview_designation);
                mDeleteView = (ImageView) itemView.findViewById(R.id.delete_employee);
                mEditView = (ImageView) itemView.findViewById(R.id.edit_info);
                divider = itemView.findViewById(R.id.divider);
                mEditView.setVisibility(View.GONE);
            }

            public void bindView(final int pos) {
                if (pos == ManagerRequestHolderFragment.mPendingEmployeeList.size() - 1) divider.setVisibility(View.GONE);
                final PendingInvitationList employee = ManagerRequestHolderFragment.mPendingEmployeeList.get(pos);
                mEmployee_manage_ActionList = Arrays.asList(getResources().getStringArray(R.array.employee_management_action));

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + employee.getProfilePictures().get(0).getUrl(),
                        false);
                mNameView.setText(employee.getManagerName());
                mMobileNumberView.setText(employee.getManagerMobileNumber());

                if (employee.getRoleName() != null && !employee.getRoleName().isEmpty()) {
                    mDesignationView.setText(employee.getRoleName());
                    mDesignationView.setVisibility(View.VISIBLE);
                } else {
                    mDesignationView.setVisibility(View.GONE);
                }
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });

                mDeleteView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteEmployeeConfirmationDialog(employee);
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_employee,
                    parent, false);

            return new EmployeeListAdapter.EmployeeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                EmployeeListAdapter.EmployeeViewHolder vh = (EmployeeListAdapter.EmployeeViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (ManagerRequestHolderFragment.mPendingEmployeeList != null)
                return ManagerRequestHolderFragment.mPendingEmployeeList.size();
            else
                return 0;
        }
    }
}

