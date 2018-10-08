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
import android.widget.ImageView;
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
		final RoundedImageView receiverProfilePictureImageView = view.findViewById(R.id.receiver_profile_picture_image_view);
		final ImageView arrowIconImageView = view.findViewById(R.id.arrow_icon_image_view);
		final RoundedImageView senderProfilePictureImageView = view.findViewById(R.id.sender_profile_picture_image_view);
		final ImageView cardImageView = view.findViewById(R.id.card_image_view);
		final TextView successDescriptionTextView = view.findViewById(R.id.success_description_text_view);
		final Button goToWalletButton = view.findViewById(R.id.go_to_wallet_button);
		final String amountValue = getString(R.string.balance_holder, numberFormat.format(amount));
		switch (transactionType) {
			case IPayTransactionActionActivity.TRANSACTION_TYPE_ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD:
				updateTransactionDescription(transactionSuccessMessageTextView, getString(R.string.add_money_card_success_message, amountValue), 19, 19 + amountValue.length());
				successDescriptionTextView.setText(R.string.add_money_card_success_description);
				cardImageView.setVisibility(View.VISIBLE);
				senderProfilePictureImageView.setVisibility(View.GONE);
				receiverProfilePictureImageView.setVisibility(View.GONE);
				arrowIconImageView.setVisibility(View.GONE);
				break;
			case IPayTransactionActionActivity.TRANSACTION_TYPE_SEND_MONEY:
				updateTransactionDescription(transactionSuccessMessageTextView, getString(R.string.send_money_success_message, amountValue), 18, 18 + amountValue.length());
				successDescriptionTextView.setText(R.string.send_money_success_description);
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
