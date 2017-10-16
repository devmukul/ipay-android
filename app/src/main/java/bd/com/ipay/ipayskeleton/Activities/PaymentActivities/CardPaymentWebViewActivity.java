package bd.com.ipay.ipayskeleton.Activities.PaymentActivities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

public class CardPaymentWebViewActivity extends AppCompatActivity {

    public static final int CARD_TRANSACTION_FAILED = -1;
    public static final int CARD_TRANSACTION_CANCELED = 0;
    public static final int CARD_TRANSACTION_SUCCESSFUL = 1;

    private MaterialDialog transactionCancelDialog;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_or_debit_card_payment_web_view);
        WebView mWebView = (WebView) findViewById(R.id.web_view);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (Constants.SERVER_TYPE != Constants.SERVER_TYPE_LIVE) {
                    handler.proceed();
                }
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return super.shouldInterceptRequest(view, request);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Logger.logD("Redirect URL", url);
                if (url.matches("(.+)app/card/cancelled(/?.*)")) {
                    finishWithResult(CARD_TRANSACTION_CANCELED, null);
                    return true;
                } else if (url.matches("(.+)app/card/failed(/?.*)")) {
                    finishWithResult(CARD_TRANSACTION_FAILED, null);
                    return true;
                } else if (url.matches("(.+)/app/transaction/card/(.+)")) {
                    finishWithResult(CARD_TRANSACTION_SUCCESSFUL, url.replaceAll("(.+)/app/transaction/card/(.+)", "$2"));
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

    @Override
    public void onBackPressed() {
        if (transactionCancelDialog != null && transactionCancelDialog.isShowing()) {
            return;
        }

        transactionCancelDialog = new MaterialDialog.Builder(this).
                content(R.string.card_transaction_cancel_warning).
                positiveText(R.string.continue_add_money).
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
        data.putString(Constants.TRANSACTION_ID, transactionId);
        data.putInt(Constants.ADD_MONEY_BY_CREDIT_OR_DEBIT_CARD_STATUS, transactionStatusCode);
        Intent intent = new Intent();
        intent.putExtra(Constants.CARD_TRANSACTION_DATA,data);
        setResult(RESULT_OK, intent);
        finish();
    }
}
