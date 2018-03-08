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
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;

public class OnBoardPhotoIdUploadHelperFragment extends Fragment {

    private ImageView mUploadImageView;
    private Button mUploadButton;
    private Button mSkipButton;
    private ImageView mBackButtonTop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_onboard_identification_document, container, false);
        initializeViews(view);
        setButtonActions();
        return view;
    }

    private void initializeViews(View view) {
        mUploadImageView = (ImageView) view.findViewById(R.id.document_id_helper_image);
        mUploadButton = (Button) view.findViewById(R.id.button_upload_photo_id);
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
                if (ProfileInfoCacheManager.isSwitchedFromSignup() && SharedPrefManager.isBangladesh()) {
                    ((ProfileVerificationHelperActivity) getActivity()).switchToBasicInfoEditHelperFragment();
                } else {
                    if (!ProfileInfoCacheManager.isBasicInfoAdded() && SharedPrefManager.isBangladesh()) {
                        ((ProfileVerificationHelperActivity) getActivity()).switchToBasicInfoEditHelperFragment();
                    }
                    if (!ProfileInfoCacheManager.isSourceOfFundAdded()) {
                        ((ProfileVerificationHelperActivity) getActivity()).switchToSourceOfFundHelperFragment();
                    } else {
                        ((ProfileVerificationHelperActivity) getActivity()).switchToHomeActivity();
                    }
                }
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileVerificationHelperActivity) getActivity()).switchToIdentificationDocumentListFragment();
            }
        });

        mUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileVerificationHelperActivity) getActivity()).switchToIdentificationDocumentListFragment();
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
