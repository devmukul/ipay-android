package bd.com.ipay.ipayskeleton.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DocumentPreviewActivity extends AppCompatActivity {

    // Load a url (of an image, a pdf etc) in a webview.
    // File extension can be .pdf, .jpg etc. If you want to load a web page instead
    // of a file, pass null or empty string here.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String documentUrl = getIntent().getStringExtra(Constants.DOCUMENT_URL);
        String fileExtension = getIntent().getStringExtra(Constants.FILE_EXTENSION);
        String documentTypeName = getIntent().getStringExtra(Constants.DOCUMENT_TYPE_NAME);

        setTitle(documentTypeName);

        WebView webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);

        if (fileExtension.endsWith("pdf")) {
            webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + documentUrl);
        } else if (fileExtension.endsWith("jpg") || fileExtension.endsWith("jpeg") || fileExtension.endsWith("png")) {
            // For loading images
            webView.loadData("<html><head><style type='text/css'>body{margin:auto auto;text-align:center;} img{width:100%25;} </style></head><body><img src='" +
                    documentUrl + "'/></body></html>","text/html",  "UTF-8");
        } else {
            webView.loadUrl(documentUrl);
        }

        setContentView(webView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
