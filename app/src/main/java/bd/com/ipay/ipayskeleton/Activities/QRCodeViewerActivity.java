package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Contents;
import bd.com.ipay.ipayskeleton.Utilities.QRCodeEncoder;

public class QRCodeViewerActivity extends BaseActivity {

    private String stringToEncode = "";
    private String titleOfTheActivity = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_viewer);

        Intent intent = getIntent();
        if (intent != null) {
            stringToEncode = intent.getStringExtra(Constants.STRING_TO_ENCODE);
            titleOfTheActivity = intent.getStringExtra(Constants.ACTIVITY_TITLE);
        } else {
            Toast.makeText(QRCodeViewerActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            finish();
        }

        if (titleOfTheActivity != null) setTitle(titleOfTheActivity);

        if (stringToEncode != null) setQrCode(stringToEncode);
        else {
            Toast.makeText(QRCodeViewerActivity.this, R.string.service_not_available, Toast.LENGTH_LONG).show();
            finish();
        }
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

    private void setQrCode(String stringToEncode) {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        // display.getSize(point);
        int width = display.getWidth();
        int height = display.getHeight();
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(stringToEncode, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
            ImageView myImage = (ImageView) findViewById(R.id.qr_code_imageview);
            myImage.setImageBitmap(bitmap);

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context setContext() {
        return QRCodeViewerActivity.this;
    }
}
