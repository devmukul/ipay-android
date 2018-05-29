package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.R;

public class SendMoneySuccessFragment extends BaseFragment {

    private TextView mNameTextView;
    private TextView mAmountTextView;

    private ImageView mCancelButton;

    private ProfileImageView mSenderProfileImageView;
    private ProfileImageView mReceiverProfileImageView;

    private TextView mDescriptionTextView;

    private String mAmount;
    private String mName;
    private String mSenderImageUrl;
    private String mReceiverImageUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_money_success, container, false);
        ((SendMoneyActivity) getActivity()).toolbar.setVisibility(View.GONE);
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mAmountTextView = (TextView) view.findViewById(R.id.amount_text_view);
        mCancelButton = (ImageView) view.findViewById(R.id.cancel_button);
        mSenderProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_picture_sender);
        mReceiverProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_picture_receiver);
        mDescriptionTextView = (TextView) view.findViewById(R.id.success_description_text_view);
        getDataFromBundle();
    }

    private void getDataFromBundle() {
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            mName = bundle.getString("name");
            mSenderImageUrl = bundle.getString("receiverImageUrl");
            mReceiverImageUrl = bundle.getString("senderImageUrl");
            mAmount = bundle.getString("amount");
            setUpTextViewsAndButtonActions();
        }
    }

    private void setUpTextViewsAndButtonActions() {
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        mNameTextView.setText(mName);

        String setString = "SUCCESSFULLY SENT TK." + mAmount + " TO";
        mAmountTextView.setText(setString, TextView.BufferType.SPANNABLE);
        ForegroundColorSpan span = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
        ((Spannable) mAmountTextView.getText()).setSpan(span, 18, 18 + 3 + mAmount.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        mSenderProfileImageView.setProfilePicture(mSenderImageUrl, false);
        mReceiverProfileImageView.setProfilePicture(mReceiverImageUrl, false);
        mDescriptionTextView.setText(mDescriptionTextView.getText().toString().replace("receiver", mName));
    }
}
