package bd.com.ipay.ipayskeleton.Customview;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import bd.com.ipay.ipayskeleton.R;

public class CustomSwipeRefreshLayout extends SwipeRefreshLayout implements AppBarLayout.OnOffsetChangedListener {
    private AppBarLayout appBarLayout;

    public CustomSwipeRefreshLayout(Context context) {
        super(context);
    }

    public CustomSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getContext() instanceof Activity) {
            appBarLayout = (AppBarLayout) ((Activity) getContext()).findViewById(R.id.appbar);
            appBarLayout.addOnOffsetChangedListener(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        appBarLayout.removeOnOffsetChangedListener(this);
        appBarLayout = null;
        super.onDetachedFromWindow();
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        this.setEnabled(i == 0);
    }
}