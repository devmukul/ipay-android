package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;

public class SendMoneyRecheckFragment extends Fragment {
    private Button mContinueButton;
    private TextView mNameTextView;
    private ProfileImageView mProfileImageView;
    private TextView mIpayBalanceTextView;

    private Bundle bundle;

    private String imageUrl;
    private String name;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_money_recheck, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.GONE);
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mIpayBalanceTextView = (TextView) view.findViewById(R.id.ipay_balance_text_view);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_image_view);
        mContinueButton = (Button) view.findViewById(R.id.continue_button);
        if (getArguments() != null) {
            bundle = getArguments();
            mNameTextView.setText(bundle.getString("name"));
            mProfileImageView.setProfilePicture(bundle.getString("imageUrl"), false);
        }
        mIpayBalanceTextView.setText("Tk. " + SharedPrefManager.getUserBalance());
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SendMoneyActivity)getActivity()).switchToSendMoneyConfirmFragment(bundle);
            }
        });
    }
}
