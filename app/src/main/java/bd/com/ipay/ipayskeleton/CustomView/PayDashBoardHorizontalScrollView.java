package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class PayDashBoardHorizontalScrollView {
    HorizontalScrollView horizontalScrollView;
    Context context;

    public PayDashBoardHorizontalScrollView(final Context context) {
        this.context = context;
        initScrollView();
    }

    private void initScrollView() {
        horizontalScrollView = new HorizontalScrollView(context);
        HorizontalScrollView.LayoutParams params = new HorizontalScrollView.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        horizontalScrollView.setLayoutParams(params);
    }

    public LinearLayout addHorizontalScrollView(LinearLayout linearLayout, String title) {
        linearLayout.addView(getHorizontalScrollViewTitle(title));
        linearLayout.addView(horizontalScrollView);

        return linearLayout;
    }

    private CustomDashBoardTitleView getHorizontalScrollViewTitle(String title) {
        CustomDashBoardTitleView customDashBoardTitleView = new CustomDashBoardTitleView(context);
        customDashBoardTitleView.setTitleView(title);
        return customDashBoardTitleView;
    }

    public CustomDashboardItemView addBusinessEntryView(final BusinessAccountEntry businessAccountEntry) {
        CustomDashboardItemView customDashboardItemView = new CustomDashboardItemView(context);
        customDashboardItemView.setNameView(businessAccountEntry.getBusinessName());
        customDashboardItemView.setImageView(Constants.BASE_URL_FTP_SERVER + businessAccountEntry.getProfilePictureUrl(), true);

        horizontalScrollView.addView(customDashboardItemView);

        return customDashboardItemView;
    }
}
