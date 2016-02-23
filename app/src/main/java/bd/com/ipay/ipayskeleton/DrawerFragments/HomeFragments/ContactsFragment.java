package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments;

import android.content.ContentUris;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;

public class ContactsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "ContactsFragment";
    private static final int ALL_TAB = 0;
    private static final int IPAY_TAB = 1;

    private int selectedTab = 0;

    private static final int CONTACTS_QUERY_LOADER = 0;
    private static final int SUBSCRIBER_LOADER = 1;

    private RecyclerView mRecyclerView;
    private ContactListAdapter mAdapter;
    private SubscriberListAdapter miPayAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private TextView allContactsTab;
    private TextView iPayContactsTab;
    private File dir;

    //    private HashMap<String, String> subscriber = new HashMap<>();
    private boolean digitSectionViewAdded = false;
    private LinearLayout contactsFilterHolder;
    private HashMap<String, String> subscriber = new HashMap<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contact_list);
        allContactsTab = (TextView) v.findViewById(R.id.all_contacts_tab);
        iPayContactsTab = (TextView) v.findViewById(R.id.ipay_contacts_tab);
        contactsFilterHolder = (LinearLayout) v.findViewById(R.id.contacts_filter_linear_layout);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ContactListAdapter();
        miPayAdapter = new SubscriberListAdapter();
        mRecyclerView.setAdapter(mAdapter);

        dir = new File(Environment.getExternalStorageDirectory().getPath()
                + Constants.PICTURE_FOLDER);
        if (!dir.exists()) dir.mkdir();

        allContactsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTab = ALL_TAB;
                allContactsTab.setBackgroundResource(R.drawable.contacts_tab_selected_background);
                iPayContactsTab.setBackgroundResource(0);
                mRecyclerView.setAdapter(mAdapter);
            }
        });

        iPayContactsTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTab = IPAY_TAB;
                iPayContactsTab.setBackgroundResource(R.drawable.contacts_tab_selected_background);
                allContactsTab.setBackgroundResource(0);
                mRecyclerView.setAdapter(miPayAdapter);
            }
        });

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this);
        getLoaderManager().initLoader(SUBSCRIBER_LOADER, null, this);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(SUBSCRIBER_LOADER);
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
        final String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
        final String order = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE NOCASE ASC";

        switch (id) {
            case CONTACTS_QUERY_LOADER:
                Log.d("ContactsFragment", "Data load started");
                Uri queryUri = ContactsContract.Contacts.CONTENT_URI;
                String[] selectionArgs = null;

                return new CursorLoader(
                        getActivity(),
                        queryUri,
                        projection,
                        selection,
                        selectionArgs,
                        order
                );
            case SUBSCRIBER_LOADER:
                return new SQLiteCursorLoader(getActivity()) {
                    @Override
                    public Cursor loadInBackground() {
                        Cursor cursor = null;
                        try {
                            try {
                                cursor = DataHelper.getInstance(getActivity()).getSubscribers();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }

                            if (cursor != null) {
                                cursor.getCount();
                                if (cursor.moveToFirst()) {
                                    do {
                                        String mobileNumber = cursor.getString(cursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER));
                                        String name = cursor.getString(cursor.getColumnIndex(DBConstants.KEY_NAME));
                                        subscriber.put(mobileNumber, name);
                                    } while (cursor.moveToNext());
                                }

                                cursor.moveToFirst();
                            }

                            if (cursor != null) {
                                cursor.getCount();
                                this.registerContentObserver(cursor, DBConstants.DB_TABLE_SUBSCRIBERS_URI);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return cursor;
                    }
                };
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.d("ContactsFragment", "Data load finished");
        if (loader.getId() == CONTACTS_QUERY_LOADER) {
            mAdapter.swapCursor(data);
        } else if (loader.getId() == SUBSCRIBER_LOADER) {
            miPayAdapter.swapCursor(data);
        }

        mAdapter.notifyDataSetChanged();
        miPayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == CONTACTS_QUERY_LOADER) {
            mAdapter.swapCursor(null);
        } else if (loader.getId() == SUBSCRIBER_LOADER) {
            miPayAdapter.swapCursor(null);
        }
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
            private TextView mPortraitTxt;
            private TextView mName;
            private RoundedImageView mPortrait;
            private ImageView isSubscriber;

            public ViewHolder(View itemView) {
                super(itemView);

                mPortraitTxt = (TextView) itemView.findViewById(R.id.portraitTxt);
                mName = (TextView) itemView.findViewById(R.id.name);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
                isSubscriber = (ImageView) itemView.findViewById(R.id.is_subscriber);
            }

            public void bindView() {

                if (mCursor == null) {
                    return;
                }

                mCursor.moveToPosition(getAdapterPosition());
                int index = mCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                String name = mCursor.getString(index);
                mName.setText(name);

                index = mCursor.getColumnIndex(ContactsContract.Contacts._ID);
                final long contactId = mCursor.getLong(index);

                String number = ContactEngine.getContactNumberFromId(getActivity(), contactId);

                if (number != null) {
                    number = number.replaceAll("[^0-9]", "");
                    if (number.length() == 11) number = "+88" + number;
                    else if (number.length() == 13) number = "+" + number;
                    if (subscriber != null && subscriber.containsKey(number)) {
                        isSubscriber.setVisibility(View.VISIBLE);
                    } else {
                        isSubscriber.setVisibility(View.GONE);
                    }
                } else isSubscriber.setVisibility(View.GONE);


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

                if (name.startsWith("+") && name.length() > 1)
                    mPortraitTxt.setText(String.valueOf(name.substring(1).charAt(0)).toUpperCase());
                else mPortraitTxt.setText(String.valueOf(name.charAt(0)).toUpperCase());


                if (randomColor == 0)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle);
                else if (randomColor == 1)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_blue);
                else if (randomColor == 2)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_brightpink);
                else if (randomColor == 3)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_cyan);
                else if (randomColor == 4)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_megenta);
                else if (randomColor == 5)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_orange);
                else if (randomColor == 6)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_red);
                else if (randomColor == 7)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_springgreen);
                else if (randomColor == 8)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_violet);
                else if (randomColor == 9)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_yellow);
                else
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_azure);

                if (photoUri != null) Glide.with(ContactsFragment.this)
                        .load(photoUri.toString())
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mPortrait);
                else Glide.with(ContactsFragment.this)
                        .load(android.R.color.transparent)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(mPortrait);
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


    public class SubscriberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

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
            private TextView mPortraitTxt;
            private TextView mName;
            private RoundedImageView mPortrait;
            private ImageView isSubscriber;

            public ViewHolder(View itemView) {
                super(itemView);

                mPortraitTxt = (TextView) itemView.findViewById(R.id.portraitTxt);
                mName = (TextView) itemView.findViewById(R.id.name);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
                isSubscriber = (ImageView) itemView.findViewById(R.id.is_subscriber);
            }

            public void bindView() {

                if (mCursor == null) {
                    return;
                }

                mCursor.moveToPosition(getAdapterPosition());
                int index = mCursor.getColumnIndex(DBConstants.KEY_NAME);
                String name = mCursor.getString(index);
                mName.setText(name);

                index = mCursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                final String mobileNumber = mCursor.getString(index);

                int position = getAdapterPosition();
                final int randomColor = position % 10;

                isSubscriber.setVisibility(View.VISIBLE);

                // Set profile pic
                File file = new File(dir, mobileNumber.replaceAll("[^0-9]", "") + ".jpg");
                if (file.exists()) {
                    Glide.with(getActivity())
                            .load(file.getAbsolutePath().toString())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)     // Skip the cache. Load from disk each time
                            .into(mPortrait);
                } else {
                    Glide.with(getActivity())
                            .load(android.R.color.transparent)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)     // Skip the cache. Load from disk each time
                            .into(mPortrait);
                }

                if (name.startsWith("+") && name.length() > 1)
                    mPortraitTxt.setText(String.valueOf(name.substring(1).charAt(0)).toUpperCase());
                else mPortraitTxt.setText(String.valueOf(name.charAt(0)).toUpperCase());

                if (randomColor == 0)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle);
                else if (randomColor == 1)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_blue);
                else if (randomColor == 2)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_brightpink);
                else if (randomColor == 3)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_cyan);
                else if (randomColor == 4)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_megenta);
                else if (randomColor == 5)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_orange);
                else if (randomColor == 6)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_red);
                else if (randomColor == 7)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_springgreen);
                else if (randomColor == 8)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_violet);
                else if (randomColor == 9)
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_yellow);
                else
                    mPortraitTxt.setBackgroundResource(R.drawable.background_portrait_circle_azure);
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
                int index = mCursor.getColumnIndex(DBConstants.KEY_NAME);
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

            int index = mCursor.getColumnIndex(DBConstants.KEY_NAME);
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