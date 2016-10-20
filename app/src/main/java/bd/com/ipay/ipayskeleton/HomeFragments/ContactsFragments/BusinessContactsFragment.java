package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.AskForIntroductionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

import static bd.com.ipay.ipayskeleton.Utilities.Common.CommonColorList.PROFILE_PICTURE_BACKGROUNDS;

public class BusinessContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private MenuItem mSearchMenuItem;
    private SearchView mSearchView;

    private TextView mEmptyContactsTextView;

    private String mQuery = "";

    // When a contact item is clicked, we need to access its name and number from the sheet view.
    // So saving these in these two variables.
    private String mSelectedName;
    private String mSelectedNumber;

    private View mSheetViewNonIpayMember;
    private View mSheetViewIpayMember;
    private View selectedBottomSheetView;

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
            if (Constants.DEBUG)
                Log.d("Loader", "Resetting.. Previous query: " + mQuery);

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

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != null && item != exception) item.setVisible(visible);
        }
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

                Cursor cursor = dataHelper.searchFriends(mQuery);

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

                    this.registerContentObserver(cursor, DBConstants.DB_TABLE_FRIENDS_URI);
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


    private void setPlaceHolderImage(ImageView contactImage, int backgroundColor) {
        contactImage.setBackgroundResource(backgroundColor);
        Glide.with(getActivity())
                .load(R.drawable.place_holder)
                .fitCenter()
                .into(contactImage);
    }

    private void setSelectedName(String name) {
        this.mSelectedName = name;
    }

    private void setSelectedNumber(String contactNumber) {
        this.mSelectedNumber = contactNumber;
    }

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EMPTY_VIEW = 10;
        private static final int FRIEND_VIEW = 100;

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public final TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = (TextView) itemView.findViewById(R.id.empty_description);
            }
        }

        public boolean isInvited(String phoneNumber) {
            if (ContactsHolderFragment.mGetInviteInfoResponse == null ||
                    ContactsHolderFragment.mGetInviteInfoResponse.getInvitees() == null)
                return false;
            else if (ContactsHolderFragment.mGetInviteInfoResponse.getInvitees().contains(phoneNumber))
                return true;
            return false;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;

            private final TextView name1View;
            private final TextView name2View;
            private final ProfileImageView profilePictureView;
            private final TextView mobileNumberView;
            private final ImageView isSubscriber;
            private final ImageView verificationStatus;
            private final TextView inviteStatusTextView;
            private final Button inviteButton;

            public ViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                name1View = (TextView) itemView.findViewById(R.id.name1);
                name2View = (TextView) itemView.findViewById(R.id.name2);
                mobileNumberView = (TextView) itemView.findViewById(R.id.mobile_number);
                profilePictureView = (ProfileImageView) itemView.findViewById(R.id.profile_picture);
                isSubscriber = (ImageView) itemView.findViewById(R.id.is_member);
                verificationStatus = (ImageView) itemView.findViewById(R.id.verification_status);
                inviteStatusTextView = (TextView) itemView.findViewById(R.id.invite_status);
                inviteButton = (Button) itemView.findViewById(R.id.button_invite);
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

                boolean isInvited = isInvited(mobileNumber);

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

                if (!isDialogFragment() && !isMember && !mShowInvitedOnly && isInvited)
                    inviteStatusTextView.setVisibility(View.VISIBLE);
                else
                    inviteStatusTextView.setVisibility(View.GONE);


                if (isMember) {
                    isSubscriber.setVisibility(View.VISIBLE);
                } else {
                    isSubscriber.setVisibility(View.GONE);
                }

                if (isVerified) {
                    verificationStatus.setVisibility(View.VISIBLE);
                } else {
                    verificationStatus.setVisibility(View.GONE);
                }

                if (mShowNonInvitedNonMembersOnly) {
                    inviteButton.setVisibility(View.VISIBLE);
                    inviteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (originalName != null && !originalName.isEmpty())
                                setSelectedName(originalName);
                            else setSelectedName(name);
                            setSelectedNumber(mobileNumber);

                            new android.app.AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.are_you_sure_to_invite)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // Do nothing
                                        }
                                    })
                                    .show();
                        }
                    });
                } else {
                    inviteButton.setVisibility(View.GONE);
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
                                    } else {
                                    }

                                    if (originalName != null && !originalName.isEmpty()) {

                                    } else {
                                    }
                                }
                            }, 100);
                        }
                    }
                });
            }

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
                return FRIEND_VIEW;
        }
    }

    public interface ContactLoadFinishListener {
        void onContactLoadFinish(int contactCount);
    }
}
