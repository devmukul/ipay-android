package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DocumentPreviewActivity;
import bd.com.ipay.ipayskeleton.Model.SqLiteDatabase.BusinessAccountEntry;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CustomHorizontalScrollView {
    HorizontalScrollView horizontalScrollView;
    Context context;

    public CustomHorizontalScrollView(final Context context) {
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

    private TextView getHorizontalScrollViewTitle(String title) {
        TextView textView = new TextView(context);
        textView.setText(title);
        return textView;
    }

    public void addBusinessEntryView(BusinessAccountEntry businessAccountEntry) {

        LinearLayout linearLayout = new LinearLayout(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        linearLayout.setLayoutParams(params);

        TextView textView = new TextView(context);
        textView.setText(businessAccountEntry.getBusinessName());

        linearLayout.addView(textView);
        horizontalScrollView.addView(linearLayout);
    }

    public static LinearLayout getCustomTicketAttachmentLayout(final Context context, boolean isRightAligned, LinearLayout linearLayout, List<String> contents) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (isRightAligned)
            params.gravity = Gravity.RIGHT;
        else
            params.gravity = Gravity.LEFT;

        linearLayout.setLayoutParams(params);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        for (final String content : contents) {
            AttachmentView attachmentview = new AttachmentView(context);
            attachmentview.setAttachment(content, false);
            attachmentview.setLayoutParams(isRightAligned);
            attachmentview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(context, DocumentPreviewActivity.class);
                    intent.putExtra(Constants.FILE_EXTENSION, Utilities.getExtension(content));
                    intent.putExtra(Constants.DOCUMENT_URL, content);
                    context.startActivity(intent);
                }
            });
            linearLayout.addView(attachmentview);
        }
        return linearLayout;
    }
}
