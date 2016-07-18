package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.target.Target;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CircleTransform;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonDrawableList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.StorageManager;

public class ProfileImageView extends FrameLayout {
    private Context context;

    private TextView mProfileFirstLetterView;
    private RoundedImageView mProfilePictureView;

    public ProfileImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public ProfileImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public ProfileImageView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;

        View v = inflate(context, R.layout.profile_image_view, null);

        mProfileFirstLetterView = (TextView) v.findViewById(R.id.portraitTxt);
        mProfilePictureView = (RoundedImageView) v.findViewById(R.id.portrait);

        addView(v);
    }

    public void setProfilePicture(int photoResourceId) {
        Drawable drawable = context.getResources().getDrawable(photoResourceId);
        mProfilePictureView.setImageDrawable(drawable);
    }

    public void setProfilePicturePlaceHolder() {
        Glide.with(context)
                .load(R.drawable.ic_person)
                .crossFade()
                .into(mProfilePictureView);
    }

    public void setProfilePicture(String photoUri, boolean forceLoad) {
        try {
            final DrawableTypeRequest<String> glide = Glide.with(context).load(photoUri);

            if (forceLoad) {
                glide
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE);
            }

            glide
                .error(R.drawable.ic_user_pic)
                .placeholder(R.drawable.ic_user_pic)
                .crossFade()
                .transform(new CircleTransform(context))
                .into(mProfilePictureView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
