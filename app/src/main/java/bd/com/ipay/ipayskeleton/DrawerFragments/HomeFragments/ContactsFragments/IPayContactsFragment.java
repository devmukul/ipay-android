package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Pass (Constants.VERIFIED_USERS_ONLY, true) in the argument bundle to show only the
 * verified iPay users.
 */
public class IPayContactsFragment extends BaseContactsFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private boolean mShowVerifiedUsersOnly;
    private String mQuery = "";
    private Cursor mCursor;

    private int nameIndex;
    private int phoneNumberIndex;
    private int profilePictureUrlIndex;
    private int verificationStatusIndex;
    private int accountTypeIndex;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView(inflater, container, savedInstanceState);

        if (getArguments() != null)
            mShowVerifiedUsersOnly = getArguments().getBoolean(Constants.VERIFIED_USERS_ONLY, false);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mQuery = "";
        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new SQLiteCursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                mCursor = DataHelper.getInstance(getActivity()).searchSubscribers(mQuery, mShowVerifiedUsersOnly);

                if (mCursor != null) {
                    nameIndex = mCursor.getColumnIndex(DBConstants.KEY_NAME);
                    phoneNumberIndex = mCursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                    profilePictureUrlIndex = mCursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE);
                    verificationStatusIndex = mCursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
                    accountTypeIndex = mCursor.getColumnIndex(DBConstants.KEY_ACCOUNT_TYPE);
                }

                return mCursor;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        setContentShown(true);
        mCursor = data;

        populateList();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    protected FriendNode getFriendAtPosition(int position) {
        mCursor.moveToPosition(position);

        String name = mCursor.getString(nameIndex);
        String phoneNumber = mCursor.getString(phoneNumberIndex);
        String profilePictureUrl = mCursor.getString(profilePictureUrlIndex);
        int verificationStatus = mCursor.getInt(verificationStatusIndex);
        int accountType = mCursor.getInt(accountTypeIndex);

        FriendInfo friendInfo = new FriendInfo(accountType, true, verificationStatus, name, profilePictureUrl);
        FriendNode friend = new FriendNode(phoneNumber, friendInfo);

        return friend;
    }

    @Override
    protected int getFriendCount() {
        if (mCursor == null || mCursor.isClosed())
            return 0;
        else
            return mCursor.getCount();
    }

    @Override
    protected boolean isDialogFragment() {
        return false;
    }

    @Override
    protected boolean shouldShowIPayUserIcon() {
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

    @Override
    public void onDestroyView() {
        mCursor = null;
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        super.onDestroy();
    }
}