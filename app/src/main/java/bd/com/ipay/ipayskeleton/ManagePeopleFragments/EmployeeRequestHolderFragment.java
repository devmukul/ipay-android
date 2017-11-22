package bd.com.ipay.ipayskeleton.ManagePeopleFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManagePeopleActivity;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class EmployeeRequestHolderFragment extends Fragment {

    private RadioButton mPendingTransactionRadioButton;
    private RadioButton mCompletedTransactionRadioButton;
    private Button mAddNewEmployee;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_employee_request_holder, container, false);
        getActivity().setTitle(R.string.manage_people);

        mAddNewEmployee = (Button) view.findViewById(R.id.invite_employee);
        mAddNewEmployee.setOnClickListener(new View.OnClickListener() {
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

                System.out.println("Test   "+checkedId +" "+R.id.radio_button_pending+" "+R.id.radio_button_accepted);

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
        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.COMPLETED_TRANSACTION)) {
            mCompletedTransactionRadioButton.setChecked(true);
        } else if (ACLManager.hasServicesAccessibility(ServiceIdConstants.PENDING_TRANSACTION)) {
            mPendingTransactionRadioButton.setChecked(true);
        }
    }

    private void switchToProcessedTransactionsFragment() {
        EmployeeRequestAcceptedFragment mProcessedTransactionHistoryCompletedFragment = new EmployeeRequestAcceptedFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mProcessedTransactionHistoryCompletedFragment).commit();
    }

    private void switchToPendingTransactionsFragment() {
        EmployeeRequestPendingFragment mPendingTransactionHistoryFragment = new EmployeeRequestPendingFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mPendingTransactionHistoryFragment).commit();
    }
}
