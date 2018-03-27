package bd.com.ipay.ipayskeleton.CustomView.Dialogs;

import android.animation.Animator;
import android.content.Context;
import android.os.Handler;
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
        createView();
    }

    private void createView() {
        customView = LayoutInflater.from(context).inflate(R.layout.view_custom_progress_dialog, null, false);
        progressDialogTextView = (TextView) customView.findViewById(R.id.progress_dialog_text_view);
        animationView = (LottieAnimationView) customView.findViewById(R.id.view_animation);
        setUpAnimationAction();
        animationView.playAnimation();
        this.setView(customView);
    }

    public void showDialog() {
        this.show();
    }

    public void dismissDialog() {
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
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animationView.pauseAnimation();
                        }
                    }, 600);

                }

            }
        });
    }

    public void setLoadingMessage(String message) {
        progressDialogTextView.setText(message);
    }

    public void showSuccessAnimationAndMessage(String successMessage) {
        animationView.pauseAnimation();
        progressDialogTextView.setText(successMessage);
        animationView.setAnimation(R.raw.check);
        animationView.setScale((float)0.1);
        animationView.playAnimation();
        currentState = "SUCCESS";
    }

    public void showFailureAnimationAndMessage(String failureMessage) {
        animationView.pauseAnimation();
        progressDialogTextView.setText(failureMessage);
        animationView.setAnimation(R.raw.failure);
        animationView.playAnimation();
    }

}
