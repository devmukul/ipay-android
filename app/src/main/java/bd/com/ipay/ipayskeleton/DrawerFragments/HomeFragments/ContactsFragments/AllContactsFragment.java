package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.HashMap;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class AllContactsFragment extends BaseContactsFragment {

    private RecyclerView mRecyclerView;
    private ContactListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    // Contacts will be filtered base on this field.
    // It will be populated when the user types in the search bar.
    protected String mQuery = "";

    protected static final int CONTACTS_QUERY_LOADER = 0;

    private boolean digitSectionViewAdded = false;
    private HashMap<String, String> subscriber = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contact_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ContactListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this);

        mRecyclerView.setAdapter(mAdapter);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;
        getLoaderManager().restartLoader(CONTACTS_QUERY_LOADER, null, this);

        return true;
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        try {
            Cursor cursor = DataHelper.getInstance(getActivity()).getSubscribers();

            if (cursor != null) {
                cursor.getCount();
                if (cursor.moveToFirst()) {
                    do {
                        String mobileNumber = cursor.getString(cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER));
                        String name = cursor.getString(cursor.getColumnIndex(DBConstants.KEY_NAME));
                        subscriber.put(mobileNumber, name);
                    } while (cursor.moveToNext());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        final String[] projection;

        if (android.os.Build.VERSION.SDK_INT > 10) {
            projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_URI};
        } else {
            projection = new String[]{
                    ContactsContract.Contacts._ID,
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME,
                    ContactsContract.Contacts.PHOTO_ID};
        }
        final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1"
                + " AND " + ContactsContract.Contacts.DISPLAY_NAME
                + " LIKE '%" + mQuery + "%'";
        final String order = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC";

        Uri queryUri = ContactsContract.Contacts.CONTENT_URI;

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
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    public class ContactListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EMPTY_VIEW = 10;
        private static final int SECTION_VIEW = 20;

        private Cursor mCursor;

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = (TextView) itemView.findViewById(R.id.empty_description);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private View itemView;

            private TextView mPortraitTextView;
            private TextView mNameView;
            private RoundedImageView mPortrait;
            private TextView mMobileNumberView;
            private ImageView isSubscriber;

            public ViewHolder(View itemView) {
                super(itemView);

                this.itemView = itemView;

                mPortraitTextView = (TextView) itemView.findViewById(R.id.portraitTxt);
                mNameView = (TextView) itemView.findViewById(R.id.name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.mobile_number);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
                isSubscriber = (ImageView) itemView.findViewById(R.id.is_subscriber);
            }

            public void bindView() {

                if (mCursor == null) {
                    return;
                }

                mCursor.moveToPosition(getAdapterPosition());
                int index = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final String name = mCursor.getString(index);
                mNameView.setText(name);

                index = mCursor.getColumnIndex(ContactsContract.Contacts._ID);
                final long contactId = mCursor.getLong(index);

                String number = ContactEngine.getContactNumberFromId(getActivity(), contactId);
                mMobileNumberView.setText(number);

                if (number != null) {
                    number = ContactEngine.convertToInternationalFormat(number);
                    if (subscriber != null && subscriber.containsKey(number)) {
                        isSubscriber.setVisibility(View.VISIBLE);
                    } else {
                        isSubscriber.setVisibility(View.INVISIBLE);
                    }
                } else isSubscriber.setVisibility(View.INVISIBLE);

                int position = getAdapterPosition();
                final int randomColor = position % 10;

                Uri photoUri = null;
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    String photoPath = mCursor.getString(mCursor
                            .getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                    if (photoPath != null)
                        photoUri = Uri.parse(photoPath);
                } else {
                    String photoID = mCursor.getString(mCursor
                            .getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
                    if (photoID != null) {
                        photoUri = ContentUris.withAppendedId(
                                ContactsContract.Data.CONTENT_URI,
                                Long.parseLong(photoID));
                    }
                }

                // The Number needs to be accessed within the anonymous inner class,
                // so making it final
                final String contactNumber = number;
                final String imageUrl = (photoUri == null ? null : photoUri.toString());

                if (name.startsWith("+") && name.length() > 1)
                    mPortraitTextView.setText(String.valueOf(name.substring(1).charAt(0)).toUpperCase());
                else mPortraitTextView.setText(String.valueOf(name.charAt(0)).toUpperCase());


                if (randomColor == 0)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle);
                else if (randomColor == 1)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_blue);
                else if (randomColor == 2)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_brightpink);
                else if (randomColor == 3)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_cyan);
                else if (randomColor == 4)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_megenta);
                else if (randomColor == 5)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_orange);
                else if (randomColor == 6)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_red);
                else if (randomColor == 7)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_springgreen);
                else if (randomColor == 8)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_violet);
                else if (randomColor == 9)
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_yellow);
                else
                    mPortraitTextView.setBackgroundResource(R.drawable.background_portrait_circle_azure);

                if (photoUri != null) Glide.with(AllContactsFragment.this)
                        .load(photoUri.toString())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mPortrait);
                else Glide.with(AllContactsFragment.this)
                        .load(android.R.color.transparent)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mPortrait);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setSelectedName(name);
                        setSelectedNumber(contactNumber);

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(itemView.getWindowToken(), 0);

                        // Add a delay to hide keyboard and then open up the bottomsheet
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if (subscriber == null || !subscriber.containsKey(contactNumber)) {
                                    showNonSubscriberSheet();
                                    setContactInformationInSheet(name,
                                            contactNumber, imageUrl, COLORS[randomColor]);
                                } else {
                                    showSubscriberSheet();
                                    setContactInformationInSheet(name,
                                            contactNumber, imageUrl, COLORS[randomColor]);
                                }
                            }
                        }, 100);
                    }
                });
            }

        }

        public class SectionViewHolder extends ViewHolder {
            private TextView mSectionTitle;

            public SectionViewHolder(View itemView) {
                super(itemView);
                mSectionTitle = (TextView) itemView.findViewById(R.id.sectionTitle);
            }

            @Override
            public void bindView() {
                super.bindView();

                mCursor.moveToPosition(getAdapterPosition());
                int index = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String name = mCursor.getString(index);

                if (name.startsWith("+") || (name.charAt(0) >= '0' && name.charAt(0) <= '9')) {
                    mSectionTitle.setText("#");
                    digitSectionViewAdded = true;
                } else {
                    mSectionTitle.setText(String.valueOf(name.charAt(0)).toUpperCase());
                }
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v;

            if (viewType == EMPTY_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_empty_description, parent, false);

                EmptyViewHolder vh = new EmptyViewHolder(v);

                return vh;
            } else if (viewType == SECTION_VIEW) {
                v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_section_item_contact, parent, false);

                SectionViewHolder vh = new SectionViewHolder(v);

                return vh;
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {

                if (holder instanceof SectionViewHolder) {
                    SectionViewHolder vh = (SectionViewHolder) holder;
                    vh.bindView();
                } else if (holder instanceof ViewHolder) {
                    ViewHolder vh = (ViewHolder) holder;
                    vh.bindView();
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

            if (mCursor == null) {
                return 0;
            }

            return mCursor.getCount();
        }

        @Override
        public int getItemViewType(int position) {
            if (mCursor == null) return EMPTY_VIEW;
            else if (mCursor.getCount() == 0) return EMPTY_VIEW;

            mCursor.moveToPosition(position);

            int index = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String name = mCursor.getString(index);

            String previous = null;
            mCursor.moveToPrevious();
            if (!mCursor.isBeforeFirst()) {
                previous = mCursor.getString(index);
            }

            if (previous == null) {
                return SECTION_VIEW;
            } else if (!String.valueOf(name.charAt(0)).toUpperCase().contains(String.valueOf(previous.charAt(0)).toUpperCase())) {
                if (name.startsWith("+")) {
                    if (!digitSectionViewAdded) return SECTION_VIEW;
                } else if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
                    if (!digitSectionViewAdded) return SECTION_VIEW;
                } else
                    return SECTION_VIEW;
            }

            return super.getItemViewType(position);
        }

        public void swapCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }
}