package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.DeleteContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite.AskForIntroductionResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.DeleteContactRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.Contact.InviteContactNode;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static bd.com.ipay.ipayskeleton.Utilities.Common.CommonColorList.PROFILE_PICTURE_BACKGROUNDS;

/**
 * CAUTION: This fragment is used in contacts tab, invite page, and in contact picker.
 * Make sure to test it thoroughly after making any changes.
 * <p/>
 * <p/>
 * Pass (Constants.VERIFIED_USERS_ONLY, true) in the argument bundle to show only the
 * verified iPay users and (Constants.IPAY_MEMBERS_ONLY, true) to show member users only.
 */
public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener,
        HttpResponseListener {

    private static final String TAG = ContactsFragment.class.getSimpleName();

    private static final int CONTACTS_QUERY_LOADER = 0;

    private BottomSheetLayout mBottomSheetLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private SearchView mSearchView;
    private TextView mEmptyContactsTextView;
    private View mSheetViewNonIpayMember;
    private View mSheetViewIpayMember;
    private View selectedBottomSheetView;

    private String mQuery = "";
    // When a contact item is clicked, we need to access its name and number from the sheet view.
    // So saving these in these two variables.
    private String mSelectedName;
    private String mSelectedNumber;
    private String mInviteMessage;

    private HttpRequestPostAsyncTask mSendInviteTask = null;
    private SendInviteResponse mSendInviteResponse;
    private HttpRequestPostAsyncTask mAskForRecommendationTask = null;
    private AskForIntroductionResponse mAskForIntroductionResponse;
    private ProgressDialog mProgressDialog;

    private ContactListAdapter mAdapter;
    private Cursor mCursor;

    private boolean mShowVerifiedUsersOnly;
    private boolean miPayMembersOnly;
    private boolean mBusinessMemberOnly;
    private boolean mShowInvitedOnly;
    private boolean mShowNonInvitedNonMembersOnly;
    private boolean mShowAllMembersToInvite;

    private int nameIndex;
    private int originalNameIndex;
    private int phoneNumberIndex;
    private int profilePictureUrlIndex;
    private int profilePictureUrlQualityMediumIndex;
    private int profilePictureUrlQualityHighIndex;
    private int relationshipIndex;
    private int verificationStatusIndex;
    private int accountTypeIndex;
    private int isMemberIndex;

    private ContactLoadFinishListener contactLoadFinishListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isDialogFragment())
            setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resetSearchKeyword();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        mProgressDialog = new ProgressDialog(getActivity());

        // If the fragment is a dialog fragment, we are using the searchview at the bottom.
        // Otherwise, we are using the searchview from the action bar.
        if (!isDialogFragment()) {
            if (mBottomSheetLayout != null)
                setUpBottomSheet();
        }
        mSearchView = (SearchView) v.findViewById(R.id.search_contacts);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);

        // prevent auto focus on Dialog launch
        mSearchView.clearFocus();

        if (getArguments() != null) {
            mShowVerifiedUsersOnly = getArguments().getBoolean(Constants.VERIFIED_USERS_ONLY, false);
            miPayMembersOnly = getArguments().getBoolean(Constants.IPAY_MEMBERS_ONLY, false);
            mBusinessMemberOnly = getArguments().getBoolean(Constants.BUSINESS_ACCOUNTS_ONLY, false);
            mShowInvitedOnly = getArguments().getBoolean(Constants.SHOW_INVITED_ONLY, false);
            mShowNonInvitedNonMembersOnly = getArguments().getBoolean(Constants.SHOW_NON_INVITED_NON_MEMBERS_ONLY, false);
            mShowAllMembersToInvite = getArguments().getBoolean(Constants.SHOW_ALL_MEMBERS, false);
        }

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this).forceLoad();

        mEmptyContactsTextView = (TextView) v.findViewById(R.id.contact_list_empty);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView = (RecyclerView) v.findViewById(R.id.contact_list);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ContactListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    public void setContactLoadFinishListener(ContactLoadFinishListener contactLoadFinishListener) {
        this.contactLoadFinishListener = contactLoadFinishListener;
    }

    private void resetSearchKeyword() {
        if (mSearchView != null && !mQuery.isEmpty()) {
            Logger.logD("Loader", "Resetting.. Previous query: " + mQuery);

            mQuery = "";
            mSearchView.setQuery("", false);
            getLoaderManager().restartLoader(CONTACTS_QUERY_LOADER, null, this);
        }
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        mCursor = null;
        super.onDestroyView();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new SQLiteCursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                DataHelper dataHelper = DataHelper.getInstance(getActivity());

                // TODO hack
                /**
                 * Caution: It takes some time to load invite response from the server. So if you are
                 * loading this Fragment from Contacts page, it is very much possible that invitee list
                 * will be null. This is generally not a problem because invitee list is not used
                 * in Contacts fragment when doing database query. It is used in the database query
                 * from invite fragment, but by that time the invitee list should already have loaded.
                 */
                List<String> invitees = null;
                if (ContactsHolderFragment.mGetInviteInfoResponse != null)
                    invitees = ContactsHolderFragment.mGetInviteInfoResponse.getInvitees();
                Cursor cursor;
                if (ProfileInfoCacheManager.isAccountSwitched()) {
                    cursor = dataHelper.searchBusinessContacts(mQuery, miPayMembersOnly, mBusinessMemberOnly, mShowNonInvitedNonMembersOnly,
                            mShowVerifiedUsersOnly, mShowInvitedOnly, mShowNonInvitedNonMembersOnly, invitees, Long.parseLong(TokenManager.getOnAccountId()));
                } else {
                    cursor = dataHelper.searchContacts(mQuery, miPayMembersOnly, mBusinessMemberOnly, mShowNonInvitedNonMembersOnly,
                            mShowVerifiedUsersOnly, mShowInvitedOnly, mShowNonInvitedNonMembersOnly, invitees);
                }

                if (cursor != null) {
                    nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
                    originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_ORIGINAL_NAME);
                    phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                    profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE);
                    profilePictureUrlQualityMediumIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM);
                    profilePictureUrlQualityHighIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_HIGH);
                    relationshipIndex = cursor.getColumnIndex(DBConstants.KEY_RELATIONSHIP);
                    verificationStatusIndex = cursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
                    accountTypeIndex = cursor.getColumnIndex(DBConstants.KEY_ACCOUNT_TYPE);
                    isMemberIndex = cursor.getColumnIndex(DBConstants.KEY_IS_MEMBER);

                    if (contactLoadFinishListener != null) {
                        contactLoadFinishListener.onContactLoadFinish(cursor.getCount());
                    }

                    this.registerContentObserver(cursor, DBConstants.DB_TABLE_CONTACTS_URI);
                }

                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        populateList(data, mShowVerifiedUsersOnly ?
                getString(R.string.no_verified_contacts) : getString(R.string.no_contacts));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    boolean isDialogFragment() {
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;
        getLoaderManager().restartLoader(CONTACTS_QUERY_LOADER, null, this);

        return true;
    }

    private void populateList(Cursor cursor, String emptyText) {
        this.mCursor = cursor;

        if (cursor != null && !cursor.isClosed() && cursor.getCount() > 0) {
            mAdapter = new ContactListAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mEmptyContactsTextView.setVisibility(View.GONE);
        } else {
            mEmptyContactsTextView.setText(emptyText);
            mEmptyContactsTextView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Must be called after show(Non)SubscriberSheet
     */
    private void setContactInformationInSheet(String contactName,
                                              String imageUrl, final int backgroundColor, boolean isMember, boolean isVerified, int accountType) {
        if (selectedBottomSheetView == null)
            return;

        final TextView contactNameView = (TextView) selectedBottomSheetView.findViewById(R.id.textview_contact_name);
        final ImageView contactImage = (ImageView) selectedBottomSheetView.findViewById(R.id.image_contact);
        TextView isVerifiedView = (TextView) selectedBottomSheetView.findViewById(R.id.textview_is_verified);
        TextView accountTypeView = (TextView) selectedBottomSheetView.findViewById(R.id.textview_account_type);

        contactImage.setBackgroundResource(backgroundColor);
        contactNameView.setText(contactName);

        if (isMember) {
            if (isVerified) {
                isVerifiedView.setText(getString(R.string.verified).toUpperCase());
                isVerifiedView.setBackgroundResource(R.drawable.brackgound_bottom_sheet_verified);
            } else {
                isVerifiedView.setText(getString(R.string.unverified).toUpperCase());
                isVerifiedView.setBackgroundResource(R.drawable.brackgound_bottom_sheet_unverified);
            }

            if (accountType == Constants.BUSINESS_ACCOUNT_TYPE) {
                accountTypeView.setText(R.string.business_account);
            } else {
                accountTypeView.setText(R.string.personal_account);
            }

        } else {
            isVerifiedView.setVisibility(View.GONE);
            accountTypeView.setVisibility(View.GONE);
        }

        if (imageUrl != null && !imageUrl.equals("")) {
            Glide.with(getActivity())
                    .load(imageUrl)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            setPlaceHolderImage(contactImage, backgroundColor);
                            return true;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model,
                                                       Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .centerCrop()
                    .into(contactImage);
        } else {
            contactImage.setBackgroundResource(backgroundColor);
            setPlaceHolderImage(contactImage, backgroundColor);
        }
    }

    private void setPlaceHolderImage(ImageView contactImage, int backgroundColor) {
        contactImage.setBackgroundResource(backgroundColor);
        Glide.with(getActivity())
                .load(R.drawable.place_holder)
                .fitCenter()
                .into(contactImage);
    }

    public void setBottomSheetLayout(BottomSheetLayout bottomSheetLayout) {
        this.mBottomSheetLayout = bottomSheetLayout;
    }

    private void setUpBottomSheet() {
        mSheetViewNonIpayMember = getActivity().getLayoutInflater()
                .inflate(R.layout.sheet_view_contact_non_member, null);
        final Button mInviteButton = (Button) mSheetViewNonIpayMember.findViewById(R.id.button_invite);

        if (ContactsHolderFragment.mGetInviteInfoResponse != null &&
                ContactsHolderFragment.mGetInviteInfoResponse.invitees.contains(mSelectedNumber)) {
            mInviteButton.setEnabled(false);
        } else {
            mInviteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                @ValidateAccess(ServiceIdConstants.MANAGE_INVITATIONS)
                public void onClick(View v) {
                    if (mBottomSheetLayout.isSheetShowing()) {
                        mBottomSheetLayout.dismissSheet();
                    }

                    showInviteDialog(mSelectedName, mSelectedNumber);
                }
            });
        }

        mSheetViewIpayMember = getActivity().getLayoutInflater()
                .inflate(R.layout.sheet_view_contact_member, null);

        Button mSendMoneyButton = (Button) mSheetViewIpayMember.findViewById(R.id.button_send_money);
        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.SEND_MONEY)
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                intent.putExtra(Constants.MOBILE_NUMBER, mSelectedNumber);
                startActivity(intent);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

        Button mRequestMoneyButton = (Button) mSheetViewIpayMember.findViewById(R.id.button_request_money);
        mRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.REQUEST_MONEY)
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RequestMoneyActivity.class);
                intent.putExtra(Constants.MOBILE_NUMBER, mSelectedNumber);
                intent.putExtra(RequestMoneyActivity.LAUNCH_NEW_REQUEST, true);
                startActivity(intent);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

        Button mAskForRecommendationButton = (Button) mSheetViewIpayMember.findViewById(R.id.button_ask_for_introduction);
        Button mMakePaymentButton = (Button) mSheetViewIpayMember.findViewById(R.id.button_make_payment);

        mAskForRecommendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecommendationRequest(mSelectedNumber);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

        mMakePaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.MAKE_PAYMENT)
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PaymentActivity.class);
                intent.putExtra(Constants.MOBILE_NUMBER, mSelectedNumber);
                intent.putExtra(PaymentActivity.LAUNCH_NEW_REQUEST, true);
                startActivity(intent);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

    }

    private void showDeleteContactConfirmationDialog(final String mobileNumber) {
        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.remove_contact_title)
                .cancelable(false)
                .content(R.string.remove_contact_message)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .show();

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                deleteContact(mobileNumber);
            }
        });

        dialog.show();
    }

    private void deleteContact(String phoneNumber) {
        DeleteContactRequestBuilder deleteContactRequestBuilder = new DeleteContactRequestBuilder(phoneNumber);

        new DeleteContactAsyncTask(Constants.COMMAND_DELETE_CONTACTS,
                deleteContactRequestBuilder.generateUri(), deleteContactRequestBuilder.getDeleteContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendRecommendationRequest(String mobileNumber) {
        if (mAskForRecommendationTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_send_for_introduction));
        mProgressDialog.show();
        mAskForRecommendationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ASK_FOR_RECOMMENDATION,
                Constants.BASE_URL_MM + Constants.URL_ASK_FOR_INTRODUCTION + mobileNumber, null, getActivity(), false);
        mAskForRecommendationTask.mHttpResponseListener = this;
        mAskForRecommendationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendInvite(String phoneNumber, boolean wantToIntroduce) {
        try {
            if (ContactsHolderFragment.mGetInviteInfoResponse != null && ContactsHolderFragment.mGetInviteInfoResponse.invitees != null) {
                int numberOfInvitees = ContactsHolderFragment.mGetInviteInfoResponse.invitees.size();
                if (numberOfInvitees >= ContactsHolderFragment.mGetInviteInfoResponse.totalLimit) {
                    Toast.makeText(getActivity(), R.string.invitation_limit_exceeded,
                            Toast.LENGTH_LONG).show();
                } else {
                    mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_sending_invite));
                    mProgressDialog.show();

                    InviteContactNode inviteContactNode = new InviteContactNode(phoneNumber, wantToIntroduce);
                    Gson gson = new Gson();
                    String json = gson.toJson(inviteContactNode, InviteContactNode.class);
                    mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                            Constants.BASE_URL_MM + Constants.URL_SEND_INVITE, json, getActivity(), this, false);
                    mSendInviteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            } else {
                if (Utilities.isConnectionAvailable(getContext())) {
                    Toast.makeText(getContext(), getString(R.string.no_internet_connection), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Sending invitation failed", Toast.LENGTH_LONG).show();
                }
            }
        } catch (Exception e) {
            Logger.logD(TAG, e.getMessage());
        }
    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mProgressDialog.dismiss();
            mSendInviteTask = null;
            mAskForRecommendationTask = null;
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SEND_INVITE)) {
            try {
                mSendInviteResponse = gson.fromJson(result.getJsonString(), SendInviteResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.invitation_sent, Toast.LENGTH_LONG).show();
                    }

                    if (ContactsHolderFragment.mGetInviteInfoResponse != null)
                        ContactsHolderFragment.mGetInviteInfoResponse.invitees.add(mSelectedNumber);

                    getLoaderManager().restartLoader(CONTACTS_QUERY_LOADER, null, this);

                } else if (getActivity() != null) {
                    Toaster.makeText(getActivity(), mSendInviteResponse.getMessage(), Toast.LENGTH_LONG);
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toaster.makeText(getActivity(), R.string.failed_sending_invitation, Toast.LENGTH_LONG);
                }
            }

            mProgressDialog.dismiss();
            mSendInviteTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_ASK_FOR_RECOMMENDATION)) {
            try {
                mAskForIntroductionResponse = gson.fromJson(result.getJsonString(), AskForIntroductionResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toaster.makeText(getActivity(), R.string.introduction_request_sent, Toast.LENGTH_LONG);
                    }
                } else if (getActivity() != null) {
                    Toaster.makeText(getActivity(), mAskForIntroductionResponse.getMessage(), Toast.LENGTH_LONG);
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (getActivity() != null) {
                    Toaster.makeText(getActivity(), R.string.failed_asking_introduction, Toast.LENGTH_LONG);
                }
            }

            mProgressDialog.dismiss();
            mAskForRecommendationTask = null;
        }
    }

    private void showMemberSheet(boolean isVerified, int accountType) {
        if (mBottomSheetLayout == null)
            return;

        selectedBottomSheetView = mSheetViewIpayMember;

        Button askForConfirmationButton = (Button) mSheetViewIpayMember.findViewById(R.id.button_ask_for_introduction);
        Button mMakePaymentButton = (Button) mSheetViewIpayMember.findViewById(R.id.button_make_payment);

        if (accountType == Constants.BUSINESS_ACCOUNT_TYPE) {
            askForConfirmationButton.setVisibility(View.GONE);
            mMakePaymentButton.setVisibility(View.VISIBLE);
        } else {
            mMakePaymentButton.setVisibility(View.GONE);
            if (!isVerified) {
                if (askForConfirmationButton != null)
                    askForConfirmationButton.setVisibility(View.GONE);

            } else {
                if (askForConfirmationButton != null)
                    askForConfirmationButton.setVisibility(View.VISIBLE);
            }
        }

        mBottomSheetLayout.showWithSheetView(mSheetViewIpayMember);
        mBottomSheetLayout.expandSheet();
    }

    private void showNonMemberSheet(String mobileNumber) {
        if (mBottomSheetLayout == null)
            return;

        selectedBottomSheetView = mSheetViewNonIpayMember;

        Button inviteButton = (Button) mSheetViewNonIpayMember.findViewById(R.id.button_invite);

        // You can send invite to the person who is invited by you again for now.
        // Following segment disables this feature
        /*
        // Can not send invite to person who is invited already
        if (ContactsHolderFragment.mGetInviteInfoResponse.getInvitees().contains(mobileNumber))
            inviteButton.setEnabled(false);
        else
            inviteButton.setEnabled(true);
        */

        mBottomSheetLayout.showWithSheetView(mSheetViewNonIpayMember);
        mBottomSheetLayout.expandSheet();
    }

    public void showInviteDialog(String name, final String mobileNumber) {
        mInviteMessage = getString(R.string.are_you_sure_to_invite);
        if (!name.isEmpty())
            mInviteMessage = mInviteMessage.replace(getString(R.string.this_person), name);

        MaterialDialog dialog = new MaterialDialog.Builder(getActivity())
                .title(R.string.invite_to_ipay)
                .customView(R.layout.dialog_invite_contact_with_introduction, true)
                .positiveText(R.string.yes)
                .negativeText(R.string.no)
                .show();

        View view = dialog.getCustomView();
        final TextView mInviteText = (TextView) view.findViewById(R.id.textviewInviteMessage);
        final CheckBox introduceCheckbox = (CheckBox) view.findViewById(R.id.introduceCheckbox);

        mInviteText.setText(mInviteMessage);

        dialog.getBuilder().onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            @ValidateAccess(ServiceIdConstants.MANAGE_INVITATIONS)
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {

                boolean wantToIntroduce = introduceCheckbox.isChecked();

                sendInvite(mobileNumber, wantToIntroduce);
                dialog.dismiss();
            }
        });

        dialog.getBuilder().onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
    }


    private void setSelectedName(String name) {
        this.mSelectedName = name;
    }

    private void setSelectedNumber(String contactNumber) {
        this.mSelectedNumber = contactNumber;
    }

    public interface ContactLoadFinishListener {
        void onContactLoadFinish(int contactCount);
    }

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EMPTY_VIEW = 10;
        private static final int CONTACT_VIEW = 100;

        public boolean isInvited(String phoneNumber) {
            if (ContactsHolderFragment.mGetInviteInfoResponse == null ||
                    ContactsHolderFragment.mGetInviteInfoResponse.getInvitees() == null)
                return false;
            else if (ContactsHolderFragment.mGetInviteInfoResponse.getInvitees().contains(phoneNumber))
                return true;
            return false;
        }

        @SuppressWarnings("UnnecessaryLocalVariable")
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_empty_description, parent, false);
                return new EmptyViewHolder(v);
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
                return new ViewHolder(v);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {

                if (holder instanceof ViewHolder) {
                    ViewHolder vh = (ViewHolder) holder;
                    vh.bindView(position);
                } else if (holder instanceof EmptyViewHolder) {
                    EmptyViewHolder vh = (EmptyViewHolder) holder;
                    vh.mEmptyDescription.setText(getString(R.string.no_contacts));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            if (mCursor == null || mCursor.isClosed())
                return 0;
            else
                return mCursor.getCount();
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == 0)
                return EMPTY_VIEW;
            else
                return CONTACT_VIEW;
        }

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public final TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = (TextView) itemView.findViewById(R.id.empty_description);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;

            private final TextView name1View;
            private final TextView name2View;
            private final ProfileImageView profilePictureView;
            private final TextView mobileNumberView;
            private final ImageView verificationStatus;
            private final Button invitedButton;
            private final Button inviteButton;
            private final Button button_asked;
            private final Button button_ask;

            public ViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                name1View = (TextView) itemView.findViewById(R.id.name1);
                name2View = (TextView) itemView.findViewById(R.id.name2);
                mobileNumberView = (TextView) itemView.findViewById(R.id.mobile_number);
                profilePictureView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                verificationStatus = (ImageView) itemView.findViewById(R.id.verification_status);
                invitedButton = (Button) itemView.findViewById(R.id.button_invited);
                inviteButton = (Button) itemView.findViewById(R.id.button_invite);
                button_asked = (Button) itemView.findViewById(R.id.button_asked);
                button_ask = (Button) itemView.findViewById(R.id.button_ask);
            }

            public void bindView(int pos) {

                mCursor.moveToPosition(pos);

                final String name = mCursor.getString(nameIndex);
                final String originalName = mCursor.getString(originalNameIndex);
                final String mobileNumber = mCursor.getString(phoneNumberIndex);
                final String profilePictureUrlQualityMedium = Constants.BASE_URL_FTP_SERVER + mCursor.getString(profilePictureUrlQualityMediumIndex);
                final String profilePictureUrlQualityHigh = Constants.BASE_URL_FTP_SERVER + mCursor.getString(profilePictureUrlQualityHighIndex);
                final boolean isVerified = mCursor.getInt(verificationStatusIndex) == DBConstants.VERIFIED_USER;
                final int accountType = mCursor.getInt(accountTypeIndex);
                final boolean isMember = mCursor.getInt(isMemberIndex) == DBConstants.IPAY_MEMBER;
                final boolean isInvited = isInvited(mobileNumber);

                /**
                 * We need to show original name on the top if exists
                 */
                if (originalName != null && !originalName.isEmpty()) {
                    name1View.setText(originalName);
                    name2View.setVisibility(View.VISIBLE);
                    name2View.setText(name);
                } else {
                    name1View.setText(name);
                    name2View.setVisibility(View.GONE);
                }

                mobileNumberView.setText(mobileNumber);

                if (!isDialogFragment()) {

                    if (isMember) {
                        if (!isVerified) {
                            button_ask.setVisibility(View.GONE);
                            button_asked.setVisibility(View.GONE);
                            verificationStatus.setVisibility(View.GONE);
                            inviteButton.setVisibility(View.GONE);
                            invitedButton.setVisibility(View.GONE);

                        } else {
                            button_ask.setVisibility(View.VISIBLE);
                            verificationStatus.setVisibility(View.VISIBLE);
                            inviteButton.setVisibility(View.GONE);
                            invitedButton.setVisibility(View.GONE);
                            button_asked.setVisibility(View.GONE);
                        }
                    } else {
                        if (!mShowInvitedOnly && isInvited) {
                            invitedButton.setVisibility(View.VISIBLE);
                            inviteButton.setVisibility(View.GONE);
                        } else {
                            inviteButton.setVisibility(View.VISIBLE);
                            invitedButton.setVisibility(View.GONE);
                        }
                        button_ask.setVisibility(View.GONE);
                        verificationStatus.setVisibility(View.GONE);
                    }

                    inviteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showInviteDialog(name, mobileNumber);
                        }
                    });

                    button_ask.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendRecommendationRequest(mobileNumber);
                        }
                    });
                } else {
                    if (isMember) {
                        if (!isVerified) {
                            verificationStatus.setVisibility(View.GONE);
                        } else {
                            verificationStatus.setVisibility(View.VISIBLE);
                        }
                    } else {
                        verificationStatus.setVisibility(View.GONE);
                    }

                }

                profilePictureView.setProfilePicture(profilePictureUrlQualityMedium, false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isDialogFragment()) {
                            Intent intent = new Intent();
                            if (originalName != null && !originalName.isEmpty())
                                intent.putExtra(Constants.NAME, originalName);
                            else intent.putExtra(Constants.NAME, name);
                            intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
                            intent.putExtra(Constants.PROFILE_PICTURE, profilePictureUrlQualityHigh);
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();

                        } else if (mShowAllMembersToInvite && !isMember) {
                            if (originalName != null && !originalName.isEmpty())
                                setSelectedName(originalName);
                            else setSelectedName(name);
                            setSelectedNumber(mobileNumber);

                            showInviteDialog(name, mobileNumber);
                        } else {
                            if (originalName != null && !originalName.isEmpty())
                                setSelectedName(originalName);
                            else setSelectedName(name);
                            setSelectedNumber(mobileNumber);

                            Utilities.hideKeyboard(getActivity());

                            // Add a delay to hide keyboard and then open up the bottom sheet
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int randomProfileBackgroundColor = PROFILE_PICTURE_BACKGROUNDS[getAdapterPosition() % PROFILE_PICTURE_BACKGROUNDS.length];
                                    if (isMember) {
                                        showMemberSheet(isVerified, accountType);
                                    } else {
                                        showNonMemberSheet(mobileNumber);
                                    }
                                    if (isAdded()) {
                                        if (originalName != null && !originalName.isEmpty()) {
                                            setContactInformationInSheet(originalName,
                                                    profilePictureUrlQualityHigh, randomProfileBackgroundColor,
                                                    isMember, isVerified, accountType);
                                        } else {
                                            setContactInformationInSheet(name,
                                                    profilePictureUrlQualityHigh, randomProfileBackgroundColor,
                                                    isMember, isVerified, accountType);
                                        }
                                    }
                                }
                            }, 100);
                        }
                    }
                });

                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    @ValidateAccess(ServiceIdConstants.DELETE_CONTACTS)
                    public boolean onLongClick(View view) {
                        showDeleteContactConfirmationDialog(mobileNumber);
                        return false;
                    }
                });
            }
        }
    }
}