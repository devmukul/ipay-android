package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flipboard.bottomsheet.BottomSheetLayout;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.ContactApi.DeleteContactAsyncTask;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite.AskForIntroductionResponse;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.Model.Contact.DeleteContactRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.Utilities.TokenManager;

/**
 * CAUTION: This fragment is used in contacts tab, invite page, and in contact picker.
 * Make sure to test it thoroughly after making any changes.
 * <p/>
 * <p/>
 * Pass (Constants.VERIFIED_USERS_ONLY, true) in the argument bundle to show only the
 * verified iPay users and (Constants.IPAY_MEMBERS_ONLY, true) to show member users only.
 */
public class SendMoneyContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final String TAG = SendMoneyContactFragment.class.getSimpleName();

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
        View v = inflater.inflate(R.layout.fragment_contact_send_money, container, false);
        mProgressDialog = new ProgressDialog(getActivity());
        ((SendMoneyActivity) getActivity()).mToolbarHelpText.setVisibility(View.VISIBLE);

        // If the fragment is a dialog fragment, we are using the searchview at the bottom.
        // Otherwise, we are using the searchview from the action bar.
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
                Cursor cursor;
                if (ProfileInfoCacheManager.isAccountSwitched()) {
                    cursor = dataHelper.searchBusinessContacts(mQuery, miPayMembersOnly, mBusinessMemberOnly, mShowNonInvitedNonMembersOnly,
                            mShowVerifiedUsersOnly, mShowInvitedOnly, mShowNonInvitedNonMembersOnly, invitees, Long.parseLong(TokenManager.getOnAccountId()));
                } else {
                    cursor = dataHelper.searchContacts(mQuery, true, mBusinessMemberOnly, mShowNonInvitedNonMembersOnly,
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

            public void bindView(final int pos) {

                mCursor.moveToPosition(pos);

                final String name = mCursor.getString(nameIndex);
                final String originalName = mCursor.getString(originalNameIndex);
                final String mobileNumber = mCursor.getString(phoneNumberIndex);
                final String profilePictureUrlQualityMedium = Constants.BASE_URL_FTP_SERVER + mCursor.getString(profilePictureUrlQualityMediumIndex);
                final String profilePictureUrlQualityHigh = Constants.BASE_URL_FTP_SERVER + mCursor.getString(profilePictureUrlQualityHighIndex);
                final boolean isVerified = mCursor.getInt(verificationStatusIndex) == DBConstants.VERIFIED_USER;
                final int accountType = mCursor.getInt(accountTypeIndex);
                final boolean isMember = mCursor.getInt(isMemberIndex) == DBConstants.IPAY_MEMBER;

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
                            button_ask.setVisibility(View.GONE);
                            verificationStatus.setVisibility(View.GONE);
                            inviteButton.setVisibility(View.GONE);
                            invitedButton.setVisibility(View.GONE);
                            button_asked.setVisibility(View.GONE);
                        }
                    } else {

                        inviteButton.setVisibility(View.GONE);
                        invitedButton.setVisibility(View.GONE);

                        button_ask.setVisibility(View.GONE);
                        verificationStatus.setVisibility(View.GONE);
                    }
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
                        Bundle bundle = new Bundle();
                        bundle.putString("name", name);
                        bundle.putString("imageUrl", profilePictureUrlQualityMedium);
                        ((SendMoneyActivity) getActivity()).switchToSendMoneyRecheckFragment(profilePictureView, bundle);
                    }
                });
            }
        }
    }
}