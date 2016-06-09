package bd.com.ipay.ipayskeleton.Customview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonColorList;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonDrawableList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

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

    public void setProfilePicture(String photoUri) {
//            mProfilePictureView.setVisibility(View.VISIBLE);
//            mProfileFirstLetterView.setVisibility(View.GONE);

        Glide.with(context)
                .load(Constants.BASE_URL_FTP_SERVER + photoUri)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mProfilePictureView);
    }

    public void setProfilePicturePlaceHolder() {
        Glide.with(context)
                .load(R.drawable.ic_person)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mProfilePictureView);
    }

    public void setProfileFirstLetter(String name) {
        if (name != null && name.length() > 0) {
//            mProfilePictureView.setVisibility(View.GONE);
//            mProfileFirstLetterView.setVisibility(View.VISIBLE);

            mProfileFirstLetterView.setText(String.valueOf(name.toUpperCase().charAt(0)));

            int backgroundDrawable = CommonDrawableList.getProfilePictureBackgroundBasedOnName(name);
            mProfileFirstLetterView.setBackgroundResource(backgroundDrawable);
        }
    }

    public void setInformation(String photoUri, String name) {
//        if (photoUri != null) {
//            setProfilePicture(photoUri);
//        } else {
//            setProfileFirstLetter(name);
//        }

        if (name != null && !name.isEmpty()) {
            setProfileFirstLetter(name);
            setProfilePicture(photoUri);

        } else if (photoUri != null){
            setProfileFirstLetter(photoUri);
        } else {
            setProfilePicturePlaceHolder();
        }
    }
}
