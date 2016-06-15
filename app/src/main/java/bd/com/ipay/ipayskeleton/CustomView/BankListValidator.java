package bd.com.ipay.ipayskeleton.CustomView;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.ProfileActivity;
import bd.com.ipay.ipayskeleton.Model.MMModule.Bank.UserBankClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.ProfileCompletion.ProfileCompletionPropertyConstants;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

/**
 * Validate if the user has added any verified banks or added any banks at all.
 * Depending on that, show proper dialog to the user.
 */
public class BankListValidator {
    private List<UserBankClass> mListUserBankClasses;

    public BankListValidator(List<UserBankClass> mListUserBankClasses) {
        this.mListUserBankClasses = mListUserBankClasses;
    }

    public boolean isBankAdded() {
        if (mListUserBankClasses == null)
            return false;
        else if (mListUserBankClasses.isEmpty())
            return false;
        else
            return true;
    }

    public boolean isVerifiedBankAdded() {
        if (mListUserBankClasses == null)
            return false;

        for (UserBankClass userBankClass : mListUserBankClasses) {
            if (userBankClass.getVerificationStatus().equals(Constants.BANK_ACCOUNT_STATUS_VERIFIED))
                return true;
        }

        return false;
    }

    public void showAddBankDialog(final Activity activity) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(activity);
        dialog
            .content(R.string.add_bank_prompt)
            .cancelable(false)
            .positiveText(R.string.add_bank)
            .negativeText(R.string.cancel)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    activity.onBackPressed();
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    intent.putExtra(Constants.TARGET_FRAGMENT, ProfileCompletionPropertyConstants.ADD_BANK);
                    activity.startActivity(intent);
                }
            })
            .onNegative(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    activity.onBackPressed();
                }
            });

        dialog.show();
    }

    public void showVerifiedBankDialog(final Activity activity) {
        MaterialDialog.Builder dialog = new MaterialDialog.Builder(activity);
        dialog
                .content(R.string.verify_bank_prompt)
                .cancelable(false)
                .positiveText(R.string.verify_bank)
                .negativeText(R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.onBackPressed();
                        Intent intent = new Intent(activity, ProfileActivity.class);
                        intent.putExtra(Constants.TARGET_FRAGMENT, ProfileCompletionPropertyConstants.VERIFY_BANK);
                        activity.startActivity(intent);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        activity.onBackPressed();
                    }
                });

        dialog.show();
    }
}
