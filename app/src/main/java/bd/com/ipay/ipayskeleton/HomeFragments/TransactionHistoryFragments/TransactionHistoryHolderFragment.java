package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bd.com.ipay.android.fragment.transaction.IPayTransactionHistoryFragment;
import bd.com.ipay.android.utility.TransactionHistoryType;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class TransactionHistoryHolderFragment extends Fragment {

	private RadioButton mPendingTransactionRadioButton;
	private RadioButton mCompletedTransactionRadioButton;

	private Fragment transactionHistoryPendingFragment;
	private Fragment transactionHistoryCompletedFragment;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_transaction_history_holder,
				container, false);

		RadioGroup mTransactionHistoryTypeRadioGroup = view.
				findViewById(R.id.transaction_history_type_radio_group);
		mPendingTransactionRadioButton = view.findViewById(R.id.pending_transaction_history_radio_button);
		mCompletedTransactionRadioButton = view.findViewById(R.id.completed_transaction_history_radio_button);

		mTransactionHistoryTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			@ValidateAccess
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				switch (checkedId) {
					case R.id.pending_transaction_history_radio_button:
						switchToPendingTransactionsFragment();
						break;
					case R.id.completed_transaction_history_radio_button:
						switchToProcessedTransactionsFragment();
						break;
				}
			}
		});

		return view;
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
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
		if (transactionHistoryCompletedFragment == null) {
			transactionHistoryCompletedFragment = new IPayTransactionHistoryFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(IPayTransactionHistoryFragment.TRANSACTION_HISTORY_TYPE_KEY,
					TransactionHistoryType.COMPLETED);
			bundle.putBoolean(IPayTransactionHistoryFragment.HAS_TRANSACTION_SEARCH_KEY,
					true);
			transactionHistoryCompletedFragment.setArguments(bundle);
		}
		getChildFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.fade_in_enter, R.anim.fade_out)
				.replace(R.id.fragment_container_transaction_history,
						transactionHistoryCompletedFragment)
				.commit();

	}

	private void switchToPendingTransactionsFragment() {
		if (transactionHistoryPendingFragment == null) {
			transactionHistoryPendingFragment = new IPayTransactionHistoryFragment();
			Bundle bundle = new Bundle();
			bundle.putSerializable(IPayTransactionHistoryFragment.TRANSACTION_HISTORY_TYPE_KEY,
					TransactionHistoryType.PENDING);
			bundle.putBoolean(IPayTransactionHistoryFragment.HAS_TRANSACTION_SEARCH_KEY,
					false);
			transactionHistoryPendingFragment.setArguments(bundle);
		}
		getChildFragmentManager()
				.beginTransaction()
				.setCustomAnimations(R.anim.fade_in_enter, R.anim.fade_out)
				.replace(R.id.fragment_container_transaction_history,
						transactionHistoryPendingFragment)
				.commit();
	}
}
