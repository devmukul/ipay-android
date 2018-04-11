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

import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.EditBusinessManagerDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ManagerList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ManagerListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.RemoveEmployeeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ManagerRequestAcceptedFragment extends BaseFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetAllManagerAsyncTask;
    private ManagerListResponse mGetAllManagerResponse;

    private HttpRequestDeleteAsyncTask mRemoveAnManagerAsyncTask;
    private RemoveEmployeeResponse mRemoveAnManagerResponse;

    private EmployeeListAdapter adapter;
    private LinearLayoutManager layoutManager;

    private RecyclerView mManagerListRecyclerView;
    private TextView mEmptyListTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_management, container, false);

        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mManagerListRecyclerView = (RecyclerView) v.findViewById(R.id.list_manager);
        layoutManager = new LinearLayoutManager(getActivity());
        mManagerListRecyclerView.setLayoutManager(layoutManager);
        adapter = new EmployeeListAdapter();
        mManagerListRecyclerView.setAdapter(adapter);

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
        if (ManagerRequestHolderFragment.mAcceptedEmployeeList != null && ManagerRequestHolderFragment.mAcceptedEmployeeList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }


    private void showDeleteEmployeeConfirmationDialog(final ManagerList employee) {
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

        mGetAllManagerAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ACCEPTED_EMPLOYEE_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_EMPLOYEE_LIST, getActivity(), this);
        mGetAllManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeAnEmployee(long associationId) {
        if (mRemoveAnManagerAsyncTask != null)
            return;

        mRemoveAnManagerAsyncTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_REMOVE_AN_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_AN_EMPLOYEE_FIRST_PART + associationId, getContext(), this);
        mRemoveAnManagerAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), null)) {
            mGetAllManagerAsyncTask = null;
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_ACCEPTED_EMPLOYEE_LIST)) {
            try {
                mGetAllManagerResponse = gson.fromJson(result.getJsonString(), ManagerListResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    ManagerRequestHolderFragment.mAcceptedEmployeeList = mGetAllManagerResponse.getManagerList();
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_REMOVE_AN_EMPLOYEE)) {

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

        if (ManagerRequestHolderFragment.mAcceptedEmployeeList != null && ManagerRequestHolderFragment.mAcceptedEmployeeList.size() == 0) {
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
            private EditBusinessManagerDialog editBusinessManagerDialog;

            private CustomSelectorDialog mCustomSelectorDialog;

            public EmployeeViewHolder(View itemView) {
                super(itemView);

                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mDesignationView = (TextView) itemView.findViewById(R.id.textview_designation);
                mDeleteView = (ImageView) itemView.findViewById(R.id.delete_employee);
                mEditView = (ImageView) itemView.findViewById(R.id.edit_info);
                divider = itemView.findViewById(R.id.divider);
            }

            public void bindView(final int pos) {
                if (pos == ManagerRequestHolderFragment.mAcceptedEmployeeList.size() - 1)
                    divider.setVisibility(View.GONE);
                final ManagerList employee = ManagerRequestHolderFragment.mAcceptedEmployeeList.get(pos);

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
                        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.REMOVE_BUSINESS_MANAGER))
                            showDeleteEmployeeConfirmationDialog(employee);
                        else
                            DialogUtils.showServiceNotAllowedDialog(getActivity());
                    }
                });

                mEditView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.UPDATE_BUSINESS_MANAGER_ROLE)) {
                            editBusinessManagerDialog = new EditBusinessManagerDialog(getActivity(), "Edit Account", employee.getManagerName(), employee.getManagerAccountId(), employee.getRoleName(), employee.getProfilePictures().get(0).getUrl());
                            editBusinessManagerDialog.show();
                        } else
                            DialogUtils.showServiceNotAllowedDialog(getActivity());
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
            if (ManagerRequestHolderFragment.mAcceptedEmployeeList != null)
                return ManagerRequestHolderFragment.mAcceptedEmployeeList.size();
            else
                return 0;
        }
    }
}
