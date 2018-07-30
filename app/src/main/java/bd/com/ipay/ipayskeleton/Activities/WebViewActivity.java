package bd.com.ipay.ipayskeleton.Activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import bd.com.ipay.ipayskeleton.R;


public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private String uriString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        final Uri uri = Uri.parse(getIntent().getStringExtra("url"));
        uriString = uri.toString();
        webView = (WebView) findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        webView.loadUrl(uri.toString());
    }

    @Override
    public void onBackPressed() {
        if(uriString != null && !uriString.isEmpty() && uriString.toLowerCase().contains("promotions")){
            super.onBackPressed();
        }
        else {
            if (webView.canGoBack()) {
                webView.goBack();
            } else {
                super.onBackPressed();
            }
        }
    }
}