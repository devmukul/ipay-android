package bd.com.ipay.ipayskeleton.SourceOfFund;


import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.DialogActivities.ContactPickerDialogActivity;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class AddSourceOfFundFragment extends Fragment {

    private String mName;
    private String mMobileNumber;
    private String mProfileImageUrl;

    private EditText mNumberEditText;
    private ImageView mContactImageView;
    private TextView mNameTextView;
    private ProfileImageView profileImageView;

    private Button doneButton;

    private final int PICK_CONTACT_REQUEST = 100;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_source_of_fund, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mNumberEditText = view.findViewById(R.id.number_edit_text);
        mNameTextView = view.findViewById(R.id.name);
        doneButton = view.findViewById(R.id.done);


        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        mContactImageView = view.findViewById(R.id.contact_image_view);
        profileImageView = (ProfileImageView) view.findViewById(R.id.profile_image);
        mContactImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ContactPickerDialogActivity.class);
                startActivityForResult(intent, PICK_CONTACT_REQUEST);
            }
        });
        mNumberEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (mNumberEditText.getText().toString().equals("+880-1")) {
                        mNumberEditText.setSelection(6);
                    } else {
                        Selection.setSelection(mNumberEditText.getText(), mNumberEditText.getText().length());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().length() < 6) {
                    mNumberEditText.setText("+880-1");
                }
                if (s.toString().length() < 15) {
                    mName = "";
                    mMobileNumber = "";
                    mProfileImageUrl = "";
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (mNumberEditText.getText().toString().equals("+880-1")) {
                    mNumberEditText.setSelection(6);
                } else {
                    mNumberEditText.setSelection(mNumberEditText.getText().length());
                }

            }
        });
    }

    private ContactEngine.ContactData searchLocalContacts(String mobileNumber) {
        DataHelper dataHelper = DataHelper.getInstance(getActivity());
        int nameIndex, originalNameIndex, phoneNumberIndex, profilePictureUrlQualityMediumIndex;
        Cursor cursor = dataHelper.searchContacts(mobileNumber, false, false, false,
                false, false, false, null);
        try {
            if (cursor != null) {
                cursor.moveToFirst();
                nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
                originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_ORIGINAL_NAME);
                profilePictureUrlQualityMediumIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM);
                String name = cursor.getString(originalNameIndex);
                if (name == null || TextUtils.isEmpty(name)) {
                    name = cursor.getString(nameIndex);
                }
                String profilePictureUrl = cursor.getString(profilePictureUrlQualityMediumIndex);

                return new ContactEngine.ContactData(0, name, "", profilePictureUrl);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_CONTACT_REQUEST && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                mMobileNumber = data.getStringExtra(Constants.MOBILE_NUMBER);
                mName = data.getStringExtra(Constants.NAME);
                mProfileImageUrl = data.getStringExtra(Constants.PROFILE_PICTURE);
                if (mMobileNumber != null) {
                    mMobileNumber = mMobileNumber.substring(0, 4) + "-" + mMobileNumber.substring(4, mMobileNumber.length());
                    mNumberEditText.setText(mMobileNumber);
                }
                if (mName != null) {
                    mNameTextView.setText(mName);
                }
                if (mProfileImageUrl != null) {
                    profileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + mProfileImageUrl, false);
                }
            }
        }
    }
}

