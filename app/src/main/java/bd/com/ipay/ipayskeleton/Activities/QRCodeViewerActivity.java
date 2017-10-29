package bd.com.ipay.ipayskeleton.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Contents;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.QRCodeEncoder;
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

    private ImageView myImage;

    public static final int REQUEST_CODE_PERMISSION = 1001;

    private static int APPROPRIATE_TEXT_SIZE_UPPER = 180;
    private static int APPROPRIATE_TEXT_SIZE_LOWER = 160;
    private static int QR_CODE_HELPER_TEXT_WIDTH = 300;
    private static int QR_CODE_HELPER_TEXT_HEIGHT = 300;
    private static int APPORX_VALUE = 50;

    private static final String HELPER_TEXT_UPPER = "scan me";
    private static final String HELPER_TEXT_LOWER = "to pay me";

    private Point size;
    private Display display;

    private static final String[] NECESSARY_PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code_viewer);

        mShareButton = (Button) findViewById(R.id.share_button);

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
        getDisplayInfo();
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

    public void getDisplayInfo() {
        float density = getResources().getDisplayMetrics().density;
        float test = density;
        if (density >= 1.5 && density < 2) {
            APPORX_VALUE = 30;
            QR_CODE_HELPER_TEXT_HEIGHT = QR_CODE_HELPER_TEXT_WIDTH = 100;
            APPROPRIATE_TEXT_SIZE_LOWER = 80;
            APPROPRIATE_TEXT_SIZE_UPPER = 90;
        } else if (density >= 2 && density < 2.5) {
            APPORX_VALUE = 40;
            QR_CODE_HELPER_TEXT_HEIGHT = QR_CODE_HELPER_TEXT_WIDTH = 133;
            APPROPRIATE_TEXT_SIZE_LOWER = 106;
            APPROPRIATE_TEXT_SIZE_UPPER = 120;
        } else if (density >= 2.5 && density < 3.5) {
            APPORX_VALUE = 70;
            QR_CODE_HELPER_TEXT_HEIGHT = QR_CODE_HELPER_TEXT_WIDTH = 200;
            APPROPRIATE_TEXT_SIZE_LOWER = 180;
            APPROPRIATE_TEXT_SIZE_UPPER = 200;
        } else if (density >= 3.5 && density <= 4) {
            APPORX_VALUE = 100;
            QR_CODE_HELPER_TEXT_HEIGHT = QR_CODE_HELPER_TEXT_WIDTH = 266;
            APPROPRIATE_TEXT_SIZE_LOWER = 220;
            APPROPRIATE_TEXT_SIZE_UPPER = 250;
        }

    }

    private void shareQrCode() {
        String imageName = "Qr payment.png";
        String share_qr_code_message = getString(R.string.scan_this_qr_code_prompt) + " " +
                ProfileInfoCacheManager.getUserName() + " " + getString(R.string.scan_this_qr_code_prompt_continue);

        CameraAndImageUtilities.saveImageBitmap(imageName, bitmap, QRCodeViewerActivity.this);
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

    private void setQrCode(String stringToEncode) {
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        display = manager.getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        int smallerDimension = size.x < size.y ? size.x : size.y;

        Map<EncodeHintType, ErrorCorrectionLevel> hintMap = new HashMap<EncodeHintType, ErrorCorrectionLevel>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(stringToEncode, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            myImage = (ImageView) findViewById(R.id.qr_code_imageview);
            createAndSetQRCode(stringToEncode, "UTF-8", hintMap, smallerDimension, smallerDimension);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap mergeBitmaps(Bitmap overlay, Bitmap bitmap) {

        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        Bitmap combined = Bitmap.createBitmap(width, height, bitmap.getConfig());
        Canvas canvas = new Canvas(combined);
        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();

        canvas.drawBitmap(bitmap, new Matrix(), null);
        int centreX = (canvasWidth - overlay.getWidth()) / 2;
        int centreY = (canvasHeight - overlay.getHeight()) / 2;
        canvas.drawBitmap(overlay, centreX, centreY, null);

        return combined;
    }

    public void createAndSetQRCode(String qrCodeData, String charset, Map hintMap, int qrCodeheight, int qrCodewidth) {


        try {
            //generating qr code.
            BitMatrix matrix = new MultiFormatWriter().encode(new String(qrCodeData.getBytes(charset), charset),
                    BarcodeFormat.QR_CODE, qrCodewidth, qrCodeheight, hintMap);
            //converting bitmatrix to bitmap

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];
            // All are 0, or black, by default
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    //for black and white
                    pixels[offset + x] = matrix.get(x, y) ? BLACK : WHITE;
                    //for custom color
                    /*pixels[offset + x] = matrix.get(x, y) ?
                            ResourcesCompat.getColor(getResources(),R.color.colorPrimary,null) :WHITE;*/
                }
            }
            //creating bitmap
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            //getting the logo
            Bitmap overlay = BitmapFactory.decodeResource(getResources(), R.drawable.ic_airtel2);
            Bitmap mergedBitmap = mergeBitmaps(overlay, bitmap);
            bitmap = mergedBitmap;
            bitmap = drawTextToBitmap(bitmap);
            myImage.setImageBitmap(bitmap);


        } catch (Exception er) {
            Log.e("QrGenerate", er.getMessage());
        }
    }

    public Bitmap drawTextToBitmap(Bitmap bitmap) {
        Bitmap combined = Bitmap.createBitmap(bitmap.getWidth() + QR_CODE_HELPER_TEXT_WIDTH, bitmap.getHeight() + QR_CODE_HELPER_TEXT_HEIGHT, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(combined);
        Paint paint = new Paint();
        canvas.drawBitmap(bitmap, QR_CODE_HELPER_TEXT_WIDTH / 2, QR_CODE_HELPER_TEXT_HEIGHT / 2, null);
        paint.setColor(Color.GRAY);
        paint.setTextSize(APPROPRIATE_TEXT_SIZE_UPPER);
        canvas.drawText(HELPER_TEXT_UPPER, QR_CODE_HELPER_TEXT_WIDTH + APPORX_VALUE, QR_CODE_HELPER_TEXT_WIDTH, paint);
        paint.setTextSize(APPROPRIATE_TEXT_SIZE_LOWER);
        canvas.drawText(HELPER_TEXT_LOWER, QR_CODE_HELPER_TEXT_WIDTH + APPORX_VALUE, bitmap.getHeight() + QR_CODE_HELPER_TEXT_HEIGHT / 2, paint);
        return combined;
    }

    @Override
    public Context setContext() {
        return QRCodeViewerActivity.this;
    }
}
