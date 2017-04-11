package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.File;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.UploadApi.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;

public class DocumentUploadFragment extends Fragment implements HttpResponseListener {

    private UploadIdentifierDocumentAsyncTask mUploadIdentifierDocumentAsyncTask;
    private UploadDocumentResponse mUploadDocumentResponse;

    private TextView mDocumentTypeNameView;
    private EditText mDocumentIdField;
    private EditText mSelectFileField;
    private ImageView mSelectFileButton;
    private Button mUploadButton;

    private ProgressDialog mProgressDialog;

    private String mDocumentId;
    private String mDocumentType;
    private String mDocumentTypeName;

    private Uri mSelectedDocumentUri;

    private static final int ACTION_UPLOAD_DOCUMENT = 100;

    private static final int REQUEST_CODE_PERMISSION = 1001;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_document_upload, container, false);

        getActivity().setTitle(R.string.identification_documents);

        mProgressDialog = new ProgressDialog(getActivity());

        mDocumentTypeNameView = (TextView) v.findViewById(R.id.textview_document_type);
        mDocumentIdField = (EditText) v.findViewById(R.id.document_id);
        mSelectFileField = (EditText) v.findViewById(R.id.select_file);
        mSelectFileButton = (ImageView) v.findViewById(R.id.button_select_file);
        mUploadButton = (Button) v.findViewById(R.id.button_upload);

        Bundle bundle = getArguments();
        mDocumentId = bundle.getString(Constants.DOCUMENT_ID, "");
        mDocumentType = bundle.getString(Constants.DOCUMENT_TYPE, "");
        mDocumentTypeName = bundle.getString(Constants.DOCUMENT_TYPE_NAME, "");

        mDocumentTypeNameView.setText(getString(R.string.upload_small) + " " + mDocumentTypeName);

        mSelectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DocumentPicker.ifNecessaryPermissionExists(getActivity())) {
                    selectDocument();
                } else {
                    DocumentPicker.requestRequiredPermissions(DocumentUploadFragment.this, REQUEST_CODE_PERMISSION);
                }
            }
        });

        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    uploadDocument();
                }
            }
        });

        return v;
    }

    private void selectDocument() {
        Intent imagePickerIntent = DocumentPicker.getPickImageOrPdfIntent(getActivity(), getString(R.string.select_a_document));
        startActivityForResult(imagePickerIntent, ACTION_UPLOAD_DOCUMENT);
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        if (mDocumentIdField.getText().toString().isEmpty()) {
            mDocumentIdField.setError(getString(R.string.please_enter_document_number));
            focusView = mDocumentIdField;
            cancel = true;
        } else if (mSelectedDocumentUri == null) {
            mSelectFileField.setText(R.string.please_select_a_file_to_upload);
            focusView = mSelectFileField;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void uploadDocument() {

        if (mUploadIdentifierDocumentAsyncTask != null)
            return;

        mProgressDialog.setMessage(getString(R.string.uploading) + " " + mDocumentTypeName);
        mProgressDialog.show();

        String selectedOImagePath = mSelectedDocumentUri.getPath();

        mUploadIdentifierDocumentAsyncTask = new UploadIdentifierDocumentAsyncTask(
                Constants.COMMAND_UPLOAD_DOCUMENT, selectedOImagePath, getActivity(),
                mDocumentIdField.getText().toString(), mDocumentType);
        mUploadIdentifierDocumentAsyncTask.mHttpResponseListener = this;
        mUploadIdentifierDocumentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ACTION_UPLOAD_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    String filePath = DocumentPicker.getFilePathFromResult(getActivity(), resultCode, intent);
                    mSelectedDocumentUri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent);

                    if (filePath != null) {
                        String[] temp = filePath.split(File.separator);
                        String fileName = temp[temp.length - 1];
                        mSelectFileField.setText(fileName);
                    }
                } else {
                    mSelectFileField.setText(getString(R.string.no_file_selected));
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (DocumentPicker.ifNecessaryPermissionExists(getActivity())) {
                    selectDocument();
                } else {
                    Toast.makeText(getActivity(), R.string.prompt_grant_permission, Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mUploadIdentifierDocumentAsyncTask = null;
            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_UPLOAD_DOCUMENT)) {
            try {
                mUploadDocumentResponse = gson.fromJson(result.getJsonString(), UploadDocumentResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        // If push is delayed, we would not see the updated document list when we back
                        // to the document list fragment. Setting the update flag to true to force load
                        // the list.
                        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, true);

                        Toast.makeText(getActivity(), mUploadDocumentResponse.getMessage(), Toast.LENGTH_LONG).show();
                        ((ProfileActivity) getActivity()).switchToIdentificationDocumentListFragment();
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
            mProgressDialog.dismiss();
        }
    }
}
