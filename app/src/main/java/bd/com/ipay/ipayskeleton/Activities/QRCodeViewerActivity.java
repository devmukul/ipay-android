package bd.com.ipay.ipayskeleton.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.util.EnumMap;
import java.util.Map;

import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;
import bd.com.ipay.ipayskeleton.camera.utility.CameraAndImageUtilities;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class QRCodeViewerActivity extends BaseActivity {

    private String stringToEncode = "";
    private String titleOfTheActivity = "";

    private Button mShareButton;
    private Bitmap bitmap;
    private LinearLayout mLinearLayout;
    private LinearLayout mDummyLayout;

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private static final String[] NECESSARY_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_viewer);

        mShareButton = (Button) findViewById(R.id.share_button);
        mLinearLayout = (LinearLayout) findViewById(R.id.drawing_cache_layout);
        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilities.isNecessaryPermissionExists(QRCodeViewerActivity.this, NECESSARY_PERMISSIONS)) {
                    shareQrCode();
                } else {
                    Utilities.requestRequiredPermissions(QRCodeViewerActivity.this, REQUEST_CODE_PERMISSION, NECESSARY_PERMISSIONS);
                }
            }
        });

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
 
    public void createAndSetQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {

        try {
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            ImageView myImage = (ImageView) findViewById(R.id.qr_code_imageview);
            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.qr_ipay_logo);
            bitmap = mergeBitmaps(overlay, bitmap);
            myImage.setImageBitmap(bitmap);
        } catch (Exception er) {

        }
    }

    private void shareQrCode() {
        String imageName = "Qr payment.png";
        String share_qr_code_message = getString(R.string.scan_this_qr_code_prompt) + " " +
                ProfileInfoCacheManager.getUserName() + " " + getString(R.string.scan_this_qr_code_prompt_continue);
        CameraAndImageUtilities.saveImageBitmap(imageName, getBitmapFromLayout(), QRCodeViewerActivity.this);
        if (!TextUtils.isEmpty(imageName)) {

            File qrCodeFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageName);
            Uri contentUri = FileProvider.getUriForFile(QRCodeViewerActivity.this, BuildConfig.APPLICATION_ID, qrCodeFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setType(getContentResolver().getType(contentUri));
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.ipay_qr_code_title));
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, share_qr_code_message);
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Choose an app to share"));
            } else {
                DialogUtils.showAlertDialog(QRCodeViewerActivity.this, getString(R.string.file_save_failed));
            }
        } else {
            DialogUtils.showAlertDialog(QRCodeViewerActivity.this, getString(R.string.qr_code_unavailable));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    shareQrCode();
                } else {
                    Toaster.makeText(this, R.string.ef_ltitle_permission_denied, Toast.LENGTH_LONG);
                }
                break;
        }
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

    private Bitmap getBitmapFromLayout() {
        mLinearLayout.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(mLinearLayout.getDrawingCache(true));
        return bitmap;
    }

    private void setQrCode(String stringToEncode) {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int smallerDimension = size.x < size.y ? size.x : size.y;
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 0);
            createAndSetQRCode(stringToEncode, "UTF-8", hints, smallerDimension, smallerDimension);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {
        Canvas canvas = new Canvas(bitmap);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, paint);
        return bitmap;
    }

    @Override
    public Context setContext() {
        return QRCodeViewerActivity.this;
    }
}