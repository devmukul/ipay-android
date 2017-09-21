package bd.com.ipay.ipayskeleton.camera;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.camera.utility.CameraAndImageUtilities;


public class CameraActivity extends AppCompatActivity {

    public static final String CAMERA_FACING_NAME = "CAMERA_FACING_NAME";
    public static final String DOCUMENT_NAME = "DOCUMENT_NAME";
    private static final String TAG = CameraActivity.class.getSimpleName();
    private CameraSource mCameraSource;
    private CameraSourcePreview mCameraPreview;
    private CameraOverlay mCameraOverlay;

    private ImageButton mCameraChangeButton;
    private ImageButton mCrossButton;
    private ImageButton mOkButton;
    private ImageButton mCaptureButton;
    private ImageButton mFlashChangeButton;
    private ImageView mCapturedImageView;

    private float mDist = 0;

    private String flashMode = Camera.Parameters.FLASH_MODE_OFF;
    private boolean focusAuto = false;
    private int cameraFace = com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK;

    private String fileName;

    private byte[] imageData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initializeViews();
        setButtonActions();
        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        String tempFileName = getIntent().getStringExtra(DOCUMENT_NAME);
        fileName = !TextUtils.isEmpty(tempFileName) ? tempFileName : "document.jpg";
        cameraFace = getIntent().getIntExtra(CAMERA_FACING_NAME, com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, Camera.Parameters.FLASH_MODE_OFF, cameraFace);
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCameraPreview != null && mCameraPreview.getVisibility() == View.INVISIBLE) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mCameraPreview.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }, 750);
        }
        if (mCameraPreview != null && mCapturedImageView.getVisibility() == View.GONE) {
            try {
                mCameraPreview.start(mCameraSource, mCameraOverlay);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCameraPreview != null) {
            mCameraPreview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mCameraPreview != null) {
            mCameraPreview.release();
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
        }
    }

    private void previewFrontCamera() {
        mCameraPreview.stop();
        createCameraSource(focusAuto, flashMode, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
    }

    private void previewBackCamera() {
        mCameraPreview.stop();
        createCameraSource(focusAuto, flashMode, com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK);
    }

    private void initializeViews() {
        mCameraPreview = (CameraSourcePreview) findViewById(R.id.camera_preview);
        mCapturedImageView = (ImageView) findViewById(R.id.captured_image_view);
        mCameraOverlay = (CameraOverlay) findViewById(R.id.face_tracking_view);
        mCaptureButton = (ImageButton) findViewById(R.id.capture_button);
        mCameraChangeButton = (ImageButton) findViewById(R.id.camera_change_button);
        mCrossButton = (ImageButton) findViewById(R.id.cross_button);
        mOkButton = (ImageButton) findViewById(R.id.ok_button);
        mFlashChangeButton = (ImageButton) findViewById(R.id.flash_button);
    }

    private void setButtonActions() {
        mFlashChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK) {
                    if (mCameraSource.getFlashMode() != null) {
                        if (mCameraSource.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                            flashMode = Camera.Parameters.FLASH_MODE_ON;
                            setAppropriateFlashIcon();
                            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        } else if (mCameraSource.getFlashMode().equals(Camera.Parameters.FLASH_MODE_ON)) {
                            flashMode = Camera.Parameters.FLASH_MODE_AUTO;
                            setAppropriateFlashIcon();
                            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        } else if (mCameraSource.getFlashMode().equals(Camera.Parameters.FLASH_MODE_AUTO)) {
                            flashMode = Camera.Parameters.FLASH_MODE_OFF;
                            setAppropriateFlashIcon();
                            mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        }
                    }
                }

            }
        });

        mCameraChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            previewFrontCamera();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mFlashChangeButton.setVisibility(View.GONE);
                                }
                            });
                        }
                    }, 100);
                    setAppropriateCameraFaceIcon();

                } else if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            previewBackCamera();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mFlashChangeButton.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    }, 100);
                    setAppropriateCameraFaceIcon();
                }
            }
        });
        mCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data) {
                            mCameraPreview.stop();

                            try {
                                imageData = Arrays.copyOf(data, data.length);
                                Bitmap convertedBitmap = CameraAndImageUtilities.handleSamplingAndRotationBitmap(cameraFace, data);
                                mCapturedImageView.setImageBitmap(convertedBitmap);
                                showConfirmImageLayoutAndHideCaptureLayout();
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage(), e);
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }

        });
        mCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureLayoutAndHideConfirmImageLayout();
                try {
                    imageData = null;
                    mCameraPreview.start(mCameraSource, mCameraOverlay);

                } catch (SecurityException e) {
                    Log.e(TAG, e.getMessage(), e);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                if (imageData != null) {
                    File pictureFile = getTempFile();
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(pictureFile);
                        fos.write(imageData);
                        fos.close();
                        setResult(RESULT_OK);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, Camera.Parameters.FLASH_MODE_OFF, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
        } else {
            this.finish();
        }
    }

    private void setAppropriateFlashIcon() {
        switch (flashMode) {
            case Camera.Parameters.FLASH_MODE_ON:
                mFlashChangeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_white_24dp));
                break;
            case Camera.Parameters.FLASH_MODE_OFF:
                mFlashChangeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_white_24dp));
                break;
            case Camera.Parameters.FLASH_MODE_AUTO:
                mFlashChangeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_auto_white_24dp));
                break;
        }
    }

    private void setAppropriateCameraFaceIcon() {
        switch (cameraFace) {
            case com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK:
                mCameraChangeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_front_white_24dp));
                break;
            case com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT:
                mCameraChangeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_camera_rear_white_24dip));
                break;
        }
    }

    @NonNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getTempFile() {
        File rootDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (rootDir == null) {
            rootDir = getFilesDir();
        }
        File documentFile = new File(rootDir, fileName);
        if (!documentFile.getParentFile().exists()) {
            documentFile.getParentFile().mkdirs();
        }
        return documentFile;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void createCameraSource(boolean focusAuto, String flashMode, int cameraFace) {
        this.focusAuto = focusAuto;
        this.flashMode = flashMode;
        this.cameraFace = cameraFace;
        setAppropriateCameraFaceIcon();
        Context context = getApplicationContext();
        FaceDetector mFaceDetector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        int width = size.y;
        int height = size.x;

        mFaceDetector.setProcessor(
                new MultiProcessor.Builder<>(new CameraActivity.GraphicFaceTrackerFactory())
                        .build());
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), mFaceDetector)
                .setFacing(cameraFace)
                .setRequestedPreviewSize(width, height)
                .setRequestedFps(30.0f)
                .setFocusMode(focusAuto ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);

        mCameraSource = builder
                .setFlashMode(TextUtils.isEmpty(flashMode) ? Camera.Parameters.FLASH_MODE_OFF : flashMode)
                .build();
        startCameraSource();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Camera.Parameters params;
        try {
            if (mCameraSource != null) {
                params = mCameraSource.getCamera().getParameters();
                int action = event.getAction();
                if (event.getPointerCount() > 1) {
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                        handleZoom(event);
                    }
                } else {
                    if (action == MotionEvent.ACTION_UP) {
                        handleFocus(params);
                    }
                }
                return true;
            } else
                return false;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
            return false;
        }
    }

    private void showCaptureLayoutAndHideConfirmImageLayout() {
        mCaptureButton.setVisibility(View.VISIBLE);
        mCameraChangeButton.setVisibility(View.VISIBLE);
        mCapturedImageView.setVisibility(View.GONE);
        mCameraPreview.setVisibility(View.VISIBLE);
        mCrossButton.setVisibility(View.GONE);
        mOkButton.setVisibility(View.GONE);
        if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK)
            mFlashChangeButton.setVisibility(View.VISIBLE);
    }

    private void showConfirmImageLayoutAndHideCaptureLayout() {
        mCaptureButton.setVisibility(View.INVISIBLE);
        mCameraChangeButton.setVisibility(View.GONE);
        mFlashChangeButton.setVisibility(View.GONE);
        mCapturedImageView.setVisibility(View.VISIBLE);
        mCameraPreview.setVisibility(View.GONE);
        mCrossButton.setVisibility(View.VISIBLE);
        mOkButton.setVisibility(View.VISIBLE);
    }

    public void handleFocus(Camera.Parameters params) {
        List<String> supportedFocusModes = params.getSupportedFocusModes();
        if (supportedFocusModes != null && supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            try {
                mCameraSource.getCamera().autoFocus(new Camera.AutoFocusCallback() {
                    @Override
                    public void onAutoFocus(boolean b, Camera camera) {
                        Camera.Parameters par = camera.getParameters();
                        par.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                        camera.setParameters(par);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void startCameraSource() {
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, 1);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mCameraPreview.start(mCameraSource, mCameraOverlay);
            } catch (SecurityException e) {
                Log.e(TAG, e.getMessage(), e);
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                mCameraSource.release();
                mCameraSource = null;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private float getFingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void handleZoom(MotionEvent event) {
        float newDist = getFingerSpacing(event);
        if (newDist > mDist) {
            mCameraSource.zoomIn();
        } else if (newDist < mDist) {
            mCameraSource.zoomOut();
        }
        mDist = newDist;
        mCameraSource.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    private class GraphicFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            return new CameraActivity.GraphicFaceTracker(mCameraOverlay);
        }
    }

    private class GraphicFaceTracker extends Tracker<Face> {
        private CameraOverlay mOverlay;
        private FaceOverlayGraphics faceOverlayGraphics;

        GraphicFaceTracker(CameraOverlay overlay) {
            mOverlay = overlay;
            faceOverlayGraphics = new FaceOverlayGraphics(overlay);
        }

        @Override
        public void onNewItem(int faceId, Face item) {
        }

        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {
            mOverlay.add(faceOverlayGraphics);
            faceOverlayGraphics.updateFace(face);
        }

        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
            mOverlay.remove(faceOverlayGraphics);
        }

        @Override
        public void onDone() {
            mOverlay.remove(faceOverlayGraphics);
        }
    }
}

