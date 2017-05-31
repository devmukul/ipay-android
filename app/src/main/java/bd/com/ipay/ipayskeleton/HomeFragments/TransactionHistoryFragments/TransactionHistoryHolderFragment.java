package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class TransactionHistoryHolderFragment extends Fragment {

    private CheckBox mProcessedTransactionsSelector;
    private CheckBox mPendingTransactionsSelector;

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

        mProcessedTransactionsSelector = (CheckBox) v.findViewById(R.id.checkbox_processed);
        mPendingTransactionsSelector = (CheckBox) v.findViewById(R.id.checkbox_pending);

        mProcessedTransactionsSelector.setChecked(false);
        mPendingTransactionsSelector.setChecked(true);

        mProcessedTransactionsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));
        mPendingTransactionsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));


        mProcessedTransactionsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mProcessedTransactionsSelector.isChecked()) {
                    if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.COMPLETED_TRANSACTION)) {
                        mProcessedTransactionsSelector.setChecked(true);
                        mPendingTransactionsSelector.setChecked(false);

                        mPendingTransactionsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));
                        mProcessedTransactionsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));
                        DialogUtils.showServiceNotAllowedDialog(getContext());
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, new Fragment()).commit();
                        return;
                    }
                    switchToProcessedTransactionsFragment();
                } else {
                    mPendingTransactionsSelector.setChecked(false);
                    mProcessedTransactionsSelector.setChecked(true);
                }
            }
        });

        mPendingTransactionsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPendingTransactionsSelector.isChecked()) {
                    if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.PENDING_TRANSACTION)) {
                        mPendingTransactionsSelector.setChecked(true);
                        mProcessedTransactionsSelector.setChecked(false);

                        mProcessedTransactionsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));
                        mPendingTransactionsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));
                        DialogUtils.showServiceNotAllowedDialog(getContext());
                        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, new Fragment()).commit();
                        return;
                    }
                    switchToPendingTransactionsFragment();
                } else {
                    mPendingTransactionsSelector.setChecked(true);
                    mProcessedTransactionsSelector.setChecked(false);
                }
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.ALL_TRANSACTION)) {
            if (ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.PENDING_TRANSACTION)) {
                switchToPendingTransactionsFragment();
            } else if (ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.COMPLETED_TRANSACTION)) {
                switchToProcessedTransactionsFragment();
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
        mProcessedTransactionsSelector.setChecked(true);
        mPendingTransactionsSelector.setChecked(false);

        mProcessedTransactionsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));
        mPendingTransactionsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));

        mProcessedTransactionHistoryCompletedFragment = new TransactionHistoryCompletedFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mProcessedTransactionHistoryCompletedFragment).commit();
    }

    private void switchToPendingTransactionsFragment() {
        mProcessedTransactionsSelector.setChecked(false);
        mPendingTransactionsSelector.setChecked(true);

        mProcessedTransactionsSelector.setTextColor(getContext().getResources().getColor(R.color.colorTextPrimary));
        mPendingTransactionsSelector.setTextColor(getContext().getResources().getColor(android.R.color.white));

        mPendingTransactionHistoryFragment = new TransactionHistoryPendingFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_transaction_history, mPendingTransactionHistoryFragment).commit();
    }
}
