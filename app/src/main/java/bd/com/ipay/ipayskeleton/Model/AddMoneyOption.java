package bd.com.ipay.ipayskeleton.Model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class AddMoneyOption {

	private final int serviceId;
	@DrawableRes
	private final int optionIconResourceId;
	@StringRes
	private final int optionTitleResourceId;
	@StringRes
	private final int optionDescriptionResourceId;

	public AddMoneyOption(int serviceId, @DrawableRes int optionIconResourceId, @StringRes int optionTitleResourceId, @StringRes int optionDescriptionResourceId) {
		this.serviceId = serviceId;
		this.optionIconResourceId = optionIconResourceId;
		this.optionTitleResourceId = optionTitleResourceId;
		this.optionDescriptionResourceId = optionDescriptionResourceId;
	}

	public int getServiceId() {
		return serviceId;
	}

	public int getOptionIconResourceId() {
		return optionIconResourceId;
	}

	public int getOptionTitleResourceId() {
		return optionTitleResourceId;
	}

	public int getOptionDescriptionResourceId() {
		return optionDescriptionResourceId;
	}
}
