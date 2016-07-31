package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Api.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomUploadPickerDialog;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.DocumentPreviewBindViewHolder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.DocumentPreviewRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.IdentificationDocument;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IdentificationDocumentListFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

    private UploadIdentifierDocumentAsyncTask mUploadIdentifierDocumentAsyncTask;
    private UploadDocumentResponse mUploadDocumentResponse;

    private HttpRequestGetAsyncTask mGetDocumentAccessTokenTask = null;

    private TextView mDocumentUploadInfoView;
    private DocumentListAdapter mDocumentListAdapter;
    private RecyclerView mDocumentListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<IdentificationDocument> mIdentificationDocuments = new ArrayList<>();
    private IdentificationDocumentDetails mSelectedIdentificationDocument;

    private ProgressDialog mProgressDialog;

    private IdentificationDocumentDetails[] mIdentificationDocumentDetails;

    private SharedPreferences pref;

    private static final String[] DOCUMENT_TYPES = {
            Constants.DOCUMENT_TYPE_NATIONAL_ID,
            Constants.DOCUMENT_TYPE_PASSPORT,
            Constants.DOCUMENT_TYPE_DRIVING_LICENSE,
            Constants.DOCUMENT_TYPE_BIRTH_CERTIFICATE,
            Constants.DOCUMENT_TYPE_TIN
    };

    private String mFileName;

    private static final int[] DOCUMENT_TYPE_NAMES = {
            R.string.national_id,
            R.string.passport,
            R.string.driving_license,
            R.string.birth_certificate,
            R.string.tin
    };

    private static final int ACTION_UPLOAD_DOCUMENT = 100;
    private static final int REQUEST_CODE_PERMISSION = 1001;
    private static final int OPTION_UPLOAD_DOCUMENT = 1;
    private int mSelectedItemId = -1;
    private int mActionId = -1;
    private Uri mSelectedDocumentUri;
    private ArrayList<DocumentPreviewBindViewHolder> documentPreviewBindViewHolderList;

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
        View v = inflater.inflate(R.layout.fragment_identification_documents, container, false);
        getActivity().setTitle(R.string.identification_documents);

        mProgressDialog = new ProgressDialog(getActivity());
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mDocumentUploadInfoView = (TextView) v.findViewById(R.id.textview_document_upload_info);
        mDocumentListRecyclerView = (RecyclerView) v.findViewById(R.id.list_documents);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mDocumentListRecyclerView.setLayoutManager(mLayoutManager);
        documentPreviewBindViewHolderList = new ArrayList<DocumentPreviewBindViewHolder>(DOCUMENT_TYPES.length);
        for (int i = 0; i < DOCUMENT_TYPES.length; i++)
            documentPreviewBindViewHolderList.add(new DocumentPreviewBindViewHolder());
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE))
            getIdentificationDocuments();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE);

            if (json == null)
                getIdentificationDocuments();
            else {
                processGetDocumentListResponse(json);
            }
        }

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
                        documentPreviewBindViewHolderList.get(mSelectedItemId).setmSelectedfilePath(mFileName);
                        mSelectedDocumentUri = DocumentPicker.getDocumentFromResult(getActivity(), resultCode, intent);
                        Log.w("Loading document", mSelectedItemId + mSelectedDocumentUri.toString());
                        documentPreviewBindViewHolderList.get(mSelectedItemId).setmSelectedDocumentUri(mSelectedDocumentUri);
                        mDocumentListAdapter.notifyDataSetChanged();
                        mSelectedItemId = -1;
                    }
                } else {
                    mSelectedItemId = -1;
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
                    selectDocument(mActionId);
                } else {
                    Toast.makeText(getActivity(), R.string.prompt_grant_permission, Toast.LENGTH_LONG).show();
                }
        }
    }

    private void loadDocumentInfo() {

        mIdentificationDocumentDetails = new IdentificationDocumentDetails[DOCUMENT_TYPES.length];
        for (int i = 0; i < DOCUMENT_TYPES.length; i++) {
            String documentId = "";
            String verificationStatus = null;
            String documentUrl = null;

            for (IdentificationDocument identificationDocument : mIdentificationDocuments) {
                if (identificationDocument.getDocumentType().equals(DOCUMENT_TYPES[i])) {
                    documentId = identificationDocument.getDocumentIdNumber();
                    verificationStatus = identificationDocument.getDocumentVerificationStatus();
                    documentUrl = identificationDocument.getDocumentUrl();
                }
            }

            mIdentificationDocumentDetails[i] = new IdentificationDocumentDetails(DOCUMENT_TYPES[i],
                    getString(DOCUMENT_TYPE_NAMES[i]), documentId, verificationStatus, documentUrl);
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

        String selectedOImagePath = documentPreviewBindViewHolderList.get(mID).getmSelectedDocumentUri().getPath();
        Log.w("Loading document", mID + selectedOImagePath + mDocumentType);

        mUploadIdentifierDocumentAsyncTask = new UploadIdentifierDocumentAsyncTask(
                Constants.COMMAND_UPLOAD_DOCUMENT, selectedOImagePath, getActivity(),
                mDocumentID, mDocumentType);
        mUploadIdentifierDocumentAsyncTask.mHttpResponseListener = this;
        mUploadIdentifierDocumentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mGetIdentificationDocumentsTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST)) {
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_DOCUMENT_ACCESS_TOKEN)) {
            try {
                String resourceToken = result.getResourceToken();
                String documentUrl = DocumentPreviewRequestBuilder.generateUri(resourceToken,
                        mSelectedIdentificationDocument.getDocumentUrl(),
                        mSelectedIdentificationDocument.getDocumentId(),
                        mSelectedIdentificationDocument.getDocumentType());

                if (Constants.DEBUG)
                    Log.w("Loading document", documentUrl);

                Intent intent = new Intent(getActivity(), DocumentPreviewActivity.class);
                intent.putExtra(Constants.FILE_EXTENSION, Utilities.getExtension(mSelectedIdentificationDocument.getDocumentUrl()));
                intent.putExtra(Constants.DOCUMENT_URL, documentUrl);
                intent.putExtra(Constants.DOCUMENT_TYPE_NAME, mSelectedIdentificationDocument.getDocumentTypeName());
                startActivity(intent);

                mProgressDialog.dismiss();
                mGetDocumentAccessTokenTask = null;
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.failed_document_preview_loading, Toast.LENGTH_SHORT).show();
            }
        } else if (result.getApiCommand().equals(Constants.COMMAND_UPLOAD_DOCUMENT)) {
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

        mProgressDialog.dismiss();
    }

    private void processGetDocumentListResponse(String json) {
        Gson gson = new Gson();
        mIdentificationDocumentResponse = gson.fromJson(json, GetIdentificationDocumentResponse.class);

        mIdentificationDocuments = mIdentificationDocumentResponse.getDocuments();
        loadDocumentInfo();

        mDocumentListAdapter = new DocumentListAdapter();
        mDocumentListRecyclerView.setAdapter(mDocumentListAdapter);

        if (mIdentificationDocuments.size() < DOCUMENT_TYPES.length) {
            String accountVerificationStatus = pref.getString(
                    Constants.VERIFICATION_STATUS, Constants.ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED);
            if (accountVerificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                mDocumentUploadInfoView.setText(R.string.upload_identification_documents_to_confirm_identity);
            } else {
                mDocumentUploadInfoView.setText(R.string.you_need_to_upload_identification_documents_to_get_verified);
            }
        } else {
            mDocumentUploadInfoView.setVisibility(View.GONE);
        }

        setContentShown(true);

    }

    private void selectDocument(int id) {
        Intent imagePickerIntent = DocumentPicker.getPickImageOrPdfIntentByID(getActivity(), getString(R.string.select_a_document), id);
        startActivityForResult(imagePickerIntent, ACTION_UPLOAD_DOCUMENT);
    }

    public class DocumentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView mDocumentTypeNameView;
            private final TextView mDocumentIdView;
            private final ImageView mVerificationStatus;
            private final RelativeLayout mInfoLayout;
            private final LinearLayout mOptionsLayout;
            private EditText mDocumentIdEditTextView;
            private final EditText mSelectFile;
            private final Button mUploadButton;
            private final ImageView mPicker;

            private CustomUploadPickerDialog customUploadPickerDialog;
            private List<String> mPickerList;

            public ViewHolder(final View itemView) {
                super(itemView);

                mDocumentTypeNameView = (TextView) itemView.findViewById(R.id.textview_document_type);
                mDocumentIdView = (TextView) itemView.findViewById(R.id.textview_document_id);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.document_verification_status);
                mInfoLayout = (RelativeLayout) itemView.findViewById(R.id.info_layout);
                mOptionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                mDocumentIdEditTextView = (EditText) itemView.findViewById(R.id.edit_text_document_id);
                mSelectFile = (EditText) itemView.findViewById(R.id.select_file);
                mUploadButton = (Button) itemView.findViewById(R.id.button_upload);
                mPicker = (ImageView) itemView.findViewById(R.id.button_select_file);

            }

            public void bindView(final int pos) {
                mDocumentIdEditTextView.setError(null);
                mPickerList = new ArrayList<>();

                final IdentificationDocumentDetails identificationDocumentDetail = mIdentificationDocumentDetails[pos];

                final String verificationStatus = identificationDocumentDetail.getVerificationStatus();

                // Unverified, document not yet uploaded
                if (verificationStatus == null) {
                    mPickerList = Arrays.asList(getResources().getStringArray(R.array.upload_picker_action));
                    mVerificationStatus.setVisibility(View.GONE);
                    mDocumentIdView.setText(R.string.not_submitted);
                    mUploadButton.setText(getString(R.string.upload));
                }
                // Document uploaded and verified
                else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_verified);
                    mVerificationStatus.setColorFilter(null);
                    mDocumentIdView.setText(identificationDocumentDetail.getDocumentId());
                }
                // Document uploaded but not verified
                else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED)) {
                    mPickerList = Arrays.asList(getResources().getStringArray(R.array.pending_picker_action));
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_workinprogress);
                    mVerificationStatus.setColorFilter(Color.GRAY);
                    mDocumentIdView.setText(identificationDocumentDetail.getDocumentId());
                    mUploadButton.setText(getString(R.string.upload_again));
                }
                mDocumentTypeNameView.setText(identificationDocumentDetail.getDocumentTypeName());

                if (documentPreviewBindViewHolderList.get(pos).getmSelectedfilePath() != null) {
                    mSelectFile.setText(documentPreviewBindViewHolderList.get(pos).getmSelectedfilePath());
                } else {
                    mSelectFile.setText("");
                }

                if (documentPreviewBindViewHolderList.get(pos).isViewOpen()) {
                    mOptionsLayout.setVisibility(View.VISIBLE);
                } else
                    mOptionsLayout.setVisibility(View.GONE);

                mInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mPickerList.size() > 0) {
                            if (documentPreviewBindViewHolderList.get(pos).isViewOpen()) {
                                mOptionsLayout.setVisibility(View.GONE);
                                documentPreviewBindViewHolderList.get(pos).setIsViewOpen(false);
                            } else {
                                mOptionsLayout.setVisibility(View.VISIBLE);
                                documentPreviewBindViewHolderList.get(pos).setIsViewOpen(true);
                            }
                        }

                    }
                });
                mDocumentIdEditTextView.setText(documentPreviewBindViewHolderList.get(pos).getmDocumentId());

                mPicker.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customUploadPickerDialog = new CustomUploadPickerDialog(getActivity(), getString(R.string.select_a_document), mPickerList);
                        customUploadPickerDialog.setOnResourceSelectedListener(new CustomUploadPickerDialog.OnResourceSelectedListener() {
                            @Override
                            public void onResourceSelected(int mactionId, String name) {

                                mSelectedItemId = pos;
                                documentPreviewBindViewHolderList.get(pos).setmDocumentId(mDocumentIdEditTextView.getText().toString());
                                if (mactionId <= OPTION_UPLOAD_DOCUMENT)
                                    if (DocumentPicker.ifNecessaryPermissionExists(getActivity())) {
                                        selectDocument(mactionId);
                                    } else {
                                        mActionId = mactionId;
                                        DocumentPicker.requestRequiredPermissions(IdentificationDocumentListFragment.this, REQUEST_CODE_PERMISSION);
                                    }
                                else {
                                    getDocumentAccessToken();
                                    mSelectedIdentificationDocument = identificationDocumentDetail;
                                }
                            }
                        });
                        customUploadPickerDialog.show();
                    }
                });

                mUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mDocumentIdEditTextView.getText().toString().isEmpty()) {
                            mDocumentIdEditTextView.setError(getString(R.string.please_enter_document_number));
                            mDocumentIdEditTextView.requestFocus();
                        } else if (documentPreviewBindViewHolderList.get(pos).getmSelectedfilePath().isEmpty()) {
                            mSelectFile.setError(getString(R.string.please_select_a_file_to_upload));
                        } else
                            uploadDocument(documentPreviewBindViewHolderList.get(pos).getmDocumentId(), identificationDocumentDetail.getDocumentType(), pos);
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
            if (mIdentificationDocumentDetails != null)
                return mIdentificationDocumentDetails.length;
            else return 0;
        }

        @Override
        public int getItemViewType(int position) {
            return super.getItemViewType(position);
        }
    }

    public static class IdentificationDocumentDetails {
        private final String documentType;
        private final String documentTypeName;
        private final String documentId;
        private final String verificationStatus;
        private final String documentUrl;

        public IdentificationDocumentDetails(String documentType, String documentTypeName, String documentId, String verificationStatus, String documentUrl) {
            this.documentType = documentType;
            this.documentTypeName = documentTypeName;
            this.documentId = documentId;
            this.verificationStatus = verificationStatus;
            this.documentUrl = documentUrl;
        }

        public String getDocumentType() {
            return documentType;
        }

        public String getDocumentTypeName() {
            return documentTypeName;
        }

        public String getDocumentId() {
            return documentId;
        }

        public String getVerificationStatus() {
            return verificationStatus;
        }

        public String getDocumentUrl() {
            return documentUrl;
        }
    }

}
