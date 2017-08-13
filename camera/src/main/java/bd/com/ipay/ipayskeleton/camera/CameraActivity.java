package bd.com.ipay.ipayskeleton.camera;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class CameraActivity extends AppCompatActivity {
    private CameraSource mCameraSource;
    private CameraSourcePreview mCameraPreview;
    private FaceDetector mFaceDetector;
    private CameraOverlay mCameraOverlay;

    private ImageView mCameraChangeButton;
    private ImageView mCrossButton;
    private ImageView mOkButton;
    private ImageView mCaptureButton;
    private ImageView mFlashChangeButton;

    private float mDist = 0;

    private boolean FLASH = false;
    private boolean FOCUS_AUTO = false;

    private Context context;

    private static String TEMP_DOCUMENT_NAME = "document.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initializeViews();
        setButtonActions();
        int permissionCheck = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
        } else {
            requestCameraPermission();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            mCameraPreview.stop();
            createCameraSource(FLASH, FOCUS_AUTO, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
        }
    }

    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, 1);
            return;
        }
    }

    private void previewFrontCamera() {
        mCameraPreview.stop();
        FLASH = false;
        FOCUS_AUTO = true;
        createCameraSource(FOCUS_AUTO, FLASH, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
    }

    private void previewBackCamera() {
        mCameraPreview.stop();
        FLASH = false;
        FOCUS_AUTO = true;
        createCameraSource(FOCUS_AUTO, FLASH, com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK);
    }

    private void initializeViews() {
        mCameraPreview = (CameraSourcePreview) findViewById(R.id.camera_preview);
        mCameraOverlay = (CameraOverlay) findViewById(R.id.face_tracking_view);
        mCaptureButton = (ImageView) findViewById(R.id.capture_button);
        mCameraChangeButton = (ImageView) findViewById(R.id.camera_change_button);
        mCrossButton = (ImageView) findViewById(R.id.cross_button);
        mOkButton = (ImageView) findViewById(R.id.ok_button);
        mFlashChangeButton = (ImageView) findViewById(R.id.flash_button);
    }

    private void setButtonActions() {
        mFlashChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK) {
                    if (FLASH) {
                        FLASH = false;
                        setAppropriateFlashIcon();
                        mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    } else {
                        FLASH = true;
                        setAppropriateFlashIcon();
                        mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    }
                }

            }
        });

        mCameraChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK) {
                    previewFrontCamera();
                    mFlashChangeButton.setVisibility(View.GONE);

                } else if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT) {
                    previewBackCamera();
                    mFlashChangeButton.setVisibility(View.VISIBLE);
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
                            File pictureFile = getTempFile(getApplicationContext());
                            try {
                                FileOutputStream fos = new FileOutputStream(pictureFile);
                                fos.write(data);
                                showConfirmImageLayoutAndHideCaptureLayout();
                                fos.close();
                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception e) {

                }
            }

        });
        mCrossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCaptureLayoutAndHideConfirmImageLayout();
                try {
                    mCameraPreview.start(mCameraSource, mCameraOverlay);

                } catch (SecurityException e) {

                } catch (Exception e) {

                }
            }
        });
        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            FOCUS_AUTO = true;
            FLASH = false;
            createCameraSource(FOCUS_AUTO, FLASH, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
        } else {
            this.finish();
        }
    }

    private void setAppropriateFlashIcon() {
        if (FLASH)
            mFlashChangeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_white_24dp));
        else
            mFlashChangeButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_white_24dp));
    }

    private static File getTempFile(Context context) {
        File documentFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TEMP_DOCUMENT_NAME);
        if (documentFile != null) {
            documentFile.getParentFile().mkdirs();
            return documentFile;
        } else return null;
    }

    private void createCameraSource(boolean autoFocus, boolean useFlash, int cameraFacing) {
        Context context = getApplicationContext();
        mFaceDetector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        mFaceDetector.setProcessor(
                new MultiProcessor.Builder<>(new CameraActivity.GraphicFaceTrackerFactory())
                        .build());
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), mFaceDetector)
                .setFacing(cameraFacing)
                .setRequestedPreviewSize(1600, 1024)
                .setRequestedFps(30.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_OFF : null)
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
                        handleFocus(event, params);
                    }
                }
                return true;
            } else
                return false;
        } catch (Exception e) {
            return false;
        }
    }

    private void showCaptureLayoutAndHideConfirmImageLayout() {
        mCaptureButton.setVisibility(View.VISIBLE);
        mCameraChangeButton.setVisibility(View.VISIBLE);
        mCrossButton.setVisibility(View.GONE);
        mOkButton.setVisibility(View.GONE);
        if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK)
            mFlashChangeButton.setVisibility(View.VISIBLE);
    }

    private void showConfirmImageLayoutAndHideCaptureLayout() {
        mCaptureButton.setVisibility(View.GONE);
        mCameraChangeButton.setVisibility(View.GONE);
        mFlashChangeButton.setVisibility(View.GONE);
        mCrossButton.setVisibility(View.VISIBLE);
        mOkButton.setVisibility(View.VISIBLE);
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        float x = event.getX(pointerIndex);
        float y = event.getY(pointerIndex);

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
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                mCameraSource.release();
                mCameraSource = null;
            } catch (Exception e) {

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

