package bd.com.ipay.ipayskeleton.Utilities;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * All local storage access (e.g. saving and accessing contact profile pictures) should be done
 * through this class
 */
public class StorageManager {

    public static File getProfilePictureFile(String mobileNumber) {
        File dir = new File(Environment.getExternalStorageDirectory().getPath()
                + Constants.PICTURE_FOLDER);
        if (!dir.exists()) dir.mkdir();

        return new File(dir, mobileNumber.replaceAll("[^0-9]", "") + ".jpg");
    }

    public static void fileCopy(File src, File dst) {
        Log.d("Copying file", "From " + src + " to " + dst);

        FileInputStream inStream = null;
        FileOutputStream outStream = null;

        try {
            inStream = new FileInputStream(src);
            outStream = new FileOutputStream(dst);
            FileChannel inChannel = inStream.getChannel();
            FileChannel outChannel = outStream.getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null)
                    inStream.close();
                if (outStream != null)
                    outStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
