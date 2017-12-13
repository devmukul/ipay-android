package bd.com.ipay.ipayskeleton.CustomView;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.Model.Service.IpayService;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;

public class ServiceSelectorView extends AbstractSelectorView<IpayService> {

    private List<String> itemTitle;

    public ServiceSelectorView(Context context) {
        this(context, null);
    }

    public ServiceSelectorView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ServiceSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ServiceSelectorView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void setItems(List<? super IpayService> itemList) {
        this.selectableOptionList = itemList;
        this.itemsIcon = new int[itemList.size()];
        this.itemTitle = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            IpayService ipayService = (IpayService) itemList.get(i);
            itemTitle.add(ipayService.getServiceTitle());
            itemsIcon[i] = ipayService.getServiceIconResId();
        }
    }

    @Override
    @Deprecated
    public void setItems(List<? super IpayService> itemList, @Nullable int[] iconsArray) {
        this.setItems(itemList);
        this.itemsIcon = iconsArray;
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
        CustomSelectorDialogWithIcon customSelectorDialogWithIcon = new CustomSelectorDialogWithIcon(getContext(), mTitle, itemTitle, itemsIcon);
        customSelectorDialogWithIcon.setOnResourceSelectedListener(new CustomSelectorDialogWithIcon.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                resourceSelectionAction(id);
            }
        });
        customSelectorDialogWithIcon.show();
    }

    @Override
    protected void resourceSelectionAction(int id) {
        setError(null);
        String name = itemTitle.get(id);
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
