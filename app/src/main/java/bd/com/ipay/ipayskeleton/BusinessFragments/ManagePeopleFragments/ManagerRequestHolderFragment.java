package bd.com.ipay.ipayskeleton.BusinessFragments.ManagePeopleFragments;

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
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ManagerList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.ManagerListResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.PendingInvitationList;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Business.Manager.PendingManagerListResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class ManagerRequestHolderFragment extends Fragment implements HttpResponseListener{

    private RadioButton mPendingTransactionRadioButton;
    private RadioButton mCompletedTransactionRadioButton;
    private Button mAddNewEmployee;
    private Button mInviteNewEmployee;

    private RelativeLayout mLoadingLayout;
    private RelativeLayout mBlankLayout;
    private RelativeLayout mEmployeeListLayout;

    private HttpRequestGetAsyncTask mGetAllAcceptedEmployeeAsyncTask;
    private ManagerListResponse mGetAllAcceptedEmployeesResponse;
    public static List<ManagerList> mAcceptedEmployeeList;

    private HttpRequestGetAsyncTask mGetAllPendingEmployeeAsyncTask;
    private PendingManagerListResponse mGetAllPendingEmployeesResponse;
    public static List<PendingInvitationList> mPendingEmployeeList;


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

        mInviteNewEmployee = (Button) view.findViewById(R.id.invite_employee_1);
        mInviteNewEmployee.setOnClickListener(new View.OnClickListener() {
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
                Constants.BASE_URL_MM + Constants.URL_GET_EMPLOYEE_LIST, getActivity(), this,false);
        mGetAllAcceptedEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getPendingEmployeeList() {
        if (mGetAllPendingEmployeeAsyncTask != null)
            return;

        mGetAllPendingEmployeeAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PENDING_EMPLOYEE_LIST,
                Constants.BASE_URL_MM + Constants.URL_GET_PENDING_EMPLOYEE_LIST, getActivity(), this,false);
        mGetAllPendingEmployeeAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void switchToProcessedTransactionsFragment() {
        ManagerRequestAcceptedFragment mProcessedTransactionHistoryCompletedFragment = new ManagerRequestAcceptedFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mProcessedTransactionHistoryCompletedFragment).commit();
    }

    private void switchToPendingTransactionsFragment() {
        ManagerRequestPendingFragment mPendingTransactionHistoryFragment = new ManagerRequestPendingFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mPendingTransactionHistoryFragment).commit();
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result,getContext(),null)) {
            mGetAllAcceptedEmployeeAsyncTask = null;
            mGetAllPendingEmployeeAsyncTask = null;
            return;
        }

        Gson gson = new Gson();
        if (result.getApiCommand().equals(Constants.COMMAND_GET_ACCEPTED_EMPLOYEE_LIST)) {
            try {
                mGetAllAcceptedEmployeesResponse = gson.fromJson(result.getJsonString(), ManagerListResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mAcceptedEmployeeList = mGetAllAcceptedEmployeesResponse.getManagerList();
                    getPendingEmployeeList();
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

            mGetAllAcceptedEmployeeAsyncTask = null;
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_PENDING_EMPLOYEE_LIST)) {
            try {
                mGetAllPendingEmployeesResponse = gson.fromJson(result.getJsonString(), PendingManagerListResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mPendingEmployeeList = mGetAllPendingEmployeesResponse.getPendingInvitationList();

                    if (mAcceptedEmployeeList.size()>0) {
                        mLoadingLayout.setVisibility(View.GONE);
                        mBlankLayout.setVisibility(View.GONE);
                        mEmployeeListLayout.setVisibility(View.VISIBLE);
                        mCompletedTransactionRadioButton.setChecked(true);
                    } else if (mPendingEmployeeList.size()>0) {
                        mLoadingLayout.setVisibility(View.GONE);
                        mBlankLayout.setVisibility(View.GONE);
                        mEmployeeListLayout.setVisibility(View.VISIBLE);
                        mPendingTransactionRadioButton.setChecked(true);
                    }else{
                        mLoadingLayout.setVisibility(View.GONE);
                        mBlankLayout.setVisibility(View.VISIBLE);
                        mEmployeeListLayout.setVisibility(View.GONE);
                    }
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

            mGetAllPendingEmployeeAsyncTask = null;
        }
    }
}
