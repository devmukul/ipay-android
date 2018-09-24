package bd.com.ipay.ipayskeleton.PaymentFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPayTransactionConfirmationFragment extends Fragment {

    private MandatoryBusinessRules mandatoryBusinessRules;
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private int transactionType;
    private String name;
    private BigDecimal amount;
    private String mobileNumber;
    private String profilePicture;

    private EditText mNoteEditText;
    private EditText mPinEditText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
            name = getArguments().getString(Constants.NAME);
            mobileNumber = getArguments().getString(Constants.MOBILE_NUMBER);
            profilePicture = getArguments().getString(Constants.PHOTO_URI);
            amount = (BigDecimal) getArguments().getSerializable(Constants.AMOUNT);
        }
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumIntegerDigits(2);
        mandatoryBusinessRules = BusinessRuleCacheManager.getBusinessRules(BusinessRuleCacheManager.getTag(transactionType));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ipay_transaction_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNoteEditText = view.findViewById(R.id.note_edit_text);
        mPinEditText = view.findViewById(R.id.pin_edit_text);

        final Toolbar toolbar = view.findViewById(R.id.toolbar);
        final TextView transactionDescriptionTextView = view.findViewById(R.id.transaction_description_text_view);
        final TextView nameTextView = view.findViewById(R.id.name_text_view);
        final View pinLayoutHolder = view.findViewById(R.id.pin_layout_holder);
        final RoundedImageView profileImageView = view.findViewById(R.id.profile_image_view);
        final Button transactionConfirmationButton = view.findViewById(R.id.transaction_confirmation_button);

        if (getActivity() instanceof AppCompatActivity) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setDisplayHomeAsUpEnabled(true);
            }
            getActivity().setTitle(R.string.empty_string);
        }

        if (mandatoryBusinessRules != null)
            pinLayoutHolder.setVisibility(mandatoryBusinessRules.IS_PIN_REQUIRED() ? View.VISIBLE : View.GONE);

        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                pinLayoutHolder.setVisibility(View.GONE);
                transactionDescriptionTextView.setText(Html.fromHtml(getString(R.string.request_money_confirmation_message, numberFormat.format(amount))));
                mNoteEditText.setHint(R.string.short_note_hint);
                transactionConfirmationButton.setText(R.string.request_money);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                transactionDescriptionTextView.setText(Html.fromHtml(getString(R.string.send_money_confirmation_message, numberFormat.format(amount))));
                mNoteEditText.setHint(R.string.short_note_optional_hint);
                transactionConfirmationButton.setText(R.string.send_money);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                transactionDescriptionTextView.setText(R.string.empty_string);
                mNoteEditText.setHint(R.string.empty_string);
                break;
        }
        nameTextView.setText(name);
        Glide.with(this)
                .load(profilePicture)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .transform(new CircleTransform(getContext()))
                .into(profileImageView);

        transactionConfirmationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pinLayoutHolder.getVisibility() == View.VISIBLE) {
                    Editable pin = mPinEditText.getText();
                    if (TextUtils.isEmpty(pin)) {
                        showErrorMessage(getString(R.string.please_enter_a_pin));
                        return;
                    } else if (pin.length() != 4) {
                        showErrorMessage(getString(R.string.minimum_pin_length_message));
                        return;
                    }
                }
                if (transactionType == IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY) {
                    Editable noteEditTextText = mNoteEditText.getText();
                    if (TextUtils.isEmpty(noteEditTextText)) {
                        showErrorMessage(getString(R.string.please_write_note));
                        return;
                    }
                }
                confirmTransaction();
            }
        });
//        Html.fromHtml(getString(R.string.send_money_confirmation_message, "HELLO"))
    }

    private void showErrorMessage(String errorMessage) {
        if (getActivity() != null && getView() != null) {
            Snackbar snackbar = Snackbar.make(getView(), errorMessage, Snackbar.LENGTH_LONG);
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
            ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
            layoutParams.height = getResources().getDimensionPixelSize(R.dimen.value50);
            snackbarView.setLayoutParams(layoutParams);
            TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(ActivityCompat.getColor(getActivity(), android.R.color.white));
            snackbar.show();
        }
    }

    private void confirmTransaction() {

    }
}
