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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;

/**
 * Selector View is to mimic the view type for select a single item.
 * On click of this view, It will show a dialog for selectable items. It can return the selected item by implementing the {@link SelectorView.OnItemSelectListener}
 * We can also Add Access Validation to an item by implementing the {@link SelectorView.OnItemAccessValidation}.
 * <p>
 * TODO: Currently, most of the layout has this type view. We should replace those view with this to reduce the code duplication.
 */
public class SelectorView extends LinearLayout implements View.OnClickListener {

    protected int mSelectedItemPosition = -1;
    private View mChildView;
    private EditText mSelectedItemEditText;
    private TextView mSelectedItemErrorTextView;
    private TextInputLayout mSelectedItemTextInputLayout;
    private List<String> selectableOptionList = new ArrayList<>();
    private int[] itemsIcon = {};
    private String mTitle;
    private OnItemSelectListener mOnItemSelectListener;

    private OnItemAccessValidation mOnItemAccessValidation;

    public SelectorView(Context context) {
        this(context, null);
    }

    public SelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs, defStyleAttr, 0);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr, defStyleRes);
    }

    protected void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);

        mChildView = LayoutInflater.from(getContext()).inflate(R.layout.layout_selector_view, this, true);

        mSelectedItemEditText = findViewByIdAutoCast(R.id.selected_item_edit_text);
        mSelectedItemErrorTextView = findViewByIdAutoCast(R.id.selected_item_error_text_view);
        mSelectedItemTextInputLayout = findViewByIdAutoCast(R.id.selected_item_text_input_layout);

        mSelectedItemEditText.setInputType(InputType.TYPE_NULL);
        setupCustomAttributes(attrs, defStyleAttr, defStyleRes);
        setClickable(true);
        setOnClickListener(this);
    }

    public void setItems(List<String> itemList) {
        setItems(itemList, null);
    }

    public void setItems(List<String> itemList, @Nullable int[] iconsArray) {
        this.selectableOptionList = itemList;
        this.itemsIcon = iconsArray;
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
        TypedArray typedArray = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.SelectorView, defStyleAttr, defStyleRes);
        try {
            final String hint = typedArray.getString(R.styleable.SelectorView_android_hint);
            final String selectorDialogTitle = typedArray.getString(R.styleable.SelectorView_selectorDialogTitle);
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

    @Override
    public void onClick(View v) {
        if (itemsIcon == null) {
            CustomSelectorDialog customSelectorDialog = new CustomSelectorDialog(getContext(), mTitle, selectableOptionList);
            customSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
                @Override
                public void onResourceSelected(int id, String name) {
                    resourceSelectionAction(id, name);

                }
            });
            customSelectorDialog.show();
        } else {
            CustomSelectorDialogWithIcon customSelectorDialogWithIcon = new CustomSelectorDialogWithIcon(getContext(), mTitle, selectableOptionList, itemsIcon);
            customSelectorDialogWithIcon.setOnResourceSelectedListener(new CustomSelectorDialogWithIcon.OnResourceSelectedListener() {
                @Override
                public void onResourceSelected(int id, String name) {
                    resourceSelectionAction(id, name);
                }
            });
            customSelectorDialogWithIcon.show();
        }
    }

    private void resourceSelectionAction(int id, String name) {
        setError(null);
        if (mOnItemAccessValidation != null && !mOnItemAccessValidation.hasItemAccessAbility(id, name)) {
            DialogUtils.showServiceNotAllowedDialog(getContext());
            return;
        }

        mSelectedItemEditText.setText(name);
        mSelectedItemPosition = id;
        if (mOnItemSelectListener != null)
            mOnItemSelectListener.onItemSelected(id, name);
    }

    public interface OnItemSelectListener {
        boolean onItemSelected(int id, String name);
    }

    public interface OnItemAccessValidation {
        boolean hasItemAccessAbility(int id, String name);
    }
}
