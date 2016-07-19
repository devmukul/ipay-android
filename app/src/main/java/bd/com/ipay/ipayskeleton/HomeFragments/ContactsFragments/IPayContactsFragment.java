package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Pass (Constants.VERIFIED_USERS_ONLY, true) in the argument bundle to show only the
 * verified iPay users.
 */
public class IPayContactsFragment extends BaseContactsFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private boolean mShowVerifiedUsersOnly;

    private int nameIndex;
    private int phoneNumberIndex;
    private int profilePictureUrlIndex;
    private int verificationStatusIndex;
    private int accountTypeIndex;
    private int isMemberIndex;
    private int updateTimeIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null)
            mShowVerifiedUsersOnly = getArguments().getBoolean(Constants.VERIFIED_USERS_ONLY, false);

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this).forceLoad();

        return v;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("Loader", "Started");

        return new SQLiteCursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                DataHelper dataHelper = DataHelper.getInstance(getActivity());

                Cursor cursor = dataHelper.searchFriends(getQuery(), mShowVerifiedUsersOnly);

                if (cursor != null) {
                    nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
                    phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                    profilePictureUrlIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE);
                    verificationStatusIndex = cursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
                    accountTypeIndex = cursor.getColumnIndex(DBConstants.KEY_ACCOUNT_TYPE);
                    updateTimeIndex = cursor.getColumnIndex(DBConstants.KEY_UPDATE_TIME);
                    isMemberIndex = cursor.getColumnIndex(DBConstants.KEY_IS_MEMBER);

                    this.registerContentObserver(cursor, DBConstants.DB_TABLE_FRIENDS_URI);
                }

                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("Loader", "Finished");
        populateList(data, mShowVerifiedUsersOnly ?
                getString(R.string.no_verified_contacts) : getString(R.string.no_contacts));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected FriendNode getFriendAtPosition(Cursor mCursor, int position) {
        mCursor.moveToPosition(position);

        String name = mCursor.getString(nameIndex);
        String phoneNumber = mCursor.getString(phoneNumberIndex);
        String profilePictureUrl = mCursor.getString(profilePictureUrlIndex);
        int verificationStatus = mCursor.getInt(verificationStatusIndex);
        int accountType = mCursor.getInt(accountTypeIndex);
        int isMember = mCursor.getInt(isMemberIndex);
        long updateTime = mCursor.getLong(updateTimeIndex);

        FriendInfo friendInfo = new FriendInfo(accountType, isMember, verificationStatus, name, updateTime, profilePictureUrl);
        FriendNode friend = new FriendNode(phoneNumber, friendInfo);

        return friend;
    }

    @Override
    protected boolean isDialogFragment() {
        return false;
    }

    @Override
    protected boolean shouldShowIPayUserIcon() {
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        setQuery(newText);
        getLoaderManager().restartLoader(CONTACTS_QUERY_LOADER, null, this);

        return true;
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        super.onDestroyView();
    }
}