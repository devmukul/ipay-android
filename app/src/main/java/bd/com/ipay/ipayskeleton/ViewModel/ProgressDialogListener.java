package bd.com.ipay.ipayskeleton.ViewModel;

public interface ProgressDialogListener {
    void showDialog();
    void dismissDialog();
    void setLoadingMessage(String message);

    void showSuccessAnimationAndMessage(final String successMessage);

    void showFailureAnimationAndMessage(String failureMessage);
}
