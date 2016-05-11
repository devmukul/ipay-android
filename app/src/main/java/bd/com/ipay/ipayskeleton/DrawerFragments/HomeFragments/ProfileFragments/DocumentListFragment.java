package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ProfileFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.UploadIdentifierDocumentAsyncTask;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.GetIdentificationDocumentResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.IdentificationDocument;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Documents.UploadDocumentResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DocumentListFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetIdentificationDocumentsTask = null;
    private GetIdentificationDocumentResponse mIdentificationDocumentResponse = null;

    private TextView mDocumentUploadInfoView;
    private DocumentListAdapter mDocumentListAdapter;
    private RecyclerView mDocumentListRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private List<IdentificationDocument> mIdentificationDocuments = new ArrayList<>();

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
        View v = inflater.inflate(R.layout.fragment_documents, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mDocumentUploadInfoView = (TextView) v.findViewById(R.id.textview_document_upload_info);
        mDocumentListRecyclerView = (RecyclerView) v.findViewById(R.id.list_documents);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mDocumentListRecyclerView.setLayoutManager(mLayoutManager);

        getIdentificationDocuments();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setContentShown(false);
    }

    private void loadDocumentInfo() {

        mIdentificationDocumentDetails = new IdentificationDocumentDetails[DOCUMENT_TYPES.length];
        for (int i = 0; i < DOCUMENT_TYPES.length; i++) {
            String documentId = "";
            String verificationStatus = null;

            for (IdentificationDocument identificationDocument : mIdentificationDocuments) {
                if (identificationDocument.getDocumentType().equals(DOCUMENT_TYPES[i])) {
                    documentId = identificationDocument.getDocumentIdNumber();
                    verificationStatus = identificationDocument.getDocumentVerificationStatus();
                }
            }

            mIdentificationDocumentDetails[i] = new IdentificationDocumentDetails(DOCUMENT_TYPES[i],
                    getString(DOCUMENT_TYPE_NAMES[i]), documentId, verificationStatus);
        }
    }

    private void getIdentificationDocuments() {
        if (mGetIdentificationDocumentsTask != null) {
            return;
        }

        mGetIdentificationDocumentsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST,
                Constants.BASE_URL + "/" + Constants.URL_GET_DOCUMENTS, getActivity(), this);
        mGetIdentificationDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();
            mGetIdentificationDocumentsTask = null;
            Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();
        
        if (resultList.get(0).equals(Constants.COMMAND_GET_IDENTIFICATION_DOCUMENTS_REQUEST)) {
            try {
                mIdentificationDocumentResponse = gson.fromJson(resultList.get(2), GetIdentificationDocumentResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
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
        }

        mProgressDialog.dismiss();
    }

    public class DocumentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mDocumentTypeNameView;
            private TextView mDocumentIdView;
            private ImageView mVerificationStatus;
            private LinearLayout mOptionsLayout;
            private Button mUploadButton;

            public ViewHolder(final View itemView) {
                super(itemView);

                mDocumentTypeNameView = (TextView) itemView.findViewById(R.id.textview_document_type);
                mDocumentIdView = (TextView) itemView.findViewById(R.id.textview_document_id);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.document_verification_status);

                mOptionsLayout = (LinearLayout) itemView.findViewById(R.id.options_layout);
                mUploadButton = (Button) itemView.findViewById(R.id.button_upload);
            }

            public void bindView(int pos) {

                final IdentificationDocumentDetails identificationDocumentDetail = mIdentificationDocumentDetails[pos];

                String verificationStatus = identificationDocumentDetail.getVerificationStatus();

                if (verificationStatus == null) {
                    mVerificationStatus.setVisibility(View.GONE);
                    mDocumentIdView.setText(R.string.not_submitted);

                    mOptionsLayout.setVisibility(View.VISIBLE);
                    mUploadButton.setText(getString(R.string.upload));
                }
                else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_VERIFIED)) {
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_verified);
                    mVerificationStatus.setColorFilter(null);
                    mDocumentIdView.setText(identificationDocumentDetail.getDocumentId());

                    mOptionsLayout.setVisibility(View.GONE);
                } else if (verificationStatus.equals(Constants.ACCOUNT_VERIFICATION_STATUS_NOT_VERIFIED)) {
                    mVerificationStatus.setVisibility(View.VISIBLE);
                    mVerificationStatus.setImageResource(R.drawable.ic_cached_black_24dp);
                    mVerificationStatus.setColorFilter(Color.GRAY);
                    mDocumentIdView.setText(identificationDocumentDetail.getDocumentId());

                    mOptionsLayout.setVisibility(View.VISIBLE);
                    mUploadButton.setText(getString(R.string.upload_again));
                }
                mDocumentTypeNameView.setText(identificationDocumentDetail.getDocumentTypeName());

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

        public IdentificationDocumentDetails(String documentType, String documentTypeName, String documentId, String verificationStatus) {
            this.documentType = documentType;
            this.documentTypeName = documentTypeName;
            this.documentId = documentId;
            this.verificationStatus = verificationStatus;
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
    }
}
