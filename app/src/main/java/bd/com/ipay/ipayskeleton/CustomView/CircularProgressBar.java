package bd.com.ipay.ipayskeleton.CustomView;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;

public class CircularProgressBar extends FrameLayout {
    private Context context;
    private int duration = 3000;

    private ProgressBar mProgressBar;
    private TextView mPercentageView;
    private int progressStatus;
    private Handler handler = new Handler();

    public CircularProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CircularProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CircularProgressBar(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;

        View v = inflate(context, R.layout.layout_animated_progress_bar, null);

        mProgressBar = (ProgressBar) v.findViewById(R.id.circular_progress_bar);
        mPercentageView = (TextView) v.findViewById(R.id.text_view_percentage);
        addView(v);
    }

    public void startAnimation(final int finish) {
        ObjectAnimator anim = ObjectAnimator.ofInt(mProgressBar, "progress", 0, finish);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
        progressStatus = 0;

        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < finish) {
                    progressStatus += 1;
                    handler.post(new Runnable() {
                        public void run() {
                            mProgressBar.setProgress(progressStatus);
                            mPercentageView.setText(progressStatus+"");
                        }
                    });

                    try {
                        Thread.sleep(duration / finish);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
