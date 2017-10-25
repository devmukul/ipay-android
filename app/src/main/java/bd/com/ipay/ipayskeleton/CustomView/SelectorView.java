package bd.com.ipay.ipayskeleton.CustomView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;

/**
 * Selector View is to mimic the view type for select a single item.
 * On click of this view, It will show a dialog for selectable items. It can return the selected item by implementing the {@link SelectorView.OnItemSelectListener}
 * We can also Add Access Validation to an item by implementing the {@link SelectorView.OnItemAccessValidation}.
 * <p>
 * TODO: Currently, most of the layout has this type view. We should replace those view with this to reduce the code duplication.
 */
public class SelectorView extends AbstractSelectorView<String> {

    public SelectorView(Context context) {
        this(context, null);
    }

    public SelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onClick(View v) {
        if (!isSelectable()) {
            return;
        }
        if (getProgressBarVisibility() == VISIBLE) {
            MaterialDialog materialDialog = new MaterialDialog.Builder(getContext()).content("Item isn\'t loaded yet. try again later.").negativeText("Dismiss").build();
            materialDialog.show();
            return;
        }
        if (itemsIcon == null) {
            CustomSelectorDialog customSelectorDialog = new CustomSelectorDialog(getContext(), mTitle, (List<String>) selectableOptionList);
            customSelectorDialog.setOnResourceSelectedListener(new CustomSelectorDialog.OnResourceSelectedListener() {
                @Override
                public void onResourceSelected(int id, String name) {
                    resourceSelectionAction(id);

                }
            });
            customSelectorDialog.show();
        } else {
            CustomSelectorDialogWithIcon customSelectorDialogWithIcon = new CustomSelectorDialogWithIcon(getContext(), mTitle, (List<String>) selectableOptionList, itemsIcon);
            customSelectorDialogWithIcon.setOnResourceSelectedListener(new CustomSelectorDialogWithIcon.OnResourceSelectedListener() {
                @Override
                public void onResourceSelected(int id, String name) {
                    resourceSelectionAction(id);
                }
            });
            customSelectorDialogWithIcon.show();
        }
    }

    @Override
    protected void resourceSelectionAction(int id) {
        setError(null);
        String name = (String) selectableOptionList.get(id);
        if (mOnItemAccessValidation != null && !mOnItemAccessValidation.hasItemAccessAbility(id, name)) {
            DialogUtils.showServiceNotAllowedDialog(getContext());
            return;
        }

        mSelectedItemEditText.setText(name);
        mSelectedItemPosition = id;
        if (mOnItemSelectListener != null)
            mOnItemSelectListener.onItemSelected(mSelectedItemPosition);
    }
}
