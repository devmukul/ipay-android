package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.animation.Animator;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

import bd.com.ipay.ipayskeleton.R;


public class CustomProgressDialog extends android.support.v7.app.AlertDialog {
    private Context context;
    private LayoutInflater layoutInflater;
    private View customView;
    private LottieAnimationView animationView;
    private TextView progressDialogTextView;
    private String currentState;


    public CustomProgressDialog(Context context) {
        super(context);
        this.context = context;
        currentState = "LOADING";
    }

    public void createView() {
        customView = LayoutInflater.from(context).inflate(R.layout.view_custom_progress_dialog, null, false);
        progressDialogTextView = (TextView) customView.findViewById(R.id.progress_dialog_text_view);
        animationView = (LottieAnimationView) customView.findViewById(R.id.view_animation);
        animationView.setSpeed(1.3f);
        setUpAnimationAction();
        animationView.playAnimation();
        this.setView(customView);
        this.setCancelable(false);
        this.setCanceledOnTouchOutside(false);
    }

    public void showDialog() {
        this.show();
    }

    public void dismissDialog() {
        animationView.resumeAnimation();
        this.dismiss();
    }

    private void setUpAnimationAction() {
        animationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
                if (currentState.equals("SUCCESS") || currentState.equals("FAILED")) {
                    animationView.pauseAnimation();
                }
            }
        });
    }

    public void setLoadingMessage(String message) {
        progressDialogTextView.setText(message);
    }

    public void showSuccessAnimationAndMessage(final String successMessage) {
        animationView.pauseAnimation();
        animationView.setAnimation(R.raw.check_success);
        animationView.setMinAndMaxProgress(0.5f, 1.0f);
        animationView.playAnimation();
        progressDialogTextView.setText(successMessage);
        currentState = "SUCCESS";
    }

    public void showFailureAnimationAndMessage(String failureMessage) {
        animationView.pauseAnimation();
        animationView.setMinAndMaxProgress(0.0f, 0.3f);
        progressDialogTextView.setText(failureMessage);
        animationView.setAnimation(R.raw.cruz);
        currentState = "FAILED";
        animationView.playAnimation();
    }

    public void clearAnimation() {
        animationView.clearAnimation();
    }
}

