package bd.com.ipay.ipayskeleton.ManagePeopleFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ManagerList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ManagerListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.PendingInvitationList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.PendingManagerListResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class EmployeeRequestHolderFragment extends Fragment implements HttpResponseListener{

    private RadioButton mPendingTransactionRadioButton;
    private RadioButton mCompletedTransactionRadioButton;
    private Button mAddNewEmployee;
    private Button mAddNewEmployee_1;

    private RelativeLayout mLoadingLayout;
    private RelativeLayout mBlankLayout;
    private RelativeLayout mEmployeeListLayout;

    public static List<ManagerList> mAcceptedEmployeeList;
    private HttpRequestGetAsyncTask mGetAllAcceptedEmployeeAsyncTask;
    private ManagerListResponse mGetAllAcceptedEmployeesResponse;

    public static List<PendingInvitationList> mPendingEmployeeList;
    private HttpRequestGetAsyncTask mGetAllPendingEmployeeAsyncTask;
    private PendingManagerListResponse mGetAllPendingEmployeesResponse;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_request_holder, container, false);
        getActivity().setTitle(R.string.manage_people);

        mLoadingLayout = (RelativeLayout) view.findViewById(R.id.loading_layout);
        mBlankLayout = (RelativeLayout) view.findViewById(R.id.invite_layout);
        mEmployeeListLayout = (RelativeLayout) view.findViewById(R.id.manager_list_layout);

        mAddNewEmployee = (Button) view.findViewById(R.id.invite_employee);
        mAddNewEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ManagePeopleActivity) getActivity()).switchToEmployeeInformationFragment();
            }
        });

        mAddNewEmployee_1 = (Button) view.findViewById(R.id.invite_employee_1);
        mAddNewEmployee_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ManagePeopleActivity) getActivity()).switchToEmployeeInformationFragment();
            }
        });


        RadioGroup mTransactionHistoryTypeRadioGroup = (RadioGroup) view.findViewById(R.id.employee_request_radio_group);
        mPendingTransactionRadioButton = (RadioButton) view.findViewById(R.id.radio_button_pending);
        mCompletedTransactionRadioButton = (RadioButton) view.findViewById(R.id.radio_button_accepted);

        mTransactionHistoryTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            @ValidateAccess
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                System.out.println("FTEST   "+checkedId +" "+R.id.radio_button_pending+" "+R.id.radio_button_accepted);

                switch (checkedId) {
                    case R.id.radio_button_pending:
                        switchToPendingTransactionsFragment();
                        break;
                    case R.id.radio_button_accepted:
                        switchToProcessedTransactionsFragment();
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getAcceptedEmployeeList();
    }

    private void getAcceptedEmployeeList() {
        mLoadingLayout.setVisibility(View.VISIBLE);

        if (mGetAllAcceptedEmployeeAsyncTask != null)
            return;

        mGetAllAcceptedEmployeeAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_ACCEPTED_EMPLOYEE_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_EMPLOYEE_LIST, getActivity(), this);
        mGetAllAcceptedEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPendingEmployeeList() {
        if (mGetAllPendingEmployeeAsyncTask != null)
            return;

        mGetAllPendingEmployeeAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PENDING_EMPLOYEE_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_PENDING_EMPLOYEE_LIST, getActivity(), this);
        mGetAllPendingEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void switchToProcessedTransactionsFragment() {
        EmployeeRequestAcceptedFragment mProcessedTransactionHistoryCompletedFragment = new EmployeeRequestAcceptedFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mProcessedTransactionHistoryCompletedFragment).commit();
    }

    private void switchToPendingTransactionsFragment() {
        EmployeeRequestPendingFragment mPendingTransactionHistoryFragment = new EmployeeRequestPendingFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mPendingTransactionHistoryFragment).commit();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetAllAcceptedEmployeeAsyncTask = null;
            mGetAllPendingEmployeeAsyncTask = null;

            if (getActivity() != null)
                Toaster.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT);
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_ACCEPTED_EMPLOYEE_LIST)) {
            try {
                mGetAllAcceptedEmployeesResponse = gson.fromJson(result.getJsonString(), ManagerListResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mAcceptedEmployeeList = mGetAllAcceptedEmployeesResponse.getManagerList();
                    System.out.println("FTEST " + mAcceptedEmployeeList.size());
                    getPendingEmployeeList();
                } else {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), "Failed to fetch manager list", Toast.LENGTH_LONG);
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

            mGetAllAcceptedEmployeeAsyncTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_EMPLOYEE_LIST)) {
            try {
                mGetAllPendingEmployeesResponse = gson.fromJson(result.getJsonString(), PendingManagerListResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mPendingEmployeeList = mGetAllPendingEmployeesResponse.getPendingInvitationList();


                    System.out.println("FTEST " + mPendingEmployeeList.size());


                    if (mAcceptedEmployeeList.size()>0) {
                        mLoadingLayout.setVisibility(View.GONE);
                        mBlankLayout.setVisibility(View.GONE);
                        mEmployeeListLayout.setVisibility(View.VISIBLE);
                        mCompletedTransactionRadioButton.setChecked(true);
                        System.out.println("FTEST1 " + mPendingEmployeeList.size());
                    } else if (mPendingEmployeeList.size()>0) {
                        mLoadingLayout.setVisibility(View.GONE);
                        mBlankLayout.setVisibility(View.GONE);
                        mEmployeeListLayout.setVisibility(View.VISIBLE);

                        System.out.println("FTEST2 " + mPendingEmployeeList.size());
                        mPendingTransactionRadioButton.setChecked(true);
                    }else{
                        mLoadingLayout.setVisibility(View.GONE);
                        mBlankLayout.setVisibility(View.VISIBLE);
                        mEmployeeListLayout.setVisibility(View.GONE);
                    }
                } else {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), "Failed to fetch manager list", Toast.LENGTH_LONG);
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

            mGetAllPendingEmployeeAsyncTask = null;
        }
    }
}
