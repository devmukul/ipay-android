package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Notification.BusinessRoleManagerInvitation;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessRoleReviewFragment extends Fragment {
    private ProfileImageView mProfileImageView;
    private Button mAcceptButton;
    private Button mRejectButton;
    private Bundle mBundle;
    private BusinessRoleManagerInvitation mBusinessRoleManagerInvitation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_business_role_review, container, false);
        return v;
    }

    public void setUpViews(View v) {
        mAcceptButton = (Button) v.findViewById(R.id.button_accept);
        mRejectButton = (Button) v.findViewById(R.id.button_reject);
        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        if (getArguments() != null) {
            mBundle = getArguments();
            String jsonString = mBundle.getString(Constants.BUSINESS_ROLE_REQUEST);
            Gson gson = new Gson();
            mBusinessRoleManagerInvitation = gson.fromJson(jsonString, BusinessRoleManagerInvitation.class);
           // Uri ImageUri = mBusinessRoleManagerInvitation.getImageUrl();
            //mProfileImageView.setProfilePicture();
        }
    }
}
