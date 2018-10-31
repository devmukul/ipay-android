package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.ContactFragments.IPayContactListFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments.TransactionHelperFragment;
import bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments.TopUpEnterNumberFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IPayMakePaymentFragment extends Fragment {

	private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
	private int transactionType;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		if (getArguments() != null) {
			transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
		}
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ipay_transaction_contact, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final Bundle bundle = new Bundle();
		bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
		final MakePaymentNewFragment iPayContactListFragment = new MakePaymentNewFragment();
		iPayContactListFragment.setArguments(bundle);
		getChildFragmentManager().beginTransaction().replace(R.id.contact_fragment_container, iPayContactListFragment).commit();

		final Button helperBottomSheetDismissButton = view.findViewById(R.id.helper_bottom_sheet_dismiss_button);
		final Toolbar toolbar = view.findViewById(R.id.toolbar);
		final LinearLayout helpBottomSheetLayout = view.findViewById(R.id.help_bottom_sheet_layout);

		if (getActivity() instanceof AppCompatActivity) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
			ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			if (actionBar != null)
				actionBar.setDisplayHomeAsUpEnabled(true);
		}

		if (getFragmentManager() != null) {
			final TransactionHelperFragment transactionHelperFragment = new TransactionHelperFragment();
			transactionHelperFragment.setArguments(getArguments());
			getFragmentManager().beginTransaction().replace(R.id.help_fragment_container, transactionHelperFragment).commit();
		}

		bottomSheetBehavior = BottomSheetBehavior.from(helpBottomSheetLayout);
		getActivity().setTitle(R.string.make_payment);
		if (SharedPrefManager.ifFirstMakePayment()) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
		} else {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
		}

		bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
			@Override
			public void onStateChanged(@NonNull View bottomSheet, int newState) {
				if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
					SharedPrefManager.setIfFirstMakePayment(false);
				}
			}

			@Override
			public void onSlide(@NonNull View bottomSheet, float slideOffset) {

			}
		});
		helperBottomSheetDismissButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
					bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
				}
			}
		});

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.transaction_contact_option_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
			case R.id.menu_help:
				if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
					if (getActivity() != null)
						Utilities.hideKeyboard(getActivity());
					bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
				}
			default:
				return super.onOptionsItemSelected(item);
		}
	}
}