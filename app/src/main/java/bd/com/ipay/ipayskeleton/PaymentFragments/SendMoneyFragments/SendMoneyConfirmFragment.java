package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.R;


public class SendMoneyConfirmFragment extends Fragment {
    private EditText mNoteEditText;
    private EditText mPinEditText;
    private TextView mNameTextView;
    private ProfileImageView mProfileImageView;
    private Button mSendMoneyButton;
    private Bundle bundle;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_send_money_confirm, container, false);
        setUpViews(view);
        return view;
    }

    private void setUpViews(View view) {
        mNoteEditText = (EditText) view.findViewById(R.id.note_edit_text);
        mPinEditText = (EditText) view.findViewById(R.id.pin_edit_text);
        mProfileImageView = (ProfileImageView) view.findViewById(R.id.profile_image_view);
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        mNameTextView = (TextView) view.findViewById(R.id.name_text_view);
        ((SendMoneyActivity) getActivity()).toolbar.setBackgroundColor(Color.WHITE);

        mSendMoneyButton = (Button) view.findViewById(R.id.send_money_button);
        if (getArguments() != null) {
            bundle = getArguments();
            mProfileImageView.setProfilePicture(bundle.getString("imageUrl"), false);
            mNameTextView.setText(bundle.getString("name"));
        }

        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
