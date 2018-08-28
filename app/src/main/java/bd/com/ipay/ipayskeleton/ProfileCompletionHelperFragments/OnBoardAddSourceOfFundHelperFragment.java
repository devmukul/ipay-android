package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManageBanksActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileVerificationHelperActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;


public class OnBoardAddSourceOfFundHelperFragment extends Fragment {
    private Button mAddMoneyByCardButton;
    private Button mSkipButton;
    private Button mAddBankButton;
    private ImageView mBackButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboard_add_source_of_fund, container, false);
        initializeViews(view);
        return view;
    }

    public void initializeViews(View view) {
        mAddMoneyByCardButton = (Button) view.findViewById(R.id.button_add_money_by_card);
        mSkipButton = (Button) view.findViewById(R.id.button_skip);
        mAddBankButton = (Button) view.findViewById(R.id.button_add_bank);
        mBackButton = (ImageView) view.findViewById(R.id.back);
        setButtonActions();
    }

    public void setButtonActions() {
        mAddMoneyByCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ACLManager.hasServicesAccessibility(ServiceIdConstants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD)) {
                    Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
                    intent.putExtra(Constants.FROM_ON_BOARD, true);
                    intent.putExtra(Constants.TAG, "CARD");
                    startActivity(intent);
                } else {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                }
            }
        });
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileVerificationHelperActivity) getActivity()).switchToHomeActivity();
            }
        });
        mAddBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ACLManager.hasServicesAccessibility(ServiceIdConstants.ADD_MONEY_BY_BANK)) {
                    Intent intent = new Intent(getActivity(), ManageBanksActivity.class);
                    intent.putExtra(Constants.FROM_ON_BOARD, true);
                    startActivity(intent);
                } else {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                }
            }
        });

        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mBackButton.setVisibility(View.INVISIBLE);
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

    }
}
