package bd.com.ipay.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.R;

public class IPayContainedProgressFragment extends BaseFragment {

	private FrameLayout contentViewHolder;
	private View progressViewHolder;
	private TextView progressTextView;

	private static final int MIN_SHOW_TIME = 500; // ms
	private static final int MIN_DELAY = 500; // ms

	long mStartTime = -1;

	boolean mPostedHide = false;

	boolean mPostedShow = false;

	boolean mDismissed = false;

	private final Runnable mDelayedHide = new Runnable() {

		@Override
		public void run() {
			mPostedHide = false;
			mStartTime = -1;
			contentViewHolder.setVisibility(View.GONE);
			progressViewHolder.setVisibility(View.VISIBLE);
		}
	};

	private final Runnable mDelayedShow = new Runnable() {

		@Override
		public void run() {
			mPostedShow = false;
			if (!mDismissed) {
				mStartTime = System.currentTimeMillis();
				contentViewHolder.setVisibility(View.VISIBLE);
				progressViewHolder.setVisibility(View.GONE);
			}
		}
	};

	@NonNull
	@Override
	public final View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                               @Nullable Bundle savedInstanceState) {
		final View mainView = onCreateContentView(inflater, container, savedInstanceState);
		final View progressView = inflater
				.inflate(R.layout.fragment_ipay_contained_progress, container, false);
		if (mainView != null) {
			contentViewHolder = mainView.findViewById(R.id.content_container);
			progressViewHolder = mainView.findViewById(R.id.progress_container);
			if (progressViewHolder instanceof ViewGroup) {
				((ViewGroup) progressViewHolder).addView(progressView);
			}
			progressTextView = progressView.findViewById(R.id.progress_text_view);
			return mainView;
		} else {
			return progressView;
		}
	}

	@Nullable
	public View onCreateContentView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
	                                @Nullable Bundle savedInstanceState) {
		return null;
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		removeCallbacks();
	}

	@Override
	public void onDetach() {
		super.onDetach();
		removeCallbacks();
	}

	private void removeCallbacks() {
		if (getView() != null) {
			getView().removeCallbacks(mDelayedHide);
			getView().removeCallbacks(mDelayedShow);
		}
	}

	public void setProgressText(@StringRes int progressTextId) {
		setProgressText(getText(progressTextId));
	}

	public void setProgressText(CharSequence progressText) {
		setProgressText(progressText, TextView.BufferType.SPANNABLE);
	}

	public void setProgressText(CharSequence progressText, TextView.BufferType type) {
		this.progressTextView.setText(progressText, type);
	}

	/**
	 * Hide the content view if it is visible. The content view will not be
	 * hidden until it has been shown for at least a minimum show time. If the
	 * content view was not yet visible, cancels showing the content view.
	 */
	public synchronized void hideContentView() {
		if (getView() != null) {
			mDismissed = true;

			getView().removeCallbacks(mDelayedShow);
			mPostedShow = false;
			long diff = System.currentTimeMillis() - mStartTime;
			if (diff >= MIN_SHOW_TIME || mStartTime == -1) {
				// The container view has been shown long enough
				// OR was not shown yet. If it wasn't shown yet,
				// it will just never be shown.
				contentViewHolder.setVisibility(View.GONE);
				progressViewHolder.setVisibility(View.VISIBLE);
			} else {
				// The container view is shown, but not long enough,
				// so put a delayed message in to hide it when its been
				// shown long enough.
				if (!mPostedHide) {
					getView().postDelayed(mDelayedHide, MIN_SHOW_TIME - diff);
					mPostedHide = true;
				}
			}
		}
	}

	/**
	 * Show the content view after waiting for a minimum delay. If
	 * during that time, hide() is called, the view is never made visible.
	 */
	public synchronized void showContentView() {
		// Reset the start time.
		if (getView() != null) {
			mStartTime = -1;
			mDismissed = false;

			getView().removeCallbacks(mDelayedHide);
			mPostedHide = false;
			if (!mPostedShow) {
				getView().postDelayed(mDelayedShow, MIN_DELAY);
				mPostedShow = true;
			}
		}
	}
}
