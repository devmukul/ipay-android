package bd.com.ipay.ipayskeleton.Widgets;

import android.content.res.ColorStateList;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.SnackbarContentLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;

public class IPaySnackbar extends BaseTransientBottomBar<IPaySnackbar> {

	/**
	 * Show the IPaySnackbar indefinitely. This means that the IPaySnackbar will be displayed from the time
	 * that is {@link #show() shown} until either it is dismissed, or another IPaySnackbar is shown.
	 *
	 * @see #setDuration
	 */
	public static final int LENGTH_INDEFINITE = BaseTransientBottomBar.LENGTH_INDEFINITE;

	/**
	 * Show the IPaySnackbar for a short period of time.
	 *
	 * @see #setDuration
	 */
	public static final int LENGTH_SHORT = BaseTransientBottomBar.LENGTH_SHORT;

	/**
	 * Show the IPaySnackbar for a long period of time.
	 *
	 * @see #setDuration
	 */
	public static final int LENGTH_LONG = BaseTransientBottomBar.LENGTH_LONG;
	@Nullable
	private BaseCallback<IPaySnackbar> mCallback;

	private IPaySnackbar(ViewGroup parent, View content, android.support.design.snackbar.ContentViewCallback contentViewCallback) {
		super(parent, content, contentViewCallback);
	}

	/**
	 * Make a IPaySnackbar to display a error message
	 *
	 * <p>IPaySnackbar will try and find a parent view to hold IPaySnackbar's view from the value given
	 * to {@code view}. IPaySnackbar will walk up the view tree trying to find a suitable parent,
	 * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
	 * whichever comes first.
	 *
	 * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows IPaySnackbar to enable
	 * certain features, such as swipe-to-dismiss and automatically moving of widgets like
	 * {@link android.support.design.widget.FloatingActionButton}.
	 *
	 * @param view     The view to find a parent from.
	 * @param text     The text to show.  Can be formatted text.
	 * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
	 *                 #LENGTH_LONG}
	 */
	public static IPaySnackbar error(@NonNull View view, @NonNull CharSequence text, int duration) {
		final IPaySnackbar iPaySnackbar = make(view, text, duration);
		final View snackBarView = iPaySnackbar.getView();
		snackBarView.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.colorRed));
		ViewGroup.LayoutParams layoutParams = snackBarView.getLayoutParams();
		layoutParams.height = view.getHeight();
		snackBarView.setLayoutParams(layoutParams);
		TextView textView = snackBarView.findViewById(android.support.design.R.id.snackbar_text);
		textView.setTextColor(ActivityCompat.getColor(view.getContext(), android.R.color.white));
		return iPaySnackbar;
	}

	/**
	 * Make a IPaySnackbar to display a error message
	 *
	 * <p>IPaySnackbar will try and find a parent view to hold IPaySnackbar's view from the value given
	 * to {@code view}. IPaySnackbar will walk up the view tree trying to find a suitable parent,
	 * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
	 * whichever comes first.
	 *
	 * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows IPaySnackbar to enable
	 * certain features, such as swipe-to-dismiss and automatically moving of widgets like
	 * {@link android.support.design.widget.FloatingActionButton}.
	 *
	 * @param view     The view to find a parent from.
	 * @param resId    The resource id of the string resource to use. Can be formatted text.
	 * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
	 *                 #LENGTH_LONG}
	 */
	@NonNull
	public static IPaySnackbar error(@NonNull View view, @StringRes int resId, int duration) {
		return error(view, view.getResources().getText(resId), duration);
	}

	/**
	 * Make a IPaySnackbar to display a message
	 *
	 * <p>IPaySnackbar will try and find a parent view to hold IPaySnackbar's view from the value given
	 * to {@code view}. IPaySnackbar will walk up the view tree trying to find a suitable parent,
	 * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
	 * whichever comes first.
	 *
	 * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows IPaySnackbar to enable
	 * certain features, such as swipe-to-dismiss and automatically moving of widgets like
	 * {@link android.support.design.widget.FloatingActionButton}.
	 *
	 * @param view     The view to find a parent from.
	 * @param text     The text to show.  Can be formatted text.
	 * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
	 *                 #LENGTH_LONG}
	 */
	@NonNull
	public static IPaySnackbar make(@NonNull View view, @NonNull CharSequence text, int duration) {
		final ViewGroup parent = findSuitableParent(view);
		if (parent == null) {
			throw new IllegalArgumentException("No suitable parent found from the given view. "
					+ "Please provide a valid view.");
		}

		final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		final SnackbarContentLayout content =
				(SnackbarContentLayout) inflater.inflate(
						android.support.design.R.layout.design_layout_snackbar_include, parent, false);
		final IPaySnackbar snackbar = new IPaySnackbar(parent, content, content);
		snackbar.setText(text);
		snackbar.setDuration(duration);
		return snackbar;
	}

	/**
	 * Make a IPaySnackbar to display a message.
	 *
	 * <p>IPaySnackbar will try and find a parent view to hold IPaySnackbar's view from the value given
	 * to {@code view}. IPaySnackbar will walk up the view tree trying to find a suitable parent,
	 * which is defined as a {@link CoordinatorLayout} or the window decor's content view,
	 * whichever comes first.
	 *
	 * <p>Having a {@link CoordinatorLayout} in your view hierarchy allows IPaySnackbar to enable
	 * certain features, such as swipe-to-dismiss and automatically moving of widgets like
	 * {@link android.support.design.widget.FloatingActionButton}.
	 *
	 * @param view     The view to find a parent from.
	 * @param resId    The resource id of the string resource to use. Can be formatted text.
	 * @param duration How long to display the message.  Either {@link #LENGTH_SHORT} or {@link
	 *                 #LENGTH_LONG}
	 */
	@NonNull
	public static IPaySnackbar make(@NonNull View view, @StringRes int resId, @Duration int duration) {
		return make(view, view.getResources().getText(resId), duration);
	}

	private static ViewGroup findSuitableParent(View view) {
		ViewGroup fallback = null;
		do {
			if (view instanceof CoordinatorLayout) {
				// We've found a CoordinatorLayout, use it
				return (ViewGroup) view;
			} else if (view instanceof FrameLayout) {
				if (view.getId() == android.R.id.content) {
					// If we've hit the decor content view, then we didn't find a CoL in the
					// hierarchy, so use it.
					return (ViewGroup) view;
				} else {
					// It's not the content view but we'll use it as our fallback
					fallback = (ViewGroup) view;
				}
			}

			if (view != null) {
				// Else, we will loop and crawl up the view hierarchy and try to find a parent
				final ViewParent parent = view.getParent();
				view = parent instanceof View ? (View) parent : null;
			}
		} while (view != null);

		// If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
		return fallback;
	}

	/**
	 * Update the text in this {@link IPaySnackbar}.
	 *
	 * @param message The new text for this {@link BaseTransientBottomBar}.
	 */
	@NonNull
	public IPaySnackbar setText(@NonNull CharSequence message) {
		final SnackbarContentLayout contentLayout = (SnackbarContentLayout) ((FrameLayout) getView()).getChildAt(0);
		final TextView tv = contentLayout.findViewById(android.support.design.R.id.snackbar_text);
		tv.setText(message);
		return this;
	}

	/**
	 * Update the text in this {@link IPaySnackbar}.
	 *
	 * @param resId The new text for this {@link BaseTransientBottomBar}.
	 */
	@NonNull
	public IPaySnackbar setText(@StringRes int resId) {
		return setText(getContext().getText(resId));
	}

	/**
	 * Set the action to be displayed in this {@link BaseTransientBottomBar}.
	 *
	 * @param resId    String resource to display for the action
	 * @param listener callback to be invoked when the action is clicked
	 */
	@NonNull
	public IPaySnackbar setAction(@StringRes int resId, View.OnClickListener listener) {
		return setAction(getContext().getText(resId), listener);
	}

	/**
	 * Set the action to be displayed in this {@link BaseTransientBottomBar}.
	 *
	 * @param text     Text to display for the action
	 * @param listener callback to be invoked when the action is clicked
	 */
	@NonNull
	public IPaySnackbar setAction(CharSequence text, final View.OnClickListener listener) {
		final SnackbarContentLayout contentLayout = (SnackbarContentLayout) ((FrameLayout) getView()).getChildAt(0);
		final TextView tv = contentLayout.findViewById(android.support.design.R.id.snackbar_action);

		if (TextUtils.isEmpty(text) || listener == null) {
			tv.setVisibility(View.GONE);
			tv.setOnClickListener(null);
		} else {
			tv.setVisibility(View.VISIBLE);
			tv.setText(text);
			tv.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					listener.onClick(view);
					// Now dismiss the IPaySnackbar
					dismiss();
				}
			});
		}
		return this;
	}

	/**
	 * Sets the text color of the action specified in
	 * {@link #setAction(CharSequence, View.OnClickListener)}.
	 */
	@NonNull
	public IPaySnackbar setActionTextColor(ColorStateList colors) {
		final SnackbarContentLayout contentLayout = (SnackbarContentLayout) ((FrameLayout) getView()).getChildAt(0);
		final TextView tv = contentLayout.findViewById(android.support.design.R.id.snackbar_action);
		tv.setTextColor(colors);
		return this;
	}

	/**
	 * Sets the text color of the action specified in
	 * {@link #setAction(CharSequence, View.OnClickListener)}.
	 */
	@NonNull
	public IPaySnackbar setActionTextColor(@ColorInt int color) {
		final SnackbarContentLayout contentLayout = (SnackbarContentLayout) ((FrameLayout) getView()).getChildAt(0);
		final TextView tv = contentLayout.findViewById(android.support.design.R.id.snackbar_action);
		tv.setTextColor(color);
		return this;
	}

	/**
	 * Set a callback to be called when this the visibility of this {@link IPaySnackbar}
	 * changes. Note that this method is deprecated
	 * and you should use {@link #addCallback(BaseCallback)} to add a callback and
	 * {@link #removeCallback(BaseCallback)} to remove a registered callback.
	 *
	 * @param callback Callback to notify when transient bottom bar events occur.
	 * @see IPaySnackbar.Callback
	 * @see #addCallback(BaseCallback)
	 * @see #removeCallback(BaseCallback)
	 * @deprecated Use {@link #addCallback(BaseCallback)}
	 */
	@Deprecated
	@NonNull
	public IPaySnackbar setCallback(IPaySnackbar.Callback callback) {
		// The logic in this method emulates what we had before support for multiple
		// registered callbacks.
		if (mCallback != null) {
			removeCallback(mCallback);
		}
		if (callback != null) {
			addCallback(callback);
		}
		// Update the deprecated field so that we can remove the passed callback the next
		// time we're called
		mCallback = callback;
		return this;
	}

	/**
	 * Callback class for {@link IPaySnackbar} instances.
	 * <p>
	 * Note: this class is here to provide backwards-compatible way for apps written before
	 * the existence of the base {@link BaseTransientBottomBar} class.
	 *
	 * @see BaseTransientBottomBar#addCallback(BaseCallback)
	 */
	public static class Callback extends BaseCallback<IPaySnackbar> {
		/**
		 * Indicates that the IPaySnackbar was dismissed via a swipe.
		 */
		public static final int DISMISS_EVENT_SWIPE = BaseCallback.DISMISS_EVENT_SWIPE;
		/**
		 * Indicates that the IPaySnackbar was dismissed via an action click.
		 */
		public static final int DISMISS_EVENT_ACTION = BaseCallback.DISMISS_EVENT_ACTION;
		/**
		 * Indicates that the IPaySnackbar was dismissed via a timeout.
		 */
		public static final int DISMISS_EVENT_TIMEOUT = BaseCallback.DISMISS_EVENT_TIMEOUT;
		/**
		 * Indicates that the IPaySnackbar was dismissed via a call to {@link #dismiss()}.
		 */
		public static final int DISMISS_EVENT_MANUAL = BaseCallback.DISMISS_EVENT_MANUAL;
		/**
		 * Indicates that the IPaySnackbar was dismissed from a new IPaySnackbar being shown.
		 */
		public static final int DISMISS_EVENT_CONSECUTIVE = BaseCallback.DISMISS_EVENT_CONSECUTIVE;

		@Override
		public void onShown(IPaySnackbar sb) {
			// Stub implementation to make API check happy.
		}

		@Override
		public void onDismissed(IPaySnackbar transientBottomBar, @DismissEvent int event) {
			// Stub implementation to make API check happy.
		}
	}
}
