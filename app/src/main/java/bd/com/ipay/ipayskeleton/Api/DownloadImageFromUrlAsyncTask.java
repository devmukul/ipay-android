package bd.com.ipay.ipayskeleton.Api;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DownloadImageFromUrlAsyncTask extends AsyncTask<Void, Void, String> {

    private String mProfilePictureUrl;
    private String mUserID;

    public DownloadImageFromUrlAsyncTask(String mProfilePictureUrl, String mUserID) {
        this.mProfilePictureUrl = mProfilePictureUrl;
        this.mUserID = mUserID;
    }

    @Override
    protected String doInBackground(Void... params) {

        try {
            if (mProfilePictureUrl.length() > 0) {

                File dir = new File(Environment.getExternalStorageDirectory().getPath()
                        + Constants.PICTURE_FOLDER);
                if (!dir.exists()) dir.mkdir();

                File file = new File(dir, mUserID.replaceAll("[^0-9]", "") + ".jpg");

                URL url = new URL(Constants.BASE_URL_IMAGE_SERVER + mProfilePictureUrl);

                URLConnection connection = url.openConnection();
                InputStream is = connection.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                FileOutputStream fos = new FileOutputStream(file);
                byte buffer[] = new byte[1024];
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

    @Override
    protected void onPostExecute(final String result) {
    }
}