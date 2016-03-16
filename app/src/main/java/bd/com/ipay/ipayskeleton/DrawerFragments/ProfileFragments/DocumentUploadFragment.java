package bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.Activities.EditProfileActivity;
import bd.com.ipay.ipayskeleton.Api.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DocumentUploadFragment extends Fragment {

    private EditText mNationalIdNumber;
    private EditText mPassportNumber;
    private EditText mDrivingLicenseNumber;
    private EditText mBirthCertificateNumber;
    private EditText mTinNumber;

    private Button mNationalIdUploadButton;
    private Button mPassportUploadButton;
    private Button mDrivingLicenseUploadButton;
    private Button mBirthCertificateUploadButton;
    private Button mTinUploadButton;

    private Button clickedButton;

    private UploadIdentifierDocumentAsyncTask uploadIdentifierDocumentAsyncTask;

    private static final int ACTION_PICK_NATIONAL_ID = 0;
    private static final int ACTION_PICK_PASSPORT = 1;
    private static final int ACTION_PICK_DRIVING_LICENSE = 2;
    private static final int ACTION_PICK_BIRTH_CERTIFICATE = 3;
    private static final int ACTION_PICK_TIN = 4;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_document_upload, container, false);

        mNationalIdNumber = (EditText) v.findViewById(R.id.edit_text_national_id);
        mPassportNumber = (EditText) v.findViewById(R.id.edit_text_passport);
        mDrivingLicenseNumber = (EditText) v.findViewById(R.id.edit_text_driving_license);
        mBirthCertificateNumber = (EditText) v.findViewById(R.id.edit_text_birth_certificate);
        mTinNumber = (EditText) v.findViewById(R.id.edit_text_tin);

        mNationalIdUploadButton = (Button) v.findViewById(R.id.button_national_id);
        mPassportUploadButton = (Button) v.findViewById(R.id.button_passport);
        mDrivingLicenseUploadButton = (Button) v.findViewById(R.id.button_driving_license);
        mBirthCertificateUploadButton = (Button) v.findViewById(R.id.button_birth_certificate);
        mTinUploadButton = (Button) v.findViewById(R.id.button_tin);

        mNationalIdUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_NATIONAL_ID, mTinUploadButton, mTinNumber);
            }
        });
        mPassportUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_PASSPORT, mPassportUploadButton, mPassportNumber);
            }
        });
        mDrivingLicenseNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_DRIVING_LICENSE, mDrivingLicenseUploadButton, mDrivingLicenseNumber);
            }
        });
        mBirthCertificateUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_BIRTH_CERTIFICATE, mDrivingLicenseUploadButton, mDrivingLicenseNumber);
            }
        });
        mTinUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_TIN, mTinUploadButton, mTinNumber);
            }
        });

        return v;
    }

    private void selectProfilePicture(int requestCode, Button uploadButton, EditText numberEditText) {
        startActivityForResult(new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            try {
                if (intent != null && intent.getData() != null) {
                    String uri = intent.getData().toString();

                    String command = null;
                    String text = null;
                    String documentType = null;

                    switch (requestCode) {
                        case ACTION_PICK_NATIONAL_ID:
                            command = Constants.COMMAND_UPLOAD_NATIONAL_ID;
                            text = getString(R.string.national_id);
                            documentType = Constants.DOCUMENT_TYPE_NATIONAL_ID;
                            break;
                        case ACTION_PICK_PASSPORT:
                            command = Constants.COMMAND_UPLOAD_PASSPORT;
                            text = getString(R.string.passport);
                            documentType = Constants.DOCUMENT_TYPE_PASSPORT;
                            break;
                    }

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity().getApplicationContext(),
                                R.string.could_not_load_document,
                                Toast.LENGTH_SHORT).show();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
}
