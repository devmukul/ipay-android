package bd.com.ipay.ipayskeleton.camera;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
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

    private ImageView cameraChange;
    private ImageView crossButton;
    private ImageView okButton;
    private ImageView capture;
    private ImageView mFlash;

    private float mDist = 0;


    private boolean FLASH_AUTO = false;
    private boolean FOCUS_AUTO = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraPreview = (CameraSourcePreview) findViewById(R.id.camera_preview);
        mCameraOverlay = (CameraOverlay) findViewById(R.id.face_tracking_view);
        capture = (ImageView) findViewById(R.id.capture);
        cameraChange = (ImageView) findViewById(R.id.camera_change);
        crossButton = (ImageView) findViewById(R.id.cross);
        okButton = (ImageView) findViewById(R.id.right);
        mFlash = (ImageView) findViewById(R.id.flash);
        mFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK) {
                    if (FLASH_AUTO) {
                        FLASH_AUTO = false;
                        mFlash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_off_white_24dp));
                        mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    } else {
                        FLASH_AUTO = true;
                        mFlash.setImageDrawable(getResources().getDrawable(R.drawable.ic_flash_on_white_24dp));
                        mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    }
                }
            }
        });

        cameraChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK) {
                    mCameraPreview.stop();
                    FLASH_AUTO = true;
                    FOCUS_AUTO = true;
                    createCameraSource(FOCUS_AUTO, FLASH_AUTO, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
                    mFlash.setVisibility(View.GONE);
                } else if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT) {
                    mCameraPreview.stop();
                    FLASH_AUTO = false;
                    FOCUS_AUTO = false;
                    createCameraSource(FOCUS_AUTO, FLASH_AUTO, com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK);
                    mFlash.setVisibility(View.VISIBLE);
                }
            }
        });
        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    mCameraSource.takePicture(null, new CameraSource.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data) {
                            mCameraPreview.stop();
                            File mFolder = new File(getFilesDir() + "/sample");
                            File pictureFile = new File(mFolder.getAbsolutePath() + "/someimage.jpg");
                            if (!mFolder.exists()) {
                                mFolder.mkdir();
                            }
                            try {
                                if (!pictureFile.exists()) {
                                    pictureFile.createNewFile();
                                }
                            } catch (Exception e) {

                            }
                            try {
                                FileOutputStream fos = new FileOutputStream(pictureFile);
                                fos.write(data);
                                //filePath = pictureFile.getPath();
                                capture.setVisibility(View.GONE);
                                cameraChange.setVisibility(View.GONE);
                                mFlash.setVisibility(View.GONE);
                                crossButton.setVisibility(View.VISIBLE);
                                okButton.setVisibility(View.VISIBLE);
                                fos.close();
                            } catch (Exception e) {
                            }
                        }
                    });
                } catch (Exception e) {

                }
            }

        });
        crossButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capture.setVisibility(View.VISIBLE);
                cameraChange.setVisibility(View.VISIBLE);
                crossButton.setVisibility(View.GONE);
                okButton.setVisibility(View.GONE);
                if (mCameraSource.getCameraFacing() == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK)
                    mFlash.setVisibility(View.VISIBLE);
                try {
                    mCameraPreview.start(mCameraSource, mCameraOverlay);

                } catch (SecurityException e) {

                } catch (Exception e) {

                }
            }
        });

        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
        } else {
            requestCameraPermission();
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

    @Override
    protected void onResume() {
        super.onResume();
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            mCameraPreview.stop();
            createCameraSource(FLASH_AUTO, FOCUS_AUTO, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // we have permission, so create the camerasource
            createCameraSource(true, false, com.google.android.gms.vision.CameraSource.CAMERA_FACING_FRONT);
        }
    }

    private void createCameraSource(boolean autoFocus, boolean useFlash, int cameraFacing) {
        Context context = getApplicationContext();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.


        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
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


        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(
                    autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }


        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
        startCameraSource();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Get the pointer ID
        Camera.Parameters params;
        try {
            if (mCameraSource != null) {
                params = mCameraSource.getCamera().getParameters();

                int action = event.getAction();

                if (event.getPointerCount() > 1) {
                    // handle multi-touch events
                    if (action == MotionEvent.ACTION_POINTER_DOWN) {
                        mDist = getFingerSpacing(event);
                    } else if (action == MotionEvent.ACTION_MOVE && params.isZoomSupported()) {
                        //mCameraSource.getCamera().cancelAutoFocus();
                        handleZoom(event);
                    }
                } else {
                    // handle single touch events
                    if (action == MotionEvent.ACTION_UP) {
                        handleFocus(event, params);
                    }
                }
                return true;
            } else return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void handleFocus(MotionEvent event, Camera.Parameters params) {
        int pointerId = event.getPointerId(0);
        int pointerIndex = event.findPointerIndex(pointerId);
        // Get the pointer's current position
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
        // ...
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
            //faceOverlayGraphics.setId(faceId);
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

