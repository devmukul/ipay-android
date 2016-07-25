package bd.com.ipay.ipayskeleton.Api;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import bd.com.ipay.ipayskeleton.Utilities.StorageManager;

class DownloadImageFromUrlAsyncTask extends AsyncTask<Void, Void, String> {

    private final String mProfilePictureUrl;
    private final String mUserID;

    public DownloadImageFromUrlAsyncTask(String mProfilePictureUrl, String mUserID) {
        Log.d("Downloading picture", mUserID + " : " + mProfilePictureUrl);
        this.mProfilePictureUrl = mProfilePictureUrl;
        this.mUserID = mUserID;
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            if (mProfilePictureUrl != null && !mProfilePictureUrl.isEmpty()) {

                Log.d("Picture URL : ", mProfilePictureUrl);
                URL url = new URL(mProfilePictureUrl);

                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);

                File imageFile = StorageManager.getProfilePictureFile(mUserID);
                FileOutputStream fos = new FileOutputStream(imageFile);

                byte buffer[] = new byte[8192];
                int read;
                while ((read = bis.read(buffer)) > 0) {
                    fos.write(buffer, 0, read);
                }
                fos.flush();
                fos.close();
                bis.close();
                is.close();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }

}