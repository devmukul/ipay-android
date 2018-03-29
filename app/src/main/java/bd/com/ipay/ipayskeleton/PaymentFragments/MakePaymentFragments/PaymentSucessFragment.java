package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.Tracker;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentRequest;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentSucessFragment extends BaseFragment {

    private HttpRequestPostAsyncTask mPaymentTask = null;

    private PaymentRequest mPaymentRequest;

    private ProgressDialog mProgressDialog;
    private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;

    private BigDecimal mAmount;
    private String mReceiverBusinessName;
    private String mReceiverBusinessMobileNumber;
    private String mPhotoUri;
    private String mDescription;
    private String mTransactionId;

    private Tracker mTracker;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverBusinessMobileNumber = getActivity().getIntent().getStringExtra(Constants.RECEIVER_MOBILE_NUMBER);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);


        if (getArguments() != null) {
            mReceiverBusinessName = getArguments().getString(Constants.NAME);
            mAmount = new BigDecimal(getArguments().getString(Constants.AMOUNT));
            mPhotoUri = getArguments().getString(Constants.PHOTO_URI);
            mTransactionId = getArguments().getString(Constants.TRANSACTION_ID);

        }

        mProgressDialog = new ProgressDialog(getActivity());

        mTracker = Utilities.getTracker(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_make_payment_review));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_success, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ProfileImageView businessProfileImageView = findViewById(R.id.business_profile_image_view);
        final TextView businessNameTextView = findViewById(R.id.business_name_text_view);
        final TextView businessMobileNumberTextView = findViewById(R.id.business_mobile_number_text_view);
        final TextView statusTextView = findViewById(R.id.status_text_view);
        final TextView successAmountTextView = findViewById(R.id.success_amount);
        final View transactionIdNumberViewHolder = findViewById(R.id.transaction_id_view_holder);
        final TextView transactionIdNumberTextView = findViewById(R.id.transaction_id_text_view);
        final View descriptionViewHolder = findViewById(R.id.description_view_holder);
        final TextView descriptionTextView = findViewById(R.id.description_text_view);
        final Button anotherPaymentButton = findViewById(R.id.another_payment_button);

        if (!TextUtils.isEmpty(mPhotoUri)) {
            businessProfileImageView.setBusinessProfilePicture(Constants.BASE_URL_FTP_SERVER + mPhotoUri, false);
        }
        if (TextUtils.isEmpty(mReceiverBusinessName)) {
            businessNameTextView.setVisibility(View.GONE);
        } else {
            businessNameTextView.setVisibility(View.VISIBLE);
            businessNameTextView.setText(mReceiverBusinessName);
        }
        businessMobileNumberTextView.setText(mReceiverBusinessMobileNumber);

        statusTextView.setText(getContext().getResources().getText(R.string.success));
        successAmountTextView.setText("You have paid " + Utilities.formatTaka(mAmount) + " to");


        if (TextUtils.isEmpty(mTransactionId)) {
            transactionIdNumberViewHolder.setVisibility(View.GONE);
        } else {
            transactionIdNumberViewHolder.setVisibility(View.VISIBLE);
            transactionIdNumberTextView.setText(mTransactionId);
        }

        if (TextUtils.isEmpty(mDescription)) {
            descriptionViewHolder.setVisibility(View.GONE);
        } else {
            descriptionViewHolder.setVisibility(View.VISIBLE);
            descriptionTextView.setText(mDescription);
        }

        anotherPaymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PinChecker makePaymentPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                    @Override
                    public void ifPinAdded() {
                        Intent intent = new Intent(getActivity(), PaymentActivity.class);
                        intent.putExtra(PaymentActivity.LAUNCH_NEW_REQUEST, true);
                        startActivity(intent);

                        getActivity().finish();
                    }
                });
                makePaymentPinChecker.execute();
            }
        });
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(id);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuInflater menuInflater = getActivity().getMenuInflater();
        menuInflater.inflate(R.menu.done, menu);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_payment:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
