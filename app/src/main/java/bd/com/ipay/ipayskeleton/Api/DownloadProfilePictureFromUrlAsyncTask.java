package bd.com.ipay.ipayskeleton.Api;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class DownloadProfilePictureFromUrlAsyncTask extends AsyncTask<Void, Void, String> {

    private String mProfilePictureUrl;
    private String mUserID;

    public DownloadProfilePictureFromUrlAsyncTask(String mProfilePictureUrl, String mUserID) {
        this.mProfilePictureUrl = mProfilePictureUrl;
        this.mUserID = mUserID;
    }

    @Override
    protected String doInBackground(Void... params) {

        InputStream input = null;

        try {
            if (mProfilePictureUrl.length() > 0) {

                File dir = new File(Environment.getExternalStorageDirectory().getPath()
                        + Constants.PICTURE_FOLDER);
                if (!dir.exists()) dir.mkdir();

                File file = new File(dir, mUserID.replaceAll("[^0-9]", "") + ".jpg");

                URL url = new URL(Constants.BASE_URL_IMAGE_SERVER + mProfilePictureUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);

                FileOutputStream stream = new FileOutputStream(file);

                ByteArrayOutputStream outstream = new ByteArrayOutputStream();
                myBitmap.compress(Bitmap.CompressFormat.JPEG, 85, outstream);
                byte[] byteArray = outstream.toByteArray();

                stream.write(byteArray);
                stream.close();
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