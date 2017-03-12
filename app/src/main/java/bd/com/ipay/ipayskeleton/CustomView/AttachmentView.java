package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import bd.com.ipay.ipayskeleton.R;

public class AttachmentView extends FrameLayout {
    private Context context;

    private ImageView mAttachmentView;

    public AttachmentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public AttachmentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AttachmentView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;

        View v = inflate(context, R.layout.attachment_view, null);

        mAttachmentView = (ImageView) v.findViewById(R.id.attachment);

        addView(v);
    }

    public void setAttachment(int photoResourceId) {
        Drawable drawable = context.getResources().getDrawable(photoResourceId);
        mAttachmentView.setImageDrawable(drawable);
    }

    public void setAttachment(String attachmentUri, boolean forceLoad) {
        try {
            final DrawableTypeRequest<String> glide = Glide.with(context).load(attachmentUri);

            glide
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            if (forceLoad) {
                glide
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())));
            }

            if (attachmentUri.contains(getResources().getString(R.string.pdf))) {
                glide
                        .placeholder(R.drawable.icon_pdf)
                        .error(R.drawable.icon_pdf)
                        .crossFade()
                        .dontAnimate()
                        .override(100, 80)
                        .into(mAttachmentView);
            } else {
                glide
                        .placeholder(R.drawable.ic_image)
                        .error(R.drawable.ic_image)
                        .crossFade()
                        .dontAnimate()
                        .fitCenter()
                        .override(300, 300)
                        .into(mAttachmentView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
