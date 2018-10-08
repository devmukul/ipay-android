package bd.com.ipay.ipayskeleton.PaymentFragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.analytics.Tracker;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.NumberFormat;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Api.HttpResponse.HttpResponseListener;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.CustomProgressDialog;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.OTPVerificationForTwoFactorAuthenticationServicesDialog;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public abstract class IPayAbstractTransactionConfirmationFragment extends Fragment implements HttpResponseListener {
	protected Tracker mTracker;

	private EditText pinEditText;
	private EditText noteEditText;
	private TextView transactionDescriptionTextView;
	private RoundedImageView transactionImageView;
	private TextView nameTextView;
	private Button transactionConfirmationButton;
	protected final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);
	private OTPVerificationForTwoFactorAuthenticationServicesDialog mOTPVerificationForTwoFactorAuthenticationServicesDialog;
	protected CustomProgressDialog customProgressDialog;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		numberFormat.setMinimumFractionDigits(0);
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumIntegerDigits(2);
		if (getActivity() != null)
			mTracker = Utilities.getTracker(getActivity());
		customProgressDialog = new CustomProgressDialog(getActivity());
		customProgressDialog.setCancelable(false);
	}

	@Nullable
	@Override
	public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ipay_transaction_confirmation, container, false);
	}

	@Override
	public final void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		final Toolbar toolbar = view.findViewById(R.id.toolbar);
		final View pinLayoutHolder = view.findViewById(R.id.pin_layout_holder);
		final View noteLayoutHolder = view.findViewById(R.id.note_layout_holder);
		transactionConfirmationButton = view.findViewById(R.id.transaction_confirmation_button);
		transactionDescriptionTextView = view.findViewById(R.id.transaction_description_text_view);
		transactionImageView = view.findViewById(R.id.profile_image_view);
		nameTextView = view.findViewById(R.id.name_text_view);
		pinEditText = view.findViewById(R.id.pin_edit_text);
		noteEditText = view.findViewById(R.id.note_edit_text);

		if (getActivity() instanceof AppCompatActivity) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
			ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
			getActivity().setTitle(R.string.empty_string);
		}

		transactionConfirmationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (verifyInput()) {
					performContinueAction();
				}
			}
		});

		if (isPinRequired())
			pinLayoutHolder.setVisibility(View.VISIBLE);
		else
			pinLayoutHolder.setVisibility(View.GONE);

		if (canUserAddNote())
			noteLayoutHolder.setVisibility(View.VISIBLE);
		else
			noteLayoutHolder.setVisibility(View.GONE);

		if (getContext() != null) {
			if (isPinRequired()) {
				Utilities.showKeyboard(getContext(), pinEditText);
			} else if (canUserAddNote()) {
				Utilities.showKeyboard(getContext(), noteEditText);
			}
		}

		setupViewProperties();
	}

	protected void setTransactionDescription(CharSequence transactionDescription) {
		transactionDescriptionTextView.setText(transactionDescription, TextView.BufferType.SPANNABLE);
	}

	protected void setName(CharSequence name) {
		nameTextView.setText(name, TextView.BufferType.SPANNABLE);
	}

	protected void setTransactionConfirmationButtonTitle(CharSequence title) {
		transactionConfirmationButton.setText(title, TextView.BufferType.SPANNABLE);
	}

	protected void setTransactionImageResource(@SuppressWarnings("SameParameterValue") int imageResource) {
		Glide.with(getContext()).load(imageResource)
				.transform(new CircleTransform(getContext()))
				.crossFade()
				.into(transactionImageView);
	}

	@SuppressWarnings("unused")
	protected void setTransactionImage(String imageUrl) {
		Glide.with(getContext()).load(imageUrl)
				.transform(new CircleTransform(getContext()))
				.crossFade()
				.into(transactionImageView);
	}

	protected String getPin() {
		final Editable pin = pinEditText.getText();
		return pin != null ? pin.toString() : "";
	}

	protected final void sendSuccessEventTracking(Number amount) {
		Utilities.sendSuccessEventTracker(mTracker, getTrackerCategory(), ProfileInfoCacheManager.getAccountId(), amount.longValue());
	}

	protected final void sendFailedEventTracking(@NonNull String failMessage, @NonNull Number amount) {
		Utilities.sendFailedEventTracker(mTracker, getTrackerCategory(), ProfileInfoCacheManager.getAccountId(),
				failMessage, amount.longValue());
	}

	protected final void sendBlockedEventTracking(@NonNull Number amount) {
		Utilities.sendBlockedEventTracker(mTracker, getTrackerCategory(), ProfileInfoCacheManager.getAccountId(), amount.longValue());
	}

	protected void setNoteEditTextHint(CharSequence hint) {
		noteEditText.setVisibility(View.VISIBLE);
		noteEditText.setHint(hint);
	}

	protected void showErrorMessage(String errorMessage) {
		if (!TextUtils.isEmpty(errorMessage) && getActivity() != null) {
			Snackbar snackbar = Snackbar.make(transactionConfirmationButton, errorMessage, Snackbar.LENGTH_SHORT);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
			ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
			layoutParams.height = transactionConfirmationButton.getHeight();
			snackbarView.setLayoutParams(layoutParams);
			TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
			textView.setTextColor(ActivityCompat.getColor(getActivity(), android.R.color.white));
			snackbar.show();
		}
	}

	protected CharSequence getStyledTransactionDescription(@StringRes int transactionStringId, Number amount) {
		final String amountValue = numberFormat.format(amount);
		final String spannedString = getString(transactionStringId, amountValue);
		int position = spannedString.indexOf(String.format("Tk.%s", amountValue));
		final Spannable spannableAmount = new SpannableString(getString(transactionStringId, amountValue));
		spannableAmount.setSpan(new StyleSpan(Typeface.BOLD), position, position + amountValue.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannableAmount.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorLightGreenSendMoney)), position, position + amountValue.length() + 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return spannableAmount;
	}

	protected void hideOtpDialog() {
		if (isOtpDialogShowing()) {
			mOTPVerificationForTwoFactorAuthenticationServicesDialog.dismiss();
		}
	}

	protected boolean isOtpDialogShowing() {
		return mOTPVerificationForTwoFactorAuthenticationServicesDialog != null && mOTPVerificationForTwoFactorAuthenticationServicesDialog.isShowing();
	}

	protected void launchOtpVerification(long otpValidFor, String requestJson, @SuppressWarnings("SameParameterValue") String apiCommand, String url) {
		if (getActivity() == null)
			return;

		mOTPVerificationForTwoFactorAuthenticationServicesDialog = new OTPVerificationForTwoFactorAuthenticationServicesDialog(getActivity(), requestJson, apiCommand,
				url, Constants.METHOD_POST, otpValidFor);
		mOTPVerificationForTwoFactorAuthenticationServicesDialog.setOtpValidFor(otpValidFor);
		mOTPVerificationForTwoFactorAuthenticationServicesDialog.mParentHttpResponseListener = this;
	}

	protected abstract void setupViewProperties();

	protected abstract boolean isPinRequired();

	protected abstract boolean canUserAddNote();

	protected abstract String getTrackerCategory();

	protected abstract boolean verifyInput();

	protected abstract void performContinueAction();
}
