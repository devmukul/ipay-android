package bd.com.ipay.ipayskeleton.Utilities;

import android.content.Context;
import android.view.Gravity;
import android.widget.LinearLayout;

public class CustomDrawable {

    public static LinearLayout getCustomTicketAttachmentLayout(Context context, boolean isRightAligned,LinearLayout linearLayout) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (isRightAligned)
            params.gravity = Gravity.RIGHT;
        else
            params.gravity = Gravity.LEFT;

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        return linearLayout;
    }
}
