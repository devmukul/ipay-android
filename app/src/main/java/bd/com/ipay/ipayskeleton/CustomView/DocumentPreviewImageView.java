package bd.com.ipay.ipayskeleton.CustomView;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import bd.com.ipay.ipayskeleton.R;

public class DocumentPreviewImageView extends RelativeLayout {

    private View mChildView;
    private RelativeLayout mDocumentImagePreviewHolder;
    private ImageView mImageView;
    private ImageView mCrossButton;

    public DocumentPreviewImageView(@NonNull Context context) {
        this(context, null);
    }

    public DocumentPreviewImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DocumentPreviewImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupView();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DocumentPreviewImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setupView();
    }

    private void setupView() {
        mChildView = LayoutInflater.from(getContext()).inflate(R.layout.layout_document_preview_image_view, null, false);
        mDocumentImagePreviewHolder = findViewByIdAutoCast(R.id.document_image_preview_holder);
        mImageView = findViewByIdAutoCast(R.id.image_view);
        mCrossButton=findViewByIdAutoCast(R.id.cancel_button);
        addView(mChildView);
        setCancelButtonAction();
    }

    private void hidePreview(){
        this.setVisibility(GONE);
    }

    private void setCancelButtonAction(){
        mCrossButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hidePreview();
            }
        });
    }

    private <T extends View> T findViewByIdAutoCast(@IdRes int id) {
        //noinspection unchecked
        return (T) mChildView.findViewById(id);
    }

    public ImageView getImageView() {
        return mImageView;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        mDocumentImagePreviewHolder.setOnClickListener(l);
    }

    public void setImageResource(@DrawableRes int resId) {
        mImageView.setImageResource(resId);
    }

    public void setImageBitmap(Bitmap bitmap) {
        mImageView.setImageBitmap(bitmap);
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }
}
