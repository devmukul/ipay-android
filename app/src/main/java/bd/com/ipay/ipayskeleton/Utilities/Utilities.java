package bd.com.ipay.ipayskeleton.Utilities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.BasicInfo.UserProfilePictureClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.TokenParserClass;

public class Utilities {

    public static boolean isConnectionAvailable(Context context) {
        if (context == null) return false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnected();
    }

    public static boolean isTabletDevice(Context context) {
        boolean large = (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE;
        boolean xlarge = (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE;
        return large || xlarge;
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @return address or empty string
     * @paramipv4 true=return ipv4, false=return ipv6
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

    /**
     * Convert byte array to hex string
     *
     * @param bytes
     * @return
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for (byte aByte : bytes) {
            int intVal = aByte & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Convert date string to millisecond
     */
    public static long dateToMilliSecond(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date mDate = sdf.parse(dateString);
            long timeInMilliseconds = mDate.getTime();
            return timeInMilliseconds;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Convert millisecond to date string
     */
    public static String milliSecondToDate(long millisecond) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String mDate = dateFormat.format(new Date(millisecond));
        return mDate;
    }

    /**
     * Get utf8 byte array.
     *
     * @param str
     * @return array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     *
     * @param filename
     * @return
     * @throws java.io.IOException
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? new String(baos.toByteArray(), "UTF-8") : new String(baos.toByteArray());
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static String getLongLatWithoutGPS(Context context) {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        double latitude;
        double longitude;

        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        } else {
            Location location = lm
                    .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();

                return longitude + ", " + latitude;
            } else return null;
        }
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac == null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:", aMac));
                if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
                return buf.toString();
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static String extractDate(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        Pattern pattern = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
        Matcher matcher = pattern.matcher(str);
        return matcher.find() ? matcher.group() : null;
    }

    public static float getFontSize(int position) {
        switch (position) {
            case 0:
                return 12.0f;
            case 1:
                return 14.0f;
            case 2:
                return 18.0f;
            case 3:
                return 22.0f;
            default:
                return 14.0f;
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setFontSize(View view, float fontSize) {
        try {
            Class[] paramFloat = new Class[1];
            paramFloat[0] = Float.TYPE;

            Class clas = Class.forName("android.widget.TextView");
            Method method = clas.getDeclaredMethod("setTextSize", paramFloat);

            if (view instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) view;
                for (int i = 0; i < group.getChildCount(); i++) {
                    View child = group.getChildAt(i);
                    setFontSize(child, fontSize);
                }
            } else if (view instanceof TextView) {
                method.invoke(view, fontSize);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static long getTimeFromBase64Token(String base64) {

        long timeForTokenExpiration = 0;

        // Token divided in three parts. Middle portion of the token contains expiration time
        String[] tokensArray = base64.split("\\.");
        base64 = tokensArray[1];
        byte[] data = Base64.decode(base64, Base64.DEFAULT);
        try {
            String parsedToken = new String(data, "UTF-8");
            Log.d(Constants.PARSED_TOKEN, Constants.PARSED_TOKEN + parsedToken);
            Gson gson = new Gson();
            TokenParserClass mTokenParserClass = gson.fromJson(parsedToken, TokenParserClass.class);

            // Returns time is second. Multiply by 1000 to get the time in milli-seconds
            timeForTokenExpiration = (mTokenParserClass.getExp() - mTokenParserClass.getIat()) * 1000;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return timeForTokenExpiration;
    }

    public static String getFilePath(Context context, Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        String filePath = null;
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
            cursor.close();
        }

        return filePath;
    }

    public static String getFilePathfromData(Context context, Uri uri) {
        String[] projection = new String[]{"_data"};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        String filePath = null;
        int columnIndex = cursor.getColumnIndex(projection[0]);
        if (cursor != null && cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }

        return filePath;
    }

    public static void setLayoutAnim_slideDown(ViewGroup panel) {

        AnimationSet set = new AnimationSet(true);

        Animation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, -1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f);
        animation.setDuration(200);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(
                set, 0.25f);
        panel.setLayoutAnimation(controller);
    }

    public static void setUpNonScrollableListView(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, AbsListView.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public static boolean isValueAvailable(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) >= 0;
    }

    public static String formatTakaWithComma(double amount) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        return String.format("\u09F3%s", numberFormat.format(amount));
    }

    public static String takaWithComma(double amount) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(amount);
    }

    public static String formatTaka(BigDecimal amount) {
        return String.format("\u09F3%.2f", amount);
    }

    public static String formatTaka(double amount) {
        return String.format("\u09F3%.2f", amount);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Context context, View v) {
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    public static void setKeyboardHide(Activity activity) {
        activity.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }


    public static void showKeyboard(Context context) {
        final InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static void showKeyboard(Context context, final EditText editText) {

        final InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!editText.hasFocus()) {
            editText.requestFocus();
        }

        editText.post(new Runnable() {
            @Override
            public void run() {
                imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
            }
        });
    }

    public static String getDateFormat(long time) {
        return new SimpleDateFormat("MMM d, yyyy, h:mm a").format(time);
    }

    public static boolean checkPlayServices(Context context) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(context);

        return resultCode == ConnectionResult.SUCCESS;
    }

    public static String getExtension(String filePath) {
        if (filePath != null && filePath.lastIndexOf('.') >= 0)
            return filePath.substring(0, filePath.lastIndexOf('.')).toLowerCase();
        else
            return "";
    }

    public static String[] parseEventTicket(String qrcodeEncoded) {
        return qrcodeEncoded.split(":");
    }

    public static String getImage(List<UserProfilePictureClass> profilePictureClasses, String quality) {
        String imageQualityHigh = null;
        String imageQualityMedium = null;
        String imageQualityLow = null;

        for (UserProfilePictureClass profilePicture : profilePictureClasses) {
            switch (profilePicture.getQuality()) {
                case Constants.IMAGE_QUALITY_HIGH:
                    imageQualityHigh = profilePicture.getUrl();
                    break;
                case Constants.IMAGE_QUALITY_MEDIUM:
                    imageQualityMedium = profilePicture.getUrl();
                    break;
                case Constants.IMAGE_QUALITY_LOW:
                    imageQualityLow = profilePicture.getUrl();
                    break;
            }
        }

        switch (quality) {
            case Constants.IMAGE_QUALITY_HIGH:
                if (imageQualityHigh != null)
                    return imageQualityHigh;
                else if (imageQualityMedium != null)
                    return imageQualityMedium;
                else if (imageQualityLow != null)
                    return imageQualityLow;
                break;
            case Constants.IMAGE_QUALITY_MEDIUM:
                if (imageQualityMedium != null)
                    return imageQualityMedium;
                else if (imageQualityHigh != null)
                    return imageQualityHigh;
                else if (imageQualityLow != null)
                    return imageQualityLow;
                break;
            case Constants.IMAGE_QUALITY_LOW:
                if (imageQualityLow != null)
                    return imageQualityLow;
                else if (imageQualityHigh != null)
                    return imageQualityHigh;
                else if (imageQualityMedium != null)
                    return imageQualityMedium;
                break;
        }
        return "";
    }

    public static void setActionBarTitle(Activity activity, String title) {
        activity.getActionBar().setTitle(title);
    }

    public static BigDecimal bigDecimalPercentage(BigDecimal base, BigDecimal pct) {
        return base.multiply(pct).divide(new BigDecimal(100));
    }
}
