package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
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

import java.io.File;
import java.util.HashMap;

import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPayContactsFragment extends BaseContactsFragment {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private RecyclerView mRecyclerView;
    private SubscriberListAdapter miPayAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private File dir;

    private HashMap<String, String> subscriber = new HashMap<>();

    // Contacts will be filtered base on this field.
    // It will be populated when the user types in the search bar.
    private String mQuery = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = super.onCreateView(inflater, container, savedInstanceState);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contact_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        miPayAdapter = new SubscriberListAdapter();

        dir = new File(Environment.getExternalStorageDirectory().getPath()
                + Constants.PICTURE_FOLDER);
        if (!dir.exists()) dir.mkdir();

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this);

        mRecyclerView.setAdapter(miPayAdapter);

        return v;
    }

    @Override
    protected boolean isDialogFragment() {
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        mQuery = "";
        miPayAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        super.onDestroy();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mQuery = newText;
        getLoaderManager().restartLoader(CONTACTS_QUERY_LOADER, null, this);

        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SQLiteCursorLoader(getActivity()) {
            @Override
            public Cursor loadInBackground() {
                Cursor cursor = null;
                try {
                    cursor = DataHelper.getInstance(getActivity()).searchSubscribers(mQuery);

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
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        miPayAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        miPayAdapter.swapCursor(null);
    }

    public class SubscriberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int EMPTY_VIEW = 10;

        private Cursor mCursor;

        public class EmptyViewHolder extends RecyclerView.ViewHolder {
            public TextView mEmptyDescription;

            public EmptyViewHolder(View itemView) {
                super(itemView);
                mEmptyDescription = (TextView) itemView.findViewById(R.id.empty_description);
            }
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView mPortraitTextView;
            private TextView mNameView;
            private TextView mMobileNumberView;
            private RoundedImageView mPortrait;
            private ImageView mVerificationStatus;

            public ViewHolder(View itemView) {
                super(itemView);

                mPortraitTextView = (TextView) itemView.findViewById(R.id.portraitTxt);
                mNameView = (TextView) itemView.findViewById(R.id.name);
                mMobileNumberView = (TextView) itemView.findViewById(R.id.mobile_number);
                mPortrait = (RoundedImageView) itemView.findViewById(R.id.portrait);
                mVerificationStatus = (ImageView) itemView.findViewById(R.id.verification_status);
            }

            public void bindView() {

                if (mCursor == null) {
                    return;
                }

                mCursor.moveToPosition(getAdapterPosition());
                int index = mCursor.getColumnIndex(DBConstants.KEY_NAME);
                final String name = mCursor.getString(index);
                mNameView.setText(name);

                index = mCursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                final String mobileNumber = mCursor.getString(index);
                mMobileNumberView.setText(mobileNumber);

                index = mCursor.getColumnIndex(DBConstants.KEY_VERIFICATION_STATUS);
                final int verificationStatus = mCursor.getInt(index);
                if (verificationStatus == DBConstants.VERIFIED_USER)
                    mVerificationStatus.setVisibility(View.VISIBLE);
                else
                    mVerificationStatus.setVisibility(View.GONE);

                index = mCursor.getColumnIndex(DBConstants.KEY_ACCOUNT_TYPE);
                final int accountType = mCursor.getInt(index);

                int position = getAdapterPosition();
                final int randomColor = position % 10;

                final String imageUrl;

                // Set profile pic
                File file = new File(dir, mobileNumber.replaceAll("[^0-9]", "") + ".jpg");
                if (file.exists()) {
                    imageUrl = file.getAbsolutePath();
                    Glide.with(getActivity())
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)     // Skip the cache. Load from disk each time
                            .into(mPortrait);
                } else {
                    imageUrl = null;
                    Glide.with(getActivity())
                            .load(android.R.color.transparent)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)     // Skip the cache. Load from disk each time
                            .into(mPortrait);
                }

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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isDialogFragment()) {

                            Intent intent = new Intent();
                            intent.putExtra(Constants.NAME, name);
                            intent.putExtra(Constants.MOBILE_NUMBER, mobileNumber);
                            intent.putExtra(Constants.PHOTO_URI, imageUrl);
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();

                        } else {
                            setSelectedName(name);
                            setSelectedNumber(mobileNumber);

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(itemView.getWindowToken(), 0);

                            // Add a delay to hide keyboard and then open up the bottomsheet
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    showSubscriberSheet(verificationStatus);
                                    setContactInformationInSheet(name,
                                            mobileNumber, imageUrl, COLORS[randomColor],
                                            verificationStatus, accountType);
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
            }

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contact, parent, false);

            ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            try {
                if (holder instanceof ViewHolder) {
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

            return super.getItemViewType(position);
        }

        public void swapCursor(Cursor cursor) {
            mCursor = cursor;
            notifyDataSetChanged();
        }
    }
}