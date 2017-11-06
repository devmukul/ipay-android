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
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

public class OnBoardPhotoIdUploadHelperFragment extends Fragment{

    private ImageView mUploadImageView;
    private Button mUploadButton;
    private Button mSkipButton;
    private ImageView mBackButtonTop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_board_identification_document, container, false);
        initializeViews(view);
        setButtonActions();
        return view;
    }

    private void initializeViews(View view) {
        mUploadImageView = (ImageView) view.findViewById(R.id.document_id_helper_image);
        mUploadButton = (Button) view.findViewById(R.id.button_upload_photo_id);
        mSkipButton = (Button) view.findViewById(R.id.button_skip);
        mBackButtonTop = (ImageView) view.findViewById(R.id.back);
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount()<=1){
            mBackButtonTop.setVisibility(View.INVISIBLE);
        }
    }
    public void setButtonActions() {

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ProfileInfoCacheManager.isSwitchedFromSignup()){
                    ((ProfileCompletionHelperActivity) getActivity()).switchToBasicInfoEditHelperFragment();
                }
                else{
                    if(!ProfileInfoCacheManager.isBasicInfoAdded()){
                        ((ProfileCompletionHelperActivity) getActivity()).switchToBasicInfoEditHelperFragment();
                    }else {
                        ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
                    }
                }
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileCompletionHelperActivity) getActivity()).switchToPhotoIdUploadFragment();
            }
        });

        mUploadImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileCompletionHelperActivity) getActivity()).switchToPhotoIdUploadFragment();
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
