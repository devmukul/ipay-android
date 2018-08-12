package bd.com.ipay.ipayskeleton.Activities;

import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.List;

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
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            if (isLaunchedFromHomeScreen()) {
                Intent intent = new Intent(WebViewActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                finish();
            }
        }
    }

    /*We need to check if promotional notification is launched from home screen of android device,
    in that case we will try redirect to wallet page on back button press. otherwise back press will just finish
     the web view activity and behave normally
    */

    private boolean isLaunchedFromHomeScreen() {
        try {
            ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

            List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);

            if (taskList.get(0).numActivities == 1 &&
                    taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}