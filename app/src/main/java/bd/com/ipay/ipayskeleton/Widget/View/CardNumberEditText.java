package bd.com.ipay.ipayskeleton.Widget.View;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;

import java.util.Map;
import java.util.TreeMap;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CardNumberValidator;

public class CardNumberEditText extends AppCompatEditText {

	private static final int INDEX_NOT_FOUND = -1;
	private String separator = " ";
	private int maximumCreditCardLength = 19;
	private String mPreviousText;
	private int currentCardIcon = -1;
	private final LengthLimitFilter inputFilter = new LengthLimitFilter(maximumCreditCardLength);
	private Map<Integer, Drawable> cardIconMap = new TreeMap<>();

	private final TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence text, int start, int before, int count) {
			if (text != null) {
				final String currentText = text.toString();
				setCardDrawable(currentText);
				if (mPreviousText != null && text.length() > mPreviousText.length()) {
					String difference = difference(currentText, mPreviousText);
					if (!difference.equals(separator)) {
						addSeparatorToText();
					}
				}
				mPreviousText = currentText;
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	public CardNumberEditText(Context context) {
		this(context, null);
	}

	public CardNumberEditText(Context context, @Nullable AttributeSet attrs) {
		this(context, attrs, android.support.v7.appcompat.R.attr.editTextStyle);
	}

	public CardNumberEditText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView();
	}

	private void initView() {
		showCardDrawable(R.drawable.ic_generic_card_square);
		addTextChangedListener(textWatcher);
		setMaxLines(1);
		setFilters(new InputFilter[]{inputFilter});
	}

	@Override
	public void setMaxLines(int maxLines) {
		super.setMaxLines(1);
	}

	private void addSeparatorToText() {
		String text = getText().toString();
		text = text.replace(separator, "");
		if (text.length() > maximumCreditCardLength) {
			text = text.substring(0, maximumCreditCardLength);
		}
		if (text.length() >= maximumCreditCardLength || text.length() % 4 == 0) {
			return;
		}
		int interval = 4;
		char separator = this.separator.charAt(0);

		StringBuilder stringBuilder = new StringBuilder(text);
		for (int i = 0; i < text.length() / interval; i++) {
			stringBuilder.insert(((i + 1) * interval) + i, separator);
		}
		removeTextChangedListener(textWatcher);
		setText(stringBuilder.toString());
		setSelection(getText().length());
		addTextChangedListener(textWatcher);
	}

	private void setCardDrawable(String text) {
		CardNumberValidator.Cards cards = CardNumberValidator.getLikelyType(text);
		if (cards != null) {
			showCardDrawable(cards.getCardIconId());
			setMaximumCreditCardLength(cards.getCardLength()[cards.getCardLength().length - 1]);
		} else {
			showCardDrawable(R.drawable.ic_generic_card_square);
		}
	}

	private void showCardDrawable(int drawableId) {
		if (drawableId != currentCardIcon) {
			if (!cardIconMap.containsKey(drawableId)) {
				final Drawable drawable = ResourcesCompat.getDrawable(getResources(), drawableId, getContext().getTheme());
				if (drawable != null) {
					drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
					final int size = getResources().getDimensionPixelSize(R.dimen.value24);
					drawable.setBounds(0, 0, (int) (size * 1.6f), size);
					cardIconMap.put(drawableId, drawable);
				} else {
					return;
				}
			}
			currentCardIcon = drawableId;
			setCompoundDrawables(cardIconMap.get(drawableId), getCompoundDrawables()[1], getCompoundDrawables()[2], getCompoundDrawables()[3]);
		}
	}

	@SuppressWarnings("unused")
	public int getMaximumCreditCardLength() {
		return maximumCreditCardLength;
	}

	public void setMaximumCreditCardLength(int maximumCreditCardLength) {
		this.maximumCreditCardLength = maximumCreditCardLength;
		this.inputFilter.setLength(maximumCreditCardLength);
	}

	public String difference(String str1, String str2) {
		if (str1 == null) {
			return str2;
		}
		if (str2 == null) {
			return str1;
		}
		int at = indexOfDifference(str1, str2);
		if (at == INDEX_NOT_FOUND) {
			return "";
		}
		return str2.substring(at);
	}

	public int indexOfDifference(CharSequence cs1, CharSequence cs2) {
		if (cs1 == cs2) {
			return INDEX_NOT_FOUND;
		}
		if (cs1 == null || cs2 == null) {
			return 0;
		}
		int i;
		for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
			if (cs1.charAt(i) != cs2.charAt(i)) {
				break;
			}
		}
		if (i < cs2.length() || i < cs1.length()) {
			return i;
		}
		return INDEX_NOT_FOUND;
	}

	private class LengthLimitFilter implements InputFilter {
		private int length;

		LengthLimitFilter(int length) {
			this.length = length;
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
			int keep = length - (dest.length() - (dend - dstart));
			if (keep <= 0) {
				return "";
			} else if (keep >= end - start) {
				return null; // keep original
			} else {
				keep += start;
				if (Character.isHighSurrogate(source.charAt(keep - 1))) {
					--keep;
					if (keep == start) {
						return "";
					}
				}
				return source.subSequence(start, keep);
			}
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length + (length % 4 == 0 ? (length / 4) - 1 : (length / 4));
		}
	}
}
