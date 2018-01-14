package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.Activities.ProfileVerificationHelperActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

public class OnBoardAddBasicInfoHelperFragment extends Fragment {
    private Button mUploadButton;
    private Button mSkipButton;
    private View view;
    private ImageView mBackButtonTop;

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
        mBackButtonTop = (ImageView) view.findViewById(R.id.back);

        if (getActivity().getSupportFragmentManager().getBackStackEntryCount() <= 1) {
            mBackButtonTop.setVisibility(View.INVISIBLE);
        }
    }

    public void setButtonActions() {
        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ProfileInfoCacheManager.isSourceOfFundAdded()) {
                    ((ProfileVerificationHelperActivity) getActivity()).switchToSourceOfFundHelperFragment();
                } else {
                    ((ProfileVerificationHelperActivity) getActivity()).switchToHomeActivity();
                }
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileVerificationHelperActivity) getActivity()).switchToBasicInfoEditFragment();
            }
        });

        mBackButtonTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
