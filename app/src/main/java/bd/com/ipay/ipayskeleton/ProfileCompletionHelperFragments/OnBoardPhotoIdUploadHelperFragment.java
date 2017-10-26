package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.ProfileCompletionHelperActivity;
import bd.com.ipay.ipayskeleton.Api.DocumentUploadApi.UploadIdentifierDocumentAsyncTask;
//import bd.com.ipay.ipayskeleton.CustomView.Dialogs.IdentificationDocumentSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class OnBoardPhotoIdUploadHelperFragment extends Fragment{
    private TextView mDocumentHelperTextView;
    private ImageView mIdentificationDocumentImageView;
    private ProfileImageView mProfileImageView;
    private Button mUploadButton;
    private Button mSkipButton;
    private ProgressDialog mProgressDialog;
    private String mSelectedImagePath = "";
    ImageView back;


    private static String IDENTIFICATION_DOCUMENT_HINT;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_on_board_identification_document, container, false);

        initializeViews(view);
        setButtonActions();
        return view;
    }

    private void initializeViews(View view) {
        mProgressDialog = new ProgressDialog(getActivity());
        mUploadButton = (Button) view.findViewById(R.id.button_upload_photo_id);
        mSkipButton = (Button) view.findViewById(R.id.button_skip);
        mProfileImageView  = (ProfileImageView) view.findViewById(R.id.profile_image);
        mDocumentHelperTextView  = (TextView) view.findViewById(R.id.profile_pic_upload_helper_title);
        mIdentificationDocumentImageView  = (ImageView) view.findViewById(R.id.document_id_helper_image);

        back  = (ImageView) view.findViewById(R.id.back);

        if (getActivity().getSupportFragmentManager().getBackStackEntryCount()<=1){
            back.setVisibility(View.INVISIBLE);
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

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
    }
}
