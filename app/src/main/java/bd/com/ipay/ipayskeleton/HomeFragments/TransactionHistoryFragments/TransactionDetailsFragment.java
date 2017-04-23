package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionDetailsFragment extends Fragment {

    private TransactionHistory transactionHistory;

    private TextView descriptionTextView;
    private TextView timeTextView;
    private TextView amountTextView;
    private TextView feeTextView;
    private TextView transactionIDTextView;
    private TextView netAmountTextView;
    private TextView balanceTextView;
    private TextView purposeTextView;
    private TextView statusTextView;
    private TextView failureCauseTextView;
    private TextView mobileNumberTextView;
    private LinearLayout purposeLayout;
    private LinearLayout failureCauseLayout;
    private ProfileImageView mProfileImageView;
    private ImageView otherImageView;
    private TextView mMobileNumberView;
    private TextView mNameView;
    private Button mAddInContactsButton;


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
        failureCauseTextView = (TextView) v.findViewById(R.id.failure_cause);
        mobileNumberTextView = (TextView) v.findViewById(R.id.your_number);
        purposeLayout = (LinearLayout) v.findViewById(R.id.purpose_layout);
        failureCauseLayout = (LinearLayout) v.findViewById(R.id.failure_cause_layout);

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        otherImageView = (ImageView) v.findViewById(R.id.other_image);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAddInContactsButton = (Button) v.findViewById(R.id.add_in_contacts);

        String mMobileNumber = ProfileInfoCacheManager.getMobileNumber();
        if (transactionHistory.getDescription(mMobileNumber) != null)
            descriptionTextView.setText(transactionHistory.getDescription(mMobileNumber));
        timeTextView.setText(Utilities.formatDateWithTime(transactionHistory.getResponseTime()));
        amountTextView.setText(Utilities.formatTaka(transactionHistory.getAmount()));
        feeTextView.setText(Utilities.formatTaka(transactionHistory.getFee()));
        transactionIDTextView.setText(transactionHistory.getTransactionID());
        netAmountTextView.setText(Utilities.formatTaka(transactionHistory.getNetAmount()));
        if (transactionHistory.getBalance() != null)
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

        if (serviceId == Constants.TRANSACTION_HISTORY_SEND_MONEY || serviceId == Constants.TRANSACTION_HISTORY_REQUEST_MONEY) {
            if (!new ContactSearchHelper(getActivity()).searchMobileNumber(transactionHistory.getAdditionalInfo().getUserMobileNumber())) {
                mAddInContactsButton.setVisibility(View.VISIBLE);
            }
        }

        mAddInContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.are_you_sure)
                        .setMessage(getString(R.string.confirmation_add_to_contacts))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAddInContactsButton.setVisibility(View.GONE);
                                addContact(transactionHistory.getAdditionalInfo().getUserName(),
                                        transactionHistory.getAdditionalInfo().getUserMobileNumber(), null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);

                dialog.show();
            }
        });


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

        } else if (serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY
                || serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_REVERT
                || serviceId == Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY_ROLL_BACK) {
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
            otherImageView.setImageResource(R.drawable.ic_transaction_ipaylogo);

        } else if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP) {
            mNameView.setText(R.string.recharge_to);
            mMobileNumberView.setText(receiver);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            if (ContactEngine.isValidNumber(receiver)) {
                int mIcon = getOperatorIcon(receiver);
                otherImageView.setImageResource(mIcon);
            } else
                otherImageView.setImageResource(R.drawable.ic_top_up);
        } else if (serviceId == Constants.TRANSACTION_HISTORY_TOP_UP_ROLLBACK) {
            mNameView.setText(R.string.topup_rollback);
            mMobileNumberView.setText(receiver);
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            if (ContactEngine.isValidNumber(receiver)) {
                int mIcon = getOperatorIcon(receiver);
                otherImageView.setImageResource(mIcon);
            } else
                otherImageView.setImageResource(R.drawable.ic_top_up);
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
        final String status = transactionHistory.getStatus();

        if (statusCode != Constants.TRANSACTION_STATUS_ACCEPTED && statusCode != Constants.TRANSACTION_STATUS_PROCESSING) {
            final String status_description = transactionHistory.getStatusDescription();
            failureCauseTextView.setText(status_description);
            failureCauseTextView.setTextColor(getResources().getColor(R.color.background_red));
            failureCauseLayout.setVisibility(View.VISIBLE);
        } else
            failureCauseLayout.setVisibility(View.GONE);

        statusTextView.setText(status);

        if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
            statusTextView.setTextColor(getResources().getColor(R.color.bottle_green));
        } else if (statusCode == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
            statusTextView.setTextColor(getResources().getColor(R.color.colorAmber));
        } else {
            if (serviceId != Constants.TRANSACTION_HISTORY_TOP_UP
                    && serviceId != Constants.TRANSACTION_HISTORY_WITHDRAW_MONEY
                    && serviceId != Constants.TRANSACTION_HISTORY_ADD_MONEY) {
                balanceTextView.setText(getString(R.string.not_applicable));
            }
            statusTextView.setTextColor(getResources().getColor(R.color.background_red));
        }

        return v;
    }

    private void addContact(String name, String phoneNumber, String relationship) {
        AddContactRequestBuilder addContactRequestBuilder = new
                AddContactRequestBuilder(name, phoneNumber, relationship);

        new AddContactAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }


    private int getOperatorIcon(String phoneNumber) {
        phoneNumber = ContactEngine.trimPrefix(phoneNumber);

        final String[] OPERATOR_PREFIXES = getResources().getStringArray(R.array.operator_prefix);
        int[] operator_array = new int[]{
                R.drawable.ic_gp2,
                R.drawable.ic_gp2,
                R.drawable.ic_robi2,
                R.drawable.ic_airtel2,
                R.drawable.ic_banglalink2,
                R.drawable.ic_teletalk2,
        };

        for (int i = 0; i < OPERATOR_PREFIXES.length; i++) {
            if (phoneNumber.startsWith(OPERATOR_PREFIXES[i])) {
                return operator_array[i];
            }
        }
        return 0;
    }

}
