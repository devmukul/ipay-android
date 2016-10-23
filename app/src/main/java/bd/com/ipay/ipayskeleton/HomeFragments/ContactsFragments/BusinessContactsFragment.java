package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class BusinessContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    private SearchView mSearchView;

    private TextView mEmptyContactsTextView;

    private String mQuery = "";

    private ContactListAdapter mAdapter;
    private Cursor mCursor;

    private int originalNameIndex;
    private int phoneNumberIndex;
    private int profilePictureUrlIndex;
    private int businessTypeIndex;


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

        // We are using the searchview at the bottom.
        mSearchView = (SearchView) v.findViewById(R.id.search_contacts);
        mSearchView.setIconified(false);
        mSearchView.setOnQueryTextListener(this);

        // prevent auto focus on Dialog launch
        mSearchView.clearFocus();

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

                Cursor cursor = dataHelper.searchBusinessContacts(mQuery);

                if (cursor != null) {
                    originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_NAME);
                    phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                    profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_PROFILE_PICTURE);
                    businessTypeIndex = cursor.getColumnIndex(DBConstants.KEY_BUSINESS_TYPE);

                    if (contactLoadFinishListener != null) {
                        contactLoadFinishListener.onContactLoadFinish(cursor.getCount());
                    }

                    this.registerContentObserver(cursor, DBConstants.DB_TABLE_BUSINESS_URI);
                }

                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        populateList(data, getString(R.string.no_contacts));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

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

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EMPTY_VIEW = 10;
        private static final int CONTACT_VIEW = 100;

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
                final String originalName = mCursor.getString(originalNameIndex);
                final String mobileNumber = mCursor.getString(phoneNumberIndex);
                final String profilePictureUrl = Constants.BASE_URL_FTP_SERVER + mCursor.getString(profilePictureUrlIndex);

                /**
                 * We need to show original name on the top if exists
                 */
                if (originalName != null && !originalName.isEmpty()) {
                    name1View.setText(originalName);
                }

                mobileNumberView.setText(mobileNumber);
                profilePictureView.setProfilePicture(profilePictureUrl, false);

                name2View.setVisibility(View.GONE);
                inviteStatusTextView.setVisibility(View.GONE);
                verificationStatus.setVisibility(View.GONE);
                isSubscriber.setVisibility(View.GONE);
                inviteButton.setVisibility(View.GONE);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        if (originalName != null && !originalName.isEmpty())
                            intent.putExtra(Constants.NAME, originalName);
                        intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
                        intent.putExtra(Constants.PROFILE_PICTURE, profilePictureUrl);
                        getActivity().setResult(Activity.RESULT_OK, intent);
                        getActivity().finish();

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
                return CONTACT_VIEW;
        }
    }

    public interface ContactLoadFinishListener {
        void onContactLoadFinish(int contactCount);
    }
}
