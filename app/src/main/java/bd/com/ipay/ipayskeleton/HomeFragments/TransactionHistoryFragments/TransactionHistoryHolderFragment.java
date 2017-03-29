package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.FCM.FCMPushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

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

        mProcessedTransactionsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToProcessedTransactionsFragment();
            }
        });

        mPendingTransactionsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToPendingTransactionsFragment();
            }
        });

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mTransactionHistoryBroadcastReceiver,
                new IntentFilter(Constants.TRANSACTION_HISTORY_UPDATE_BROADCAST));

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switchToPendingTransactionsFragment();
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

    private final BroadcastReceiver mTransactionHistoryBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (mProcessedTransactionsSelector.isChecked() && mProcessedTransactionHistoryCompletedFragment != null) {
                TransactionHistory transactionHistory = intent.getParcelableExtra(Constants.TRANSACTION_DETAILS);
                mProcessedTransactionHistoryCompletedFragment.addLastTransactionHistory(transactionHistory);
            } else if (mPendingTransactionsSelector.isChecked() && mPendingTransactionHistoryFragment != null) {
                TransactionHistory transactionHistory = intent.getParcelableExtra(Constants.TRANSACTION_DETAILS);
                mPendingTransactionHistoryFragment.addLastTransactionHistory(transactionHistory);
            }
        }
    };

}
