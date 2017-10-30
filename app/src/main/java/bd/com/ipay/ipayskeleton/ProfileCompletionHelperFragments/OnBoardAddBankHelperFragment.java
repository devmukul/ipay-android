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

import bd.com.ipay.ipayskeleton.Activities.ProfileCompletionHelperActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.AddressInputSignUpView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Address.SetUserAddressResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;

public class OnBoardAddBankHelperFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_onboard_add_bank_helper, container, false);

        initializeViews(view);
        setButtonActions();
        return view;
    }

    private void initializeViews(View view) {
        mProgressDialog = new ProgressDialog(getActivity());
        mUploadButton = (Button) view.findViewById(R.id.button_add_basic_info);
        mSkipButton = (Button) view.findViewById(R.id.button_skip);

    }

    public void setButtonActions() {

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!ProfileInfoCacheManager.isIntroductionAsked() && !ProfileInfoCacheManager.isSwitchedFromSignup()){
                    ((ProfileCompletionHelperActivity) getActivity()).switchToAskedIntroductionHelperFragment();
                }else {
                    ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
                }
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileCompletionHelperActivity) getActivity()).switchToAddNewBankFragment();
            }
        });
    }
}
