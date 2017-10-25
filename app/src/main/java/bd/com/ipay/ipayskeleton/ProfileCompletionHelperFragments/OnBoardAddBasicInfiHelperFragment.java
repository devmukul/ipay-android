package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.app.ProgressDialog;
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
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.AddressInputSignUpView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.SetUserAddressResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

public class OnBoardAddBasicInfiHelperFragment extends Fragment {
    private HttpRequestPostAsyncTask mPersonalInfoAsyncTask;
    private SetUserAddressResponse mSetUserAddressResponse;

    private AddressInputSignUpView mPermanentAddressView;
    private AddressClass mPermanentAddress;

    private ProgressDialog mProgressDialog;

    private ImageView mStepOneImageView;
    private ImageView mStepTwoImageView;

    private Button mUploadButton;
    private Button mSkipButton;
    private View view;
    private TextView mDocumentHelperTextView;

    private ImageView mIdentificationDocumentImageView;

    ImageView back;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_onboard_add_personal_information, container, false);

        initializeViews(view);
        setButtonActions();

        if(!ProfileInfoCacheManager.isSwitchedFromSignup()) {
            mDocumentHelperTextView.setText("Add Basic Info");
            mIdentificationDocumentImageView.setImageResource(R.drawable.ic_onboard_basic_info_upload_helper);
        }
        else {
            mDocumentHelperTextView.setText("One Last Step");
            if(!ProfileInfoCacheManager.isIdentificationDocumentUploaded())
                mIdentificationDocumentImageView.setImageResource(R.drawable.ic_onboard_basic_info_upload_helper);
            else
                mIdentificationDocumentImageView.setImageResource(R.drawable.ic_onboard_step_three_done);

        }
        return view;
    }

    private void initializeViews(View view) {
        mProgressDialog = new ProgressDialog(getActivity());
        mUploadButton = (Button) view.findViewById(R.id.button_add_basic_info);
        mSkipButton = (Button) view.findViewById(R.id.button_skip);

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

                ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();

//                if(!ProfileInfoCacheManager.isBankInfoAdded() && !ProfileCompletionHelperActivity.isFromSignUp){
//                    ((ProfileCompletionHelperActivity) getActivity()).switchToAddNewBankHelperFragment();
//                }else if(!ProfileInfoCacheManager.isIntroductionAsked() && !ProfileCompletionHelperActivity.isFromSignUp){
//                    ((ProfileCompletionHelperActivity) getActivity()).switchToAskedIntroductionHelperFragment();
//                }else {
//                    ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
//                }
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
