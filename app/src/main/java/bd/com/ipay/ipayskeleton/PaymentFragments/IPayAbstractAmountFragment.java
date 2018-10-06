package bd.com.ipay.ipayskeleton.PaymentFragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SuperscriptSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bd.com.ipay.ipayskeleton.Activities.IPayTransactionActionActivity;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.BusinessRuleAndServiceCharge.BusinessRule.MandatoryBusinessRules;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.BusinessRuleCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.SharedPrefManager;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.Widget.View.ShortcutSelectionRadioGroup;

public abstract class IPayAbstractAmountFragment extends Fragment {
	private TextView transactionDescriptionTextView;
	private TextView amountTextView;
	private TextView nameTextView;
	private EditText amountDummyEditText;
	private RoundedImageView transactionImageView;
	private View balanceInfoLayout;
	private Button continueButton;
	private List<ShortCutOption> shortCutOptionList;
	private ShortcutSelectionRadioGroup shortcutSelectionRadioGroup;
	private final NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.US);

	protected MandatoryBusinessRules businessRules;

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		numberFormat.setMinimumFractionDigits(0);
		numberFormat.setMaximumFractionDigits(2);
		numberFormat.setMinimumIntegerDigits(2);

		businessRules = BusinessRuleCacheManager.getBusinessRules(BusinessRuleCacheManager.getTag(getServiceId()));

		if (getContext() != null)
			LocalBroadcastManager.getInstance(getContext()).registerReceiver(mBusinessRuleUpdateBroadcastReceiver, new IntentFilter(Constants.BUSINESS_RULE_UPDATE_BROADCAST));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		if (getContext() != null)
			LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mBusinessRuleUpdateBroadcastReceiver);
	}

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_ipay_transaction_amount_input_new, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		final Toolbar toolbar = view.findViewById(R.id.toolbar);
		final TextView ipayBalanceTextView = view.findViewById(R.id.ipay_balance_text_view);

		continueButton = view.findViewById(R.id.continue_button);
		amountDummyEditText = view.findViewById(R.id.amount_dummy_edit_text);
		nameTextView = view.findViewById(R.id.name_text_view);
		transactionDescriptionTextView = view.findViewById(R.id.transaction_description_text_view);
		transactionImageView = view.findViewById(R.id.transaction_image_view);
		amountTextView = view.findViewById(R.id.amount_text_view);
		shortcutSelectionRadioGroup = view.findViewById(R.id.shortcut_selection_radio_group);
		balanceInfoLayout = view.findViewById(R.id.balance_info_layout);

		if (getActivity() instanceof AppCompatActivity) {
			((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
			ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
			if (actionBar != null) {
				actionBar.setDisplayHomeAsUpEnabled(true);
			}
			getActivity().setTitle(R.string.empty_string);
		}

		amountDummyEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (amountDummyEditText.getText() != null)
					amountDummyEditText.setSelection(amountDummyEditText.getText().length());
			}
		});
		amountDummyEditText.setFilters(new InputFilter[]{getInputFilter()});

		amountDummyEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
				double result = 0;

				String addSuffix = "";
				final String resultString;
				if (charSequence != null)
					resultString = charSequence.toString();
				else
					resultString = null;
				if (resultString != null && resultString.length() > 0) {
					if (resultString.matches("[0]+")) {
						amountTextView.setText("");
					}

					if (resultString.charAt(0) != '.' || resultString.length() > 1)
						result = Double.valueOf(resultString);
					if (resultString.endsWith(".") || resultString.endsWith(".0") || resultString.endsWith(".00"))
						addSuffix = resultString.substring(resultString.indexOf('.'), resultString.length());
					else if (resultString.matches("[0-9]*\\.[1-9]0"))
						addSuffix = "0";
				}
				shouldRemoveCheck(result);
				setAmount(result, addSuffix);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

		shortcutSelectionRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (group.findViewById(checkedId) != null && !((RadioButton) group.findViewById(checkedId)).isChecked()) {
					return;
				}

				int index = shortCutOptionList.indexOf(new ShortCutOption(checkedId));
				if (index == -1)
					return;
				amountDummyEditText.setText(String.valueOf(shortCutOptionList.get(index).amountValue));
				Utilities.hideKeyboard(getActivity(), amountDummyEditText);
			}
		});

		amountTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				v.clearFocus();
				if (getContext() != null)
					Utilities.showKeyboard(getContext(), amountDummyEditText);
				if (amountDummyEditText.getText() != null)
					amountDummyEditText.setSelection(amountDummyEditText.getText().length());
			}
		});

		continueButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (verifyInput()) {
					performContinueAction();
				}
			}
		});

		if (!TextUtils.isEmpty(SharedPrefManager.getUserBalance())) {
			ipayBalanceTextView.setText(numberFormat.format(new BigDecimal(SharedPrefManager.getUserBalance())));
		}

		if (getContext() != null)
			Utilities.showKeyboard(getContext(), amountDummyEditText);
		setAmount(0, "");
	}

	private void shouldRemoveCheck(double result) {
		int checkedId = shortcutSelectionRadioGroup.getCheckedRadioButtonId();
		if (shortCutOptionList != null) {
			int index = shortCutOptionList.indexOf(new ShortCutOption(checkedId));
			if (index != -1) {
				ShortCutOption shortCutOption = shortCutOptionList.get(index);
				if (shortCutOption.amountValue != result) {
					shortcutSelectionRadioGroup.clearCheck();
				}
			}
		}
	}

	public void setInputType(int inputType) {
		if (inputType == InputType.TYPE_CLASS_NUMBER || inputType == InputType.TYPE_NUMBER_FLAG_DECIMAL)
			amountDummyEditText.setInputType(inputType);
	}

	private void setAmount(double result, String addSuffix) {
		SuperscriptSpan superscriptSpan = new SuperscriptSpan();
		AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.super_script_size));
		final Spannable spannable = new SpannableString(String.format("%s%sTk", numberFormat.format(result), addSuffix));
		spannable.setSpan(superscriptSpan, spannable.length() - 2, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		spannable.setSpan(absoluteSizeSpan, spannable.length() - 2, spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		amountTextView.setText(spannable, TextView.BufferType.SPANNABLE);
	}

	public void setBalanceInfoLayoutVisibility(int visibility) {
		balanceInfoLayout.setVisibility(visibility);
	}

	public void setName(String name) {
		nameTextView.setText(name);
	}

	public void addShortCutOption(int id, String title, int value) {
		shortcutSelectionRadioGroup.setVisibility(View.VISIBLE);

		if (shortCutOptionList == null) {
			shortCutOptionList = new ArrayList<>();
		}

		if (shortcutSelectionRadioGroup.findViewById(id) != null) {
			return;
		}

		final Spannable spannable = new SpannableString(String.format(Locale.US, "%s\nTK. %s", title, numberFormat.format(value)));
		spannable.setSpan(new StyleSpan(Typeface.BOLD), title.length(), spannable.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ShortCutOption shortCutOption = new ShortCutOption(id, spannable, value);

		if (!shortCutOptionList.contains(shortCutOption))
			shortCutOptionList.add(shortCutOption);

		final RadioButton radioButton = new RadioButton(getContext());
		radioButton.setId(shortCutOption.id);
		radioButton.setGravity(Gravity.CENTER);

		radioButton.setText(shortCutOption.title, TextView.BufferType.SPANNABLE);
		shortcutSelectionRadioGroup.addView(radioButton);
	}

	public void setTransactionDescription(String transactionDescription) {
		transactionDescriptionTextView.setText(transactionDescription);
	}

	public void setTransactionImageResource(int imageResource) {
		Glide.with(getContext()).load(imageResource)
				.transform(new CircleTransform(getContext()))
				.crossFade()
				.into(transactionImageView);
	}

	@SuppressWarnings("unused")
	public void setTransactionImage(String imageUrl) {
		Glide.with(getContext()).load(imageUrl)
				.transform(new CircleTransform(getContext()))
				.crossFade()
				.into(transactionImageView);
	}

	protected void showErrorMessage(String errorMessage) {
		if (!TextUtils.isEmpty(errorMessage) && getActivity() != null) {
			Snackbar snackbar = Snackbar.make(continueButton, errorMessage, Snackbar.LENGTH_SHORT);
			View snackbarView = snackbar.getView();
			snackbarView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorRed));
			ViewGroup.LayoutParams layoutParams = snackbarView.getLayoutParams();
			layoutParams.height = continueButton.getHeight();
			snackbarView.setLayoutParams(layoutParams);
			TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
			textView.setTextColor(ActivityCompat.getColor(getActivity(), android.R.color.white));
			snackbar.show();
		}
	}

	@Nullable
	public Number getAmount() {
		if (TextUtils.isEmpty(amountTextView.getText())) {
			return null;
		} else {
			try {
				return numberFormat.parse(amountTextView.getText().toString());
			} catch (ParseException e) {
				return 0.0;
			}
		}
	}

	protected abstract InputFilter getInputFilter();

	protected abstract boolean verifyInput();

	protected abstract void performContinueAction();

	protected abstract int getServiceId();

	class ShortCutOption {
		private final int id;
		private final CharSequence title;
		private final int amountValue;

		ShortCutOption(int id) {
			this(id, null, 0);
		}

		ShortCutOption(int id, CharSequence title, int amountValue) {
			this.id = id;
			this.title = title;
			this.amountValue = amountValue;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			ShortCutOption that = (ShortCutOption) o;

			return id == that.id;
		}

		@Override
		public int hashCode() {
			return id;
		}

		@Override
		public String toString() {
			return "ShortCutOption{" + "id=" + id + ", title=" + title + ", amountValue=" + amountValue + '}';
		}
	}

	private final BroadcastReceiver mBusinessRuleUpdateBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getIntExtra(BusinessRuleCacheManager.SERVICE_ID_KEY, IPayTransactionActionActivity.TRANSACTION_TYPE_INVALID) == getServiceId())
				businessRules = BusinessRuleCacheManager.getBusinessRules(BusinessRuleCacheManager.getTag(getServiceId()));
		}
	};
}
