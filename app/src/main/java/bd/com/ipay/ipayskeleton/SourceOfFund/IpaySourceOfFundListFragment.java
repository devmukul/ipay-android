package bd.com.ipay.ipayskeleton.SourceOfFund;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.Activities.AddCardActivity;
import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManageBanksActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class IpaySourceOfFundListFragment extends Fragment implements View.OnClickListener {
    private View bankView;
    private View iPayBeneficiaryView;
    private View iPaySponsorView;
    private View cardView;
    private View divider;
    private View divider2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_source_of_fund_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        bankView = view.findViewById(R.id.bank_layout);
        iPaySponsorView = view.findViewById(R.id.sponsor_layout);
        iPayBeneficiaryView = view.findViewById(R.id.beneficiary_layout);
        divider = view.findViewById(R.id.divider2);
        divider2 = view.findViewById(R.id.divider3);
        cardView = view.findViewById(R.id.card_layout);
        iPayBeneficiaryView.setOnClickListener(this);
        iPaySponsorView.setOnClickListener(this);
        bankView.setOnClickListener(this);
        cardView.setOnClickListener(this);

        if (ProfileInfoCacheManager.isBusinessAccount()) {
            iPaySponsorView.setVisibility(View.GONE);
            iPayBeneficiaryView.setVisibility(View.GONE);
            cardView.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        }
        View backButton = view.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.bank_layout:
                if (ACLManager.hasServicesAccessibility(ServiceIdConstants.SEE_BANK_ACCOUNTS)) {
                    Intent intent = new Intent(getActivity(), ManageBanksActivity.class);
                    startActivity(intent);
                } else {
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                }
                break;
            case R.id.sponsor_layout:
                if(ACLManager.hasServicesAccessibility(ServiceIdConstants.GET_SOURCE_OF_FUND)) {
                    ((SourceOfFundActivity) getActivity()).switchToAddSponsorFragment();
                }else{
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                }
                break;
            case R.id.beneficiary_layout:
                if(ACLManager.hasServicesAccessibility(ServiceIdConstants.GET_SOURCE_OF_FUND)) {
                    ((SourceOfFundActivity) getActivity()).switchToAddBeneficiaryFragment();
                }else{
                    DialogUtils.showServiceNotAllowedDialog(getContext());
                }
                break;
            case R.id.card_layout:
                Intent intent1 = new Intent(getActivity(), AddCardActivity.class);
                startActivity(intent1);
        }
    }
}
