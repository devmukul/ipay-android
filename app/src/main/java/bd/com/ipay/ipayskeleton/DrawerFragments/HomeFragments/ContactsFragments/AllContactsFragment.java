package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Model.Friend.FriendNode;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AllContactsFragment extends BaseContactsFragment {

    private HttpRequestGetAsyncTask mGetAllContactsTask;
    private List<FriendNode> mGetAllContactsResponse;

    private RecyclerView mRecyclerView;
    private ContactListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Contacts will be filtered base on this field.
    // It will be populated when the user types in the search bar.
    protected String mQuery = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contact_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        loadContacts();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mGetAllContactsResponse == null)
            setContentShown(false);
        else
            setContentShown(true);
    }

    @Override
    protected boolean isDialogFragment() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mQuery = "";
    }

    private void loadContacts() {
        if (mGetAllContactsTask != null) {
            return;
        }

        mGetAllContactsTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_CONTACTS,
                Constants.BASE_URL_FRIEND + Constants.URL_GET_CONTACTS, getActivity(), this);
        mGetAllContactsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void populateList(List<FriendNode> friends) {
        mAdapter = new ContactListAdapter(friends);
        mRecyclerView.setAdapter(mAdapter);
        setContentShown(true);
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;

        return true;
    }

    @Override
    public void httpResponseReceiver(String result) {
        super.httpResponseReceiver(result);

        if (result == null) {
            mGetAllContactsTask = null;
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_CONTACTS)) {
            try {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    FriendNode[] friendNodeArray = gson.fromJson(resultList.get(2), FriendNode[].class);
                    mGetAllContactsResponse = Arrays.asList(friendNodeArray);
                    populateList(mGetAllContactsResponse);
                } else {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.failed_loading_friends, Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_loading_friends, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}