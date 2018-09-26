package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import bd.com.ipay.ipayskeleton.Activities.ProfileVerificationHelperActivity;
import bd.com.ipay.ipayskeleton.R;

public class OnBoardAskForIntroductionHelperFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_onboard_ask_for_introduction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Button mUploadButton = view.findViewById(R.id.button_add_basic_info);
        final Button mSkipButton = view.findViewById(R.id.button_skip);


        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ProfileVerificationHelperActivity)
                    ((ProfileVerificationHelperActivity) getActivity()).switchToHomeActivity();
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getActivity() instanceof ProfileVerificationHelperActivity)
                    ((ProfileVerificationHelperActivity) getActivity()).switchToContactFragment();
            }
        });
    }
}
