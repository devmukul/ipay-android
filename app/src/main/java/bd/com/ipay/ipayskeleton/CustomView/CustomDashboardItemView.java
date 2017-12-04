package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;

import bd.com.ipay.ipayskeleton.R;

public class CustomDashboardItemView extends LinearLayout {
    private Context context;

    private ImageView mImageView;
    private TextView mTextView;

    public CustomDashboardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public CustomDashboardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public CustomDashboardItemView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;

        View v = inflate(context, R.layout.dashboard_item_view, null);

        mImageView = (ImageView) v.findViewById(R.id.imageView);
        mTextView = (TextView) v.findViewById(R.id.nameView);

        addView(v);
    }

    public void setImageView(int photoResourceId) {
        Drawable drawable = context.getResources().getDrawable(photoResourceId);
        mImageView.setImageDrawable(drawable);
    }

    public void setNameView(String name) {

        mTextView.setText(name);
    }

    public void setImageView(String attachmentUri, boolean forceLoad) {
        try {
            final DrawableTypeRequest<String> glide = Glide.with(context).load(attachmentUri);

            glide
                    .diskCacheStrategy(DiskCacheStrategy.ALL);

            if (forceLoad) {
                glide
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())));
            }

            glide
                    .placeholder(R.drawable.background_icon_make_payment)
                    .error(R.drawable.background_icon_make_payment)
                    .crossFade()
                    .dontAnimate()
                    .fitCenter()
                    .into(mImageView);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
