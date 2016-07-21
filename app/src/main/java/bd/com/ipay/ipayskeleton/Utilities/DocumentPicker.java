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

    private static final String TAG = "Picker";
    private static final String TEMP_DOCUMENT_NAME = "document.jpg";

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
}