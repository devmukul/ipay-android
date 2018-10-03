package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.R;

public class IPayWithdrawMoneyOptionFragment extends Fragment {

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ipay_withdraw_money_option, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final Toolbar toolbar = view.findViewById(R.id.toolbar);

		if (getActivity() == null || !(getActivity() instanceof AppCompatActivity)) {
			return;
		}

		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}
		final Fragment addBankOptionFragment = new IPayChooseBankOptionFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_WITHDRAW_MONEY);
		addBankOptionFragment.setArguments(bundle);
		if (getFragmentManager() != null)
			getFragmentManager().beginTransaction()
					.replace(R.id.bank_option_fragment_container, addBankOptionFragment)
					.commit();
	}
}
