package bd.com.ipay.ipayskeleton.Utilities;

import java.util.Random;

import bd.com.ipay.ipayskeleton.R;

public class Res {
    public static int[] BACKGROUNDS = {
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

    public static int getRandomBackground() {
        Random random = new Random();

        return BACKGROUNDS[random.nextInt(BACKGROUNDS.length)];
    }
}
