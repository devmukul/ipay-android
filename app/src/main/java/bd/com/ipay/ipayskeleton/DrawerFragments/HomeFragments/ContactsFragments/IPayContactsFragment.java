package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Pass (Constants.VERIFIED_USERS_ONLY, true) in the argument bundle to show only the
 * verified iPay users.
 */
public class IPayContactsFragment extends BaseContactsFragment
        implements LoaderManager.LoaderCallbacks<List<FriendNode>> {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private boolean mShowVerifiedUsersOnly;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView(inflater, container, savedInstanceState);

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this).forceLoad();

        if (getArguments() != null)
            mShowVerifiedUsersOnly = getArguments().getBoolean(Constants.VERIFIED_USERS_ONLY, false);

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setContentShown(false);
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
    public void onDestroyView() {
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        super.onDestroy();
    }

    @Override
    public Loader<List<FriendNode>> onCreateLoader(int id, Bundle args) {
        Loader<List<FriendNode>> loader = new AsyncTaskLoader<List<FriendNode>>(getActivity()) {
            @Override
            public List<FriendNode> loadInBackground() {
                return DataHelper.getInstance(getActivity()).getSubscriberList("", mShowVerifiedUsersOnly);
            }
        };

        return loader;
    }

    @Override
    public void onLoadFinished(Loader<List<FriendNode>> loader, List<FriendNode> data) {
        populateList(data);
    }

    @Override
    public void onLoaderReset(Loader<List<FriendNode>> loader) {

    }
}