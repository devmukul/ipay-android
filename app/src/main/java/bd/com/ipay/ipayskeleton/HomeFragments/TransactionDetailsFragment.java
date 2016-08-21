package bd.com.ipay.ipayskeleton.HomeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.MMModule.TransactionHistory.TransactionHistoryClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionDetailsFragment extends Fragment {

    private TransactionHistoryClass transactionHistory;

    private TextView descriptionTextView;
    private TextView timeTextView;
    private TextView amountTextView;
    private TextView feeTextView;
    private TextView transactionIDTextView;
    private TextView netAmountTextView;
    private TextView balanceTextView;
    private TextView purposeTextView;
    private TextView statusTextView;
    private TextView mobileNumberTextView;
    private LinearLayout purposeLayout;
    private ProfileImageView mProfileImageView;
    private ImageView otherImageView;
    private TextView mMobileNumberView;
    private TextView mNameView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        transactionHistory = getArguments().getParcelable(Constants.TRANSACTION_DETAILS);

        descriptionTextView = (TextView) v.findViewById(R.id.description);
        timeTextView = (TextView) v.findViewById(R.id.time);
        amountTextView = (TextView) v.findViewById(R.id.amount);
        feeTextView = (TextView) v.findViewById(R.id.fee);
        transactionIDTextView = (TextView) v.findViewById(R.id.transaction_id);
        netAmountTextView = (TextView) v.findViewById(R.id.netAmount);
        balanceTextView = (TextView) v.findViewById(R.id.balance);
        purposeTextView = (TextView) v.findViewById(R.id.purpose);
        statusTextView = (TextView) v.findViewById(R.id.status);
        mobileNumberTextView = (TextView) v.findViewById(R.id.your_number);
        purposeLayout = (LinearLayout) v.findViewById(R.id.purpose_layout);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        otherImageView = (ImageView) v.findViewById(R.id.other_image);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);

        String mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        descriptionTextView.setText(transactionHistory.getDescription(mMobileNumber));
        timeTextView.setText(Utilities.getDateFormat(transactionHistory.getResponseTime()));
        amountTextView.setText(Utilities.formatTaka(transactionHistory.getAmount()));
        feeTextView.setText(Utilities.formatTaka(transactionHistory.getFee()));
        transactionIDTextView.setText(transactionHistory.getTransactionID());
        netAmountTextView.setText(Utilities.formatTaka(transactionHistory.getNetAmount()));
        balanceTextView.setText(Utilities.formatTaka(transactionHistory.getBalance()));
        mobileNumberTextView.setText(ProfileInfoCacheManager.getMobileNumber());

        int serviceId = transactionHistory.getServiceID();
        String purpose = transactionHistory.getPurpose();
        String bankName = transactionHistory.getAdditionalInfo().getBankName();
        String bankAccountNumber = transactionHistory.getAdditionalInfo().getBankAccountNumber();
        int bankIcon = transactionHistory.getAdditionalInfo().getBankIcon(getContext());
        String bankCode = transactionHistory.getAdditionalInfo().getBankCode();

        final String receiver = transactionHistory.getReceiver();
        final String otherProfilePicture = transactionHistory.getAdditionalInfo().getUserProfilePic();
        final String otherMobileNumber = transactionHistory.getAdditionalInfo().getUserMobileNumber();
        final String otherName = transactionHistory.getAdditionalInfo().getUserName();

        if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK || serviceId == Constants.TRANSACTION_HISTORY_TOP_UP) {
            purposeLayout.setVisibility(View.GONE);
        } else if (purpose != null && purpose.length() > 0) {
            purposeTextView.setText(purpose);
        } else {
            purposeLayout.setVisibility(View.GONE);
        }

        if (serviceId == Constants.TRANSACTION_HISTORY_ADD_MONEY) {
            mNameView.setText(bankName);
            mMobileNumberView.setText(bankAccountNumber);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            if (bankCode != null) otherImageView.setImageResource(bankIcon);
            else otherImageView.setImageResource(R.drawable.ic_tran_add);

        } else if (serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY) {
            mNameView.setText(bankName);
            mMobileNumberView.setText(bankAccountNumber);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            if (bankCode != null) otherImageView.setImageResource(bankIcon);
            else otherImageView.setImageResource(R.drawable.ic_tran_withdraw);

        }  else if (serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK) {
            mNameView.setText(bankName);
            mMobileNumberView.setText(bankAccountNumber);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            if (bankCode != null) otherImageView.setImageResource(bankIcon);
            else otherImageView.setImageResource(R.drawable.ic_tran_withdraw);

        } else if (serviceId == Constants.TRANSACTION_HISTORY_OPENING_BALANCE) {
            mNameView.setText(R.string.opening_balance_to);
            mMobileNumberView.setText(mMobileNumber);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(R.drawable.ic_openingbalance);

        } else if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP) {
            mNameView.setText(R.string.recharge_to);
            mMobileNumberView.setText(receiver);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(R.drawable.ic_top);

        } else if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK) {
            mNameView.setText(R.string.topup_rollback);
            mMobileNumberView.setText(receiver);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(R.drawable.ic_top);
        } else {
            if (otherName != null) {
                mNameView.setText(otherName);
            }

            mMobileNumberView.setText(otherMobileNumber);
            otherImageView.setVisibility(View.GONE);
            mProfileImageView.setVisibility(View.VISIBLE);
            mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + otherProfilePicture, false);
        }

        final Integer statusCode = transactionHistory.getStatusCode();

        if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
            statusTextView.setText(getString(R.string.transaction_successful));
            statusTextView.setTextColor(getResources().getColor(R.color.bottle_green));
        } else if (statusCode == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
            statusTextView.setText(getString(R.string.in_progress));
            statusTextView.setTextColor(getResources().getColor(R.color.colorAmber));
        } else {
            if (serviceId != Constants.TRANSACTION_HISTORY_TOP_UP && serviceId != Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY && serviceId != Constants.TRANSACTION_HISTORY_ADD_MONEY) {
                balanceTextView.setText(getString(R.string.not_applicable));
            }
            statusTextView.setText(getString(R.string.transaction_failed));
            statusTextView.setTextColor(getResources().getColor(R.color.background_red));
        }

        return v;
    }
}
