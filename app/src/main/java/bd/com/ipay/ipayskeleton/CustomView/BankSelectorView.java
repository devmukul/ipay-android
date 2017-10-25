package bd.com.ipay.ipayskeleton.CustomView;

import android.animation.LayoutTransition;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomSelectorDialogWithIcon;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BankListValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class BankSelectorView extends LinearLayout implements View.OnClickListener {

    private View mChildView;

    private TextView mHintTextView;
    private ImageView mBankIconImageView;
    private TextView mBankNameTextView;
    private TextView mBankBranchTextView;
    private TextView mBankAccountTextView;

    private List<UserBankClass> mListUserBankClasses;

    private String mSelectorDialogTitle;
    private String mHint;

    private int mSelectedItemPosition = -1;

    private BankListValidator mBankListValidator = new BankListValidator(null);
    protected boolean mIsSelectable;

    private List<String> mUserBankList = new ArrayList<>();
    private int[] mBankIconArray = {};

    public BankSelectorView(@NonNull Context context) {
        this(context, null);
    }

    public BankSelectorView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BankSelectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BankSelectorView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(attrs, defStyleAttr, defStyleRes);
    }

    protected void initView(AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this.setOrientation(VERTICAL);
        this.setGravity(Gravity.CENTER);
        LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.disableTransitionType(LayoutTransition.DISAPPEARING);
        setLayoutTransition(layoutTransition);
        mChildView = LayoutInflater.from(getContext()).inflate(R.layout.layout_bank_selector_view, this, true);

        mBankNameTextView = findViewByIdAutoCast(R.id.bank_name_text_view);
        mHintTextView = findViewByIdAutoCast(R.id.hint_text_view);
        mBankIconImageView = findViewByIdAutoCast(R.id.bank_icon_image_view);
        mBankBranchTextView = findViewByIdAutoCast(R.id.bank_branch_text_view);
        mBankAccountTextView = findViewByIdAutoCast(R.id.bank_account_number_text_view);

        setupCustomAttributes(attrs, defStyleAttr, defStyleRes);
        setOnClickListener(this);
        setSelectable(true);
    }

    public void setSelectable(boolean isSelectable) {
        this.mIsSelectable = isSelectable;
    }

    public boolean isSelectable() {
        return mIsSelectable;
    }


    public void setItems(List<UserBankClass> listUserBankClasses) {
        mBankListValidator = new BankListValidator(listUserBankClasses);
        if (isBankAdded() && isVerifiedBankAdded()) {
            this.mListUserBankClasses = listUserBankClasses;
            mUserBankList.clear();
            mBankIconArray = new int[this.mListUserBankClasses.size()];
            for (UserBankClass userBankClass : this.mListUserBankClasses) {
                mBankIconArray[mUserBankList.size()] = userBankClass.getBankIcon(getContext());
                mUserBankList.add(userBankClass.getBankName() + "\n" + userBankClass.getBranchName() + "\n" + userBankClass.getAccountNumber());
            }
            if (this.mListUserBankClasses.size() == 1) {
                mSelectedItemPosition = 0;
                showBankDetails();
            }
        }
    }

    public int getSelectedItemPosition() {
        return mSelectedItemPosition;
    }

    public boolean isVerifiedBankAdded() {
        return mBankListValidator.isVerifiedBankAdded();
    }

    public boolean isBankAdded() {
        return mBankListValidator.isBankAdded();
    }


    public CharSequence getHint() {
        return mHint;
    }

    public void setHint(CharSequence hint) {
        this.mHint = (String) hint;
        mHintTextView.setHint(this.mHint);
        mBankNameTextView.setHint(this.mHint);
    }

    public void setHint(@StringRes int resId) {
        this.mHint = getContext().getString(resId);
        mHintTextView.setHint(this.mHint);
        mBankNameTextView.setHint(this.mHint);
    }

    public CharSequence getError() {
        return this.mBankNameTextView.getError();
    }

    public void setError(CharSequence error) {
        this.mBankNameTextView.setError(error);
        this.mBankNameTextView.requestFocus();
    }

    public void setError(@StringRes int resId) {
        this.mBankNameTextView.setError(getContext().getString(resId));
        this.mBankNameTextView.requestFocus();
    }

    public void setError(CharSequence error, Drawable icon) {
        this.mBankNameTextView.setError(error, icon);
        this.mBankNameTextView.requestFocus();
    }

    public CharSequence getSelectorDialogTitle() {
        return mSelectorDialogTitle;
    }

    public void setSelectorDialogTitle(String title) {
        this.mSelectorDialogTitle = title;
    }

    private <T extends View> T findViewByIdAutoCast(@IdRes int viewId) {
        return (T) mChildView.findViewById(viewId);
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
                setHint(hint);
            }
            if (selectorDialogTitle != null) {
                setSelectorDialogTitle(selectorDialogTitle);
            }
        } finally {
            typedArray.recycle();
        }
    }

    @Override
    @ValidateAccess(ServiceIdConstants.SEE_BANK_ACCOUNTS)
    public void onClick(View v) {
        if (!isSelectable()) {
            return;
        }
        if (!mBankListValidator.isBankAdded()) {
            if (getContext() instanceof AppCompatActivity)
                mBankListValidator.showAddBankDialog(((AppCompatActivity) getContext()), false);
        } else if (!mBankListValidator.isVerifiedBankAdded()) {
            if (getContext() instanceof AppCompatActivity)
                mBankListValidator.showVerifyBankDialog(((AppCompatActivity) getContext()), false);
        } else {
            CustomSelectorDialogWithIcon bankSelectorDialogWithIcon = new CustomSelectorDialogWithIcon(getContext(), mSelectorDialogTitle, mUserBankList, mBankIconArray);
            bankSelectorDialogWithIcon.setOnResourceSelectedListener(new CustomSelectorDialogWithIcon.OnResourceSelectedListener() {
                @Override
                public void onResourceSelected(int id, String name) {
                    mSelectedItemPosition = id;
                    showBankDetails();

                }
            });
            bankSelectorDialogWithIcon.show();
        }
    }

    private void showBankDetails() {
        mBankNameTextView.setError(null);
        mBankBranchTextView.setVisibility(View.VISIBLE);
        mBankAccountTextView.setVisibility(View.VISIBLE);
        mHintTextView.setVisibility(View.VISIBLE);
        mBankIconImageView.setVisibility(View.VISIBLE);

        mBankIconImageView.setImageResource(mListUserBankClasses.get(mSelectedItemPosition).getBankIcon(getContext()));
        mBankNameTextView.setText(mListUserBankClasses.get(mSelectedItemPosition).getBankName());
        mBankBranchTextView.setText(mListUserBankClasses.get(mSelectedItemPosition).getBranchName());
        mBankAccountTextView.setText(mListUserBankClasses.get(mSelectedItemPosition).getAccountNumber());
    }
}
