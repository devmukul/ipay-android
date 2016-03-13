package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DBConstants;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.DatabaseHelper.SQLiteCursorLoader;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.AskForRecommendationRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.AskForRecommendationResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPayContactsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>, HttpResponseListener {

    private static final int CONTACTS_QUERY_LOADER = 0;

    private RecyclerView mRecyclerView;
    private SubscriberListAdapter miPayAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private File dir;

    //    private HashMap<String, String> subscriber = new HashMap<>();
    private boolean digitSectionViewAdded = false;
    private HashMap<String, String> subscriber = new HashMap<>();

    private BottomSheetLayout mBottomSheetLayout;

    // When a contact item is clicked, we need to access its name and number from the sheet view.
    // So saving these in these two variables.
    private String mSelectedName;
    private String mSelectedNumber;

    private HttpRequestPostAsyncTask mAskForRecommendationTask = null;
    private AskForRecommendationResponse mAskForRecommendationResponse;

    private ProgressDialog mProgressDialog;

    private View mSheetViewSubscriber;

    private Button mSendMoneyButton;
    private Button mRequestMoneyButton;
    private Button mAskForRecommendationButton;

    private final int[] COLORS = {
            R.color.background_default,
            R.color.background_blue,
            R.color.background_bright_pink,
            R.color.background_cyan,
            R.color.background_magenta,
            R.color.background_orange,
            R.color.background_red,
            R.color.background_spring_green,
            R.color.background_violet,
            R.color.background_yellow,
            R.color.background_azure
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);
        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.contact_list);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        miPayAdapter = new SubscriberListAdapter();

        mProgressDialog = new ProgressDialog(getActivity());

        dir = new File(Environment.getExternalStorageDirectory().getPath()
                + Constants.PICTURE_FOLDER);
        if (!dir.exists()) dir.mkdir();

        getLoaderManager().initLoader(CONTACTS_QUERY_LOADER, null, this);

        mSheetViewSubscriber = getActivity().getLayoutInflater()
                .inflate(R.layout.sheet_view_contact_subscriber, null);
        mSendMoneyButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_send_money);
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
        mRequestMoneyButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_request_money);
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
        mAskForRecommendationButton = (Button) mSheetViewSubscriber.findViewById(R.id.button_ask_for_recommendation);
        mAskForRecommendationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRecommendationRequest(mSelectedNumber);

                if (mBottomSheetLayout.isSheetShowing()) {
                    mBottomSheetLayout.dismissSheet();
                }
            }
        });

        mRecyclerView.setAdapter(miPayAdapter);

        return v;
    }

    private void sendRecommendationRequest(String mobileNumber) {
        if (mAskForRecommendationTask != null) {
            return;
        }

        mProgressDialog.setMessage(getString(R.string.progress_dialog_send_for_recommendation));
        mProgressDialog.show();
        AskForRecommendationRequest mAskForRecommendationRequest =
                new AskForRecommendationRequest(mobileNumber);
        Gson gson = new Gson();
        String json = gson.toJson(mAskForRecommendationRequest);
        mAskForRecommendationTask = new HttpRequestPostAsyncTask(Constants.COMMAND_ASK_FOR_RECOMMENDATION,
                Constants.BASE_URL_POST_MM + Constants.URL_ASK_FOR_RECOMMENDATION, json, getActivity());
        mAskForRecommendationTask.mHttpResponseListener = this;
        mAskForRecommendationTask.execute((Void) null);
    }

    private void setContactInformation(View v, String contactName, String contactNumber,
                                       String imageUrl, final int backgroundColor) {
        final TextView contactNameView = (TextView) v.findViewById(R.id.textview_contact_name);
        final ImageView contactImage = (ImageView) v.findViewById(R.id.image_contact);

        contactImage.setBackgroundResource(backgroundColor);
        contactNameView.setText(contactName);

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
        }
        else {
            contactImage.setBackgroundResource(backgroundColor);
            setPlaceHolderImage(contactImage, backgroundColor);
        }
    }

    private void setPlaceHolderImage(ImageView contactImage, int backgroundColor) {
        contactImage.setBackgroundResource(backgroundColor);
        Glide.with(getActivity())
                .load(R.drawable.people)
                .fitCenter()
                .into(contactImage);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        getLoaderManager().destroyLoader(CONTACTS_QUERY_LOADER);
        super.onDestroy();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        miPayAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        miPayAdapter.swapCursor(null);
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mProgressDialog.dismiss();

            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.failed_request, Toast.LENGTH_SHORT).show();

            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_ASK_FOR_RECOMMENDATION)) {
            try {

                if (resultList.size() > 2) {
                    mAskForRecommendationResponse = gson.fromJson(resultList.get(2), AskForRecommendationResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.ask_for_recommendation_sent, Toast.LENGTH_LONG).show();
                        }
                    } else if (getActivity() != null) {
                        Toast.makeText(getActivity(), mAskForRecommendationResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else if (getActivity() != null) {
                    Toast.makeText(getActivity(), R.string.failed_asking_recommendation, Toast.LENGTH_LONG).show();
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
                final String name = mCursor.getString(index);
                mName.setText(name);

                index = mCursor.getColumnIndex(DBConstants.KEY_MOBILE_NUMBER);
                final String mobileNumber = mCursor.getString(index);

                int position = getAdapterPosition();
                final int randomColor = position % 10;

                isSubscriber.setVisibility(View.VISIBLE);

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

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mSelectedNumber = mobileNumber;
                        mSelectedName = name;

                        setContactInformation(mSheetViewSubscriber, mSelectedName,
                                mSelectedNumber, imageUrl, COLORS[randomColor]);
                        mBottomSheetLayout.showWithSheetView(mSheetViewSubscriber);
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
            } else if (!String.valueOf(name.charAt(0)).toUpperCase().
                    contains(String.valueOf(previous.charAt(0)).toUpperCase())) {
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