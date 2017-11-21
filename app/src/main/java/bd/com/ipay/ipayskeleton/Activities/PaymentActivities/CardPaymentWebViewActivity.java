package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class CardPaymentWebViewActivity extends AppCompatActivity {

    public static final int CARD_TRANSACTION_FAILED = -1;
    public static final int CARD_TRANSACTION_CANCELED = 0;
    public static final int CARD_TRANSACTION_SUCCESSFUL = 1;

    public static final String URL_REGEX_APP_CARD_CANCELLED = "(.+)app/card/cancelled(/?.*)";
    public static final String URL_REGEX_APP_CARD_FAILED = "(.+)app/card/failed(/?.*)";
    public static final String URL_REGEX_APP_TRANSACTION_CARD = "(.+)/app/transaction/card/(.+)";

    private static final String TRANSACTION_ID_POSITION = "$2";

    private MaterialDialog transactionCancelDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_or_debit_card_payment_web_view);
        final WebView mWebView = (WebView) findViewById(R.id.web_view);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });


        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

                if (error.getUrl().matches(Constants.VALID_IPAY_BD_ADDRESS)) {
                    showSslErrorDialog(handler, error);
                } else {
                    super.onReceivedSslError(view, handler, error);
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Logger.logD("Redirect URL", url);
                if (url.matches(URL_REGEX_APP_CARD_CANCELLED)) {
                    finishWithResult(CARD_TRANSACTION_CANCELED, null);
                    return true;
                } else if (url.matches(URL_REGEX_APP_CARD_FAILED)) {
                    finishWithResult(CARD_TRANSACTION_FAILED, null);
                    return true;
                } else if (url.matches(URL_REGEX_APP_TRANSACTION_CARD)) {
                    finishWithResult(CARD_TRANSACTION_SUCCESSFUL, url.replaceAll(URL_REGEX_APP_TRANSACTION_CARD, TRANSACTION_ID_POSITION));
                    return true;
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }
        });
        mWebView.getSettings().setJavaScriptEnabled(true);
        final String cardPaymentUrl = getIntent().getStringExtra(Constants.CARD_PAYMENT_URL);
        mWebView.loadUrl(cardPaymentUrl);
    }

    private void showSslErrorDialog(final SslErrorHandler handler, SslError error) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(CardPaymentWebViewActivity.this);
        String message = "";
        switch (error.getPrimaryError()) {
            case SslError.SSL_UNTRUSTED:
                message = getString(R.string.ssl_untrustred_message);
                break;
            case SslError.SSL_EXPIRED:
                message = getString(R.string.ssl_expired_message);
                break;
            case SslError.SSL_IDMISMATCH:
                message = getString(R.string.ssl_id_mismatch_message);
                break;
            case SslError.SSL_NOTYETVALID:
                message = getString(R.string.ssl_not_yet_valid_message);
                break;
        }
        message += getString(R.string.do_you_want_to_continue);
        builder.setTitle(R.string.ssl_error_title);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.continue_message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.proceed();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handler.cancel();
            }
        });
        final AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        if (transactionCancelDialog != null && transactionCancelDialog.isShowing()) {
            return;
        }

        transactionCancelDialog = new MaterialDialog.Builder(this).
                content(R.string.card_transaction_cancel_warning).
                positiveText(R.string.continue_message).
                negativeText(R.string.cancel).
                onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        finishWithResult(CARD_TRANSACTION_CANCELED, null);
                        dialog.cancel();
                    }
                }).cancelable(false).build();
        transactionCancelDialog.show();
    }

    private void finishWithResult(final int transactionStatusCode, final String transactionId) {
        Bundle data = new Bundle();
        data.putInt(Constants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_STATUS, transactionStatusCode);
        Intent intent = new Intent();
        intent.putExtra(Constants.CARD_TRANSACTION_DATA, data);

        switch (transactionStatusCode) {
            case CARD_TRANSACTION_CANCELED:
                showTransactionErrorDialog(intent, getString(R.string.add_money_from_credit_or_debit_card_cancel_title), getString(R.string.add_money_from_credit_or_debit_card_cancel_message));
                break;
            case CARD_TRANSACTION_FAILED:
                showTransactionErrorDialog(intent, getString(R.string.add_money_from_credit_or_debit_card_failed_title), getString(R.string.add_money_from_credit_or_debit_card_failed_message));
                break;
            case CARD_TRANSACTION_SUCCESSFUL:
                data.putString(Constants.TRANSACTION_ID, transactionId);
                setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }

    private void showTransactionErrorDialog(final Intent intent, String title, String message) {
        MaterialDialog transactionErrorDialog = new MaterialDialog.Builder(this).
                title(title).
                content(message).
                negativeText(R.string.ok).
                onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.cancel();
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).cancelable(false).build();
        transactionErrorDialog.show();
    }
}