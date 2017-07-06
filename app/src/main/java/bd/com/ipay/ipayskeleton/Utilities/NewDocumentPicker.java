package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;

/**
 * Created by sajid.shahriar on 7/3/17.
 */

public class NewDocumentPicker {

    private static final String TAG = NewDocumentPicker.class.getSimpleName();

    private static final String IMAGE_FILE_SUFFIX = ".jpeg";
    private static final String PDF_FILE_SUFFIX = ".pdf";

    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_EXTERNAL_STORAGE = 1;


    public static Intent getPickerIntentByID(Context context, String chooserTitle, int id) {
        Set<Intent> intentList = new LinkedHashSet<>();
        switch (id) {
            case OPTION_EXTERNAL_STORAGE:
                Intent pickIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);


                intentList = addIntentsToList(context, intentList, pickIntent);
                break;
            case OPTION_CAMERA:
            default:
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File tempFile = getImageFile(context, "Document");

                takePhotoIntent.putExtra("return-data", true);
                if (tempFile != null)
                    takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, tempFile));
                intentList = addIntentsToList(context, intentList, takePhotoIntent);
                break;
        }
        return getChooserIntent(intentList, chooserTitle);
    }

    private static File getImageFile(Context context, String fileName) {
        return null;
    }

    private static Intent getChooserIntent(Set<Intent> intentList, String chooserTitle) {
        Intent chooserIntent = null;

        if (intentList.size() > 0) {
            Intent intent = intentList.iterator().next();
            intentList.remove(intent);
            chooserIntent = Intent.createChooser(intent, chooserTitle);
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[intentList.size()]));
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
            Logger.logE(TAG, "Intent: " + intent.getAction() + " package: " + packageName);
        }
        return list;
    }
}
