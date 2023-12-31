package bd.com.ipay.ipayskeleton.Utilities;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;

public class FileUtilities {

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getAbsolutePath(Context context, Uri uri) {
        String path = uri.getPath();
        String[] split = path.split(":");

        if (isExternalStorageDocument(uri)) {
            return Environment.getExternalStorageDirectory() + "/" + split[1];

        } else if (isDownloadsDocument(uri)) {
            String id = DocumentsContract.getDocumentId(uri);
            Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            return getDataColumn(context, contentUri);

        } else {
            String[] splitPath = path.split("//");
            File file = new File(splitPath[1]);
            if (file.exists())
                return splitPath[1];
            else return uri.getPath();
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri) {
        Cursor cursor = null;
        String[] projection = {MediaStore.Video.Media.DATA};
        try {
            cursor = context.getContentResolver().query(uri, projection, null, null, null);
            String filePath = null;
            if (cursor != null && cursor.moveToFirst()) {
                filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                return filePath;
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
}
