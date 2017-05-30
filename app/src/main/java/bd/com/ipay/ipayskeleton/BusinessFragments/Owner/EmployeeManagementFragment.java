package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.Employee;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.GetAllEmployeesResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Owner.RemoveEmployeeResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class EmployeeManagementFragment extends ProgressFragment implements HttpResponseListener {

    private FloatingActionButton mFabAddNewEmployee;
    private List<Employee> mEmployeeList;
    private TextView mEmptyListTextView;

    private HttpRequestGetAsyncTask mGetAllEmployeeAsyncTask;
    private GetAllEmployeesResponse mGetAllEmployeesResponse;


    private HttpRequestPutAsyncTask mRemoveAnEmployeeAsyncTask;
    private RemoveEmployeeResponse mRemoveAnEmployeeResponse;

    private EmployeeListAdapter adapter;

    private RecyclerView mEmployeeListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_management, container, false);
        getActivity().setTitle(R.string.manage_people);

        mFabAddNewEmployee = (FloatingActionButton) v.findViewById(R.id.fab_add_employee);
        mFabAddNewEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_EMPLOYEE)) {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                    return;
                }
                ((ManagePeopleActivity) getActivity()).switchToEmployeeInformationFragment(null);
            }
        });


        mEmptyListTextView = (TextView) v.findViewById(R.id.empty_list_text);
        mEmployeeListView = (RecyclerView) v.findViewById(R.id.list_employee);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mEmployeeListView.setLayoutManager(layoutManager);

        adapter = new EmployeeListAdapter();
        mEmployeeListView.setAdapter(adapter);

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

        getEmployeeList();

        setContentShown(false);
    }

    private void showDeleteEmployeeConfirmationDialog(final Employee employee) {
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
        if (mGetAllEmployeeAsyncTask != null)
            return;

        mGetAllEmployeeAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_EMPLOYEE_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_EMPLOYEE_LIST, getActivity(), this);
        mGetAllEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void removeAnEmployee(long associationId) {
        if (mRemoveAnEmployeeAsyncTask != null)
            return;

        mRemoveAnEmployeeAsyncTask = new HttpRequestPutAsyncTask(Constants.COMMAND_REMOVE_AN_EMPLOYEE,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_AN_EMPLOYEE_FIRST_PART + associationId + Constants.URL_REMOVE_AN_EMPLOYEE_LAST_PART, null, getContext(), this);
        mRemoveAnEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetAllEmployeeAsyncTask = null;

            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_EMPLOYEE_LIST)) {
            try {
                mGetAllEmployeesResponse = gson.fromJson(result.getJsonString(), GetAllEmployeesResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mEmployeeList = mGetAllEmployeesResponse.getPersonList();
                    adapter.notifyDataSetChanged();
                    if (isAdded())
                        setContentShown(true);
                } else {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), mGetAllEmployeesResponse.getMessage(), Toast.LENGTH_LONG);
                        getActivity().onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toaster.makeText(getActivity(), R.string.failed_loading_employee_list, Toast.LENGTH_LONG);
                    getActivity().onBackPressed();
                }
            }

            mGetAllEmployeeAsyncTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_REMOVE_AN_EMPLOYEE)) {

            try {
                mRemoveAnEmployeeResponse = gson.fromJson(result.getJsonString(), RemoveEmployeeResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    String message = mRemoveAnEmployeeResponse.getMessage();

                    if (getActivity() != null) {

                        Toaster.makeText(getActivity(), message, Toast.LENGTH_LONG);
                        getEmployeeList();
                    }

                } else {
                    if (getActivity() != null)
                        Toaster.makeText(getActivity(), mRemoveAnEmployeeResponse.getMessage(), Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toaster.makeText(getActivity(), R.string.could_not_remove_employee, Toast.LENGTH_LONG);
            }

            mRemoveAnEmployeeAsyncTask = null;
        }

        if (mEmployeeList != null && mEmployeeList.size() == 0) {
            mEmptyListTextView.setVisibility(View.VISIBLE);
        } else mEmptyListTextView.setVisibility(View.GONE);
    }

    public class EmployeeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_employee,
                    parent, false);

            return new EmployeeViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                EmployeeViewHolder vh = (EmployeeViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mEmployeeList != null)
                return mEmployeeList.size();
            else
                return 0;
        }

        private class EmployeeViewHolder extends RecyclerView.ViewHolder {
            private final ProfileImageView mProfileImageView;
            private final TextView mNameView;
            private final TextView mMobileNumberView;
            private final TextView mDesignationView;
            private final ImageView mStatusView;
            private final View divider;

            private CustomSelectorDialog mCustomSelectorDialog;
            private List<String> mEmployee_manage_ActionList;

            public EmployeeViewHolder(View itemView) {
                super(itemView);

                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mDesignationView = (TextView) itemView.findViewById(R.id.textview_designation);
                mStatusView = (ImageView) itemView.findViewById(R.id.verification_status);
                divider = itemView.findViewById(R.id.divider);
            }

            public void bindView(final int pos) {
                if (pos == mEmployeeList.size() - 1) divider.setVisibility(View.GONE);
                final Employee employee = mEmployeeList.get(pos);
                mEmployee_manage_ActionList = Arrays.asList(getResources().getStringArray(R.array.employee_management_action));

                mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + employee.getProfilePictureUrl(),
                        false);
                mNameView.setText(employee.getName());
                mMobileNumberView.setText(employee.getMobileNumber());

                if (employee.getDesignation() != null && !employee.getDesignation().isEmpty()) {
                    mDesignationView.setText(employee.getDesignation());
                    mDesignationView.setVisibility(View.VISIBLE);
                } else {
                    mDesignationView.setVisibility(View.GONE);
                }

                switch (employee.getStatus()) {
                    case Constants.BUSINESS_INVITATION_ACCEPTED:
                        mStatusView.setImageResource(R.drawable.ic_verified);
                        mStatusView.setColorFilter(null);
                        break;
                    case Constants.BUSINESS_STATUS_PENDING:
                        mStatusView.setImageResource(R.drawable.ic_workinprogress);
                        mStatusView.setColorFilter(Color.GRAY);
                        break;
                    default:
                        mStatusView.setImageResource(R.drawable.ic_notverifiedgrey);
                        mStatusView.setColorFilter(null);
                        break;
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!ProfileInfoCacheManager.hasServicesAccessibility(ServiceIdConstants.MANAGE_EMPLOYEE)) {
                            DialogUtils.showServiceNotAllowedDialog(getContext());
                            return;
                        }

                        mCustomSelectorDialog = new CustomSelectorDialog(getActivity(), employee.getName(), mEmployee_manage_ActionList);
                        mCustomSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
                            @Override
                            public void onResourceSelected(int selectedIndex, String action) {
                                if (Constants.ACTION_TYPE_REMOVE.equals(action)) {
                                    showDeleteEmployeeConfirmationDialog(employee);
                                } else if (Constants.ACTION_TYPE_VIEW.equals(action)) {
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(Constants.ASSOCIATION_ID, employee.getId());
                                    ((ManagePeopleActivity) getActivity()).switchToEmployeeInformationDetailsFragment(bundle);
                                } else if (Constants.ACTION_TYPE_EDIT.equals(action)) {
                                    Bundle bundle = new Bundle();
                                    bundle.putLong(Constants.ASSOCIATION_ID, employee.getId());
                                    bundle.putString(Constants.MOBILE_NUMBER, employee.getMobileNumber());
                                    bundle.putString(Constants.DESIGNATION, employee.getDesignation());
                                    ((ManagePeopleActivity) getActivity()).switchToEditEmployeeInformationFragment(bundle);
                                }
                            }
                        });
                        mCustomSelectorDialog.show();
                    }
                });
            }
        }
    }
}
