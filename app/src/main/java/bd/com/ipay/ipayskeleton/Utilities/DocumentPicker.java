package bd.com.ipay.ipayskeleton.Utilities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import bd.com.ipay.ipayskeleton.R;

/**
 * Source: https://gist.github.com/Mariovc/f06e70ebe8ca52fbbbe2
 */
public class DocumentPicker {

//    private static final int DEFAULT_MIN_WIDTH_QUALITY = 400;        // min pixels
    private static final String TAG = "Picker";
    private static final String TEMP_DOCUMENT_NAME = "document.jpg";

//    public static int minWidthQuality = DEFAULT_MIN_WIDTH_QUALITY;


    public static Intent getPickImageIntent(Context context, String chooserTitle) {

        Set<Intent> intentList = new LinkedHashSet<>();

        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));

        intentList = addIntentsToList(context, intentList, takePhotoIntent);
        intentList = addIntentsToList(context, intentList, pickIntent);

        return getChooserIntent(intentList, chooserTitle);
    }

    public static Intent getPickImageOrPdfIntent(Context context, String chooserTitle) {

        Set<Intent> intentList = new LinkedHashSet<>();

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*|application/pdf");

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));

        intentList = addIntentsToList(context, intentList, takePhotoIntent);
        intentList = addIntentsToList(context, intentList, pickIntent);

        return getChooserIntent(intentList, chooserTitle);
    }

    private static Intent getChooserIntent(Set<Intent> intentList, String chooserTitle) {
        Intent chooserIntent = null;

        if (intentList.size() > 0) {
            Intent intent = intentList.iterator().next();
            intentList.remove(intent);
            chooserIntent = Intent.createChooser(intent, chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static Set<Intent> addIntentsToList(Context context, Set<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
            Log.e(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }

    public static String getFileFromResult(Context context, int resultCode, Intent returnedIntent) {
        try {
            File documentFile = getTempFile(context);

            boolean isCamera = (returnedIntent == null ||
                    returnedIntent.getData() == null ||
                    returnedIntent.getData().toString().contains(documentFile.toString()));

            if (isCamera) {     /** CAMERA **/
                return getTempFile(context).getAbsolutePath();
            } else if (returnedIntent.getData().toString().startsWith("file://")) {
                return returnedIntent.getData().toString();
            } else {            /** ALBUM **/
                return Utilities.getFilePath(context, returnedIntent.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static Uri getDocumentFromResult(Context context, int resultCode, Intent returnedIntent) {
        Log.e(TAG, "getDocumentFromResult, resultCode: " + resultCode);
//        Bitmap bm = null;
        Uri selectedImage = null;
        try {
            File documentFile = getTempFile(context);
            if (resultCode == Activity.RESULT_OK) {
                boolean isCamera = (returnedIntent == null ||
                        returnedIntent.getData() == null ||
                        returnedIntent.getData().toString().contains(documentFile.toString()));

                if (returnedIntent != null)
                    Log.e(TAG, "Returned Intent: " + returnedIntent.getData());
                if (isCamera) {     /** CAMERA **/
                    selectedImage = Uri.fromFile(documentFile);
                } else if (returnedIntent.getData().toString().startsWith("file://")) {
                    selectedImage = Uri.parse(returnedIntent.getData().toString());
                } else {            /** ALBUM **/
                    selectedImage = Uri.parse(Utilities.getFilePath(context, returnedIntent.getData()));
                }
                Log.e(TAG, "selectedImage: " + selectedImage.getPath());

                //            bm = getImageResized(context, selectedImage);
                //            int rotation = getRotation(context, selectedImage, isCamera);
                //            bm = rotate(bm, rotation);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedImage;
    }


    private static File getTempFile(Context context) {
        File documentFile = new File(context.getExternalCacheDir(), TEMP_DOCUMENT_NAME);
        documentFile.getParentFile().mkdirs();
        return documentFile;
    }

//    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize = sampleSize;
//
//        AssetFileDescriptor fileDescriptor = null;
//        try {
//            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(
//                fileDescriptor.getFileDescriptor(), null, options);
//
//        Log.e(TAG, options.inSampleSize + " sample method bitmap ... " +
//                actuallyUsableBitmap.getWidth() + " " + actuallyUsableBitmap.getHeight());
//
//        return actuallyUsableBitmap;
//    }
//
//    /**
//     * Resize to avoid using too much memory loading big images (e.g.: 2560*1920)
//     **/
//    private static Bitmap getImageResized(Context context, Uri selectedImage) {
//        Bitmap bm = null;
//        int[] sampleSizes = new int[]{5, 3, 2, 1};
//        int i = 0;
//        do {
//            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
//            Log.e(TAG, "resizer: new bitmap width = " + bm.getWidth());
//            i++;
//        } while (bm.getWidth() < minWidthQuality && i < sampleSizes.length);
//        return bm;
//    }
//
//
//    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
//        int rotation;
//        if (isCamera) {
//            rotation = getRotationFromCamera(context, imageUri);
//        } else {
//            rotation = getRotationFromGallery(context, imageUri);
//        }
//        Log.e(TAG, "Image rotation: " + rotation);
//        return rotation;
//    }
//
//    private static int getRotationFromCamera(Context context, Uri imageFile) {
//        int rotate = 0;
//        try {
//
//            context.getContentResolver().notifyChange(imageFile, null);
//            ExifInterface exif = new ExifInterface(imageFile.getPath());
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotate = 270;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotate = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotate = 90;
//                    break;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return rotate;
//    }
//
//    public static int getRotationFromGallery(Context context, Uri imageUri) {
//        int result = 0;
//        String[] columns = {MediaStore.Images.Media.ORIENTATION};
//        Cursor cursor = null;
//        try {
//            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
//            if (cursor != null && cursor.moveToFirst()) {
//                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
//                result = cursor.getInt(orientationColumnIndex);
//            }
//        } catch (Exception e) {
//            //Do nothing
//        } finally {
//            if (cursor != null) {
//                cursor.close();
//            }
//        }//End of try-catch block
//        return result;
//    }
//
//
//    private static Bitmap rotate(Bitmap bm, int rotation) {
//        if (rotation != 0) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(rotation);
//            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
//            return bmOut;
//        }
//        return bm;
//    }
}