package bd.com.ipay.ipayskeleton.DrawerFragments.HelpAndSupportFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.devspark.progressfragment.ProgressFragment;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HelpAndSupportActivity;
import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Api.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.EditTextWithProgressBar;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.Email.GetEmailResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Resource.TicketCategory;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.CreateTicketRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.CreateTicketResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.GetTicketCategoriesRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Ticket.GetTicketCategoriesResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CreateTicketFragment extends ProgressFragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mCreateTicketTask = null;
    private CreateTicketResponse mCreateTicketResponse;

    private HttpRequestGetAsyncTask mGetTicketCategoriesTask = null;
    private GetTicketCategoriesResponse mGetTicketCategoriesResponse;

    private HttpRequestGetAsyncTask mGetEmailsTask = null;
    private GetEmailResponse mGetEmailResponse;

    private EditText mMessageEditText;
    private EditText mSubjectEditText;
    private EditTextWithProgressBar mCategoryEditText;
    private Button mCreateTicketButton;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mFileAttachmentRecyclerView;

    private ProgressDialog mProgressDialog;

    private ArrayList<String> alName;
    private ArrayList<Integer> alImage;

    private String mSubject;
    private String mMessage;

    private ResourceSelectorDialog<TicketCategory> ticketCategorySelectorDialog;
    private List<TicketCategory> mTicketCategoryList;
    private String mSelectedCategoryCode;
    private int mSelectedCategoryId;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_create_ticket, container, false);

        mSubjectEditText = (EditText) view.findViewById(R.id.subject_edit_text);
        mCategoryEditText = (EditTextWithProgressBar) view.findViewById(R.id.category_edit_text);
        mMessageEditText = (EditText) view.findViewById(R.id.message_edit_text);
        mCreateTicketButton = (Button) view.findViewById(R.id.button_create_ticket);

        mProgressDialog = new ProgressDialog(getActivity());

        mCreateTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (verifyUserInputs()) {
                    Utilities.hideKeyboard(getActivity());
                    createTicket();
                }
            }
        });

        getTicketCategories();

        setFileAttachmentAdapter();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (PushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE))
            getEmails();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE);

            if (json == null)
                getEmails();
            else {
                processGetEmailListResponse(json);
            }
        }
    }

    private void setFileAttachmentAdapter() {
        alName = new ArrayList<>(Arrays.asList("Cheesy...", "Crispy... ", "Fizzy...", "Cool...", "Softy...", "Fruity...", "Fresh...", "Sticky..."));
        // Calling the RecyclerView
        mFileAttachmentRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mFileAttachmentRecyclerView.setHasFixedSize(true);

        // The number of Columns
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mFileAttachmentRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new AttachFileAdapter();
        mFileAttachmentRecyclerView.setAdapter(mAdapter);
    }

    private void setTicketCategoryAdapter(List<TicketCategory> mTicketCategoryList) {
        ticketCategorySelectorDialog = new ResourceSelectorDialog<>(getContext(), getString(R.string.select_category), mTicketCategoryList, mSelectedCategoryId, true);
        ticketCategorySelectorDialog.setOnResourceSelectedListenerWithStringID(new ResourceSelectorDialog.OnResourceSelectedListenerWithStringID() {
            @Override
            public void onResourceSelectedWithStringID(String code, String name, int selectedIndex) {
                mCategoryEditText.setText(name);
                mSelectedCategoryCode = code;
                mSelectedCategoryId = selectedIndex;
            }
        });

        mCategoryEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ticketCategorySelectorDialog.show();
            }
        });
    }

    private void processGetEmailListResponse(String json) {
        try {
            Gson gson = new Gson();
            mGetEmailResponse = gson.fromJson(json, GetEmailResponse.class);

            if (mGetEmailResponse.getEmailAdressList().isEmpty()) {
                MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
                dialog
                        .title(R.string.no_email_added)
                        .content(R.string.dialog_add_new_email)
                        .cancelable(false)
                        .positiveText(R.string.add_email)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                launchEmailPage();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                ((HelpAndSupportActivity) getActivity()).switchToTicketListFragment();
                            }
                        })
                        .show();
            } else {
                String primaryEmail = mGetEmailResponse.getVerifiedEmail();
                if (primaryEmail == null) {
                    MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
                    dialog
                            .title(R.string.no_primary_email)
                            .content(R.string.dialog_verify_email)
                            .positiveText(R.string.verify_email)
                            .negativeText(R.string.cancel)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    launchEmailPage();
                                }
                            })
                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                    ((HelpAndSupportActivity) getActivity()).switchToTicketListFragment();
                                }
                            })
                            .show();
                }
            }

            setContentShown(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launchEmailPage() {
        getActivity().onBackPressed();
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        intent.putExtra(Constants.TARGET_FRAGMENT, ProfileCompletionPropertyConstants.VERIFIED_EMAIL);
        startActivity(intent);
    }

    private void showCreateTicketSuccessDialog() {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(getActivity());
        dialog
                .title(R.string.ticket_created)
                .content(R.string.ticket_created_dialog_text)
                .cancelable(false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        getActivity().onBackPressed();
                    }
                })
                .show();
    }

    private boolean verifyUserInputs() {
        boolean cancel = false;
        View focusView = null;

        mSubject = mSubjectEditText.getText().toString();
        mMessage = mMessageEditText.getText().toString();

        if (mSubject.isEmpty()) {
            cancel = true;
            focusView = mSubjectEditText;
            mSubjectEditText.setError(getString(R.string.failed_empty_subject));
        }
        if (mMessage.isEmpty()) {
            cancel = true;
            focusView = mMessageEditText;
            mMessageEditText.setError(getString(R.string.failed_empty_message));
        }

        if (cancel) {
            focusView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void getTicketCategories() {
        if (mGetTicketCategoriesTask != null)
            return;

        mCategoryEditText.showProgressBar();
        GetTicketCategoriesRequestBuilder mGetTicketCategoriesRequestBuilder = new GetTicketCategoriesRequestBuilder();

        String mUri = mGetTicketCategoriesRequestBuilder.generateUri().toString();
        mGetTicketCategoriesTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TICKET_CATEGORIES,
                mUri, getActivity());
        mGetTicketCategoriesTask.mHttpResponseListener = this;

        mGetTicketCategoriesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void createTicket() {
        if (mCreateTicketTask != null)
            return;

        mProgressDialog.setMessage(getString(R.string.creating_ticket));
        mProgressDialog.show();

        CreateTicketRequest createTicketRequest = new CreateTicketRequest(mSubject, mSelectedCategoryCode, mMessage);

        Gson gson = new Gson();
        String json = gson.toJson(createTicketRequest);

        mCreateTicketTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CREATE_TICKET,
                Constants.BASE_URL_ADMIN + Constants.URL_CREATE_TICKET, json, getActivity(), this);
        mCreateTicketTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getEmails() {
        if (mGetEmailsTask != null) {
            return;
        }

        setContentShown(false);

        mGetEmailsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_EMAILS,
                Constants.BASE_URL_MM + Constants.URL_GET_EMAIL, getActivity(), this);
        mGetEmailsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (getActivity() != null)
            mProgressDialog.dismiss();

        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mCreateTicketTask = null;
            mGetEmailsTask = null;
            mGetTicketCategoriesTask = null;

            if (getActivity() != null) {
                Toast.makeText(getActivity(), R.string.failed_request, Toast.LENGTH_SHORT).show();
                mProgressDialog.dismiss();
                getActivity().onBackPressed();
            }

            return;
        }

        Gson gson = new Gson();

        switch (result.getApiCommand()) {
            case Constants.COMMAND_CREATE_TICKET:
                try {
                    mCreateTicketResponse = gson.fromJson(result.getJsonString(), CreateTicketResponse.class);

                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.ticket_created, Toast.LENGTH_LONG).show();
                            showCreateTicketSuccessDialog();
                        }
                    } else if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_PAYMENT_REQUIRED) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.no_email_added, Toast.LENGTH_LONG).show();
                            launchEmailPage();
                        }
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), mCreateTicketResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mCreateTicketResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                mCreateTicketTask = null;
                break;
            case Constants.COMMAND_GET_EMAILS:
                try {
                    mGetEmailResponse = gson.fromJson(result.getJsonString(), GetEmailResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        processGetEmailListResponse(result.getJsonString());

                        DataHelper dataHelper = DataHelper.getInstance(getActivity());
                        dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE, result.getJsonString());

                        PushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_EMAIL_UPDATE, false);
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                        getActivity().onBackPressed();
                    }
                }
                mGetEmailsTask = null;
                break;
            case Constants.COMMAND_GET_TICKET_CATEGORIES:
                try {
                    mGetTicketCategoriesResponse = gson.fromJson(result.getJsonString(), GetTicketCategoriesResponse.class);
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        mTicketCategoryList = mGetTicketCategoriesResponse.getTicketCategories();

                        setTicketCategoryAdapter(mTicketCategoryList);
                        mCategoryEditText.hideProgressBar();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.service_not_available, Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
                mGetTicketCategoriesTask = null;
                break;

            default:
                break;
        }
    }

    public class AttachFileAdapter extends RecyclerView.Adapter<AttachFileAdapter.ViewHolder> {
        ArrayList<String> alName;
        ArrayList<Integer> alImage;

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_attach_file_ticket, viewGroup, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int i) {
            viewHolder.imgThumbnail.setImageResource(alImage.get(i));

            viewHolder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {
                        Toast.makeText(getActivity(), "#" + position + " - " + alName.get(position) + " (Long click)", Toast.LENGTH_SHORT).show();
                        //context.startActivity(new Intent(context, MainActivity.class));
                    } else {
                        Toast.makeText(getActivity(), "#" + position + " - " + alName.get(position), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return alName.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            public ImageView imgThumbnail;
            private ItemClickListener clickListener;

            public ViewHolder(View itemView) {
                super(itemView);
                imgThumbnail = (ImageView) itemView.findViewById(R.id.imageView_file);
                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            public void setClickListener(ItemClickListener itemClickListener) {
                this.clickListener = itemClickListener;
            }

            @Override
            public void onClick(View view) {
                clickListener.onClick(view, getPosition(), false);
            }

            @Override
            public boolean onLongClick(View view) {
                clickListener.onClick(view, getPosition(), true);
                return true;
            }
        }

    }

    public interface ItemClickListener {

        void onClick(View view, int position, boolean isLongClick);
    }
}
