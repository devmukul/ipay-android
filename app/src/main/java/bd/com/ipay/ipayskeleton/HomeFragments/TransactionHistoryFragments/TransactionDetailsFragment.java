package bd.com.ipay.ipayskeleton.HomeFragments.TransactionHistoryFragments;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Api.ContactApi.AddContactAsyncTask;
import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.TransactionHistory.TransactionHistory;
import bd.com.ipay.ipayskeleton.Model.Contact.AddContactRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.ContactSearchHelper;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TransactionDetailsFragment extends BaseFragment {

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
    private TextView mobileNumberTextView;
    private ProfileImageView mProfileImageView;
    private ImageView otherImageView;
    private TextView mMobileNumberView;
    private TextView mNameView;
    private Button mAddInContactsButton;
    private String otherPartyNumber;
    private String otherPartyName;
    private String purpose;
    private int serviceId;
    private Integer statusCode;
    private String status;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_transaction_details, container, false);

        transactionHistory = getArguments().getParcelable(Constants.TRANSACTION_DETAILS);
        serviceId = transactionHistory.getServiceID();
        purpose = transactionHistory.getPurpose();

        otherPartyNumber = transactionHistory.getAdditionalInfo().getNumber();
        otherPartyName = transactionHistory.getAdditionalInfo().getName();

        statusCode = transactionHistory.getStatusCode();
        status = transactionHistory.getStatus();

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

        mProfileImageView = (ProfileImageView) v.findViewById(R.id.profile_picture);
        otherImageView = (ImageView) v.findViewById(R.id.other_image);
        mNameView = (TextView) v.findViewById(R.id.textview_name);
        mMobileNumberView = (TextView) v.findViewById(R.id.textview_mobile_number);
        mAddInContactsButton = (Button) v.findViewById(R.id.add_in_contacts);

        if (transactionHistory.getDescription() != null)
            descriptionTextView.setText(transactionHistory.getDescription());
        timeTextView.setText(Utilities.formatDateWithTime(transactionHistory.getInsertTime()));
        amountTextView.setText(Utilities.formatTaka(transactionHistory.getAmount()));
        feeTextView.setText(Utilities.formatTaka(transactionHistory.getFee()));
        transactionIDTextView.setText(transactionHistory.getTransactionID());
        netAmountTextView.setText(Utilities.formatTaka(transactionHistory.getNetAmount()));
        if (transactionHistory.getAccountBalance() != null)
            balanceTextView.setText(Utilities.formatTaka(transactionHistory.getAccountBalance()));
        mobileNumberTextView.setText(ProfileInfoCacheManager.getMobileNumber());


        if (serviceId == Constants.TRANSACTION_HISTORY_SEND_MONEY || serviceId == Constants.TRANSACTION_HISTORY_REQUEST_MONEY) {
            if (!new ContactSearchHelper(getActivity()).searchMobileNumber(transactionHistory.getAdditionalInfo().getNumber())) {
                mAddInContactsButton.setVisibility(View.VISIBLE);
            }
        }

        mAddInContactsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            @ValidateAccess(ServiceIdConstants.ADD_CONTACTS)
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.are_you_sure)
                        .setMessage(getString(R.string.confirmation_add_to_contacts))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mAddInContactsButton.setVisibility(View.GONE);

                                addContact(transactionHistory.getAdditionalInfo().getName(),
                                        transactionHistory.getAdditionalInfo().getNumber(), null);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null);

                dialog.show();
            }
        });

        if (transactionHistory.getAdditionalInfo().getType().equalsIgnoreCase(Constants.TRANSACTION_TYPE_USER)) {
            String imageUrl = transactionHistory.getAdditionalInfo().getUserProfilePic();
            otherImageView.setVisibility(View.GONE);
            mProfileImageView.setVisibility(View.VISIBLE);
            mProfileImageView.setProfilePicture(Constants.BASE_URL_FTP_SERVER + imageUrl, false);
        } else {
            int iconId = transactionHistory.getAdditionalInfo().getImageWithType(getContext());
            mProfileImageView.setVisibility(View.GONE);
            otherImageView.setVisibility(View.VISIBLE);
            otherImageView.setImageResource(iconId);
        }

        mNameView.setText(otherPartyName);
        mMobileNumberView.setText(otherPartyNumber);
        purposeTextView.setText(purpose);
        statusTextView.setText(status);

        if (statusCode == Constants.HTTP_RESPONSE_STATUS_OK) {
            statusTextView.setTextColor(getResources().getColor(R.color.bottle_green));
        } else if (statusCode == Constants.HTTP_RESPONSE_STATUS_PROCESSING) {
            statusTextView.setTextColor(getResources().getColor(R.color.colorAmber));
        } else {
            statusTextView.setTextColor(getResources().getColor(R.color.background_red));
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_transaction_details));
    }

    @ValidateAccess
    private void addContact(String name, String phoneNumber, String relationship) {
        AddContactRequestBuilder addContactRequestBuilder = new
                AddContactRequestBuilder(name, phoneNumber, relationship);

        new AddContactAsyncTask(Constants.COMMAND_ADD_CONTACTS,
                addContactRequestBuilder.generateUri(), addContactRequestBuilder.getAddContactRequest(),
                getActivity()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
