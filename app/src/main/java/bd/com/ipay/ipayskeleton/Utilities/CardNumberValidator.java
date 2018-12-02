package bd.com.ipay.ipayskeleton.Utilities;

import java.util.regex.Pattern;

import bd.com.ipay.ipayskeleton.R;

public class CardNumberValidator {

	public enum Cards {
		MASTERCARD("mastercard",
				R.drawable.ic_master_card_square,
				"^(5[1-5][0-9]{14})|(222[1-9][0-9]{12})|(22[3-9][0-9]{13})|(2[3-6][0-9]{14})|(27[01][0-9]{13})|(2720[0-9]{12})$",
				"^(5[1-5](.*))|(222[1-9](.*))|(22[3-9](.*))|(2[3-6](.*))|(27[01](.*))|(2720(.*))$",
				new int[][]{{4, 4, 4, 4}},
				new int[]{16},
				true
		),
		VISA("visa",
				R.drawable.ic_visa_square,
				"^4[0-9]{12}((?:[0-9]{3})?|(?:[0-9]{6})?)$",
				"^4(.*)$",
				new int[][]{{4, 4, 4, 1}, {4, 4, 4, 4}, {4, 4, 4, 4, 3}},
				new int[]{13, 16, 19},
				true),
		AMEX("amex",
				R.drawable.ic_amex_square,
				"^3[47][0-9]{13}$",
				"^3[47](.*)$",
				new int[][]{{4, 6, 5}},
				new int[]{15},
				true),
		DINERS_CLUB("dinersclub",
				R.drawable.ic_diners_club_square,
				"^3(?:0[0-5]|[68][0-9])?[0-9]{11}$",
				"^3(?:0[0-5]|[68][0-9])(.*)$",
				new int[][]{{4, 6, 4}},
				new int[]{14},
				true),
		DISCOVER("discover",
				R.drawable.ic_discover_square,
				"^6(?:011|5[0-9]{2})[0-9]{12}$",
				"^6(?:011|5[0-9]{2})(.*)$",
				new int[][]{{4, 4, 4, 4}},
				new int[]{13, 16, 19},
				true);
		private final String name;
		private final int cardIconId;
		private final String pattern;
		private final String likelyPattern;
		private final int[][] format;
		private final int[] cardLength;
		private final boolean luhn;

		Cards(String name, int cardIconId, String pattern, String likelyPattern, int[][] format, int[] cardLength, boolean luhn) {
			this.name = name;
			this.cardIconId = cardIconId;
			this.pattern = pattern;
			this.likelyPattern = likelyPattern;
			this.format = format;
			this.cardLength = cardLength;
			this.luhn = luhn;
		}

		public String getName() {
			return name;
		}

		public int getCardIconId() {
			return cardIconId;
		}

		public String getPattern() {
			return pattern;
		}

		public String getLikelyPattern() {
			return likelyPattern;
		}

		public int[][] getFormat() {
			return format;
		}

		public int[] getCardLength() {
			return cardLength;
		}

		public boolean isLuhn() {
			return luhn;
		}
	}

	public static String sanitizeEntry(String entry, boolean isNumber) {
		final String a = "".replaceAll("^4(.*)$", "");
		return isNumber ? entry.replaceAll("\\D", "") : entry.replaceAll("\\s+|-", "");
	}

	public static String deSanitizeEntry(String entry, char separator) {
		entry = entry.replace(String.valueOf(separator), "");

		int interval = 4;

		StringBuilder stringBuilder = new StringBuilder(entry);
		for (int i = 0; i < entry.length() / interval; i++) {
			stringBuilder.insert(((i + 1) * interval) + i, separator);
		}
		return stringBuilder.toString();
	}

	public static Cards getLikelyType(String num) {
		num = sanitizeEntry(num, true);
		Cards[] cards = Cards.values();
		for (Cards card : cards) {
			if (Pattern.matches(card.getLikelyPattern(), num)) {
				return card;
			}
		}
		return null;
	}

	public static Cards getCardType(String num) {
		num = sanitizeEntry(num, true);


		Cards[] cards = Cards.values();
		for (Cards card : cards) {
			if (Pattern.matches(card.getPattern(), num)) {
				return card;
			}
		}
		return null;
	}

	/*
	 * Applies the Luhn Algorithm to the given card number
	 * @param num String containing the card's number to be tested
	 * @return boolean containing the result of the computation
	 */
	private static boolean validateLuhnNumber(String num) {
		if (num.equals("")) return false;
		int nCheck = 0;
		int nDigit;
		boolean bEven = false;
		num = sanitizeEntry(num, true);

		for (int i = num.length() - 1; i >= 0; i--) {
			nDigit = Integer.parseInt(String.valueOf(num.charAt(i)));

			if (bEven) {
				if ((nDigit *= 2) > 9) nDigit -= 9;
			}
			nCheck += nDigit;
			bEven = !bEven;
		}

		return (nCheck % 10) == 0;
	}

	/**
	 * Checks if the card's number is valid by identifying the card's type and checking its conditions
	 *
	 * @param num String containing the card's code to be verified
	 * @return boolean containing the result of the verification
	 */
	public static boolean validateCardNumber(String num) {
		if (num.equals("")) return false;
		num = sanitizeEntry(num, true);
		if (Pattern.matches("^\\d+$", num)) {
			Cards c = getCardType(num);
			if (c != null) {
				boolean len = false;
				for (int i = 0; i < c.getCardLength().length; i++) {
					if (c.getCardLength()[i] == num.length()) {
						len = true;
						break;
					}
				}
				return len && (!c.isLuhn() || validateLuhnNumber(num));
			}
		}
		return false;
	}

	/**
	 * Checks if the card's number is valid by identifying the card's type and checking its conditions
	 *
	 * @param num String containing the card's code to be verified
	 * @return boolean containing the result of the verification
	 */
	public static boolean validateCardNumber(String num, Cards... filteredCardList) {
		if (validateCardNumber(num)) {
			Cards cards = getCardType(num);
			for (Cards filteredCard : filteredCardList) {
				if (filteredCard.equals(cards)) {
					return true;
				}
			}
		}
		return false;
	}
}
