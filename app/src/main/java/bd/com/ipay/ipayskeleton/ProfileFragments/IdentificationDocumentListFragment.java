package bd.com.ipay.ipayskeleton.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DocumentPreviewActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.DocumentPreviewRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.IdentificationDocument;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IdentificationDocumentListFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

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

    private static final int[] DOCUMENT_TYPE_NAMES = {
            R.string.national_id,
            R.string.passport,
            R.string.driving_license,
            R.string.birth_certificate,
            R.string.tin
    };

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
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
        if (pushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE))
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

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetIdentificationDocumentsTask = null;
            Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }


        Gson gson = new Gson();
        
        if (result.getApiCommand().equals(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST)) {
            try {
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    processGetDocumentListResponse(result.getJsonString());

                    DataHelper dataHelper = DataHelper.getInstance(getActivity());
                    dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, result.getJsonString());

                    PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
                    pushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_IDENTIFICATION_DOCUMENT_UPDATE, false);
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

    public class DocumentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mDocumentTypeNameView;
            private TextView mDocumentIdView;
            private ImageView mVerificationStatus;
            private LinearLayout mOptionsLayout;
            private Button mUploadButton;
            private Button mPreviewButton;
            private View mDivider;

            public ViewHolder(final View itemView) {
                super(itemView);

                mDocumentTypeNameView = (TextView) itemView.findViewById(R.id.textview_document_type);
                mDocumentIdView = (TextView) itemView.findViewById(R.id.textview_document_id);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.document_verification_status);

                mOptionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                mUploadButton = (Button) itemView.findViewById(R.id.button_upload);
                mPreviewButton = (Button) itemView.findViewById(R.id.button_preview);
                mDivider = itemView.findViewById(R.id.divider);

            }

            public void bindView(final int pos) {

                final IdentificationDocumentDetails identificationDocumentDetail = mIdentificationDocumentDetails[pos];

                final String verificationStatus = identificationDocumentDetail.getVerificationStatus();

                // Unverified, document not yet uploaded
                if (verificationStatus == null) {
                    mVerificationStatus.setVisibility(View.GONE);
                    mDocumentIdView.setText(R.string.not_submitted);

//                    mOptionsLayout.setVisibility(View.VISIBLE);
                    mUploadButton.setText(getString(R.string.upload));

                    mPreviewButton.setVisibility(View.GONE);
                    mDivider.setVisibility(View.GONE);
                }
                // Document uploaded and verified
                else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_verified);
                    mVerificationStatus.setColorFilter(null);
                    mDocumentIdView.setText(identificationDocumentDetail.getDocumentId());

//                    mOptionsLayout.setVisibility(View.GONE);

                    mPreviewButton.setVisibility(View.GONE);
                    mDivider.setVisibility(View.GONE);
                }
                // Document uploaded but not verified
                else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED)) {
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_cached_black_24dp);
                    mVerificationStatus.setColorFilter(Color.GRAY);
                    mDocumentIdView.setText(identificationDocumentDetail.getDocumentId());

//                    mOptionsLayout.setVisibility(View.GONE);
                    mUploadButton.setText(getString(R.string.upload_again));

                    mPreviewButton.setVisibility(View.VISIBLE);
                    mDivider.setVisibility(View.VISIBLE);
                }
                mDocumentTypeNameView.setText(identificationDocumentDetail.getDocumentTypeName());

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    if (mOptionsLayout.getVisibility() == View.VISIBLE) {
                        mOptionsLayout.setVisibility(View.GONE);
                    } else {
                        mOptionsLayout.setVisibility(View.VISIBLE);
                    }
                    }
                });

                mUploadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.DOCUMENT_TYPE, identificationDocumentDetail.getDocumentType());
                        bundle.putString(Constants.DOCUMENT_TYPE_NAME, identificationDocumentDetail.getDocumentTypeName());
                        bundle.putString(Constants.DOCUMENT_ID, identificationDocumentDetail.getDocumentId());
                        ((ProfileActivity) getActivity()).switchToDocumentUploadFragment(bundle);
                    }
                });

                mPreviewButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getDocumentAccessToken();
                        mSelectedIdentificationDocument = identificationDocumentDetail;
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_document,
                    parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
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
        private String documentType;
        private String documentTypeName;
        private String documentId;
        private String verificationStatus;
        private String documentUrl;

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
