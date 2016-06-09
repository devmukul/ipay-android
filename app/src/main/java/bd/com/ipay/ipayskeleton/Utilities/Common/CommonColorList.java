package bd.com.ipay.ipayskeleton.Utilities.Common;

import bd.com.ipay.ipayskeleton.R;

public class CommonColorList {

    public static final int[] PROFILE_PICTURE_BACKGROUNDS = {
            R.color.background_default,
            R.color.background_blue,
            R.color.background_bright_pink,
            R.color.background_cyan,
            R.color.background_magenta,
            R.color.background_orange,
            R.color.background_red,
            R.color.background_spring_green,
            R.color.background_violet,
            R.color.background_yellow,
            R.color.background_azure
    };

    public static final int getProfilePictureBackgroundBasedOnName(String name) {
        int hash = 0;

        for (char c : name.toCharArray()) {
            hash += c;
        }

        int colorPosition = hash % PROFILE_PICTURE_BACKGROUNDS.length;

        return PROFILE_PICTURE_BACKGROUNDS[colorPosition];
    }
}
