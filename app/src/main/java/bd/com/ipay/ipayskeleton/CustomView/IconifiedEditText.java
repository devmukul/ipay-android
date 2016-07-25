package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import bd.com.ipay.ipayskeleton.R;

public class IconifiedEditText extends FrameLayout {

    private ImageView mImageView;
    private EditText mEditText;
    private View mDivider;

    private OnClickListener mOnClickListener;

    public IconifiedEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context, attrs);
    }

    public IconifiedEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public IconifiedEditText(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.view_iconified_edit_text, this, true);

        mImageView = (ImageView) v.findViewById(R.id.icon);
        mEditText = (EditText) v.findViewById(R.id.edit_text);
        mDivider = v.findViewById(R.id.divider);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.IconifiedEditText, 0, 0);

            Drawable drawable = a.getDrawable(R.styleable.IconifiedEditText_android_drawableLeft);
            mImageView.setImageDrawable(drawable);

            String text = a.getString(R.styleable.IconifiedEditText_android_text);
            String imeActionLabel = a.getString(R.styleable.IconifiedEditText_android_imeActionLabel);
            String hint = a.getString(R.styleable.IconifiedEditText_android_hint);
            int inputType = a.getInt(R.styleable.IconifiedEditText_android_inputType, EditorInfo.TYPE_CLASS_TEXT);
            int maxLength = a.getInt(R.styleable.IconifiedEditText_android_maxLength, -1);
            int maxLines = a.getInt(R.styleable.IconifiedEditText_android_maxLines, -1);
            int borderType = a.getInt(R.styleable.IconifiedEditText_borderType, 0x0);
            boolean enabled = a.getBoolean(R.styleable.IconifiedEditText_android_enabled, true);

            if (text != null)
                mEditText.setText(text);
            if (imeActionLabel != null)
                mEditText.setImeActionLabel(imeActionLabel, EditorInfo.IME_ACTION_DONE);
            if (hint != null)
                mEditText.setHint(hint);
            mEditText.setInputType(inputType);
            if (maxLength != -1) {
                mEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
            }
            if (maxLines != -1)
                mEditText.setMaxLines(maxLines);
            mEditText.setEnabled(enabled);
            // Corresponding values can be found in attr.xml
            switch (borderType) {
                case 0x0:
                    v.setBackgroundResource(R.drawable.background_no_round_white);
                    break;
                case 0x1:
                    v.setBackgroundResource(R.drawable.background_half_upper_round_white);
                    mEditText.requestFocus();
                    break;
                case 0x2:
                    v.setBackgroundResource(R.drawable.background_half_lower_round_white);
                    mDivider.setVisibility(View.GONE);
                    break;
                case 0x1|0x2:
                    v.setBackgroundResource(R.drawable.background_rounded_white);
                    mDivider.setVisibility(View.GONE);
                    break;
            }

            a.recycle();
        }

//        setBackgroundResource(R.drawable.background_half_upper_round_white);

    }

    public EditText getEditText() {
        return mEditText;
    }

    public Editable getText() {
        return mEditText.getText();
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        mEditText.addTextChangedListener(textWatcher);
    }

    public void setError(String error) {
        mEditText.setError(error);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        super.setOnClickListener(l);
        mOnClickListener = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mOnClickListener != null;
    }
}
