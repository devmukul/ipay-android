package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DocumentPreviewActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomUploadPickerDialog;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.DocumentPreviewDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.DocumentPreviewRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.IdentificationDocument;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.FCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IdentificationDocumentListFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

    private HttpRequestGetAsyncTask mGetIdentificationBusinessDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationBusinessDocumentResponse = null;

    private UploadIdentifierDocumentAsyncTask mUploadIdentifierDocumentAsyncTask;
    private UploadDocumentResponse mUploadDocumentResponse;

    private HttpRequestGetAsyncTask mGetDocumentAccessTokenTask = null;

    private ProgressDialog mProgressDialog;

    private DocumentListAdapter mDocumentListAdapter;
    private RecyclerView mDocumentListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<IdentificationDocument> mIdentificationDocuments = new ArrayList<>();
    private List<IdentificationDocument> mIdentificationBusinessDocuments = new ArrayList<>();

    private DocumentPreviewDetails mSelectedDocumentDetails;

    private ArrayList<DocumentPreviewDetails> documentPreviewDetailsList;

    private String[] DOCUMENT_TYPES;
    private String[] BUSINESS_DOCUMENT_TYPES;
    private String[] DOCUMENT_HINT_TYPES;

    private Uri mSelectedDocumentUri;
    private String mFileName;

    private static final int[] DOCUMENT_TYPE_NAMES = {
            R.string.national_id,
            R.string.passport,
            R.string.driving_license,
    };

    private static final int[] BUSINESS_DOCUMENT_TYPE_NAMES = {
            R.string.national_id,
            R.string.business_tin,
            R.string.trade_license,
            R.string.vat_registration_certificate
    };

    private static final int ACTION_UPLOAD_DOCUMENT = 100;
    private static final int REQUEST_CODE_PERMISSION = 1001;
    private static final int OPTION_UPLOAD_TYPE_PERSONAL_DOCUMENT = 1;
    private static final int OPTION_UPLOAD_TYPE_BUSINESS_DOCUMENT = 2;

    private int mSelectedItemId = -1;
    private int mPickerActionId = -1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Before making any change, make sure DOCUMENT_TYPES matches with DOCUMENT_TYPE_NAMES
        DOCUMENT_TYPES = new String[]{
                Constants.DOCUMENT_TYPE_NATIONAL_ID,
                Constants.DOCUMENT_TYPE_PASSPORT,
                Constants.DOCUMENT_TYPE_DRIVING_LICENSE,
        };
        BUSINESS_DOCUMENT_TYPES = new String[]{
                Constants.DOCUMENT_TYPE_NATIONAL_ID,
                Constants.DOCUMENT_TYPE_BUSINESS_TIN,
                Constants.DOCUMENT_TYPE_TRADE_LICENSE,
                Constants.DOCUMENT_TYPE_VAT_REG_CERT
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_identification_documents, container, false);
        getActivity().setTitle(R.string.profile_documents);

        mProgressDialog = new ProgressDialog(getActivity());
        
        mDocumentListRecyclerView = (RecyclerView) v.findViewById(R.id.list_documents);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mDocumentListRecyclerView.setLayoutManager(mLayoutManager);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE)) {
            if (ProfileInfoCacheManager.isBusinessAccount()) {
                getIdentificationBusinessDocuments();
            } else
                getIdentificationDocuments();
        } else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE);

            if (json == null) {

                if (ProfileInfoCacheManager.isBusinessAccount())
                    getIdentificationBusinessDocuments();
                else
                    getIdentificationDocuments();
            } else {
                if (ProfileInfoCacheManager.isBusinessAccount())
                    processGetBusinessDocumentListResponse(json);
                else
                    processGetDocumentListResponse(json);
            }
        }

        if (ProfileInfoCacheManager.isBusinessAccount()) {
            DOCUMENT_HINT_TYPES = getResources().getStringArray(R.array.business_document_id);
        } else
            DOCUMENT_HINT_TYPES = getResources().getStringArray(R.array.personal_document_id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ACTION_UPLOAD_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    String filePath = DocumentPicker.getFilePathFromResult(getActivity(), resultCode, intent);

                    if (filePath != null) {
                        String[] temp = filePath.split(File.separator);
                        mFileName = temp[temp.length - 1];
                        documentPreviewDetailsList.get(mSelectedItemId).setSelectedFilePath(mFileName);
                        mSelectedDocumentUri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent);
                        if (Constants.DEBUG)
                            Log.w("Loading document", mSelectedItemId + " " + mSelectedDocumentUri.toString());

                        documentPreviewDetailsList.get(mSelectedItemId).setSelectedDocumentUri(mSelectedDocumentUri);
                        mDocumentListAdapter.notifyDataSetChanged();
                        mSelectedItemId = -1;
                    }
                } else
                    mSelectedItemId = -1;

                break;
            default:
                super.onActivityResult(requestCode, resultCode, intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (DocumentPicker.ifNecessaryPermissionExists(getActivity()))
                    selectDocument(mPickerActionId);
                else
                    Toast.makeText(getActivity(), R.string.prompt_grant_permission, Toast.LENGTH_LONG).show();
        }
    }

    private void loadDocumentInfo() {

        if (ProfileInfoCacheManager.isBusinessAccount())
            documentPreviewDetailsList = new ArrayList<>(BUSINESS_DOCUMENT_TYPE_NAMES.length);
        else
            documentPreviewDetailsList = new ArrayList<>(DOCUMENT_TYPE_NAMES.length);

        if (ProfileInfoCacheManager.isBusinessAccount()) {
            for (int i = 0; i < BUSINESS_DOCUMENT_TYPES.length; i++) {
                String documentId = "";
                String verificationStatus = null;
                String documentUrl = null;

                documentPreviewDetailsList.add(new DocumentPreviewDetails());

                for (IdentificationDocument identificationDocument : mIdentificationBusinessDocuments) {
                    if (identificationDocument.getDocumentType().equals(BUSINESS_DOCUMENT_TYPES[i])) {
                        documentId = identificationDocument.getDocumentIdNumber();
                        verificationStatus = identificationDocument.getDocumentVerificationStatus();
                        documentUrl = identificationDocument.getDocumentUrl();

                        documentPreviewDetailsList.get(i).setDocumentId(documentId);
                        documentPreviewDetailsList.get(i).setVerificationStatus(verificationStatus);
                        documentPreviewDetailsList.get(i).setDocumentUrl(documentUrl);
                        break;
                    }
                }
                documentPreviewDetailsList.get(i).setDocumentType(BUSINESS_DOCUMENT_TYPES[i]);
                documentPreviewDetailsList.get(i).setDocumentTypeName(getString(BUSINESS_DOCUMENT_TYPE_NAMES[i]));
            }
        } else {
            for (int i = 0; i < DOCUMENT_TYPES.length; i++) {
                String documentId = "";
                String verificationStatus = null;
                String documentUrl = null;

                documentPreviewDetailsList.add(new DocumentPreviewDetails());

                for (IdentificationDocument identificationDocument : mIdentificationDocuments) {
                    if (identificationDocument.getDocumentType().equals(DOCUMENT_TYPES[i])) {
                        documentId = identificationDocument.getDocumentIdNumber();
                        verificationStatus = identificationDocument.getDocumentVerificationStatus();
                        documentUrl = identificationDocument.getDocumentUrl();

                        documentPreviewDetailsList.get(i).setDocumentId(documentId);
                        documentPreviewDetailsList.get(i).setVerificationStatus(verificationStatus);
                        documentPreviewDetailsList.get(i).setDocumentUrl(documentUrl);
                        break;
                    }
                }
                documentPreviewDetailsList.get(i).setDocumentType(DOCUMENT_TYPES[i]);
                documentPreviewDetailsList.get(i).setDocumentTypeName(getString(DOCUMENT_TYPE_NAMES[i]));
            }
        }
    }

    private void getIdentificationDocuments() {
        if (mGetIdentificationDocumentsTask != null) {
            return;
        }

        setContentShown(false);

        mGetIdentificationDocumentsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_DOCUMENTS, getActivity(), this);
        mGetIdentificationDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getIdentificationBusinessDocuments() {
        if (mGetIdentificationBusinessDocumentsTask != null) {
            return;
        }
        setContentShown(false);

        mGetIdentificationBusinessDocumentsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_IDENTIFICATION_BUSINESS_DOCUMENTS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_BUSINESS_DOCUMENTS, getActivity(), this);
        mGetIdentificationBusinessDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getDocumentAccessToken() {
        if (mGetDocumentAccessTokenTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_loading_document_preview));
        mProgressDialog.show();

        mGetDocumentAccessTokenTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DOCUMENT_ACCESS_TOKEN,
                Constants.BASE_URL_MM + Constants.URL_GET_DOCUMENT_ACCESS_TOKEN, getActivity(), this);
        mGetDocumentAccessTokenTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void uploadDocument(String mDocumentID, String mDocumentType, int mID) {

        if (mUploadIdentifierDocumentAsyncTask != null)
            return;

        mProgressDialog.setMessage(getString(R.string.uploading) + " " + mDocumentType);
        mProgressDialog.show();

        String selectedOImagePath = documentPreviewDetailsList.get(mID).getSelectedDocumentUri().getPath();

        if (Constants.DEBUG)
            Log.w("Loading document", mDocumentID + " " + mID + " " + selectedOImagePath + " " + mDocumentType);

        if (ProfileInfoCacheManager.isBusinessAccount()) {
            mUploadIdentifierDocumentAsyncTask = new UploadIdentifierDocumentAsyncTask(
                    Constants.COMMAND_UPLOAD_DOCUMENT, selectedOImagePath, getActivity(),
                    mDocumentID, mDocumentType, OPTION_UPLOAD_TYPE_BUSINESS_DOCUMENT);
        } else {
            mUploadIdentifierDocumentAsyncTask = new UploadIdentifierDocumentAsyncTask(
                    Constants.COMMAND_UPLOAD_DOCUMENT, selectedOImagePath, getActivity(),
                    mDocumentID, mDocumentType, OPTION_UPLOAD_TYPE_PERSONAL_DOCUMENT);
        }

        mUploadIdentifierDocumentAsyncTask.mHttpResponseListener = this;
        mUploadIdentifierDocumentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetIdentificationDocumentsTask = null;
            mGetIdentificationBusinessDocumentsTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST:
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processGetDocumentListResponse(result.getJsonString());

                        DataHelper dataHelper = DataHelper.getInstance(getActivity());
                        dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, result.getJsonString());

                        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, false);
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_get_document_list, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_get_document_list, Toast.LENGTH_SHORT).show();
                }

                mGetIdentificationDocumentsTask = null;
                mUploadIdentifierDocumentAsyncTask = null;
                break;

            case Constants.COMMAND_GET_IDENTIFICATION_BUSINESS_DOCUMENTS_REQUEST:
                try {
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processGetBusinessDocumentListResponse(result.getJsonString());

                        DataHelper dataHelper = DataHelper.getInstance(getActivity());
                        dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, result.getJsonString());

                        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, false);
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), R.string.failed_get_document_list, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_get_document_list, Toast.LENGTH_SHORT).show();
                }

                mGetIdentificationBusinessDocumentsTask = null;
                mUploadIdentifierDocumentAsyncTask = null;
                break;
            case Constants.COMMAND_GET_DOCUMENT_ACCESS_TOKEN:
                try {
                    String resourceToken = result.getResourceToken();
                    String documentUrl = DocumentPreviewRequestBuilder.generateUri(resourceToken,
                            mSelectedDocumentDetails.getDocumentUrl(),
                            mSelectedDocumentDetails.getDocumentId(),
                            mSelectedDocumentDetails.getDocumentType());

                    if (Constants.DEBUG)
                        Log.w("Loading document", documentUrl);

                    Intent intent = new Intent(getActivity(), DocumentPreviewActivity.class);
                    intent.putExtra(Constants.FILE_EXTENSION, Utilities.getExtension(mSelectedDocumentDetails.getDocumentUrl()));
                    intent.putExtra(Constants.DOCUMENT_URL, documentUrl);
                    intent.putExtra(Constants.DOCUMENT_TYPE_NAME, mSelectedDocumentDetails.getDocumentTypeName());
                    startActivity(intent);

                    mProgressDialog.dismiss();
                    mGetDocumentAccessTokenTask = null;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.failed_document_preview_loading, Toast.LENGTH_SHORT).show();
                }
                break;
            case Constants.COMMAND_UPLOAD_DOCUMENT:
                try {
                    mUploadDocumentResponse = gson.fromJson(result.getJsonString(), UploadDocumentResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            // If push is delayed, we would not see the updated document list when we back
                            // to the document list fragment. Setting the update flag to true to force load
                            // the list.
                            PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, true);

                            Toast.makeText(getActivity(), mUploadDocumentResponse.getMessage(), Toast.LENGTH_LONG).show();

                            if (ProfileInfoCacheManager.isBusinessAccount())
                                getIdentificationBusinessDocuments();
                            else
                                getIdentificationDocuments();
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
                break;
        }

        mProgressDialog.dismiss();
    }

    private void processGetDocumentListResponse(String json) {
        try {
            Gson gson = new Gson();
            mIdentificationDocumentResponse = gson.fromJson(json, GetIdentificationDocumentResponse.class);

            mIdentificationDocuments = mIdentificationDocumentResponse.getDocuments();
            loadDocumentInfo();

            mDocumentListAdapter = new DocumentListAdapter();
            mDocumentListRecyclerView.setAdapter(mDocumentListAdapter);

            setContentShown(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processGetBusinessDocumentListResponse(String json) {
        try {
            Gson gson = new Gson();
            mIdentificationBusinessDocumentResponse = gson.fromJson(json, GetIdentificationDocumentResponse.class);

            mIdentificationBusinessDocuments = mIdentificationBusinessDocumentResponse.getDocuments();
            loadDocumentInfo();

            mDocumentListAdapter = new DocumentListAdapter();
            mDocumentListRecyclerView.setAdapter(mDocumentListAdapter);

            setContentShown(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void selectDocument(int id) {
        Intent imagePickerIntent = DocumentPicker.getPickerIntentByID(getActivity(), getString(R.string.select_a_document), id);
        startActivityForResult(imagePickerIntent, ACTION_UPLOAD_DOCUMENT);
    }

    private void setOtherOptionLayoutClosed(int id) {
        for (int i = 0; i < documentPreviewDetailsList.size(); i++) {
            if (id != i) {
                if (documentPreviewDetailsList.get(i).isViewOpen()) {
                    documentPreviewDetailsList.get(i).setIsViewOpen(false);
                }
            }
        }
        documentPreviewDetailsList.get(id).setSelectedDocumentUri(null);
        documentPreviewDetailsList.get(id).setSelectedFilePath("");
        mDocumentListAdapter.notifyDataSetChanged();
    }

    public class DocumentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDocumentTypeNameView;
            private final TextView mDocumentIdView;
            private final ImageView mVerificationStatus;
            private final RelativeLayout mInfoLayout;
            private final LinearLayout mOptionsLayout;
            private TextInputLayout mDocumentIdTextInputLayoutView;
            private EditText mDocumentIdEditTextView;
            private final EditText mSelectFile;
            private final Button mUploadButton;
            private final ImageView mPicker;

            private CustomUploadPickerDialog customUploadPickerDialog;
            private List<String> mPickerList;
            private File mFile;
            private Bitmap mBitmap;

            public ViewHolder(final View itemView) {
                super(itemView);

                mDocumentTypeNameView = (TextView) itemView.findViewById(R.id.textview_document_type);
                mDocumentIdView = (TextView) itemView.findViewById(R.id.textview_document_id);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.document_verification_status);
                mInfoLayout = (RelativeLayout) itemView.findViewById(R.id.info_layout);
                mOptionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                mDocumentIdTextInputLayoutView = (TextInputLayout) itemView.findViewById(R.id.text_inputlayout_document_id);
                mDocumentIdEditTextView = (EditText) itemView.findViewById(R.id.edit_text_document_id);
                mSelectFile = (EditText) itemView.findViewById(R.id.select_file);
                mUploadButton = (Button) itemView.findViewById(R.id.button_upload);
                mPicker = (ImageView) itemView.findViewById(R.id.button_select_file);
            }

            public void bindView(final int pos) {
                mDocumentIdEditTextView.setError(null);
                mSelectFile.setError(null);
                mSelectFile.setText("");
                mPickerList = new ArrayList<>();
                mBitmap = null;

                String documentTypeName = documentPreviewDetailsList.get(pos).getDocumentTypeName();
                String documentID = documentPreviewDetailsList.get(pos).getDocumentId();
                String verificationStatus = documentPreviewDetailsList.get(pos).getVerificationStatus();
                String documentHintType = DOCUMENT_HINT_TYPES[pos];

                mDocumentIdTextInputLayoutView.setHint(documentHintType);

                // Unverified, document not yet uploaded
                if (verificationStatus == null) {
                    mPickerList = Arrays.asList(getResources().getStringArray(R.array.upload_picker_action));
                    mVerificationStatus.setVisibility(View.GONE);
                    mDocumentIdView.setText(R.string.not_submitted);
                    mUploadButton.setText(getString(R.string.upload));
                    mUploadButton.setVisibility(View.VISIBLE);
                    mDocumentIdEditTextView.setEnabled(true);
                }
                // Document uploaded and verified
                else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                    mPickerList = Arrays.asList(getResources().getStringArray(R.array.verified_picker_action));
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_verified);
                    mVerificationStatus.setColorFilter(null);
                    mDocumentIdView.setText(documentID);
                    mUploadButton.setVisibility(View.GONE);
                    mDocumentIdEditTextView.setEnabled(false);

                }
                // Document uploaded but not verified
                else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED)) {
                    mPickerList = Arrays.asList(getResources().getStringArray(R.array.pending_picker_action));
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_workinprogress);
                    mVerificationStatus.setColorFilter(Color.GRAY);
                    mDocumentIdView.setText(documentID);
                    mUploadButton.setVisibility(View.VISIBLE);
                    mUploadButton.setText(getString(R.string.upload_again));
                    mDocumentIdEditTextView.setEnabled(true);
                }
                mDocumentTypeNameView.setText(documentTypeName);

                mDocumentIdEditTextView.setText(documentID);

                if (documentPreviewDetailsList.get(pos).getSelectedDocumentUri() != null) {
                    mFile = new File(documentPreviewDetailsList.get(pos).getSelectedDocumentUri().getPath());
                    if (mFile.exists()) {
                        mBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                        mPicker.setImageBitmap(mBitmap);
                    }
                } else {
                    if (verificationStatus == null)
                        mPicker.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_addw));
                    else
                        mPicker.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_image));
                }

                if (documentPreviewDetailsList.get(pos).getSelectedFilePath().isEmpty())
                    mSelectFile.setText("");
                else
                    mSelectFile.setText(documentPreviewDetailsList.get(pos).getSelectedFilePath());

                if (documentPreviewDetailsList.get(pos).isViewOpen())
                    mOptionsLayout.setVisibility(View.VISIBLE);
                else
                    mOptionsLayout.setVisibility(View.GONE);

                mInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utilities.hideKeyboard(getActivity());
                        if (mPickerList.size() > 0) {
                            // When account is verified, we wouldn't allow the user to upload new document
                            if (documentPreviewDetailsList.get(pos).isViewOpen()) {
                                mOptionsLayout.setVisibility(View.GONE);
                                documentPreviewDetailsList.get(pos).setIsViewOpen(false);
                            } else {
                                mOptionsLayout.setVisibility(View.VISIBLE);
                                documentPreviewDetailsList.get(pos).setIsViewOpen(true);
                                documentPreviewDetailsList.get(pos).setDocumentId(mDocumentIdEditTextView.getText().toString());
                                documentPreviewDetailsList.get(pos).setSelectedFilePath(mSelectFile.getText().toString());
                                setOtherOptionLayoutClosed(pos);
                            }
                        }
                    }
                });

                mPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customUploadPickerDialog = new CustomUploadPickerDialog(getActivity(), getString(R.string.select_a_document), mPickerList);
                        customUploadPickerDialog.setOnResourceSelectedListener(new CustomUploadPickerDialog.OnResourceSelectedListener() {
                            @Override
                            public void onResourceSelected(int mActionId, String action) {
                                mSelectedItemId = pos;
                                documentPreviewDetailsList.get(pos).setDocumentId(mDocumentIdEditTextView.getText().toString());
                                documentPreviewDetailsList.get(pos).setSelectedFilePath(mSelectFile.getText().toString());
                                if (Constants.ACTION_TYPE_TAKE_PICTURE.equals(action) || Constants.ACTION_TYPE_SELECT_FROM_GALLERY.equals(action))
                                    if (DocumentPicker.ifNecessaryPermissionExists(getActivity()))
                                        selectDocument(mActionId);
                                    else {
                                        mPickerActionId = mActionId;
                                        DocumentPicker.requestRequiredPermissions(IdentificationDocumentListFragment.this, REQUEST_CODE_PERMISSION);
                                    }
                                else {
                                    getDocumentAccessToken();
                                    mSelectedDocumentDetails = documentPreviewDetailsList.get(pos);
                                }
                            }
                        });
                        customUploadPickerDialog.show();
                    }
                });

                mUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDocumentIdEditTextView.getText().toString() == null || mDocumentIdEditTextView.getText().toString().isEmpty()) {
                            mDocumentIdEditTextView.setError(getString(R.string.please_enter_document_number));
                            mDocumentIdEditTextView.requestFocus();
                        } else if (documentPreviewDetailsList.get(pos).getSelectedDocumentUri() == null)
                            mSelectFile.setError(getString(R.string.please_select_a_file_to_upload));
                        else {
                            Utilities.hideKeyboard(getActivity());
                            documentPreviewDetailsList.get(pos).setDocumentId(mDocumentIdEditTextView.getText().toString());
                            uploadDocument(documentPreviewDetailsList.get(pos).getDocumentId(), documentPreviewDetailsList.get(pos).getDocumentType(), pos);
                        }
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_document,
                    parent, false);

            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                ViewHolder vh = (ViewHolder) holder;
                vh.bindView(position);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (documentPreviewDetailsList != null)
                return documentPreviewDetailsList.size();
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }
}