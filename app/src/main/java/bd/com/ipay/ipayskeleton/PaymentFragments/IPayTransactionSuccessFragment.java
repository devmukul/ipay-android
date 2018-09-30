package bd.com.ipay.ipayskeleton.PaymentFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class IPayTransactionSuccessFragment extends Fragment {
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private int transactionType;
    private String name;
    private BigDecimal amount;
    private String senderProfilePicture;
    private String receiverProfilePicture;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
            name = getArguments().getString(Constants.NAME);
            senderProfilePicture = getArguments().getString(Constants.SENDER_IMAGE_URL);
            receiverProfilePicture = getArguments().getString(Constants.RECEIVER_IMAGE_URL);
            amount = (BigDecimal) getArguments().getSerializable(Constants.AMOUNT);
        }
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumIntegerDigits(1);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ipay_transaction_success, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final TextView transactionSuccessMessageTextView = view.findViewById(R.id.transaction_success_message_text_view);
        final TextView nameTextView = view.findViewById(R.id.name_text_view);
        final RoundedImageView receiverProfilePictureImageUrl = view.findViewById(R.id.receiver_profile_picture_image_url);
        final RoundedImageView senderProfilePictureImageUrl = view.findViewById(R.id.sender_profile_picture_image_url);
        final TextView successDescriptionTextView = view.findViewById(R.id.success_description_text_view);
        final Button goToWalletButton = view.findViewById(R.id.go_to_wallet_button);
        final String amountValue = getString(R.string.balance_holder, numberFormat.format(amount));
        final Spannable spannableAmount;

        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                spannableAmount = new SpannableString(getString(R.string.send_money_success_message, amountValue));
                spannableAmount.setSpan(new StyleSpan(Typeface.BOLD), 18, 18 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableAmount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney)), 18, 18 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                transactionSuccessMessageTextView.setText(spannableAmount, TextView.BufferType.SPANNABLE);
                successDescriptionTextView.setText(R.string.send_money_success_description);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                spannableAmount = new SpannableString(getString(R.string.request_money_success_message, amountValue));
                spannableAmount.setSpan(new StyleSpan(Typeface.BOLD), 23, 23 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableAmount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney)), 23, 23 + amountValue.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                transactionSuccessMessageTextView.setText(spannableAmount, TextView.BufferType.SPANNABLE);
                successDescriptionTextView.setText(R.string.request_money_success_description);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                transactionSuccessMessageTextView.setText(R.string.empty_string);
                successDescriptionTextView.setText(R.string.empty_string);
                break;
        }
        nameTextView.setText(name);

        Glide.with(this)
                .load(senderProfilePicture)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .transform(new CircleTransform(getContext()))
                .into(senderProfilePictureImageUrl);

        Glide.with(this)
                .load(receiverProfilePicture)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.ic_profile)
                .transform(new CircleTransform(getContext()))
                .into(receiverProfilePictureImageUrl);

        goToWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
    }
}
