package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.DocumentPreviewActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class CustomDrawable {

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

    public static ImageView getCustomFileThumbnailView(Context context, ImageView mFileView, String attachFileName) {
        File mFile;
        Bitmap mBitmap;
        if (attachFileName.contains(context.getResources().getString(R.string.pdf)))
            mFileView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_pdf));
        else {
            try {
                mFile = new File(attachFileName);
                if (mFile.exists()) {
                    mBitmap = BitmapFactory.decodeFile(mFile.getPath());
                    if (mBitmap != null) mFileView.setImageBitmap(mBitmap);
                }
            } catch (Exception e) {
                mFileView.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_touch_id));
            }
        }
        return mFileView;
    }
}
