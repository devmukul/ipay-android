package bd.com.ipay.ipayskeleton.PaymentFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
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

import static android.view.View.GONE;

public class IPayTransactionSuccessFragment extends Fragment {
    private static final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
    private int transactionType;
    private String name;
    private BigDecimal amount;
    private String senderProfilePicture;
    private String receiverProfilePicture;
    private String mAddressString;

    private RoundedImageView sponsorImageView;

    private String sponsorProfilePictureUrl;
    private String sponsorName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            transactionType = getArguments().getInt(IPayTransactionActionActivity.TRANSACTION_TYPE_KEY);
            name = getArguments().getString(Constants.NAME);
            senderProfilePicture = getArguments().getString(Constants.SENDER_IMAGE_URL);
            receiverProfilePicture = getArguments().getString(Constants.RECEIVER_IMAGE_URL);
            amount = (BigDecimal) getArguments().getSerializable(Constants.AMOUNT);
            mAddressString = getArguments().getString(Constants.ADDRESS);
            sponsorProfilePictureUrl = getArguments().getString(Constants.SPONSOR_PROFILE_PICTURE);
            sponsorName = getArguments().getString(Constants.SPONSOR_NAME);

            if (sponsorProfilePictureUrl != null) {
                if (sponsorProfilePictureUrl.contains("ipay.com")) {
                    sponsorProfilePictureUrl = Constants.BASE_URL_FTP_SERVER + sponsorProfilePictureUrl;
                }
            }
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
        final TextView addressTextView = view.findViewById(R.id.address_text_view);
        final RoundedImageView sponsorImageView = view.findViewById(R.id.sponsor_image_view);
        final RoundedImageView receiverProfilePictureImageView = view.findViewById(R.id.receiver_profile_picture_image_view);
        final RoundedImageView senderProfilePictureImageView = view.findViewById(R.id.sender_profile_picture_image_view);
        final TextView successDescriptionTextView = view.findViewById(R.id.success_description_text_view);
        final Button goToWalletButton = view.findViewById(R.id.go_to_wallet_button);
        final String amountValue = getString(R.string.balance_holder, numberFormat.format(amount));

        if (sponsorName != null) {
            sponsorImageView.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(sponsorProfilePictureUrl)
                    .centerCrop()
                    .error(R.drawable.user_brand_bg)
                    .into(sponsorImageView);
        } else {
            sponsorImageView.setVisibility(GONE);
        }

        switch (transactionType) {
            case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
                updateTransactionDescription(transactionSuccessMessageTextView, getString(R.string.send_money_success_message, amountValue), 18, 18 + amountValue.length());
                successDescriptionTextView.setText(R.string.send_money_success_description);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_MAKE_PAYMENT:
                updateTransactionDescription(transactionSuccessMessageTextView, getString(R.string.make_payment_success_message, amountValue), 18, 18 + amountValue.length());
                if (sponsorName != null) {
                    successDescriptionTextView.setText("Successfully paid to " + name + " using " + sponsorName + "'s" +
                            " iPay wallet. " + name + " and " + sponsorName + " will be notified about this transaction." +
                            " See the details of this transaction in your Transaction History.");
                } else {
                    successDescriptionTextView.setText(getString(R.string.make_payment_success_description, name));
                }
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_REQUEST_MONEY:
                updateTransactionDescription(transactionSuccessMessageTextView, getString(R.string.request_money_success_message, amountValue), 23, 23 + amountValue.length());
                successDescriptionTextView.setText(R.string.request_money_success_description);
                break;
            case IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID:
            default:
                transactionSuccessMessageTextView.setText(R.string.empty_string);
                successDescriptionTextView.setText(R.string.empty_string);
                break;
        }
        if (name != null) {
            nameTextView.setText(name);
        }

        if (!TextUtils.isEmpty(mAddressString)) {
            addressTextView.setVisibility(View.VISIBLE);
            addressTextView.setText(mAddressString);
        } else {
            addressTextView.setVisibility(GONE);
        }

        if (senderProfilePicture != null) {
            Glide.with(this)
                    .load(senderProfilePicture)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .transform(new CircleTransform(getContext()))
                    .into(senderProfilePictureImageView);
        }
        if (receiverProfilePicture != null) {
            Glide.with(this)
                    .load(receiverProfilePicture)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .transform(new CircleTransform(getContext()))
                    .into(receiverProfilePictureImageView);
        }

        goToWalletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity() != null)
                    getActivity().finish();
            }
        });
    }

    private void updateTransactionDescription(TextView textView, String string, int startPoint, int endPoint) {
        final Spannable spannableAmount;
        spannableAmount = new SpannableString(string);
        spannableAmount.setSpan(new StyleSpan(Typeface.BOLD), startPoint, endPoint, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableAmount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney)), startPoint, endPoint, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spannableAmount, TextView.BufferType.SPANNABLE);
    }
}
