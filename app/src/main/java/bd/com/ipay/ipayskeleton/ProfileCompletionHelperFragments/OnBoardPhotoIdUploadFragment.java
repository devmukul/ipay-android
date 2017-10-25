package bd.com.ipay.ipayskeleton.ProfileCompletionHelperFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
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
import bd.com.ipay.ipayskeleton.Activities.ProfileCompletionHelperActivity;
import bd.com.ipay.ipayskeleton.Api.DocumentUploadApi.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomUploadPickerDialog;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.DocumentPreviewDetails;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.DocumentPreviewRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.IdentificationDocument;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Documents.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.camera.CameraActivity;

public class OnBoardPhotoIdUploadFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

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
    private String[] DOCUMENT_HINT_TYPES;

    private Uri mSelectedDocumentUri;
    private String mFileName;
    private String mOtherTypeName;
    private String mDocumentName;

    private static final int[] DOCUMENT_TYPE_NAMES = {
            R.string.national_id,
            R.string.passport,
            R.string.driving_license,
            R.string.birth_certificate,
            R.string.tin,
            R.string.other
    };

    private static String[] DOCUMENT_ID_MAX_LENGTH;

    private static final int ACTION_UPLOAD_DOCUMENT = 100;
    private static final int REQUEST_CODE_PERMISSION = 1001;
    private static final int OPTION_UPLOAD_TYPE_PERSONAL_DOCUMENT = 1;

    private int mSelectedItemId = -1;
    private int mPickerActionId = -1;
    ImageView back;

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Before making any change, make sure DOCUMENT_TYPES matches with DOCUMENT_TYPE_NAMES
        DOCUMENT_TYPES = new String[]{
                Constants.DOCUMENT_TYPE_NATIONAL_ID,
                Constants.DOCUMENT_TYPE_PASSPORT,
                Constants.DOCUMENT_TYPE_DRIVING_LICENSE,
                Constants.DOCUMENT_TYPE_BIRTH_CERTIFICATE,
                Constants.DOCUMENT_TYPE_TIN,
                Constants.DOCUMENT_TYPE_OTHER
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_onboard_identification_documents, container, false);
        getActivity().setTitle(R.string.profile_documents);

        mProgressDialog = new ProgressDialog(getActivity());

        mDocumentListRecyclerView = (RecyclerView) v.findViewById(R.id.list_documents);
        mDocumentListRecyclerView.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mDocumentListRecyclerView.setLayoutManager(mLayoutManager);

        back  = (ImageView) v.findViewById(R.id.back);
        if (getActivity().getSupportFragmentManager().getBackStackEntryCount()<=1){
            back.setVisibility(View.INVISIBLE);
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getIdentificationDocuments();
        DOCUMENT_HINT_TYPES = getResources().getStringArray(R.array.personal_document_id);
        DOCUMENT_ID_MAX_LENGTH = getResources().getStringArray(R.array.personal_document_id_max_length);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case ACTION_UPLOAD_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    String filePath = DocumentPicker.getFilePathFromResult(getActivity(), intent);

                    if (filePath != null) {
                        String[] temp = filePath.split(File.separator);
                        mFileName = temp[temp.length - 1];
                        documentPreviewDetailsList.get(mSelectedItemId).setSelectedFilePath(mFileName);
                        mSelectedDocumentUri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent);
                        Logger.logW("Loading document", mSelectedItemId + " " + mSelectedDocumentUri.toString());

                        documentPreviewDetailsList.get(mSelectedItemId).setSelectedDocumentUri(mSelectedDocumentUri);
                        mDocumentListAdapter.notifyDataSetChanged();
                        mSelectedItemId = -1;
                    }
                } else if (resultCode == CameraActivity.CAMERA_ACTIVITY_CRASHED) {
                    Intent systemCameraOpenIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    systemCameraOpenIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, DocumentPicker.getTempFile(getActivity(), "document.jpg")));
                    startActivityForResult(systemCameraOpenIntent, ACTION_UPLOAD_DOCUMENT);
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
                if (Utilities.isNecessaryPermissionExists(getActivity(), DocumentPicker.DOCUMENT_PICK_PERMISSIONS))
                    selectDocument(mPickerActionId);
                else
                    Toast.makeText(getActivity(), R.string.prompt_grant_permission, Toast.LENGTH_LONG).show();
        }
    }

    private void loadDocumentInfo() {

        documentPreviewDetailsList = new ArrayList<>(DOCUMENT_TYPE_NAMES.length);

        for (int i = 0; i < DOCUMENT_TYPES.length; i++) {
            String documentId = "";
            String verificationStatus = null;
            String documentUrl = null;

            documentPreviewDetailsList.add(new DocumentPreviewDetails());

            for (IdentificationDocument identificationDocument : mIdentificationDocuments) {
                if (identificationDocument.getDocumentType().equals(DOCUMENT_TYPES[i].toLowerCase())) {
                    documentId = identificationDocument.getDocumentIdNumber();
                    verificationStatus = identificationDocument.getDocumentVerificationStatus();
                    documentUrl = identificationDocument.getDocumentUrl();
                    mDocumentName = identificationDocument.getDocumentName();
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

    private void getIdentificationDocuments() {
        if (mGetIdentificationDocumentsTask != null) {
            return;
        }

        setContentShown(false);

        mGetIdentificationDocumentsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST,
                Constants.BASE_URL_MM + Constants.URL_GET_DOCUMENTS, getActivity(), this);
        mGetIdentificationDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void setEditTextMaxLength(EditText editText, int pos) {
        int maxLength = Integer.parseInt(DOCUMENT_ID_MAX_LENGTH[pos]);
        InputFilter[] inputFilters = new InputFilter[1];
        inputFilters[0] = new InputFilter.LengthFilter(maxLength);
        editText.setFilters(inputFilters);
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

    private void uploadDocument(int mID) {

        if (mUploadIdentifierDocumentAsyncTask != null)
            return;

        String mDocumentTypeName = documentPreviewDetailsList.get(mID).getDocumentTypeName();
        String mDocumentID = documentPreviewDetailsList.get(mID).getDocumentId();
        String mDocumentType = documentPreviewDetailsList.get(mID).getDocumentType();
        mProgressDialog.setMessage(getString(R.string.uploading) + " " + mDocumentTypeName);
        mProgressDialog.show();

        String selectedOImagePath = documentPreviewDetailsList.get(mID).getSelectedDocumentUri().getPath();

        Logger.logW("Loading document", mDocumentID + " " + mID + " " + selectedOImagePath + " " + mDocumentType);

        mUploadIdentifierDocumentAsyncTask = new UploadIdentifierDocumentAsyncTask(
                    Constants.COMMAND_UPLOAD_DOCUMENT, selectedOImagePath, getActivity(),
                    mDocumentID, mDocumentType.toLowerCase(), mDocumentName, OPTION_UPLOAD_TYPE_PERSONAL_DOCUMENT);

        mUploadIdentifierDocumentAsyncTask.mHttpResponseListener = this;
        mUploadIdentifierDocumentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetIdentificationDocumentsTask = null;
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

            case Constants.COMMAND_GET_DOCUMENT_ACCESS_TOKEN:
                try {
                    String resourceToken = result.getResourceToken();
                    String documentUrl = DocumentPreviewRequestBuilder.generateUri(resourceToken,
                            mSelectedDocumentDetails.getDocumentUrl(),
                            mSelectedDocumentDetails.getDocumentId(),
                            mSelectedDocumentDetails.getDocumentType());

                    Logger.logW("Loading document", documentUrl);

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
                            Toast.makeText(getActivity(), mUploadDocumentResponse.getMessage(), Toast.LENGTH_LONG).show();
                            ProfileInfoCacheManager.uploadIdentificationDocument(true);

                            if(ProfileInfoCacheManager.isSwitchedFromSignup()){
                                ((ProfileCompletionHelperActivity) getActivity()).switchToBasicInfoEditHelperFragment();
                            }
                            else{
                                if(!ProfileInfoCacheManager.isBasicInfoAdded()){
                                    ((ProfileCompletionHelperActivity) getActivity()).switchToBasicInfoEditHelperFragment();
                                }else {
                                    ((ProfileCompletionHelperActivity) getActivity()).switchToHomeActivity();
                                }
                            }
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
            private TextInputLayout mDocumentTypeForOtherTypeLayoutView;
            private EditText mDocumentTypeOtherEditText;
            private EditText mDocumentIdEditTextView;
            private final EditText mSelectFile;
            private final Button mUploadButton;
            private final ImageView mPicker;
            private String documentTypeName;

            private View mDividerDocumentType;

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
                mDocumentTypeForOtherTypeLayoutView = (TextInputLayout) itemView.findViewById(R.id.text_inputlayout_document_type);
                mDocumentTypeOtherEditText = (EditText) itemView.findViewById(R.id.edit_text_document_type);
                mDividerDocumentType = itemView.findViewById(R.id.divider_document_type);
            }

            public void bindView(final int pos) {
                mDocumentIdEditTextView.setError(null);
                mSelectFile.setError(null);
                mSelectFile.setText("");
                mPickerList = new ArrayList<>();
                mBitmap = null;

                documentTypeName = documentPreviewDetailsList.get(pos).getDocumentTypeName();
                String documentID = documentPreviewDetailsList.get(pos).getDocumentId();
                String verificationStatus = documentPreviewDetailsList.get(pos).getVerificationStatus();
                String documentHintType;
                if (pos != documentPreviewDetailsList.size() - 1)
                    documentHintType = DOCUMENT_HINT_TYPES[pos];
                else
                    documentHintType = DOCUMENT_HINT_TYPES[pos] + " " + getString(R.string.number_uppercase);

                mDocumentIdTextInputLayoutView.setHint(documentHintType);
                setEditTextMaxLength(mDocumentIdEditTextView, pos);
                Utilities.setAppropriateKeyboard(getActivity(), documentPreviewDetailsList.get(pos).getDocumentType(), mDocumentIdEditTextView);
                if (pos == documentPreviewDetailsList.size() - 1) {
                    mDocumentTypeForOtherTypeLayoutView.setVisibility(View.VISIBLE);
                    mDividerDocumentType.setVisibility(View.VISIBLE);
                    mDocumentTypeOtherEditText.setText(mDocumentName);
                } else {
                    mDocumentTypeForOtherTypeLayoutView.setVisibility(View.GONE);
                    mDividerDocumentType.setVisibility(View.GONE);
                }

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
                        try {
                            customUploadPickerDialog = new CustomUploadPickerDialog(getActivity(), getActivity().getString(R.string.select_a_document), mPickerList);
                            customUploadPickerDialog.setOnResourceSelectedListener(new CustomUploadPickerDialog.OnResourceSelectedListener() {
                                @Override
                                public void onResourceSelected(int mActionId, String action) {
                                    mSelectedItemId = pos;
                                    mDocumentName = mDocumentTypeOtherEditText.getText().toString();
                                    documentPreviewDetailsList.get(pos).setDocumentId(mDocumentIdEditTextView.getText().toString());
                                    documentPreviewDetailsList.get(pos).setSelectedFilePath(mSelectFile.getText().toString());
                                    if (Constants.ACTION_TYPE_TAKE_PICTURE.equals(action) || Constants.ACTION_TYPE_SELECT_FROM_GALLERY.equals(action))
                                        if (Utilities.isNecessaryPermissionExists(getActivity(), DocumentPicker.DOCUMENT_PICK_PERMISSIONS))
                                            selectDocument(mActionId);
                                        else {
                                            mPickerActionId = mActionId;
                                            Utilities.requestRequiredPermissions(OnBoardPhotoIdUploadFragment.this, REQUEST_CODE_PERMISSION, DocumentPicker.DOCUMENT_PICK_PERMISSIONS);
                                        }
                                    else {
                                        getDocumentAccessToken();
                                        mSelectedDocumentDetails = documentPreviewDetailsList.get(pos);
                                    }
                                }
                            });
                            customUploadPickerDialog.show();
                            Utilities.hideKeyboard(getActivity());

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toaster.makeText(getActivity(), R.string.try_again_later, Toast.LENGTH_SHORT);
                        }
                    }
                });

                mUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attemptUploadDocument(pos);
                    }
                });
            }

            private void attemptUploadDocument(int pos) {
                mDocumentListRecyclerView.scrollToPosition(pos);
                String documentID = mDocumentIdEditTextView.getText().toString();
                String errorMessage;
                boolean cancel = false;
                errorMessage = InputValidator.isValidDocumentID(getActivity(), documentID, DOCUMENT_TYPES[pos], pos);

                if (errorMessage != null) {
                    mDocumentIdEditTextView.requestFocus();
                    mDocumentIdEditTextView.setError(errorMessage);
                } else {
                    if (mDocumentTypeForOtherTypeLayoutView.getVisibility() == View.VISIBLE) {
                        if (mDocumentTypeOtherEditText.getText().toString().equals("")) {
                            mDocumentTypeOtherEditText.requestFocus();
                            mDocumentTypeOtherEditText.setError(getString(R.string.please_enter_a_document_name));
                            cancel = true;
                        } else
                            mDocumentName = mDocumentTypeOtherEditText.getText().toString();
                    }
                    if (!cancel) {
                        if (documentPreviewDetailsList.get(pos).getSelectedDocumentUri() == null) {
                            mSelectFile.setError(getString(R.string.please_select_a_file_to_upload));
                            cancel = true;
                        }
                    }
                    if (!cancel) {
                        Utilities.hideKeyboard(getActivity());
                        documentPreviewDetailsList.get(pos).setDocumentId(documentID);
                        if (Utilities.isConnectionAvailable(getActivity())) {
                            uploadDocument(pos);
                        } else
                            Toaster.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT);
                    }

                }
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