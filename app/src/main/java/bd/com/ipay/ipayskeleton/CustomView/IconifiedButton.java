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
    private Button mEditText;

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
        View v = inflater.inflate(R.layout.view_iconified_button, this, true);

        mImageView = (ImageView) findViewById(R.id.icon);
        mEditText = (Button) findViewById(R.id.button);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconifiedButton, 0, 0);

            String text = a.getString(R.styleable.IconifiedButton_text);
            Drawable drawable = a.getDrawable(R.styleable.IconifiedButton_iconLeft);

            mEditText.setText(text);
            mImageView.setImageDrawable(drawable);

            a.recycle();
        }

        setBackgroundResource(R.drawable.background_iconified_button);

//        v.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d("Event", event.getAction() + "");
//
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        v.setBackgroundResource(R.drawable.background_iconified_button_pressed);
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        v.setBackgroundResource(R.drawable.background_iconified_button_normal);
//                        return true;
//                }
//                return false;
//            }
//        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
