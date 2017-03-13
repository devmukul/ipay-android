package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.devspark.progressfragment.ProgressFragment;
import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ImagePickerActivity;
import com.esafirm.imagepicker.model.Image;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import bd.com.ipay.ipayskeleton.Activities.DocumentPreviewActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.UploadTicketAttachmentAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.AttachmentView;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomUploadPickerDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.AddCommentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.Comment;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.CommentIdResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.GetTicketDetailsRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.GetTicketDetailsResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.TicketAttachmentUploadResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DocumentPicker;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TicketDetailsFragment extends ProgressFragment implements HttpResponseListener {
    private HttpRequestGetAsyncTask mGetTicketDetailsTask = null;
    private GetTicketDetailsResponse mGetTicketDetailsResponse;

    private HttpRequestPostAsyncTask mNewCommentTask = null;

    private CommentIdResponse mCommentIdResponse;

    private RecyclerView mCommentListRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private View mAttachmentView;
    private TextView mAttachmentNumberTextView;
    private ImageButton mSendCommentButton;
    private ImageButton mAttachFileButton;
    private ImageButton mRemoveAttachFileButton;
    private EditText mUserCommentEditText;

    private ProgressDialog mProgressDialog;
    private CustomUploadPickerDialog customUploadPickerDialog;

    private List<Comment> mComments;
    private String requesterId;
    private CommentListAdapter mCommentListAdapter;

    private List<String> mPickerList;
    private ArrayList<String> attachedFiles;
    private ArrayList<Image> images = new ArrayList<>();

    private long ticketId;
    private long mCommentId;
    private int mPickerActionId = -1;

    private static final int REQUEST_CODE_PERMISSION = 1001;
    private static final int REQUEST_CODE_PICK_MULTIPLE_IMAGE = 1000;
    private static final int REQUEST_CODE_PICK_IMAGE_OR_DOCUMENT = 1002;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_ticket_details, container, false);

        ticketId = getArguments().getLong(Constants.TICKET_ID);

        mAttachmentView = v.findViewById(R.id.attachmentLayout);
        mAttachmentNumberTextView = (TextView) v.findViewById(R.id.textview_attachment_number);
        mUserCommentEditText = (EditText) v.findViewById(R.id.user_comment_text);
        mSendCommentButton = (ImageButton) v.findViewById(R.id.btn_send);
        mAttachFileButton = (ImageButton) v.findViewById(R.id.btn_attach);
        mRemoveAttachFileButton = (ImageButton) v.findViewById(R.id.btn_remove_attachment);

        mCommentListAdapter = new CommentListAdapter();
        mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setStackFromEnd(true);
        mCommentListRecyclerView = (RecyclerView) v.findViewById(R.id.list_comments);
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);

        mCommentListRecyclerView.setLayoutManager(mLayoutManager);
        mCommentListRecyclerView.setAdapter(mCommentListAdapter);
        attachedFiles = new ArrayList<>();

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (Utilities.isConnectionAvailable(getActivity())) {
                    getTicketDetails();
                }
            }
        });

        mProgressDialog = new ProgressDialog(getActivity());

        mSendCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addUserComment();
            }
        });
        mAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (attachedFiles.size() < Constants.MAX_FILE_ATTACHMENT_LIMIT)
                    selectAttachmentDialog();
                else
                    Toast.makeText(getActivity(), R.string.max_limit_exceed, Toast.LENGTH_LONG).show();
            }
        });
        mRemoveAttachFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachedFiles.removeAll(attachedFiles);
                setAttachmentVisibility();
            }
        });

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getTicketDetails();
    }

    private void setTitle(String title) {
        getActivity().setTitle(title);
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

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_PICK_IMAGE_OR_DOCUMENT:
                if (resultCode == Activity.RESULT_OK) {
                    String filePath = DocumentPicker.getFilePathForCameraOrPDFResult(getActivity(), resultCode, data);

                    if (filePath != null) {
                        Random r = new Random();
                        int fileIndex = r.nextInt(100 - 1) + 1;
                        Uri mSelectedDocumentUri = DocumentPicker.getDocumentUriWithIndexFromResult(getActivity(), resultCode, data, fileIndex);
                        if (mSelectedDocumentUri != null)
                            attachedFiles.add(mSelectedDocumentUri.getPath());
                        else attachedFiles.add(filePath);
                        setAttachmentVisibility();
                    }
                }
                break;

            case REQUEST_CODE_PICK_MULTIPLE_IMAGE:
                if (resultCode == Activity.RESULT_OK && data != null) {
                    images = (ArrayList<Image>) ImagePicker.getImages(data);
                    if (images != null) setImagePathsFromMultiplePicker(images);
                }

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setAttachmentVisibility() {
        if (attachedFiles.size() > 0) {
            mAttachmentView.setVisibility(View.VISIBLE);
            if (attachedFiles.size() > 1)
                mAttachmentNumberTextView.setText(attachedFiles.size() + " " + getString(R.string.attachments));
            else
                mAttachmentNumberTextView.setText(attachedFiles.size() + " " + getString(R.string.attachment));
        } else mAttachmentView.setVisibility(View.GONE);
    }

    private void uploadMultipleAttachmentsAsyncTask() {
        for (int i = 0; i < attachedFiles.size(); i++) {
            if (!attachedFiles.get(i).isEmpty())
                uploadAttachment(attachedFiles.get(i));
        }
        attachedFiles.removeAll(attachedFiles);
        setAttachmentVisibility();
    }

    private boolean validateUserComment() {
        if (mUserCommentEditText.getText().toString().trim().isEmpty()) return false;
        else return true;
    }

    private void addUserComment() {
        if (validateUserComment()) {
            String comment = mUserCommentEditText.getText().toString().trim();
            sendComment(comment);

            mUserCommentEditText.getText().clear();
            Utilities.hideKeyboard(getActivity(), mUserCommentEditText);
        } else {
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.comment_cannot_be_empty, Toast.LENGTH_LONG).show();
        }
    }

    private void loadAttachedDocuments(long commentId, List<String> newUploadeddocuments) {
        int index = CommonData.getIndexOfComment(commentId, mComments);
        Comment comment = mComments.get(index);

        List<String> documentList = comment.getDocuments();
        documentList.addAll(newUploadeddocuments);
        comment.setDocuments(documentList);
        mComments.set(index, comment);

        mCommentListAdapter.notifyDataSetChanged();
        mCommentListRecyclerView.smoothScrollToPosition(mCommentListAdapter.getItemCount() - 1);
    }

    private void selectAttachmentDialog() {
        mPickerList = Arrays.asList(getResources().getStringArray(R.array.attach_file_picker_action));
        customUploadPickerDialog = new CustomUploadPickerDialog(getActivity(), getString(R.string.select_a_document), mPickerList);
        customUploadPickerDialog.setOnResourceSelectedListener(new CustomUploadPickerDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int mActionId, String action) {
                if (!Constants.ACTION_TYPE_SELECT_FROM_GALLERY.equals(action)) {
                    if (DocumentPicker.ifNecessaryPermissionExists(getActivity()))
                        selectDocument(mActionId);
                    else {
                        mPickerActionId = mActionId;
                        DocumentPicker.requestRequiredPermissions(TicketDetailsFragment.this, REQUEST_CODE_PERMISSION);
                    }
                } else {
                    setMultipleImagePicker();
                }
            }
        });
        customUploadPickerDialog.show();
    }

    private void getTicketDetails() {
        if (mGetTicketDetailsTask != null)
            return;

        setContentShown(false);

        mGetTicketDetailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TICKET_DETAILS,
                new GetTicketDetailsRequestBuilder().generateUri(ticketId).toString(), getActivity(), this);
        mGetTicketDetailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendComment(String comment) {
        if (mNewCommentTask != null)
            return;

        mProgressDialog.setMessage(getString(R.string.progress_dialog_submitting_comment));
        mProgressDialog.show();

        Gson gson = new Gson();

        AddCommentRequest addCommentRequest = new AddCommentRequest(mGetTicketDetailsResponse.getResponse().getTicket().getId(), comment);
        String json = gson.toJson(addCommentRequest);

        mNewCommentTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ADD_COMMENT, Constants.BASE_URL_ADMIN + Constants.URL_ADD_COMMENT,
                json, getActivity(), this);
        mNewCommentTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void uploadAttachment(String filePath) {
        UploadTicketAttachmentAsyncTask mUploadTicketAttachmentAsyncTask = new UploadTicketAttachmentAsyncTask(
                Constants.COMMAND_ADD_ATTACHMENT, filePath, mCommentId, getActivity());
        mUploadTicketAttachmentAsyncTask.mHttpResponseListener = this;
        mUploadTicketAttachmentAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (getActivity() != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mProgressDialog.dismiss();
        }

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetTicketDetailsTask = null;
            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_SHORT).show();
                getActivity().onBackPressed();
            }
            return;
        }

        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_GET_TICKET_DETAILS:
                try {
                    mGetTicketDetailsResponse = gson.fromJson(result.getJsonString(), GetTicketDetailsResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {

                        mComments = mGetTicketDetailsResponse.getResponse().getComments().getComments();
                        requesterId = mGetTicketDetailsResponse.getResponse().getTicket().getRequesterId();

                        setTitle(mGetTicketDetailsResponse.getResponse().getTicket().getSubject());
                        mCommentListAdapter.notifyDataSetChanged();

                        String ticketStatus = mGetTicketDetailsResponse.getResponse().getTicket().getStatus();

                        if (ticketStatus.equals(Constants.TICKET_STATUS_SOLVED)
                                || ticketStatus.equals(Constants.TICKET_STATUS_CLOSED)) {
                            mSendCommentButton.setVisibility(View.GONE);
                            mUserCommentEditText.setVisibility(View.GONE);
                        }

                        if (isAdded())
                            setContentShown(true);
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.failed_loading_ticket_details, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_ticket_details, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }

                mGetTicketDetailsTask = null;
                break;

            case Constants.COMMAND_ADD_COMMENT:
                try {
                    TicketAttachmentUploadResponse ticketResponseWithCommentId = gson.fromJson(result.getJsonString(), TicketAttachmentUploadResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            CommentIdResponse commentIdResponse = ticketResponseWithCommentId.getResponse();
                            mCommentId = commentIdResponse.getComment_id();
                            if (attachedFiles.size() > 0) uploadMultipleAttachmentsAsyncTask();

                            Toast.makeText(getActivity(), R.string.comment_successfully_added, Toast.LENGTH_LONG).show();
                            getTicketDetails();
                        } else {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), R.string.not_available, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_adding_comment, Toast.LENGTH_LONG).show();
                    }
                }
                mNewCommentTask = null;
                break;


            case Constants.COMMAND_ADD_ATTACHMENT:
                try {
                    CommentIdResponse commentIdResponse = gson.fromJson(result.getJsonString(), CommentIdResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            mCommentId = commentIdResponse.getComment_id();
                            List<String> documents = commentIdResponse.getDocuments();
                            loadAttachedDocuments(mCommentId, documents);
                        } else {
                            if (getActivity() != null) {
                                Toast.makeText(getActivity(), R.string.not_available, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } catch (Exception e) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_adding_comment, Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }

    }

    private class CommentListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private static final int VIEW_TYPE_FROM_ME = 1;
        private static final int VIEW_TYPE_FROM_SUPPORT = 2;

        private class CommentViewHolder extends RecyclerView.ViewHolder {

            private ProfileImageView profilePictureView;
            private TextView commentView;
            private TextView timeView;
            private LinearLayout attachmentLayout;


            public CommentViewHolder(View itemView) {
                super(itemView);

                profilePictureView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                commentView = (TextView) itemView.findViewById(R.id.textview_comment);
                timeView = (TextView) itemView.findViewById(R.id.textview_time);
                attachmentLayout = (LinearLayout) itemView.findViewById(R.id.attachmentLayout);
            }

            public void bindView(int pos) {
                final Comment comment = mComments.get(pos);

                if (comment.getAuthorId().equals(requesterId)) {
                    profilePictureView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + ProfileInfoCacheManager.getProfileImageUrl(), false);

                } else {
                    profilePictureView.setProfilePicture(R.drawable.ic_transaction_ipaylogo);
                }
                commentView.setText(comment.getBody());
                timeView.setText(Utilities.formatDateWithTime(comment.getCreatedAt()));

                attachmentLayout.removeAllViews();
                if (!comment.getDocuments().isEmpty()) {
                    List<String> documents = comment.getDocuments();

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    attachmentLayout.setLayoutParams(params);
                    attachmentLayout.setOrientation(LinearLayout.VERTICAL);
                    attachmentLayout.setGravity(Gravity.CENTER);

                    for (final String document : documents) {
                        AttachmentView attachmentview = new AttachmentView(getActivity());
                        attachmentview.setAttachment(document, false);
                        LinearLayout.LayoutParams attachmentViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        if (comment.getAuthorId().equals(requesterId))
                            attachmentViewParams.gravity = Gravity.RIGHT;
                        else
                            attachmentViewParams.gravity = Gravity.LEFT;

                        attachmentview.setLayoutParams(attachmentViewParams);

                        attachmentview.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(getActivity(), DocumentPreviewActivity.class);
                                intent.putExtra(Constants.FILE_EXTENSION, Utilities.getExtension(document));
                                intent.putExtra(Constants.DOCUMENT_URL, document);
                                startActivity(intent);
                            }
                        });
                        attachmentLayout.addView(attachmentview);
                    }
                }

            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            int layout;

            if (viewType == VIEW_TYPE_FROM_ME)
                layout = R.layout.list_item_ticket_comment_right;
            else
                layout = R.layout.list_item_ticket_comment_left;

            View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
            return new CommentViewHolder(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            CommentViewHolder vh = (CommentViewHolder) holder;
            vh.bindView(position);
        }

        @Override
        public int getItemViewType(int position) {
            if (mComments.get(position).getAuthorId().equals(requesterId))
                return VIEW_TYPE_FROM_ME;
            else
                return VIEW_TYPE_FROM_SUPPORT;
        }

        @Override
        public int getItemCount() {
            if (mComments == null)
                return 0;
            else
                return mComments.size();
        }
    }

    private void selectDocument(int id) {
        Intent imagePickerIntent = DocumentPicker.getPickImageOrPDFIntentByID(getActivity(), getString(R.string.select_a_document), id);
        startActivityForResult(imagePickerIntent, REQUEST_CODE_PICK_IMAGE_OR_DOCUMENT);
    }

    private void setImagePathsFromMultiplePicker(List<Image> images) {
        for (int i = 0; i < images.size(); i++)
            attachedFiles.add(images.get(i).getPath());
        setAttachmentVisibility();
    }

    private void setMultipleImagePicker() {
        images.removeAll(images);
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePicker.EXTRA_FOLDER_MODE, true);
        intent.putExtra(ImagePicker.EXTRA_MODE, ImagePicker.MODE_MULTIPLE);
        intent.putExtra(ImagePicker.EXTRA_LIMIT, Constants.MAX_FILE_ATTACHMENT_LIMIT - attachedFiles.size());
        intent.putExtra(ImagePicker.EXTRA_SHOW_CAMERA, false);
        intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGES, images);
        intent.putExtra(ImagePicker.EXTRA_FOLDER_TITLE, getString(R.string.album));
        intent.putExtra(ImagePicker.EXTRA_IMAGE_TITLE, getString(R.string.tap_to_select_images));
        intent.putExtra(ImagePicker.EXTRA_IMAGE_DIRECTORY, getString(R.string.camera));

        /* Will force ImagePicker to single pick */
        intent.putExtra(ImagePicker.EXTRA_RETURN_AFTER_FIRST, true);

        startActivityForResult(intent, REQUEST_CODE_PICK_MULTIPLE_IMAGE);
    }
}
