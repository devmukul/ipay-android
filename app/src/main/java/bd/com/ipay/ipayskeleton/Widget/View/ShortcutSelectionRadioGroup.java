package bd.com.ipay.ipayskeleton.Widget.View;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bd.com.ipay.ipayskeleton.R;

public class ShortcutSelectionRadioGroup extends RadioGroup {

	public ShortcutSelectionRadioGroup(Context context) {
		super(context);
	}

	public ShortcutSelectionRadioGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void addView(View child) {
		updateChild(child);
		super.addView(child);
	}

	private void updateChild(View child) {
		if (child instanceof RadioButton) {
			((RadioButton) child).setButtonDrawable(android.R.color.transparent);
			child.setBackgroundResource(R.drawable.background_shortcut_selection_radio_button);
			((RadioButton) child).setTextColor(ResourcesCompat.getColorStateList(getResources(), R.color.shortcut_selection_radio_button_text_color, getContext().getTheme()));

			final LayoutParams layoutParams;
			if (child.getLayoutParams() == null) {
				layoutParams = new LayoutParams(getContext(), null);
			} else {
				layoutParams = (LayoutParams) child.getLayoutParams();
			}
			int margin = getResources().getDimensionPixelSize(R.dimen.value4);
			layoutParams.setMargins(margin, margin, margin, margin);
			child.setLayoutParams(layoutParams);
		}
	}
}
