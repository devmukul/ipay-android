package bd.com.ipay.ipayskeleton.PaymentFragments.ServicesFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class TopUpEnterNumberFragment extends Fragment {


    private EditText mNumberEditText;
    private TextView mMyNumberTopUpTextView;
    private ImageView mContactImageView;
    private RadioGroup mTypeSelector;

    private final int PICK_CONTACT_REQUEST = 100;


    private String mMobileNumber;
    private String mName;
    private String mProfileImageUrl;
    private String mOperatorType;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_top_up_enter_amount, container, false);
        setUpView(view);
        return view;
    }

    private void setUpView(View view) {
        mNumberEditText = (EditText) view.findViewById(R.id.number_edit_text);
        mMyNumberTopUpTextView = (TextView) view.findViewById(R.id.my_number_topup_text_view);
        mContactImageView = (ImageView) view.findViewById(R.id.contact_image_view);
        mTypeSelector = (RadioGroup) view.findViewById(R.id.type_selector);
        setUpButtonActions();
    }

    private void setUpButtonActions() {
        mMyNumberTopUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = ContactEngine.formatMobileNumberBD(ProfileInfoCacheManager.getMobileNumber());
                mMobileNumber = mobileNumber;
                mNumberEditText.setText(mMobileNumber);
            }
        });
        mContactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });
        mTypeSelector.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.prepaid) {
                    mOperatorType = "PREPAID";
                } else if (i == R.id.post_paid) {
                    mOperatorType = "POSTPAID";
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mMobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                mName = data.getStringExtra(Constants.NAME);
                mProfileImageUrl = data.getStringExtra(Constants.PROFILE_PICTURE);

                if (mMobileNumber != null) {
                    mNumberEditText.setText(mMobileNumber);
                }
            }
        }
    }
}
