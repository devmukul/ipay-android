package bd.com.ipay.ipayskeleton.PaymentFragments.MakePaymentFragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;
import com.google.gson.Gson;

import java.math.BigDecimal;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.SecuritySettingsActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Api.GenericApi.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.GenericHttpResponse;
import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomPinCheckerWithInputDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.CustomView.ProfileImageView;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentRequest;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.MakePayment.PaymentResponse;
import bd.com.ipay.ipayskeleton.PaymentFragments.CommonFragments.ReviewFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ContactEngine;
import bd.com.ipay.ipayskeleton.Utilities.InputValidator;
import bd.com.ipay.ipayskeleton.Utilities.MyApplication;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class PaymentSucessFragment extends BaseFragment{

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

        mAmount = (BigDecimal) getActivity().getIntent().getSerializableExtra(Constants.AMOUNT);
        mReceiverBusinessMobileNumber = getActivity().getIntent().getStringExtra(Constants.RECEIVER_MOBILE_NUMBER);
        mDescription = getActivity().getIntent().getStringExtra(Constants.DESCRIPTION_TAG);
        mTransactionId = getActivity().getIntent().getStringExtra(Constants.REFERENCE_NUMBER);

        if (getArguments() != null) {
            mReceiverBusinessName = getArguments().getString(Constants.NAME);
            mPhotoUri = getArguments().getString(Constants.PHOTO_URI);

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
        final Button doneButton = findViewById(R.id.done_button);

        if (!TextUtils.isEmpty(mPhotoUri)) {
            businessProfileImageView.setProfilePicture(mPhotoUri, false);
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

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().finish();
            }
        });
    }

    public <T extends View> T findViewById(@IdRes int id) {
        //noinspection unchecked,ConstantConditions
        return (T) getView().findViewById(id);
    }
}
