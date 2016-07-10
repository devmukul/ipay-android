package bd.com.ipay.ipayskeleton.Utilities;

import android.os.Environment;

import java.io.File;

/**
 * All local storage access (e.g. saving and accessing contact profile pictures) should be done
 * through this class
 */
public class StorageManager {

    public static File getProfilePictureFile(String mobileNumber) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath()
                + Constants.PICTURE_FOLDER);
        if (!dir.exists()) dir.mkdir();

        File file = new File(dir, mobileNumber.replaceAll("[^0-9]", "") + ".jpg");

        return file;
    }
}
