package bd.com.ipay.ipayskeleton.Utilities.Common;

import bd.com.ipay.ipayskeleton.R;

public class CommonDrawableList {

    public static final int[] LIST_ITEM_BACKGROUNDS = {
            R.drawable.background_portrait_circle,
            R.drawable.background_portrait_circle_blue,
            R.drawable.background_portrait_circle_brightpink,
            R.drawable.background_portrait_circle_cyan,
            R.drawable.background_portrait_circle_megenta,
            R.drawable.background_portrait_circle_orange,
            R.drawable.background_portrait_circle_red,
            R.drawable.background_portrait_circle_springgreen,
            R.drawable.background_portrait_circle_violet,
            R.drawable.background_portrait_circle_yellow,
            R.drawable.background_portrait_circle_azure
    };

    public static final int getProfilePictureBackgroundBasedOnName(String name) {
        int hash = 0;

        for (char c : name.toCharArray()) {
            hash += c;
        }

        int colorPosition = hash % LIST_ITEM_BACKGROUNDS.length;

        return LIST_ITEM_BACKGROUNDS[colorPosition];
    }
}
