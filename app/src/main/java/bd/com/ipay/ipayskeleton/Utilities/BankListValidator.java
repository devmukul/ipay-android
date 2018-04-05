package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Bank.BankAccountList;
import bd.com.ipay.ipayskeleton.R;

/**
 * Validate if the user has added any verified banks or added any banks at all.
 * Depending on that, show proper dialog to the user.
 */
public class BankListValidator {
    private final List<BankAccountList> mListUserBankClasses;

    public BankListValidator(List<BankAccountList> mListUserBankClasses) {
        this.mListUserBankClasses = mListUserBankClasses;
    }

    public boolean isBankAdded() {
        return mListUserBankClasses != null && !mListUserBankClasses.isEmpty();
    }

    public boolean isVerifiedBankAdded() {
        if (mListUserBankClasses == null)
            return false;

        for (BankAccountList userBankClass : mListUserBankClasses) {
            if (userBankClass.getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED))
                return true;
        }

        return false;
    }

    public void showAddBankDialog(final Activity activity) {
        showAddBankDialog(activity, true);
    }

    public void showAddBankDialog(final Activity activity, final boolean shouldDestroyActivityOnCancel) {
        showDialog(activity, R.string.add_bank_prompt, R.string.add_bank, R.string.cancel, shouldDestroyActivityOnCancel);
    }

    public void showVerifyBankDialog(final Activity activity) {
        showVerifyBankDialog(activity, true);
    }

    public void showVerifyBankDialog(final Activity activity, final boolean shouldDestroyActivityOnCancel) {
        showDialog(activity, R.string.verify_bank_prompt, R.string.verify_bank, R.string.cancel, shouldDestroyActivityOnCancel);
    }

    private void showDialog(final Activity activity, @StringRes int contentRes, @StringRes int positiveRes, @StringRes int negativeRes, final boolean shouldDestroyActivityOnCancel) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(activity);
        dialog
                .content(contentRes)
                .cancelable(false)
                .positiveText(positiveRes)
                .negativeText(negativeRes)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.onBackPressed();
                        Intent intent = new Intent(activity, ProfileActivity.class);
                        intent.putExtra(Constants.TARGET_FRAGMENT, Constants.VERIFY_BANK);
                        activity.startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (shouldDestroyActivityOnCancel) activity.onBackPressed();
                    }
                });

        dialog.show();
    }
}
