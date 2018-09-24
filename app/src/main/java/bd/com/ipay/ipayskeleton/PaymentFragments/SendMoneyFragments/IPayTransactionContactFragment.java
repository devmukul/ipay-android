package bd.com.ipay.ipayskeleton.PaymentFragments.SendMoneyFragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.HttpErrorHandler;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Profile.BasicInfo.GetUserInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class IPayTransactionContactFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener, HttpResponseListener {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private int isFirstLoad = 0;
    private RecyclerView mContactListRecyclerView;
    private SearchView mContactSearchView;
    private TextView mContactListEmptyMessageTextView;
    private TextView mSearchedNumberTextView;
    private TextView mActionNameTextView;
    private BottomSheetBehavior<LinearLayout> bottomSheetBehavior;
    private String mQuery = "";
    private String mPhoneNumber;


    private HttpRequestGetAsyncTask mGetProfileInfoTask = null;
    private ProgressDialog mProgressDialog;

    private ContactListAdapter mAdapter;
    private Cursor mCursor;

    private int nameIndex;
    private int originalNameIndex;
    private int phoneNumberIndex;
    private int profilePictureUrlQualityMediumIndex;
    private LinearLayout mSearchedNumberLayout;

    private int transactionType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mProgressDialog = new ProgressDialog(getActivity());
        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        resetSearchKeyword();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transaction_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mActionNameTextView = view.findViewById(R.id.action_name_text_view);
        mSearchedNumberTextView = view.findViewById(R.id.searched_number_text_view);
        mContactSearchView = view.findViewById(R.id.contact_search_view);
        mSearchedNumberLayout = view.findViewById(R.id.searched_number_layout);
        mContactListEmptyMessageTextView = view.findViewById(R.id.contact_list_empty_message_text_view);
        mContactListRecyclerView = view.findViewById(R.id.contact_list_recycler_view);

        final Button helperBottomSheetDismissButton = view.findViewById(R.id.helper_bottom_sheet_dismiss_button);
        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        final Button continueButton = view.findViewById(R.id.continue_button);
        final LinearLayout helpBottomSheetLayout = view.findViewById(R.id.help_bottom_sheet_layout);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null)
                actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContactSearchView.setIconified(false);
        mContactSearchView.setOnQueryTextListener(this);
        mContactSearchView.clearFocus();

        mAdapter = new ContactListAdapter();
        mContactListRecyclerView.setAdapter(mAdapter);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isFirstLoad = 1;
                mAdapter.notifyDataSetChanged();
            }
        }, 1000);

        if (getFragmentManager() != null) {
            final TransactionHelperFragment transactionHelperFragment = new TransactionHelperFragment();
            transactionHelperFragment.setArguments(getArguments());
            getFragmentManager().beginTransaction().replace(R.id.help_fragment_container, transactionHelperFragment).commit();
        }

        bottomSheetBehavior = BottomSheetBehavior.from(helpBottomSheetLayout);
        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                getActivity().setTitle(R.string.send_money);
                if (SharedPrefManager.ifFirstSendMoney()) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                getActivity().setTitle(R.string.request_money);
                if (SharedPrefManager.ifFirstRequestMoney()) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
                break;
        }

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                    switch (transactionType) {
                        case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                            SharedPrefManager.setIfFirstSendMoney(false);
                            break;
                        case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                            SharedPrefManager.setIfFirstRequestMoney(false);
                            break;
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoneNumber = mQuery;
                getProfileInfo(mQuery);
            }
        });
        helperBottomSheetDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, IPayTransactionContactFragment.this).forceLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.transaction_contact_option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_help:
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void getProfileInfo(String mobileNumber) {
        if (mGetProfileInfoTask != null) {
            return;
        }
        GetUserInfoRequestBuilder mGetUserInfoRequestBuilder = new GetUserInfoRequestBuilder(ContactEngine.formatMobileNumberBD(mobileNumber));

        String mUri = mGetUserInfoRequestBuilder.getGeneratedUri();
        mGetProfileInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_USER_INFO,
                mUri, getContext(), this, false);
        mProgressDialog.setMessage(getString(R.string.fetching_user_info));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        mGetProfileInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void resetSearchKeyword() {
        if (mContactSearchView != null && !mQuery.isEmpty()) {
            Logger.logD("Loader", "Resetting.. Previous query: " + mQuery);

            mQuery = "";
            mContactSearchView.setQuery("", false);
            getLoaderManager().restartLoader(CONTACTS_QUERY_LOADER, null, this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        if (mCursor != null && !mCursor.isClosed())
            mCursor.close();
    }

    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new SQLiteCursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                DataHelper dataHelper = DataHelper.getInstance(getActivity());

                // TODO hack
                /*
                 * Caution: It takes some time to load invite response from the server. So if you are
                 * loading this Fragment from Contacts page, it is very much possible that invitee list
                 * will be null. This is generally not a problem because invitee list is not used
                 * in Contacts fragment when doing database query. It is used in the database query
                 * from invite fragment, but by that time the invitee list should already have loaded.
                 */
                Cursor cursor = dataHelper.searchContacts(mQuery, true, false, false,
                        false, false, false, null);

                if (cursor != null) {
                    nameIndex = cursor.getColumnIndex(DBConstants.KEY_NAME);
                    originalNameIndex = cursor.getColumnIndex(DBConstants.KEY_ORIGINAL_NAME);
                    phoneNumberIndex = cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                    profilePictureUrlQualityMediumIndex = cursor.getColumnIndex(DBConstants.KEY_PROFILE_PICTURE_QUALITY_MEDIUM);
                    this.registerContentObserver(cursor, DBConstants.DB_TABLE_CONTACTS_URI);
                }
                return cursor;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data != null && data.getCount() <= 0) {
            if (InputValidator.isValidNumber(mQuery)) {
                final String mActionName;
                switch (transactionType) {
                    case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                        mActionName = getString(R.string.send_money);
                        break;
                    case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                        mActionName = getString(R.string.request_money);
                        break;
                    case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
                    default:
                        return;
                }
                mSearchedNumberLayout.setVisibility(View.VISIBLE);
                mSearchedNumberTextView.setText(mQuery);
                mActionNameTextView.setText(String.format("%s to", mActionName));
                mContactListRecyclerView.setVisibility(View.GONE);
            } else {
                mSearchedNumberLayout.setVisibility(View.GONE);
                mSearchedNumberTextView.setText(R.string.empty_string);
                mContactListRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            mSearchedNumberLayout.setVisibility(View.GONE);
            mContactListRecyclerView.setVisibility(View.VISIBLE);
            mSearchedNumberTextView.setText(R.string.empty_string);
            populateList(data, getString(R.string.no_contacts));
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

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
            mContactListRecyclerView.setAdapter(mAdapter);
            mContactListEmptyMessageTextView.setVisibility(View.GONE);
        } else {
            mContactListEmptyMessageTextView.setText(emptyText);
            mContactListEmptyMessageTextView.setVisibility(View.VISIBLE);
        }
    }

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final LayoutInflater layoutInflater;

        ContactListAdapter() {
            layoutInflater = LayoutInflater.from(getContext());
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case R.layout.list_empty_description:
                    return new EmptyViewHolder(layoutInflater.inflate(R.layout.list_empty_description, parent, false));
                case R.layout.list_item_contact:
                    return new ViewHolder(layoutInflater.inflate(R.layout.list_item_contact, parent, false));
                default:
                    return new RecyclerView.ViewHolder(layoutInflater.inflate(R.layout.list_item_contact, parent, false)) {
                    };
            }
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
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
            if (mCursor == null || mCursor.isClosed()) {
                return 0;
            } else {
                if (isFirstLoad == 0) {
                    return 10;
                } else {
                    return mCursor.getCount();
                }
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (getItemCount() == 0)
                return R.layout.list_empty_description;
            else
                return R.layout.list_item_contact;
        }

        class EmptyViewHolder extends RecyclerView.ViewHolder {
            final TextView mEmptyDescription;

            EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = itemView.findViewById(R.id.empty_description);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private final View itemView;

            private final TextView name1View;
            private final TextView name2View;
            private final ProfileImageView profilePictureView;
            private final TextView mobileNumberView;

            public ViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                name1View = itemView.findViewById(R.id.name1);
                name2View = itemView.findViewById(R.id.name2);
                mobileNumberView = itemView.findViewById(R.id.mobile_number);
                profilePictureView = itemView.findViewById(R.id.profile_picture);
            }

            public void bindView(final int pos) {

                mCursor.moveToPosition(pos);

                final String name = mCursor.getString(nameIndex);
                final String originalName = mCursor.getString(originalNameIndex);
                final String mobileNumber = mCursor.getString(phoneNumberIndex);
                final String profilePictureUrlQualityMedium = Constants.BASE_URL_FTP_SERVER + mCursor.getString(profilePictureUrlQualityMediumIndex);

                /*
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

                profilePictureView.setProfilePicture(profilePictureUrlQualityMedium, false);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.NAME, originalName);
                        bundle.putString(Constants.PHOTO_URI, profilePictureUrlQualityMedium);
                        bundle.putString(Constants.MOBILE_NUMBER, mobileNumber);
                        bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                        if (getActivity() instanceof IPayTransactionActionActivity) {
                            ((IPayTransactionActionActivity) getActivity()).switchToAmountInputFragment(bundle);
                        }
                    }
                });
            }
        }

    }

    @Override
    public void httpResponseReceiver(GenericHttpResponse result) {
        if (HttpErrorHandler.isErrorFound(result, getContext(), mProgressDialog)) {
            mProgressDialog.dismiss();
            mGetProfileInfoTask = null;
        } else {
            try {
                if (result.getApiCommand().equals(Constants.COMMAND_GET_USER_INFO)) {
                    mGetProfileInfoTask = null;
                    mProgressDialog.dismiss();
                    if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                        GetUserInfoResponse getUserInfoResponse = new Gson().fromJson(result.getJsonString(), GetUserInfoResponse.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(Constants.NAME, getUserInfoResponse.getName());
                        if (getUserInfoResponse.getProfilePictures() != null && !getUserInfoResponse.getProfilePictures().isEmpty())
                            bundle.putString(Constants.PHOTO_URI, getUserInfoResponse.getProfilePictures().get(0).getUrl());
                        bundle.putString(Constants.MOBILE_NUMBER, mPhoneNumber);
                        bundle.putInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY, transactionType);
                        if (getActivity() instanceof IPayTransactionActionActivity) {
                            ((IPayTransactionActionActivity) getActivity()).switchToAmountInputFragment(bundle);
                        }
                    } else {
                        Toast.makeText(getContext(), getString(R.string.user_has_no_ipay_account), Toast.LENGTH_LONG).show();
                    }
                }
            } catch (Exception e) {
                mProgressDialog.dismiss();
                mGetProfileInfoTask = null;
            }
        }
    }
}