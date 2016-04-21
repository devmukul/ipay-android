package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import bd.com.ipay.ipayskeleton.Model.MMModule.RefreshToken.TokenParserClass;
import bd.com.ipay.ipayskeleton.R;

public class Utilities {

    public static boolean isConnectionAvailable(Context context) {
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

    public static String streamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
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
    public static void setFontSize(View view, float fontSize) {
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
                method.invoke(view, Float.valueOf(fontSize));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static final String md5(final String s) {
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
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
    }

    public static void setLayoutAnim_slideDown(ViewGroup panel, Context ctx) {

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

    public static String isPasswordValid(String password) {
        // Return empty string if the password is valid
        if (password.length() < 7) return "This password is too short";
        if (!password.matches(".*[a-zA-Z]+.*")) return "Password should contain an alphabet";
        if (!password.matches(".*[0-9]+.*")) return "Password should contain a number";
        return "";
    }

    // TODO return meaningful message based on the error
    public static boolean isDateOfBirthValid(String dob) {
        try {
            String[] dobFields = dob.split("/");

            int day = Integer.parseInt(dobFields[0]);
            int month = Integer.parseInt(dobFields[1]);
            int year = Integer.parseInt(dobFields[2]);

            return (day >= 1 && day <= 31 && month >= 1 && month <= 12 && year >= 0 && year <= 9999);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isValidEmail(String email) {
        Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+");
        Matcher m = emailPattern.matcher(email);
        return !m.matches();
    }

    public static String formatTaka(BigDecimal amount) {
        return String.format("\u09F3 %.2f BDT", amount);
    }

    public static String formatTaka(double amount) {
        return String.format("\u09F3 %.2f BDT", amount);
    }

}
