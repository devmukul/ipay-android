package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.Activities.ProfileCompletionHelperActivity;
import bd.com.ipay.ipayskeleton.R;

public class OnBoardAddBasicInfoHelperFragment extends Fragment {
    private Button mUploadButton;
    private Button mSkipButton;
    private View view;
    private ImageView back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_onboard_add_personal_information, container, false);

        initializeViews(view);
        setButtonActions();
        return view;
    }

    private void initializeViews(View view) {
        mUploadButton = (Button) view.findViewById(R.id.button_add_basic_info);
        mSkipButton = (Button) view.findViewById(R.id.button_skip);
        back  = (ImageView) view.findViewById(R.id.back);

        if (getActivity().getSupportFragmentManager().getBackStackEntryCount()<=1){
            back.setVisibility(View.INVISIBLE);
        }
    }

    public void setButtonActions() {
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileCompletionHelperActivity) getActivity()).switchToBasicInfoEditFragment();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
