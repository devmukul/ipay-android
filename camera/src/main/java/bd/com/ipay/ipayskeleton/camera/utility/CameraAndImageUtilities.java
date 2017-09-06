package bd.com.ipay.ipayskeleton.camera.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Source:
 * http://stackoverflow.com/questions/14066038/why-image-captured-using-camera-intent-gets-rotated-on-some-devices-in-android
 * http://stackoverflow.com/questions/649154/save-bitmap-to-location
 */
public class CameraAndImageUtilities {

    public static final String NOT_AN_IMAGE = "NOT_AN_IMAGE";
    public static final String MULTIPLE_FACES = "MULTIPLE_FACES";
    public static final String NO_FACE_DETECTED = "NO_FACE_DETECTED";
    public static final String VALID_PROFILE_PICTURE = "VALID_PROFILE_PICTURE";

    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 512x512 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException,NullPointerException throws exception for error cases.
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage)
            throws IOException, NullPointerException {
        int MAX_HEIGHT = 512;
        int MAX_WIDTH = 512;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);

        if (imageStream == null) {
            throw new NullPointerException("Can\'t open image");
        }
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        if (imageStream == null) {
            throw new NullPointerException("Can\'t open image");
        }
        Bitmap img = rotateImageIfRequired(BitmapFactory.decodeStream(imageStream, null, options), selectedImage);
        imageStream.close();
        return img;
    }

    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 512x512 resolution
     *
     * @param cameraFace    The Camera Face of the image
     * @param selectedImage The Image URI
     * @return Bitmap image results
     */
    public static Bitmap handleSamplingAndRotationBitmap(int cameraFace, byte[] imageData) {
        int MAX_HEIGHT = 1024;
        int MAX_WIDTH = 1024;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return rotateImageIfRequired(cameraFace, BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options));
    }

    /**
     * Rotate an image if required.
     *
     * @param imageBitmap   The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     * @throws IOException throws exception for error cases.
     */
    private static Bitmap rotateImageIfRequired(Bitmap imageBitmap, Uri selectedImage) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());

        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(imageBitmap, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(imageBitmap, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(imageBitmap, 270);
            default:
                return imageBitmap;
        }
    }

    /**
     * Rotate an image if required.
     *
     * @param cameraFace    flag to detect which camera is used to capture
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    public static Bitmap rotateImageIfRequired(int cameraFace, Bitmap imageBitmap) {
        if (imageBitmap.getHeight() > imageBitmap.getWidth())
            return imageBitmap;
        else if (cameraFace == com.google.android.gms.vision.CameraSource.CAMERA_FACING_BACK)
            return rotateImage(imageBitmap, 90);
        else
            return rotateImage(imageBitmap, 270);
    }

    /**
     * Rotates the image using the degree parameter.
     *
     * @param imageBitmap bitmap data of the image.
     * @param degree      angle to rotate.
     * @return Rotated image bitmap.
     */
    public static Bitmap rotateImage(Bitmap imageBitmap, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        if (rotatedImg != imageBitmap)      // Android might reuse the same bitmap again
            imageBitmap.recycle();

        return rotatedImg;
    }

    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options   An options object with out* params already populated (run through a decode*
     *                  method with inJustDecodeBounds==true
     * @param reqWidth  The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    /**
     * Checks if a profile picture is proper or not.
     *
     * @param context, selectedImageUri
     * @return Returns null when its a valid profile picture.
     * Else returns String stating the problem in the picture which is selected to upload.
     */
    public static String validateProfilePicture(Context context, String selectedImageUri) {
        int MAX_HEIGHT = 512;
        int MAX_WIDTH = 512;

        String result;
        FaceDetector detector;

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true; // Decode with inJustDecodeBounds=true to check dimensions
            BitmapFactory.decodeFile(selectedImageUri, options);

            // For larger bitmap will try to load a smaller version into memory.
            // So will reduced the inSampleSize value that is a power of two based on a target width and height.
            options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri, options); // Decode bitmap with reduced size

            // First, check if the file selected is an image.
            if (options.outWidth != -1 && options.outHeight != -1) {
                // This is an image file
                // Now initialize the face detector
                detector = new FaceDetector.Builder(context)
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .build();

                // This is a temporary workaround for a bug in the face detector with respect to operating
                // on very small images.  This will be fixed in a future release.  But in the near term, use
                // of the SafeFaceDetector class will patch the issue.
                Detector<Face> safeDetector = new SafeFaceDetector(detector);

                Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                SparseArray<Face> faces = safeDetector.detect(frame);

                // Check if the face detection is operational.
                if (!safeDetector.isOperational()) {
                    // Face detector needs a native library to be downloaded before it works perfectly.
                    // If the download interrupts, it may fail to initialize and will return erroneous value.
                    // We can not stop user from uploading profile picture if the face detector library is not available. So return valid instead.
                    // So return null
                    result = null;
                } else {
                    // Face detection is operational
                    switch (faces.size()) {
                        case 0:
                            result = NO_FACE_DETECTED;
                            break;
                        case 1:
                            result = VALID_PROFILE_PICTURE; // This is the valid case
                            break;
                        default:
                            result = MULTIPLE_FACES;
                            break;
                    }
                }

                // When it is no longer needed in order to free native resources.
                safeDetector.release();

            } else {
                // This is not an image file
                result = NOT_AN_IMAGE;
            }
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    public static void saveBitmapToFile(Bitmap bmp, File file) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 85, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void saveImageBitmap(String fileName, Bitmap bitmap, Context context) {
        try {
            File documentFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            if (!documentFile.exists()) {
                documentFile.getParentFile().mkdirs();
            }

            FileOutputStream stream = new FileOutputStream(documentFile); // overwrites this image every time
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
