package bd.com.ipay.ipayskeleton.Utilities;


import android.content.Context;

import java.util.HashMap;

import bd.com.ipay.ipayskeleton.R;

public class TwoFactorAuthConstants {
    private static HashMap<String, String> mProgressDialogStringMap;
    public static final String WRONG_OTP = "wrong";

    public static HashMap<String, String> getProgressDialogStringMap(Context context) {
        mProgressDialogStringMap = new HashMap<>();
        mProgressDialogStringMap = new HashMap<>();
        mProgressDialogStringMap.put(Constants.COMMAND_PUT_TWO_FACTOR_AUTH_SETTINGS, context.getString(R.string.change_two_fa_settings));
        mProgressDialogStringMap.put(Constants.COMMAND_SEND_MONEY, context.getString(R.string.progress_dialog_text_sending_money));
        mProgressDialogStringMap.put(Constants.COMMAND_TOPUP_REQUEST, context.getString(R.string.dialog_requesting_top_up));
        mProgressDialogStringMap.put(Constants.COMMAND_ADD_MONEY, context.getString(R.string.progress_dialog_add_money_in_progress));
        mProgressDialogStringMap.put(Constants.COMMAND_WITHDRAW_MONEY, context.getString(R.string.progress_dialog_withdraw_money_in_progress));
        mProgressDialogStringMap.put(Constants.COMMAND_PAYMENT, context.getString(R.string.progress_dialog_text_payment));
        mProgressDialogStringMap.put(Constants.COMMAND_SET_PIN, context.getString(R.string.saving_pin));
        mProgressDialogStringMap.put(Constants.COMMAND_SEND_PAYMENT_REQUEST, context.getString(R.string.progress_dialog_sending_payment_request));
        mProgressDialogStringMap.put(Constants.COMMAND_ACCEPT_PAYMENT_REQUEST, context.getString(R.string.progress_dialog_accepted));
        mProgressDialogStringMap.put(Constants.COMMAND_ACCEPT_REQUESTS_MONEY, context.getString(R.string.accepting_send_money_request));
        return mProgressDialogStringMap;
    }

}
