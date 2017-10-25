package bd.com.ipay.ipayskeleton.CustomView;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.TextInputLayout;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.R;

public abstract class AbstractSelectorView<T> extends LinearLayout implements View.OnClickListener {

    protected int mSelectedItemPosition = -1;
    protected View mChildView;
    protected EditText mSelectedItemEditText;
    protected TextView mSelectedItemErrorTextView;
    protected TextInputLayout mSelectedItemTextInputLayout;
    protected ProgressBar mProgressBar;

    protected List<? super T> selectableOptionList = new ArrayList<>();
    protected int[] itemsIcon = {};
    protected String mTitle;
    protected OnItemSelectListener mOnItemSelectListener;

    protected OnItemAccessValidation mOnItemAccessValidation;
    protected boolean mIsSelectable;

    public AbstractSelectorView(Context context) {
        this(context, null);
    }

    public AbstractSelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AbstractSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AbstractSelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr, defStyleRes);
    }

    protected final void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);

        mChildView = LayoutInflater.from(getContext()).inflate(R.layout.layout_selector_view, this, true);

        mSelectedItemEditText = findViewByIdAutoCast(R.id.selected_item_edit_text);
        mSelectedItemErrorTextView = findViewByIdAutoCast(R.id.selected_item_error_text_view);
        mSelectedItemTextInputLayout = findViewByIdAutoCast(R.id.selected_item_text_input_layout);
        mProgressBar = findViewByIdAutoCast(R.id.progress_bar);

        mSelectedItemEditText.setInputType(InputType.TYPE_NULL);
        setProgressBarVisibility(GONE);
        setupCustomAttributes(attrs, defStyleAttr, defStyleRes);
        setClickable(true);
        setOnClickListener(this);
        setSelectable(true);
    }

    public void setProgressBarVisibility(int visibility) {
        mProgressBar.setVisibility(visibility);
    }

    public int getProgressBarVisibility() {
        return mProgressBar.getVisibility();
    }

    public void setItems(List<? super T> itemList) {
        setItems(itemList, null);
    }

    public void setItems(List<? super T> itemList, @Nullable int[] iconsArray) {
        this.selectableOptionList = itemList;
        this.itemsIcon = iconsArray;
    }

    public void selectedItem(int selectedItemPosition) {
        if (selectableOptionList != null && !selectableOptionList.isEmpty()) {
            resourceSelectionAction(selectedItemPosition);
        }
    }

    public void removeSelection() {
        this.mSelectedItemPosition = -1;
        this.mSelectedItemEditText.setText("");
    }

    public void setSelectable(boolean isSelectable) {
        this.mIsSelectable = isSelectable;
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }

    public CharSequence getHint() {
        return mSelectedItemTextInputLayout.getHint();
    }

    public void setHint(@StringRes int resId) {
        mSelectedItemTextInputLayout.setHint(getContext().getString(resId));
    }

    public void setHint(CharSequence hint) {
        mSelectedItemTextInputLayout.setHint(hint);
    }

    public CharSequence getError() {
        return this.mSelectedItemEditText.getError();
    }

    public void setError(@StringRes int resId) {
        this.mSelectedItemErrorTextView.setError(getContext().getString(resId));
        this.mSelectedItemErrorTextView.requestFocus();
    }

    public void setError(CharSequence error) {
        this.mSelectedItemErrorTextView.setError(error);
        this.mSelectedItemErrorTextView.requestFocus();
    }

    public void setError(CharSequence error, Drawable icon) {
        this.mSelectedItemErrorTextView.setError(error, icon);
        this.mSelectedItemErrorTextView.requestFocus();
    }

    public OnItemSelectListener getOnItemSelectListener() {
        return mOnItemSelectListener;
    }

    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.mOnItemSelectListener = onItemSelectListener;
    }

    public OnItemAccessValidation getOnItemAccessValidation() {
        return mOnItemAccessValidation;
    }

    public void setOnItemAccessValidation(OnItemAccessValidation onItemAccessValidation) {
        this.mOnItemAccessValidation = onItemAccessValidation;
    }

    public CharSequence getSelectorDialogTitle() {
        return mTitle;
    }

    public void setSelectorDialogTitle(String title) {
        this.mTitle = title;
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    protected void setupCustomAttributes(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        if (attrs == null) {
            return;
        }
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.AbstractSelectorView, defStyleAttr, defStyleRes);
        try {
            final String hint = typedArray.getString(R.styleable.AbstractSelectorView_android_hint);
            final String selectorDialogTitle = typedArray.getString(R.styleable.AbstractSelectorView_selectorDialogTitle);
            if (hint != null) {
                this.mSelectedItemTextInputLayout.setHint(hint);
            }
            if (selectorDialogTitle != null) {
                setSelectorDialogTitle(selectorDialogTitle);
            }
        } finally {
            typedArray.recycle();
        }
    }

    private <T extends View> T findViewByIdAutoCast(@IdRes int viewId) {
        return (T) mChildView.findViewById(viewId);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    protected abstract void resourceSelectionAction(int selectedItemPosition);

    public interface OnItemSelectListener {
        boolean onItemSelected(int selectedItemPosition);
    }

    public interface OnItemAccessValidation {
        boolean hasItemAccessAbility(int id, String name);
    }
}
