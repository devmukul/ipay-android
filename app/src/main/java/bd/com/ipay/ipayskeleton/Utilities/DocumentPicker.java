package bd.com.ipay.ipayskeleton.Utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import bd.com.ipay.ipayskeleton.BuildConfig;
import bd.com.ipay.ipayskeleton.Utilities.ToasterAndLogger.Logger;
import bd.com.ipay.ipayskeleton.camera.CameraActivity;

/**
 * Source: https://gist.github.com/Mariovc/f06e70ebe8ca52fbbbe2
 */
public class DocumentPicker {

    private static final String TAG = "Picker";
    private static final String TEMP_DOCUMENT_NAME = "document.jpg";

    public static final String[] DOCUMENT_PICK_PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
    private static final int OPTION_CAMERA = 0;
    private static final int OPTION_EXTERNAL_STORAGE = 1;
    private static final String TEMP_DOCUMENT_NAME_WITH_INDEX = "document_%d.jpg";

    public static Intent getPickImageOrPdfIntent(Context context, String chooserTitle) {

        Set<Intent> intentList = new LinkedHashSet<>();

        Intent pickIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickIntent.setType("image/*|application/pdf");
        File tempFile = getTempFile(context);

        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));

        intentList = addIntentsToList(context, intentList, takePhotoIntent);
        intentList = addIntentsToList(context, intentList, pickIntent);

        return getChooserIntent(intentList, chooserTitle);
    }

    public static Intent getPickerIntentByID(Context context, String chooserTitle, int id) {
        return getPickerIntentByID(context, chooserTitle, id, Constants.CAMERA_REAR);
    }

    public static Intent getPickerIntentByID(Context context, String chooserTitle, int id, String CameraFacing) {

        Set<Intent> intentList = new LinkedHashSet<>();
        Intent returnIntent;

        if (id == OPTION_EXTERNAL_STORAGE) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            intentList = addIntentsToList(context, intentList, pickIntent);
            returnIntent = getChooserIntent(intentList, chooserTitle);
        } else {
            if (CameraFacing.equals(Constants.CAMERA_REAR)) {
                Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File tempFile = getTempFile(context);

                takePhotoIntent.putExtra("return-data", true);
                takePhotoIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, tempFile));

                intentList = addIntentsToList(context, intentList, takePhotoIntent);
                returnIntent = getChooserIntent(intentList, chooserTitle);
            } else {
                returnIntent = new Intent(context, CameraActivity.class);
            }
        }
        return returnIntent;

    }

    @SuppressLint("InlinedApi")
    public static Intent getPickImageOrPDFIntentByID(Context context, String chooserTitle, int id) {

        Set<Intent> intentList = new LinkedHashSet<>();

        if (id == OPTION_EXTERNAL_STORAGE) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intentList = addIntentsToList(context, intentList, pickIntent);
        } else if (id == OPTION_CAMERA) {
            Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            takePhotoIntent.putExtra("return-data", true);
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID, getTempFile(context)));
            intentList = addIntentsToList(context, intentList, takePhotoIntent);
        } else {
            Intent pdfIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            pdfIntent.setType("application/pdf");
            pdfIntent.addCategory(Intent.CATEGORY_OPENABLE);
            pdfIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            intentList = addIntentsToList(context, intentList, pdfIntent);
        }
        return getChooserIntent(intentList, chooserTitle);
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

    public static String getFilePathFromResult(Context context, Intent returnedIntent) {
        try {
            File documentFile = getTempFile(context);

            boolean isCamera = (returnedIntent == null ||
                    returnedIntent.getData() == null ||
                    returnedIntent.getData().toString().contains(documentFile.toString()));

            if (isCamera) {     // CAMERA
                return getTempFile(context).getAbsolutePath();
            } else if (returnedIntent.getData().toString().startsWith("file://")) {
                return returnedIntent.getData().toString();
            } else {            // ALBUM
                return FileUtilities.getDataColumn(context, returnedIntent.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFilePathForCameraOrPDFResult(Context context, Intent returnedIntent) {
        try {
            File documentFile = getTempFile(context);

            boolean isCamera = (returnedIntent == null ||
                    returnedIntent.getData() == null ||
                    returnedIntent.getData().toString().contains(documentFile.toString()));

            if (isCamera) {    // CAMERA
                return getTempFile(context).getAbsolutePath();
            } else {
                return FileUtilities.getAbsolutePath(context, returnedIntent.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getDocumentFromResult(Context context, int resultCode, Intent returnedIntent) {
        Uri selectedImage = null;
        try {
            File documentFile = getTempFile(context);
            if (resultCode == Activity.RESULT_OK) {
                boolean isCamera = (returnedIntent == null ||
                        returnedIntent.getData() == null ||
                        returnedIntent.getData().toString().contains(documentFile.toString()));

                if (returnedIntent != null)
                    Logger.logE(TAG, "Returned Intent: " + returnedIntent.getData());
                if (isCamera) {     // CAMERA
                    selectedImage = Uri.fromFile(documentFile);
                } else if (returnedIntent.getData().toString().startsWith("file://")) {
                    selectedImage = Uri.parse(returnedIntent.getData().toString());
                } else {            // ALBUM
                    selectedImage = Uri.parse(FileUtilities.getDataColumn(context, returnedIntent.getData()));
                }
                Logger.logE(TAG, "selectedImage: " + selectedImage.getPath());

                if (isCamera) {
                    Logger.logD(TAG, "Converting: " + selectedImage.getPath());

                    // Convert the image - handle auto rotate problem in some devices, scale down
                    // image if necessary (max 512*512)
                    Bitmap convertedBitmap = CameraAndImageUtilities.handleSamplingAndRotationBitmap(context, selectedImage, true);

                    // Save to file
                    File tempFile = getTempFile(context);
                    CameraAndImageUtilities.saveBitmapToFile(convertedBitmap, tempFile);
                    selectedImage = Uri.fromFile(tempFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return selectedImage;
    }

    public static Uri getDocumentUriWithIndexFromResult(Context context, int resultCode, Intent returnedIntent, int fileIndex) {
        Uri selectedImage = null;
        try {
            File documentFile = getTempFile(context);
            if (resultCode == Activity.RESULT_OK) {
                boolean isCamera = (returnedIntent == null ||
                        returnedIntent.getData() == null ||
                        returnedIntent.getData().toString().contains(documentFile.toString()));

                if (returnedIntent != null)
                    Logger.logE(TAG, "Returned Intent: " + returnedIntent.getData());
                if (isCamera) {     // CAMERA
                    selectedImage = Uri.fromFile(documentFile);
                } else if (returnedIntent.getData().toString().startsWith("file://")) {
                    selectedImage = Uri.parse(returnedIntent.getData().toString());
                } else {            // ALBUM
                    selectedImage = Uri.parse(FileUtilities.getDataColumn(context, returnedIntent.getData()));
                }
                Logger.logE(TAG, "selectedImage: " + selectedImage.getPath());

                if (isCamera) {
                    Logger.logD(TAG, "Converting: " + selectedImage.getPath());

                    // Convert the image - handle auto rotate problem in some devices, scale down
                    // image if necessary (max 512*512)
                    Bitmap convertedBitmap = CameraAndImageUtilities.handleSamplingAndRotationBitmap(context, selectedImage, true);

                    // Save to file
                    File tempFile = getFileWithIndex(context, fileIndex);
                    CameraAndImageUtilities.saveBitmapToFile(convertedBitmap, tempFile);
                    selectedImage = Uri.fromFile(tempFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return selectedImage;
    }


    @NonNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File getTempFile(Context context) {
        File documentFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TEMP_DOCUMENT_NAME);
        if (!documentFile.getParentFile().exists()) {
            documentFile.getParentFile().mkdirs();
        }
        return documentFile;
    }

    @NonNull
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static File getFileWithIndex(Context context, int index) throws IOException {
        File documentFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), String.format(Locale.US, TEMP_DOCUMENT_NAME_WITH_INDEX, index));
        if (!documentFile.getParentFile().exists()) {
            documentFile.getParentFile().mkdirs();
        }
        return documentFile;
    }
}