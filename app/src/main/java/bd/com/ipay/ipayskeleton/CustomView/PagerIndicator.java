package bd.com.ipay.ipayskeleton.CustomView;


import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.R;

public class PagerIndicator extends RecyclerView {

    public static final int DIRECTION_VERTICAL = 0;
    public static final int DIRECTION_HORIZONTAL = 1;

    //private ViewPager mViewPager;
    private int mSelectedResource;
    private int mUnselectedResource;
    private boolean mClickable;
    private int mDirection;
    private PagerIndicatorAdapter mAdapter;
    private int mCount = 0;

    private int mSelectedPosition = 0;

    private Context mContext;

    private LinearLayoutManager mLayoutManager;

    public PagerIndicator(Context context) {
        this(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        Log.d(VIEW_LOG_TAG, "PagerIndicator Constructor 2");

    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        Log.d(VIEW_LOG_TAG, "PagerIndicator Constructor 3");
        this.mContext = context;

        this.mLayoutManager = new LinearLayoutManager(context);
        this.mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        super.setLayoutManager(mLayoutManager);
        this.mAdapter = new PagerIndicatorAdapter();
        super.setAdapter(mAdapter);
    }

    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void setup(int selectedResource,
                      int unselectedResource, int count) {
        Log.d(VIEW_LOG_TAG, "Pager Indicator Setup");
        mSelectedResource = selectedResource;
        mUnselectedResource = unselectedResource;
        mClickable = false;
        mCount = count;

        if (getLayoutManager() == null) {
            Log.d(VIEW_LOG_TAG, "Layout Manager Null");
        } else {
            Log.d(VIEW_LOG_TAG, "Layout Manager instantiated");
        }

        if (getAdapter() == null) {
            Log.d(VIEW_LOG_TAG, "Adapter Null");
        } else {
            Log.d(VIEW_LOG_TAG, "Adapter instantiated");
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return false;
    }

    public void setSelectedPosition(int position) {
        if (position < mCount && position >= 0) {
            mSelectedPosition = position;
            mAdapter.notifyDataSetChanged();
        }
    }

    public class PagerIndicatorAdapter extends
            Adapter<PagerIndicatorAdapter.ViewHolder> {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public ImageView indicatorImage;

            public ViewHolder(View itemView) {
                super(itemView);
                indicatorImage = (ImageView) itemView
                        .findViewById(R.id.pager_indicator_image);
            }
        }

        @Override
        public int getItemCount() {
            return mCount;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position == mSelectedPosition) {
                holder.indicatorImage.setImageResource(mSelectedResource);
            } else {
                holder.indicatorImage.setImageResource(mUnselectedResource);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder vh = null;
            View v = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.pager_indicator_item, parent, false);
            vh = new ViewHolder(v);
            return vh;
        }
    }
}
