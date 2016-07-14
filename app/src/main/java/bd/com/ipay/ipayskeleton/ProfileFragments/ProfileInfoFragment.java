package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ProfileInfoFragment extends Fragment {

    private ProfileImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private ImageView mVerificationStatusView;

    private SharedPreferences pref;

    private String mName = "";
    private String mMobileNumber = "";

    private String mVerificationStatus = null;

    private IconifiedTextViewWithButton mBasicInfo;
    private IconifiedTextViewWithButton mEmail;
    private IconifiedTextViewWithButton mDocuments;
    private IconifiedTextViewWithButton mIntroducer;
    private IconifiedTextViewWithButton mAddress;
    private IconifiedTextViewWithButton mProfileCompleteness;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profile_information, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);
        getActivity().setTitle(R.string.account);

        mProfilePictureView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (ImageView) v.findViewById(R.id.textview_verification_status);

        mBasicInfo = (IconifiedTextViewWithButton) v.findViewById(R.id.basic_info);
        mEmail = (IconifiedTextViewWithButton) v.findViewById(R.id.email);
        mAddress = (IconifiedTextViewWithButton) v.findViewById(R.id.present_address);
        mIntroducer = (IconifiedTextViewWithButton) v.findViewById(R.id.introducer);
        mDocuments = (IconifiedTextViewWithButton) v.findViewById(R.id.documents);
        mProfileCompleteness = (IconifiedTextViewWithButton) v.findViewById(R.id.profile_completion);

        ProfileInfoCacheManager profileInfoCacheManager = new ProfileInfoCacheManager(getActivity());

        mName = profileInfoCacheManager.getName();
        mMobileNumber = profileInfoCacheManager.getMobileNumber();
        mVerificationStatus = profileInfoCacheManager.getVerificationStatus();

        setProfileInformation();

        mBasicInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToBasicInfoFragment();
            }
        });

        mEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToEmailFragment();
            }
        });

        mAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToAddressFragment();
            }
        });

        mIntroducer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) getActivity()).switchToIntroducerFragment();
            }
        });

        mDocuments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToIdentificationDocumentListFragment();
            }
        });

        mProfileCompleteness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ProfileActivity) getActivity()).switchToProfileCompletionFragment();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mProfilePictureView.setInformation(mMobileNumber, true);
    }

    private void setProfileInformation() {
        mMobileNumberView.setText(mMobileNumber);
        mNameView.setText(mName);

        if (mVerificationStatus != null) {
            if (mVerificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                mVerificationStatusView.setVisibility(View.VISIBLE);
            } else {
                mVerificationStatusView.setVisibility(View.GONE);
            }
        }
    }

}
