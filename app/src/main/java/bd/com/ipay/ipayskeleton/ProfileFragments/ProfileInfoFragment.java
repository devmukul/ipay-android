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
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ProfileInfoFragment extends Fragment {

    private RoundedImageView mProfilePictureView;
    private TextView mNameView;
    private TextView mMobileNumberView;
    private ImageView mVerificationStatusView;

    private SharedPreferences pref;

    private String mName = "";
    private String mMobileNumber = "";
    private String profileImageUrl = "";

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

        mProfilePictureView = (RoundedImageView) v.findViewById(R.id.profile_picture);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mVerificationStatusView = (ImageView) v.findViewById(R.id.textview_verification_status);

        mBasicInfo = (IconifiedTextViewWithButton) v.findViewById(R.id.basic_info);
        mEmail = (IconifiedTextViewWithButton) v.findViewById(R.id.email);
        mAddress = (IconifiedTextViewWithButton) v.findViewById(R.id.present_address);
        mIntroducer = (IconifiedTextViewWithButton) v.findViewById(R.id.introducer);
        mDocuments = (IconifiedTextViewWithButton) v.findViewById(R.id.documents);
        mProfileCompleteness = (IconifiedTextViewWithButton) v.findViewById(R.id.profile_completion);

        mName = pref.getString(Constants.USER_NAME, "");
        mMobileNumber = pref.getString(Constants.USERID, "");
        profileImageUrl = pref.getString(Constants.PROFILE_PICTURE, "");

        mVerificationStatus = pref.getString(Constants.VERIFICATION_STATUS, "");

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


    private void setProfileInformation() {
        setProfilePicture(profileImageUrl);

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

    private void setProfilePicture(String url) {
        try {
            if (!url.equals("")) {
                Glide.with(getActivity())
                        .load(url)
                        .crossFade()
                        .error(R.drawable.ic_person)
                        .transform(new CircleTransform(getActivity()))
                        .into(mProfilePictureView);
            } else {
                Glide.with(getActivity())
                        .load(R.drawable.ic_person)
                        .crossFade()
                        .into(mProfilePictureView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
