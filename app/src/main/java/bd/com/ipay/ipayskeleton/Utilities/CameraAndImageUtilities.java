package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

/**
 * Source:
 * http://stackoverflow.com/questions/14066038/why-image-captured-using-camera-intent-gets-rotated-on-some-devices-in-android
 * http://stackoverflow.com/questions/649154/save-bitmap-to-location
 */
public class CameraAndImageUtilities {

    /**
     * This method is responsible for solving the rotation issue if exist. Also scale the images to
     * 512x512 resolution
     *
     * @param context       The current context
     * @param selectedImage The Image URI
     * @return Bitmap image results
     * @throws IOException
     */
    public static Bitmap handleSamplingAndRotationBitmap(Context context, Uri selectedImage, boolean fromCamera)
            throws IOException {
        int MAX_HEIGHT = 512;
        int MAX_WIDTH = 512;

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream imageStream = context.getContentResolver().openInputStream(selectedImage);
        BitmapFactory.decodeStream(imageStream, null, options);
        imageStream.close();

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, MAX_WIDTH, MAX_HEIGHT);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        imageStream = context.getContentResolver().openInputStream(selectedImage);
        Bitmap img = BitmapFactory.decodeStream(imageStream, null, options);

        img = rotateImageIfRequired(context, img, selectedImage, fromCamera);

        return img;
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
     * Rotate an image if required.
     *
     * @param img           The image bitmap
     * @param selectedImage Image URI
     * @return The resulted Bitmap after manipulation
     */
    private static Bitmap rotateImageIfRequired(Context context, Bitmap img, Uri selectedImage, boolean fromCamera) throws IOException {

        ExifInterface ei = new ExifInterface(selectedImage.getPath());

        if (fromCamera) {
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Logger.logW("Orientation - Camera", orientation + "");

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 270);
                default:
                    return img;
            }
        } else {
            String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
            Cursor cur = context.getContentResolver().query(selectedImage, orientationColumn, null, null, null);
            int orientation = 0;
            if (cur != null && cur.moveToFirst()) {
                orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            }
            if (cur != null)
                cur.close();

            Logger.logW("Orientation - Gallery", orientation + "");

            return rotateImage(img, orientation);
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        if (rotatedImg != img)      // Android might reuse the same bitmap again
            img.recycle();

        return rotatedImg;
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

    /**
     * Checks if a profile picture is proper or not.
     *
     * @param context, selectedImageUri
     * @return Returns null when its a valid profile picture.
     * Else returns String stating the problem in the picture which is selected to upload.
     */
    public static String validateProfilePicture(Context context, String selectedImageUri) {

        String result;
        FaceDetector detector;

        BitmapFactory.Options options = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri, options);

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
                        result = Constants.NO_FACE_DETECTED;
                        break;
                    case 1:
                        result = null; // Set null value when a single face is detected. VALID PROFILE PICTURE
                        break;
                    default:
                        result = Constants.MULTIPLE_FACES;
                        break;
                }
            }

            // When it is no longer needed in order to free native resources.
            safeDetector.release();

        } else {
            // This is not an image file
            result = Constants.NOT_AN_IMAGE;
        }

        return result;
    }
}
