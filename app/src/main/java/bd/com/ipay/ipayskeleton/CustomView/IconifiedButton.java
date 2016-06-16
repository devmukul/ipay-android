package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.R;

public class IconifiedButton extends FrameLayout {

    private ImageView mImageView;
    private Button mButton;

    public IconifiedButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public IconifiedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public IconifiedButton(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_iconified_button, this, true);

        mImageView = (ImageView) findViewById(R.id.icon);
        mButton = (Button) findViewById(R.id.button);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconifiedButton, 0, 0);

            String text = a.getString(R.styleable.IconifiedButton_android_text);
            Drawable drawable = a.getDrawable(R.styleable.IconifiedButton_android_drawableLeft);

            mButton.setText(text);
            mImageView.setImageDrawable(drawable);

            a.recycle();
        }

        setBackgroundResource(R.drawable.background_iconified_button);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
