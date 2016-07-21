package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.AskForIntroductionResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.SendInviteResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

import static bd.com.ipay.ipayskeleton.Utilities.Common.CommonColorList.PROFILE_PICTURE_BACKGROUNDS;
import static bd.com.ipay.ipayskeleton.Utilities.Common.CommonDrawableList.LIST_ITEM_BACKGROUNDS;

/**
 * Pass (Constants.VERIFIED_USERS_ONLY, true) in the argument bundle to show only the
 * verified iPay users and (Constants.IPAY_MEMBERS_ONLY, true) to show member users only.
 */
public class IPayContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        SearchView.OnQueryTextListener,
        HttpResponseListener {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private BottomSheetLayout mBottomSheetLayout;
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

    private View mSheetViewNonSubscriber;
    private View mSheetViewSubscriber;
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

    private int nameIndex;
    private int phoneNumberIndex;
    private int profilePictureUrlIndex;
    private int verificationStatusIndex;
    private int accountTypeIndex;
    private int isMemberIndex;
    private int updateTimeIndex;

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

            v.findViewById(R.id.search_contacts).setVisibility(View.GONE);

            // mSearchView will be populated from the onCreateOptionsMenu
        } else {
            mSearchView = (SearchView) v.findViewById(R.id.search_contacts);
            mSearchView.setIconified(false);
            mSearchView.setOnQueryTextListener(this);
        }

        if (getArguments() != null) {
            mShowVerifiedUsersOnly = getArguments().getBoolean(Constants.VERIFIED_USERS_ONLY, false);
            miPayMembersOnly = getArguments().getBoolean(Constants.IPAY_MEMBERS_ONLY, false);
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

    public void resetSearchKeyword() {
        if (mSearchView != null && !mQuery.isEmpty()) {
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

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (!isDialogFragment()) {
            inflater.inflate(R.menu.contact, menu);

            mSearchMenuItem = menu.findItem(R.id.action_search_contacts);
            mSearchView = (SearchView) MenuItemCompat.getActionView(mSearchMenuItem);
            mSearchView.setOnQueryTextListener(this);
            mSearchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setItemsVisibility(menu, mSearchMenuItem, false);
                    mSearchView.requestFocus();
                    if (mBottomSheetLayout != null && mBottomSheetLayout.isSheetShowing())
                        mBottomSheetLayout.dismissSheet();
                }
            });
            mSearchView.setQueryHint(getString(R.string.search));

            mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    mSearchView.setQuery("", true);
                    setItemsVisibility(menu, mSearchMenuItem, true);
                    return false;
                }
            });

        }
    }

    private void setItemsVisibility(Menu menu, MenuItem exception, boolean visible) {
        for (int i = 0; i < menu.size(); ++i) {
            MenuItem item = menu.getItem(i);
            if (item != null && item != exception) item.setVisible(visible);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("Loader", "Started");

        return new SQLiteCursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                DataHelper dataHelper = DataHelper.getInstance(getActivity());

                Cursor cursor = dataHelper.searchFriends(mQuery, miPayMembersOnly, mShowVerifiedUsersOnly);

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

    protected boolean isDialogFragment() {
        return false;
    }

    protected boolean shouldShowIPayUserIcon() {
        return true;
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

    protected void populateList(Cursor cursor, String emptyText) {
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
    protected void setContactInformationInSheet(String contactName, String contactNumber,
                                                String imageUrl, final int backgroundColor, boolean isMember, boolean isVerified, int accountType) {
        if (selectedBottomSheetView == null)
            return;

        final TextView contactNameView = (TextView) selectedBottomSheetView.findViewById(R.id.textview_contact_name);
        final ImageView contactImage = (ImageView) selectedBottomSheetView.findViewById(R.id.image_contact);
        final View infoHolderView = selectedBottomSheetView.findViewById(R.id.info_holder);
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

    protected void setPlaceHolderImage(ImageView contactImage, int backgroundColor) {
        contactImage.setBackgroundResource(backgroundColor);
        Glide.with(getActivity())
                .load(R.drawable.people)
                .fitCenter()
                .into(contactImage);
    }

    public void setBottomSheetLayout(BottomSheetLayout bottomSheetLayout) {
        this.mBottomSheetLayout = bottomSheetLayout;
    }

    private void setUpBottomSheet() {
        mSheetViewNonSubscriber = getActivity().getLayoutInflater()
                .inflate(R.layout.sheet_view_contact_non_subscriber, null);
        Button mInviteButton = (Button) mSheetViewNonSubscriber.findViewById(R.id.button_invite);
        mInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }

                sendInvite(mSelectedNumber);
            }
        });

        mSheetViewSubscriber = getActivity().getLayoutInflater()
                .inflate(R.layout.sheet_view_contact_subscriber, null);

        Button mSendMoneyButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_send_money);
        mSendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                intent.putExtra(Constants.MOBILE_NUMBER, mSelectedNumber);
                startActivity(intent);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

        Button mRequestMoneyButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_request_money);
        mRequestMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
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

        Button mAskForRecommendationButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_ask_for_introduction);
        mAskForRecommendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecommendationRequest(mSelectedNumber);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });
    }

    protected void sendRecommendationRequest(String mobileNumber) {
        if (mAskForRecommendationTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_send_for_recommendation));
        mProgressDialog.show();
        mAskForRecommendationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ASK_FOR_RECOMMENDATION,
                Constants.BASE_URL_MM + Constants.URL_ASK_FOR_INTRODUCTION + mobileNumber, null, getActivity());
        mAskForRecommendationTask.mHttpResponseListener = this;
        mAskForRecommendationTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void sendInvite(String phoneNumber) {
        if (ContactsHolderFragment.mGetInviteInfoResponse == null || ContactsHolderFragment.mGetInviteInfoResponse.invitees == null) {
            Toast.makeText(getActivity(), R.string.failed_sending_invitation,
                    Toast.LENGTH_LONG).show();
            return;
        }

        int numberOfInvitees = ContactsHolderFragment.mGetInviteInfoResponse.invitees.size();
        if (numberOfInvitees >= ContactsHolderFragment.mGetInviteInfoResponse.totalLimit) {
            Toast.makeText(getActivity(), R.string.invitaiton_limit_exceeded,
                    Toast.LENGTH_LONG).show();
        } else if (ContactsHolderFragment.mGetInviteInfoResponse.invitees.contains(phoneNumber)) {
            Toast.makeText(getActivity(), R.string.invitation_already_sent,
                    Toast.LENGTH_LONG).show();
        } else {
            mProgressDialog.setMessage(getActivity().getString(R.string.progress_dialog_sending_invite));
            mProgressDialog.show();

            mSendInviteTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SEND_INVITE,
                    Constants.BASE_URL_MM + Constants.URL_SEND_INVITE + phoneNumber, null, getActivity(), this);
            mSendInviteTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mProgressDialog.dismiss();
            mSendInviteTask = null;

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_request, Toast.LENGTH_SHORT).show();

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

                    ContactsHolderFragment.mGetInviteInfoResponse.invitees.add(mSelectedNumber);

                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), mSendInviteResponse.getMessage(), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_sending_invitation, Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mSendInviteTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_ASK_FOR_RECOMMENDATION)) {
            try {

                mAskForIntroductionResponse = gson.fromJson(result.getJsonString(), AskForIntroductionResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.ask_for_recommendation_sent, Toast.LENGTH_LONG).show();
                    }
                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), mAskForIntroductionResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_asking_recommendation, Toast.LENGTH_LONG).show();
                }
            }

            mProgressDialog.dismiss();
            mAskForRecommendationTask = null;
        }
    }

    protected void showMemberSheet(boolean isVerified) {
        if (mBottomSheetLayout == null)
            return;

        selectedBottomSheetView = mSheetViewSubscriber;

        Button askForConfirmationButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_ask_for_introduction);
        if (!isVerified) {
            if (askForConfirmationButton != null)
                askForConfirmationButton.setVisibility(View.GONE);

        } else {
            if (askForConfirmationButton != null)
                askForConfirmationButton.setVisibility(View.VISIBLE);
        }

        mBottomSheetLayout.showWithSheetView(mSheetViewSubscriber);
        mBottomSheetLayout.expandSheet();
    }

    protected void showNonMemberSheet(String mobileNumber) {
        if (mBottomSheetLayout == null)
            return;

        selectedBottomSheetView = mSheetViewNonSubscriber;

        Button inviteButton = (Button) mSheetViewNonSubscriber.findViewById(R.id.button_invite);
        if (ContactsHolderFragment.mGetInviteInfoResponse.getInvitees().contains(mobileNumber))
            inviteButton.setEnabled(false);
        else
            inviteButton.setEnabled(true);

        mBottomSheetLayout.showWithSheetView(mSheetViewNonSubscriber);
        mBottomSheetLayout.expandSheet();
    }

    protected void setSelectedName(String name) {
        this.mSelectedName = name;
    }

    protected void setSelectedNumber(String contactNumber) {
        this.mSelectedNumber = contactNumber;
    }


    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EMPTY_VIEW = 10;
        private static final int FRIEND_VIEW = 100;

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = (TextView) itemView.findViewById(R.id.empty_description);
            }
        }

        public boolean isInvited(String phoneNumber) {
            if (ContactsHolderFragment.mGetInviteInfoResponse == null ||
                    ContactsHolderFragment.mGetInviteInfoResponse.getInvitees() == null) return false;
            else if (ContactsHolderFragment.mGetInviteInfoResponse.getInvitees().contains(phoneNumber))
                return true;
            return false;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View itemView;

            private TextView mPortraitTextView;
            private TextView mNameView;
            private RoundedImageView mProfilePictureView;
            private TextView mMobileNumberView;
            private ImageView isSubscriber;
            private ImageView mVerificationStatus;
            private TextView inviteStatusTextView;

            public ViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                mPortraitTextView = (TextView) itemView.findViewById(R.id.portraitTxt);
                mNameView = (TextView) itemView.findViewById(R.id.name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.mobile_number);
                mProfilePictureView = (RoundedImageView) itemView.findViewById(R.id.portrait);
                isSubscriber = (ImageView) itemView.findViewById(R.id.is_subscriber);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.verification_status);
                inviteStatusTextView = (TextView) itemView.findViewById(R.id.invite_status);
            }

            public void bindView(int pos) {

                mCursor.moveToPosition(pos);

                final String name = mCursor.getString(nameIndex);
                final String phoneNumber = mCursor.getString(phoneNumberIndex);
                final String profilePictureUrl = mCursor.getString(profilePictureUrlIndex);
                final boolean isVerified = mCursor.getInt(verificationStatusIndex) == DBConstants.VERIFIED_USER;
                final int accountType = mCursor.getInt(accountTypeIndex);
                final boolean isMember = mCursor.getInt(isMemberIndex) == DBConstants.IPAY_MEMBER;

                boolean isInvited = isInvited(phoneNumber);

                mNameView.setText(name);
                mMobileNumberView.setText(phoneNumber);

                if (!isMember && isInvited)
                    inviteStatusTextView.setVisibility(View.VISIBLE);
                else
                    inviteStatusTextView.setVisibility(View.GONE);


                if (shouldShowIPayUserIcon() && isMember) {
                    isSubscriber.setVisibility(View.VISIBLE);
                } else {
                    isSubscriber.setVisibility(View.GONE);
                }

                if (isVerified) {
                    mVerificationStatus.setVisibility(View.VISIBLE);
                } else {
                    mVerificationStatus.setVisibility(View.GONE);
                }

                if (name.startsWith("+") && name.length() > 1)
                    mPortraitTextView.setText(String.valueOf(name.substring(1).charAt(0)).toUpperCase());
                else if (name.length() > 0)
                    mPortraitTextView.setText(String.valueOf(name.charAt(0)).toUpperCase());

                int randomListItemBackgroundColor = LIST_ITEM_BACKGROUNDS[getAdapterPosition() % LIST_ITEM_BACKGROUNDS.length];
                mPortraitTextView.setBackgroundResource(randomListItemBackgroundColor);

                if (profilePictureUrl != null && !profilePictureUrl.isEmpty()) {
                    Glide.with(getActivity())
                            .load(profilePictureUrl)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(mProfilePictureView);
                } else {
                    Glide.with(getActivity())
                            .load(android.R.color.transparent)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .into(mProfilePictureView);
                }

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isDialogFragment()) {

                            Intent intent = new Intent();
                            intent.putExtra(Constants.NAME, name);
                            intent.putExtra(Constants.MOBILE_NUMBER, phoneNumber);
                            intent.putExtra(Constants.PROFILE_PICTURE, profilePictureUrl);
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();

                        } else {
                            setSelectedName(name);
                            setSelectedNumber(phoneNumber);

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(itemView.getWindowToken(), 0);

                            // Add a delay to hide keyboard and then open up the bottom sheet
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    int randomProfileBackgroundColor = PROFILE_PICTURE_BACKGROUNDS[getAdapterPosition() % PROFILE_PICTURE_BACKGROUNDS.length];
                                    if (isMember) {
                                        showMemberSheet(isVerified);
                                    } else {
                                        showNonMemberSheet(phoneNumber);
                                    }

                                    setContactInformationInSheet(name,
                                            phoneNumber, profilePictureUrl, randomProfileBackgroundColor,
                                            isMember, isVerified, accountType);
                                }
                            }, 100);
                        }
                    }
                });
            }

        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_empty_description, parent, false);
                EmptyViewHolder vh = new EmptyViewHolder(v);
                return vh;
            } else {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);
                ViewHolder vh = new ViewHolder(v);
                return vh;
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
}