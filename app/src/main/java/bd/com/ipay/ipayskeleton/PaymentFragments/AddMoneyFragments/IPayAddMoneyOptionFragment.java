package bd.com.ipay.ipayskeleton.PaymentFragments.AddMoneyFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Adapters.AddMoneyOptionAdapter;
import bd.com.ipay.ipayskeleton.Adapters.OnItemClickListener;
import bd.com.ipay.ipayskeleton.Model.AddMoneyOption;
import bd.com.ipay.ipayskeleton.PaymentFragments.IPayChooseBankOptionFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IPayAddMoneyOptionFragment extends Fragment {

	private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ipay_add_money_option, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final Toolbar toolbar = view.findViewById(R.id.toolbar);
		final RecyclerView addMoneyOptionRecyclerView = view.findViewById(R.id.add_money_option_recycler_view);
		final List<AddMoneyOption> addMoneyOptionList = Utilities.getAddMoneyOptions();
		final LinearLayout bankListBottomSheetLayout = view.findViewById(R.id.bank_option_bottom_sheet_layout);
		final ImageButton closeAddBankOptionButton = view.findViewById(R.id.close_add_bank_option_button);
		bottomSheetBehavior = BottomSheetBehavior.from(bankListBottomSheetLayout);

		if (getActivity() == null || !(getActivity() instanceof AppCompatActivity)) {
			return;
		}
		addMoneyOptionRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.HORIZONTAL));

		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		final ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
		if (supportActionBar != null) {
			supportActionBar.setDisplayHomeAsUpEnabled(true);
		}
		final AddMoneyOptionAdapter addMoneyOptionAdapter = new AddMoneyOptionAdapter(getActivity(), new OnItemClickListener() {
			@Override
			public void onItemClick(int position, View view) {
				final AddMoneyOption selectedAddMoneyOption = addMoneyOptionList.get(position);
				switch (selectedAddMoneyOption.getServiceId()) {
					case ServiceIdConstants.ADD_MONEY_BY_BANK:
						if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED || bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
							bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
							addBankOptionFragment = new IPayChooseBankOptionFragment();
							Bundle bundle = new Bundle();
							bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_ADD_MONEY_BY_BANK);
							addBankOptionFragment.setArguments(bundle);
							if (getFragmentManager() != null)
								getFragmentManager().beginTransaction()
										.replace(R.id.bank_option_fragment_container, addBankOptionFragment)
										.commit();
						}
						break;
					case ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD:
						BusinessRuleCacheManager.fetchBusinessRule(getContext(), IPayTransactionActionActivity.TRANSACTION_TYPE_ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD);
						Bundle bundle = new Bundle();
						if (getActivity() instanceof IPayTransactionActionActivity) {
							((IPayTransactionActionActivity) getActivity()).switchFragment(new IPayAddMoneyFromCardAmountInputFragment(), bundle, 1, true);
						}
						break;
				}
			}
		});
		addMoneyOptionAdapter.setItemList(addMoneyOptionList);
		addMoneyOptionRecyclerView.setAdapter(addMoneyOptionAdapter);

		closeAddBankOptionButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
					bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
					if (getFragmentManager() != null)
						getFragmentManager().beginTransaction().remove(addBankOptionFragment).commit();
				}
			}
		});
	}

	private Fragment addBankOptionFragment;

	public boolean onBackPressed() {
		if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
			bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
			return true;
		} else {
			return false;
		}
	}
}
