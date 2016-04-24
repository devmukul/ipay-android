package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.IdentificationDocument;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;

public class DocumentUploadFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

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
    private String mDocumentType;

    private TextView mVerificationStatusNID;
    private TextView mVerificationStatusPassport;
    private TextView mVerificationStatusBirthCertificate;
    private TextView mVerificationStatusTIN;
    private TextView mVerificationStatusDrivingLicense;

    private List<IdentificationDocument> mIdentificationDocuments = new ArrayList<>();

    private ProgressDialog mProgressDialog;

    private UploadIdentifierDocumentAsyncTask mUploadIdentifierDocumentAsyncTask;
    private UploadDocumentResponse mUploadDocumentResponse;

    private static final int ACTION_PICK_NATIONAL_ID = 0;
    private static final int ACTION_PICK_PASSPORT = 1;
    private static final int ACTION_PICK_DRIVING_LICENSE = 2;
    private static final int ACTION_PICK_BIRTH_CERTIFICATE = 3;
    private static final int ACTION_PICK_TIN = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_document_upload, container, false);

        mNationalIdNumber = (EditText) v.findViewById(R.id.edit_text_national_id);
        mPassportNumber = (EditText) v.findViewById(R.id.edit_text_passport);
        mDrivingLicenseNumber = (EditText) v.findViewById(R.id.edit_text_driving_license);
        mBirthCertificateNumber = (EditText) v.findViewById(R.id.edit_text_birth_certificate);
        mTinNumber = (EditText) v.findViewById(R.id.edit_text_tin);

        mVerificationStatusBirthCertificate = (TextView) v.findViewById(R.id.verification_status_birth_certificate);
        mVerificationStatusDrivingLicense = (TextView) v.findViewById(R.id.verification_status_driving_license);
        mVerificationStatusTIN = (TextView) v.findViewById(R.id.verification_status_tin);
        mVerificationStatusPassport = (TextView) v.findViewById(R.id.verification_status_passport);
        mVerificationStatusNID = (TextView) v.findViewById(R.id.verification_status_nid);

        mNationalIdUploadButton = (Button) v.findViewById(R.id.button_national_id);
        mPassportUploadButton = (Button) v.findViewById(R.id.button_passport);
        mDrivingLicenseUploadButton = (Button) v.findViewById(R.id.button_driving_license);
        mBirthCertificateUploadButton = (Button) v.findViewById(R.id.button_birth_certificate);
        mTinUploadButton = (Button) v.findViewById(R.id.button_tin);

        mProgressDialog = new ProgressDialog(getActivity());

        mNationalIdUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(ACTION_PICK_NATIONAL_ID, mNationalIdUploadButton, mNationalIdNumber);
            }
        });
        mPassportUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(ACTION_PICK_PASSPORT, mPassportUploadButton, mPassportNumber);
            }
        });
        mDrivingLicenseUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(ACTION_PICK_DRIVING_LICENSE, mDrivingLicenseUploadButton, mDrivingLicenseNumber);
            }
        });
        mBirthCertificateUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(ACTION_PICK_BIRTH_CERTIFICATE, mBirthCertificateUploadButton, mBirthCertificateNumber);
            }
        });
        mTinUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDocument(ACTION_PICK_TIN, mTinUploadButton, mTinNumber);
            }
        });

        getIdentificationDocuments();

        return v;
    }

    private void loadDocumentInfo() {

        for (IdentificationDocument identificationDocument : mIdentificationDocuments) {
            String documentType = identificationDocument.getDocumentType();
            String verificationStatus = identificationDocument.getDocumentVerificationStatus();

            if (documentType.equals(Constants.DOCUMENT_TYPE_NATIONAL_ID)) {
                if (verificationStatus.equals(Constants.VERIFICATION_STATUS_VERIFIED)) {
                    mNationalIdUploadButton.setVisibility(View.GONE);
                    mNationalIdNumber.setVisibility(View.GONE);
                    mVerificationStatusNID.setTextColor(getResources().getColor(R.color.bottle_green));
                    mVerificationStatusNID.setText(getString(R.string.verified));
                } else {
                    mNationalIdUploadButton.setText(getString(R.string.upload_again));
                    mVerificationStatusNID.setText(getString(R.string.verification_in_progress));
                }

            } else if (documentType.equals(Constants.DOCUMENT_TYPE_PASSPORT)) {
                if (verificationStatus.equals(Constants.VERIFICATION_STATUS_VERIFIED)) {
                    mPassportUploadButton.setVisibility(View.GONE);
                    mPassportNumber.setVisibility(View.GONE);
                    mVerificationStatusPassport.setTextColor(getResources().getColor(R.color.bottle_green));
                    mVerificationStatusPassport.setText(getString(R.string.verified));
                } else {
                    mPassportUploadButton.setText(getString(R.string.upload_again));
                    mVerificationStatusPassport.setText(getString(R.string.verification_in_progress));
                }

            } else if (documentType.equals(Constants.DOCUMENT_TYPE_DRIVING_LICENSE)) {
                if (verificationStatus.equals(Constants.VERIFICATION_STATUS_VERIFIED)) {
                    mDrivingLicenseUploadButton.setVisibility(View.GONE);
                    mDrivingLicenseNumber.setVisibility(View.GONE);
                    mVerificationStatusDrivingLicense.setTextColor(getResources().getColor(R.color.bottle_green));
                    mVerificationStatusDrivingLicense.setText(getString(R.string.verified));
                } else {
                    mDrivingLicenseUploadButton.setText(getString(R.string.upload_again));
                    mVerificationStatusDrivingLicense.setText(getString(R.string.verification_in_progress));
                }

            } else if (documentType.equals(Constants.DOCUMENT_TYPE_BIRTH_CERTIFICATE)) {
                if (verificationStatus.equals(Constants.VERIFICATION_STATUS_VERIFIED)) {
                    mBirthCertificateUploadButton.setVisibility(View.GONE);
                    mBirthCertificateNumber.setVisibility(View.GONE);
                    mVerificationStatusBirthCertificate.setTextColor(getResources().getColor(R.color.bottle_green));
                    mVerificationStatusBirthCertificate.setText(getString(R.string.verified));
                } else {
                    mBirthCertificateUploadButton.setText(getString(R.string.upload_again));
                    mVerificationStatusBirthCertificate.setText(getString(R.string.verification_in_progress));
                }

            } else if (documentType.equals(Constants.DOCUMENT_TYPE_TIN)) {
                if (verificationStatus.equals(Constants.VERIFICATION_STATUS_VERIFIED)) {
                    mTinUploadButton.setVisibility(View.GONE);
                    mTinNumber.setVisibility(View.GONE);
                    mVerificationStatusTIN.setTextColor(getResources().getColor(R.color.bottle_green));
                    mVerificationStatusTIN.setText(getString(R.string.verified));
                } else {
                    mTinUploadButton.setText(getString(R.string.upload_again));
                    mVerificationStatusTIN.setText(getString(R.string.verification_in_progress));
                }
            }
        }

    }

    private void selectDocument(int requestCode, Button uploadButton, EditText numberEditText) {
        if (numberEditText.getText().toString().isEmpty()) {
            numberEditText.setError(getString(R.string.please_enter_document_number));
            numberEditText.requestFocus();
        } else {
            mNationalIdNumber.setError(null);
            mPassportNumber.setError(null);
            mDrivingLicenseNumber.setError(null);
            mBirthCertificateUploadButton.setError(null);
            mTinUploadButton.setError(null);

            Intent imagePickerIntent = DocumentPicker.getPickImageOrPdfIntent(getActivity(), getString(R.string.select_a_document));
            startActivityForResult(imagePickerIntent, requestCode);

            mDocumentNumber = numberEditText.getText().toString().trim();
            mClickedButton = uploadButton;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ACTION_PICK_NATIONAL_ID:
            case ACTION_PICK_PASSPORT:
            case ACTION_PICK_DRIVING_LICENSE:
            case ACTION_PICK_BIRTH_CERTIFICATE:
            case ACTION_PICK_TIN:
                if (resultCode == Activity.RESULT_OK) {
                    Uri uri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent);
                    if (uri == null) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(),
                                    R.string.could_not_load_document,
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        if (mUploadIdentifierDocumentAsyncTask != null)
                            return;

                        String command = null;
                        String text = null;

                        switch (requestCode) {
                            case ACTION_PICK_NATIONAL_ID:
                                command = Constants.COMMAND_UPLOAD_NATIONAL_ID;
                                text = getString(R.string.national_id);
                                mDocumentType = Constants.DOCUMENT_TYPE_NATIONAL_ID;
                                break;
                            case ACTION_PICK_PASSPORT:
                                command = Constants.COMMAND_UPLOAD_PASSPORT;
                                text = getString(R.string.passport);
                                mDocumentType = Constants.DOCUMENT_TYPE_PASSPORT;
                                break;
                            case ACTION_PICK_DRIVING_LICENSE:
                                command = Constants.COMMAND_UPLOAD_DRIVING_LICENSE;
                                text = getString(R.string.driving_license);
                                mDocumentType = Constants.DOCUMENT_TYPE_DRIVING_LICENSE;
                                break;
                            case ACTION_PICK_BIRTH_CERTIFICATE:
                                command = Constants.COMMAND_UPLOAD_BIRTH_CERTIFICATE;
                                text = getString(R.string.birth_certificate);
                                mDocumentType = Constants.DOCUMENT_TYPE_BIRTH_CERTIFICATE;
                                break;
                            case ACTION_PICK_TIN:
                                command = Constants.COMMAND_UPLOAD_TIN;
                                text = getString(R.string.tin);
                                mDocumentType = Constants.DOCUMENT_TYPE_TIN;
                                break;
                        }

                        mProgressDialog.setMessage(getString(R.string.uploading) + " " + text);
                        mProgressDialog.show();

                        String selectedOImagePath = uri.getPath();

                        mUploadIdentifierDocumentAsyncTask = new UploadIdentifierDocumentAsyncTask(
                                command, selectedOImagePath, getActivity(), mDocumentNumber, mDocumentType);
                        mUploadIdentifierDocumentAsyncTask.mHttpResponseListener = this;
                        mUploadIdentifierDocumentAsyncTask.execute();

                    }
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    private void getIdentificationDocuments() {
        if (mGetIdentificationDocumentsTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_identification_documents));
        mProgressDialog.show();

        mGetIdentificationDocumentsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST,
                Constants.BASE_URL + "/" + Constants.URL_GET_DOCUMENTS, getActivity(), this);
        mGetIdentificationDocumentsTask.execute();
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mUploadIdentifierDocumentAsyncTask = null;
            mGetIdentificationDocumentsTask = null;
            Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();
        
        if (resultList.get(0).equals(Constants.COMMAND_UPLOAD_NATIONAL_ID) ||
                resultList.get(0).equals(Constants.COMMAND_UPLOAD_PASSPORT) ||
                resultList.get(0).equals(Constants.COMMAND_UPLOAD_DRIVING_LICENSE) ||
                resultList.get(0).equals(Constants.COMMAND_UPLOAD_TIN) ||
                resultList.get(0).equals(Constants.COMMAND_UPLOAD_BIRTH_CERTIFICATE)) {
            try {
                mUploadDocumentResponse = gson.fromJson(resultList.get(2), UploadDocumentResponse.class);
    
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mUploadDocumentResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
    
                    boolean documentTypeExisted = false;
                    for (int i = 0; i < mIdentificationDocuments.size(); i++) {
                        if (mIdentificationDocuments.get(i).documentType.equals(mDocumentType)) {
                            documentTypeExisted = true;
                            mIdentificationDocuments.get(i).setDocumentIdNumber(mDocumentNumber);
                        }
                    }
    
                    if (!documentTypeExisted) {
                        mIdentificationDocuments.add(
                                new IdentificationDocument(mDocumentType, mDocumentNumber));
                    }
    
                    if (mClickedButton != null) {
                        mClickedButton.setText(R.string.upload_again);
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

            mUploadIdentifierDocumentAsyncTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST)) {
            try {
                mIdentificationDocumentResponse = gson.fromJson(resultList.get(2), GetIdentificationDocumentResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mIdentificationDocuments = mIdentificationDocumentResponse.getDocuments();
                    loadDocumentInfo();

                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.profile_info_fetch_failed, Toast.LENGTH_SHORT).show();
            }

            mGetIdentificationDocumentsTask = null;
        }

        mProgressDialog.dismiss();

        mClickedButton = null;
        mDocumentNumber = null;
    }
}
