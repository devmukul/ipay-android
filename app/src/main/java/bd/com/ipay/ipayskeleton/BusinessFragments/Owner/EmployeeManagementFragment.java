package bd.com.ipay.ipayskeleton.BusinessFragments.Owner;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.Employee;
import bd.com.ipay.ipayskeleton.Model.MMModule.Business.Owner.GetAllEmployeesResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class EmployeeManagementFragment extends ProgressFragment implements HttpResponseListener {

    private Button mAddEmployeeButton;
    private List<Employee> mEmployeeList;

    private HttpRequestGetAsyncTask mGetAllEmployeeAsyncTask;
    private GetAllEmployeesResponse mGetAllEmployeesResponse;

    private RecyclerView mEmployeeListView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_employee_management, container, false);

        mAddEmployeeButton = (Button) v.findViewById(R.id.button_add_employee);
        mAddEmployeeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BusinessActivity) getActivity()).switchToEmployeeInformationFragment(null);
            }
        });

        mEmployeeListView = (RecyclerView) v.findViewById(R.id.list_employee);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mEmployeeListView.setLayoutManager(layoutManager);

        getEmployeeList();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
    }

    private void getEmployeeList() {
        if (mGetAllEmployeeAsyncTask != null)
            return;

        mGetAllEmployeeAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_EMPLOYEE_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_EMPLOYEE_LIST, getActivity(), this);
        mGetAllEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetAllEmployeeAsyncTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_EMPLOYEE_LIST)) {
            try {
                mGetAllEmployeesResponse = gson.fromJson(result.getJsonString(), GetAllEmployeesResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mEmployeeList = mGetAllEmployeesResponse.getPersonList();
                    EmployeeListAdapter adapter = new EmployeeListAdapter();
                    mEmployeeListView.setAdapter(adapter);
                    setContentShown(true);
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mGetAllEmployeesResponse.getMessage(), Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_loading_employee_list, Toast.LENGTH_LONG).show();
                    getActivity().onBackPressed();
                }
            }
        }
    }

    public class EmployeeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private class EmployeeViewHolder extends RecyclerView.ViewHolder {
            private ProfileImageView mProfileImageView;
            private TextView mNameView;
            private TextView mMobileNumberView;
            private TextView mDesignationView;
            private ImageView mStatusView;


            public EmployeeViewHolder(View itemView) {
                super(itemView);

                mProfileImageView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                mNameView = (TextView) itemView.findViewById(R.id.textview_name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.textview_mobile_number);
                mDesignationView = (TextView) itemView.findViewById(R.id.textview_designation);
                mStatusView = (ImageView) itemView.findViewById(R.id.verification_status);
            }

            public void bindView(final int pos) {
                Employee employee = mEmployeeList.get(pos);

                mProfileImageView.setInformation(employee.getProfilePictureUrl(),
                        employee.getName(), employee.getMobileNumber());
                mNameView.setText(employee.getName());
                mMobileNumberView.setText(employee.getMobileNumber());

                if (employee.getDesignation() != null && !employee.getDesignation().isEmpty()) {
                    mDesignationView.setText(employee.getDesignation());
                    mDesignationView.setVisibility(View.VISIBLE);
                } else {
                    mDesignationView.setVisibility(View.GONE);
                }

                if (employee.getStatus().equals(Constants.BUSINESS_INVITATION_ACCEPTED)) {
                    mStatusView.setImageResource(R.drawable.ic_verified3x);
                    mStatusView.setColorFilter(null);
                } else if (employee.getStatus().equals(Constants.BUSINESS_STATUS_PENDING)) {
                    mStatusView.setImageResource(R.drawable.ic_cached_black_24dp);
                    mStatusView.setColorFilter(Color.GRAY);
                } else {
                    mStatusView.setImageResource(R.drawable.ic_notverified3x);
                    mStatusView.setColorFilter(null);
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_employee,
                    parent, false);

            EmployeeViewHolder vh = new EmployeeViewHolder(v);

            return vh;
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
    }
}
