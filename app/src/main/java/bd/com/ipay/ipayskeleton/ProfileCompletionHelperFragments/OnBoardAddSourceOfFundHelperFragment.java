package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bd.com.ipay.ipayskeleton.Activities.ProfileCompletionHelperActivity;
import bd.com.ipay.ipayskeleton.R;


public class OnBoardAddSourceOfFundHelperFragment extends Fragment {
    private Button mAddSourceOfFundButton;
    private Button mSkipButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboard_add_source_of_fund, container, false);
        initializeViews(view);
        return view;
    }

    public void initializeViews(View view) {
        mAddSourceOfFundButton = (Button) view.findViewById(R.id.button_add_source_of_fund);
        mSkipButton = (Button) view.findViewById(R.id.button_skip);
    }

    public void setButtonActions() {
        mAddSourceOfFundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
            }
        });
    }
}
