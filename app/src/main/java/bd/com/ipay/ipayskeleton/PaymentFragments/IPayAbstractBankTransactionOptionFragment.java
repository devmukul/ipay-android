package bd.com.ipay.ipayskeleton.PaymentFragments;

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
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.R;

public abstract class IPayAbstractBankTransactionOptionFragment extends Fragment {

	private int transactionType;

	private TextView headerTextView;
	private TextView messageTextView;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null)
			transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID);
	}

	@Nullable
	@Override
	public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ipay_bank_transaction_option, container, false);
	}

	@Override
	public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final Toolbar toolbar = view.findViewById(R.id.toolbar);
		headerTextView = view.findViewById(R.id.header_text_view);
		messageTextView = view.findViewById(R.id.message_text_view);

		if (getActivity() == null || !(getActivity() instanceof AppCompatActivity) || transactionType == IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID) {
			return;
		}

		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}

		final Fragment addBankOptionFragment = new IPayChooseBankOptionFragment();
		Bundle bundle = new Bundle();
		bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
		addBankOptionFragment.setArguments(bundle);

		if (getFragmentManager() != null)
			getFragmentManager().beginTransaction()
					.replace(R.id.bank_option_fragment_container, addBankOptionFragment)
					.commit();

		setupViewProperties();
	}

	protected abstract void setupViewProperties();

	protected void setHeaderText(CharSequence headerText) {
		headerTextView.setText(headerText, TextView.BufferType.SPANNABLE);
	}

	protected void setMessageText(CharSequence messageText) {
		messageTextView.setText(messageText, TextView.BufferType.SPANNABLE);
	}
}
