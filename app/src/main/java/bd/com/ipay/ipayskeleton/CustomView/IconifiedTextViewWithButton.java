package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;


public class IconifiedTextViewWithButton extends FrameLayout {

    private ImageView mImageView;
    private TextView mtextView;

    public IconifiedTextViewWithButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public IconifiedTextViewWithButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public IconifiedTextViewWithButton(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_iconified_textview_with_button, this, true);

        mImageView = (ImageView) findViewById(R.id.icon);
        mtextView = (TextView) findViewById(R.id.textView);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconifiedTextViewWithButton, 0, 0);

            String text = a.getString(R.styleable.IconifiedTextViewWithButton_android_text);
            Drawable drawable = a.getDrawable(R.styleable.IconifiedTextViewWithButton_android_drawableLeft);

            mtextView.setText(text);
            mImageView.setImageDrawable(drawable);

            a.recycle();
        }

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
