package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.provider.ContactsContract.CommonDataKinds.Phone;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendInfo;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class AllContactsFragment extends BaseContactsFragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private int nameIndex;
    private int phoneNumberIndex;
    private int contactIdIndex;
    private int photoUrlIndex;

    private Map<String, FriendInfo> friendInfoMap;

    @Override
    public void onResume() {
        super.onResume();

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this).forceLoad();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        DataHelper dataHelper = DataHelper.getInstance(getActivity());
        List<FriendNode> friends = dataHelper.getFriendList();
        dataHelper.closeDbOpenHelper();

        friendInfoMap = new HashMap<>();

        for (FriendNode friend : friends) {
            friendInfoMap.put(friend.getPhoneNumber(), friend.getInfo());
        }

        final String[] projection = new String[] {
                Phone._ID,
                Phone.NUMBER,
                Phone.HAS_PHONE_NUMBER,
                Phone.PHOTO_URI,
                Phone.DISPLAY_NAME,
        };

        final String selection = Phone.HAS_PHONE_NUMBER + "=1"
                + " AND " + Phone.DISPLAY_NAME
                + " LIKE '%" + getQuery() + "%'";
        final String order = Phone.DISPLAY_NAME + " COLLATE NOCASE ASC";

        Uri queryUri = Phone.CONTENT_URI;

        return new CursorLoader(
                getActivity(),
                queryUri,
                projection,
                selection,
                null,
                order
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            nameIndex = data.getColumnIndex(Phone.DISPLAY_NAME);
            contactIdIndex = data.getColumnIndex(Phone._ID);
            photoUrlIndex = data.getColumnIndex(Phone.PHOTO_URI);
            phoneNumberIndex = data.getColumnIndex(Phone.NUMBER);
        }

        populateList(data, getString(R.string.no_contacts));
    }

    @Override
    protected FriendNode getFriendAtPosition(Cursor cursor, int position) {
        cursor.moveToPosition(position);

        long contactId = cursor.getLong(contactIdIndex);
//        String phoneNumber = ContactEngine.getContactNumberFromId(getActivity(), contactId);
        String phoneNumber = cursor.getString(phoneNumberIndex);
        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
        phoneNumber = ContactEngine.formatMobileNumberBD(phoneNumber);

        String name = cursor.getString(nameIndex);
        String photoUrl = cursor.getString(photoUrlIndex);

        FriendInfo friendInfo;
        if (!friendInfoMap.containsKey(phoneNumber))
            friendInfo = new FriendInfo(name, photoUrl);
        else {
            friendInfo = friendInfoMap.get(phoneNumber);
            friendInfo.setName(name);
            friendInfo.setProfilePictureUrl(photoUrl);
        }

        return new FriendNode(phoneNumber, friendInfo);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
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