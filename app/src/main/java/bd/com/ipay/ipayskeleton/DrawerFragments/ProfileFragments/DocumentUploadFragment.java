package bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.EditProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class DocumentUploadFragment extends Fragment implements HttpResponseListener {

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

    private Button mClickedButton;
    private String mDocumentNumber;

    private ProgressDialog mProgressDialog;

    private UploadIdentifierDocumentAsyncTask mUploadIdentifierDocumentAsyncTask;
    private UploadDocumentResponse mUploadDocumentResponse;

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

        mProgressDialog = new ProgressDialog(getActivity());

        mNationalIdUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_NATIONAL_ID, mNationalIdUploadButton, mNationalIdNumber);
            }
        });
        mPassportUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_PASSPORT, mPassportUploadButton, mPassportNumber);
            }
        });
        mDrivingLicenseUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_DRIVING_LICENSE, mDrivingLicenseUploadButton, mDrivingLicenseNumber);
            }
        });
        mBirthCertificateUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture(ACTION_PICK_BIRTH_CERTIFICATE, mBirthCertificateUploadButton, mBirthCertificateNumber);
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
        if (numberEditText.getText().toString().isEmpty()) {
            numberEditText.setError(getString(R.string.please_enter_document_number));
            numberEditText.requestFocus();
        }
        else {
            mNationalIdNumber.setError(null);
            mPassportNumber.setError(null);
            mDrivingLicenseNumber.setError(null);
            mBirthCertificateUploadButton.setError(null);
            mTinUploadButton.setError(null);

            startActivityForResult(new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI), requestCode);
            mDocumentNumber = numberEditText.getText().toString().trim();
            mClickedButton = uploadButton;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTION_PICK_NATIONAL_ID ||
                    requestCode == ACTION_PICK_PASSPORT ||
                    requestCode == ACTION_PICK_DRIVING_LICENSE ||
                    requestCode == ACTION_PICK_BIRTH_CERTIFICATE ||
                    requestCode == ACTION_PICK_TIN) {

                try {
                    if (intent != null && intent.getData() != null) {
                        if (mUploadIdentifierDocumentAsyncTask != null)
                            return;

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
                            case ACTION_PICK_DRIVING_LICENSE:
                                command = Constants.COMMAND_UPLOAD_DRIVING_LICENSE;
                                text = getString(R.string.driving_license);
                                documentType = Constants.DOCUMENT_TYPE_DRIVING_LICENSE;
                                break;
                            case ACTION_PICK_BIRTH_CERTIFICATE:
                                command = Constants.COMMAND_UPLOAD_BIRTH_CERTIFICATE;
                                text = getString(R.string.birth_certificate);
                                documentType = Constants.DOCUMENT_TYPE_BIRTH_CERTIFICATE;
                                break;
                            case ACTION_PICK_TIN:
                                command = Constants.COMMAND_UPLOAD_TIN;
                                text = getString(R.string.tin);
                                documentType = Constants.DOCUMENT_TYPE_TIN;
                                break;
                        }

                        mProgressDialog.setMessage(getString(R.string.uploading) + " " + text);
                        mProgressDialog.show();

                        String selectedOImagePath = Utilities.getFilePath(getActivity(), intent.getData());

                        mUploadIdentifierDocumentAsyncTask = new UploadIdentifierDocumentAsyncTask(
                                command, selectedOImagePath, getActivity(), mDocumentNumber, documentType);
                        mUploadIdentifierDocumentAsyncTask.mHttpResponseListener = this;
                        mUploadIdentifierDocumentAsyncTask.execute();

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

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mUploadIdentifierDocumentAsyncTask = null;
            Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();
        try {
            mUploadDocumentResponse = gson.fromJson(resultList.get(2), UploadDocumentResponse.class);
            if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.upload_successful, Toast.LENGTH_LONG).show();
                }
                if (mClickedButton != null) {
                    mClickedButton.setText(R.string.uploaded);
                }
            } else {
                if (getActivity() != null)
                    Toast.makeText(getActivity(), mUploadDocumentResponse.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (getActivity() != null)
                Toast.makeText(getActivity(), mUploadDocumentResponse.getMessage(), Toast.LENGTH_SHORT).show();
        }

        mProgressDialog.dismiss();
        mUploadIdentifierDocumentAsyncTask = null;

        mClickedButton = null;
        mDocumentNumber = null;
    }
}
