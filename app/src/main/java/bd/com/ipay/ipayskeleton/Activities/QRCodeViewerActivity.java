package bd.com.ipay.ipayskeleton.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Contents;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.QRCodeEncoder;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Toaster;

public class QRCodeViewerActivity extends BaseActivity {

    private String stringToEncode = "";
    private String titleOfTheActivity = "";

    private Button mShareButton;
    private Bitmap bitmap;

    public static final int REQUEST_CODE_PERMISSION = 1001;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_code_viewer);

        mShareButton = (Button) findViewById(R.id.share_button);

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(QRCodeViewerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(QRCodeViewerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
                } else {
                    performShareAction();
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

    private void performShareAction() {
        String imageName = saveImageBitmap(bitmap);
        if (!TextUtils.isEmpty(imageName)) {

            File qrCodeFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), imageName);
            Uri contentUri = FileProvider.getUriForFile(QRCodeViewerActivity.this, BuildConfig.APPLICATION_ID, qrCodeFile);

            if (contentUri != null) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); // temp permission for receiving app to read this file
                shareIntent.setType(getContentResolver().getType(contentUri));
                shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                startActivity(Intent.createChooser(shareIntent, "Choose an app"));
            } else {
                DialogUtils.showUnableToSaveFileDialog(QRCodeViewerActivity.this);
            }
        } else {
            DialogUtils.showQRCodeNotAvailableDialog(QRCodeViewerActivity.this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performShareAction();
                } else {
                    Toaster.makeText(this, R.string.ef_ltitle_permission_denied, Toast.LENGTH_LONG);
                }
                break;
        }
    }

    private String saveImageBitmap(Bitmap bitmap) {
        try {
            final String qrImageName = "QR Payment" + ".png";
            File documentFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), qrImageName);
            documentFile.getParentFile().mkdirs();

            FileOutputStream stream = new FileOutputStream(documentFile); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
            return qrImageName;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
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
        Point size = new Point();
        display.getSize(size);
        int smallerDimension = size.x < size.y ? size.x : size.y;
        smallerDimension = smallerDimension * 3 / 4;

        // Encode with a QR Code image
        QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(stringToEncode, null,
                Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(),
                smallerDimension);
        try {
            bitmap = qrCodeEncoder.encodeAsBitmap();
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
