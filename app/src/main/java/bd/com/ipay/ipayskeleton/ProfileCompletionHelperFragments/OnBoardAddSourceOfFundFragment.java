package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ManageBanksActivity;
import bd.com.ipay.ipayskeleton.R;


public class OnBoardAddSourceOfFundFragment extends Fragment {
    private Button mAddBankButton;
    private Button mAddMoneyByCard;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_board_add_source_of_fund, container, false);
        initializeView(view);
        return view;
    }

    private void initializeView(View view) {
        mAddBankButton = (Button) view.findViewById(R.id.button_add_bank);
        mAddMoneyByCard = (Button) view.findViewById(R.id.button_add_money_by_card);
    }

    private void setButtonActions() {
        mAddMoneyByCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getActivity(), ManageBanksActivity.class);
                startActivity(intent);
            }
        });
        mAddBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                
            }
        });
    }
}
