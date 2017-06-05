package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class TransactionHistoryHolderFragment extends Fragment {

    private RadioGroup transactionHistoryTypeRadioGroup;
    private RadioButton pendingTransactionRadioButton;
    private RadioButton completedTransactionRadioButton;

    private TransactionHistoryCompletedFragment mProcessedTransactionHistoryCompletedFragment;
    private TransactionHistoryPendingFragment mPendingTransactionHistoryFragment;

    private View v;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_transaction_history_holder, container, false);

        transactionHistoryTypeRadioGroup = (RadioGroup) v.findViewById(R.id.transaction_history_type_radio_group);
        pendingTransactionRadioButton = (RadioButton) v.findViewById(R.id.radio_button_pending);
        completedTransactionRadioButton = (RadioButton) v.findViewById(R.id.radio_button_completed);

        transactionHistoryTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            @ValidateAccess
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radio_button_pending:
                        switchToPendingTransactionsFragment();
                        break;
                    case R.id.radio_button_completed:
                        switchToProcessedTransactionsFragment();
                        break;
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.ALL_TRANSACTION)) {
            if (ACLManager.hasServicesAccessibility(ServiceIdConstants.PENDING_TRANSACTION)) {
                pendingTransactionRadioButton.setChecked(true);
            } else if (ACLManager.hasServicesAccessibility(ServiceIdConstants.COMPLETED_TRANSACTION)) {
                completedTransactionRadioButton.setChecked(true);
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Remove search action of contacts
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

        if (menu.findItem(R.id.action_filter_by_service) != null)
            menu.findItem(R.id.action_filter_by_service).setVisible(true);
        if (menu.findItem(R.id.action_filter_by_date) != null)
            menu.findItem(R.id.action_filter_by_date).setVisible(true);
    }

    private void switchToProcessedTransactionsFragment() {
        mProcessedTransactionHistoryCompletedFragment = new TransactionHistoryCompletedFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mProcessedTransactionHistoryCompletedFragment).commit();
    }

    private void switchToPendingTransactionsFragment() {
        mPendingTransactionHistoryFragment = new TransactionHistoryPendingFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mPendingTransactionHistoryFragment).commit();
    }
}
