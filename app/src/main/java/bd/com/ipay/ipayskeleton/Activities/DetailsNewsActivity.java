package bd.com.ipay.ipayskeleton.Activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import bd.com.ipay.ipayskeleton.Customview.ParallaxScrollView;
import bd.com.ipay.ipayskeleton.R;

public class DetailsNewsActivity extends FragmentActivity {

    // Extra name for the ID parameter
    public static final String EXTRA_PARAM_TITLE = "EXTRA_PARAM_TITLE";
    public static final String EXTRA_PARAM_DESCRIPTION = "EXTRA_PARAM_DESCRIPTION";
    public static final String EXTRA_PARAM_SUB_DESCRIPTION = "EXTRA_PARAM_SUB_DESCRIPTION";
    public static final String EXTRA_PARAM_IMAGE_THUMBNAIL = "EXTRA_PARAM_IMAGE_THUMBNAIL";
    public static final String EXTRA_PARAM_IMAGE_FULL = "EXTRA_PARAM_IMAGE_FULL";

    private ParallaxScrollView parallax;

    private ImageView mHeaderImageView;
    private TextView mHeaderTitle;
    private TextView mHeaderSubTitle;
    private TextView mDescription;

    private String title;
    private String description;
    private String subDescription;
    private String imageUrl;
    private String imageUrlThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        title = getIntent().getStringExtra(EXTRA_PARAM_TITLE);
        description = getIntent().getStringExtra(EXTRA_PARAM_DESCRIPTION);
        subDescription = getIntent().getStringExtra(EXTRA_PARAM_SUB_DESCRIPTION);
        imageUrl = getIntent().getStringExtra(EXTRA_PARAM_IMAGE_FULL);
        imageUrlThumbnail = getIntent().getStringExtra(EXTRA_PARAM_IMAGE_THUMBNAIL);

        parallax = (ParallaxScrollView) findViewById(R.id.scrollView);
        mHeaderImageView = (ImageView) findViewById(R.id.imageview_header);
        mHeaderTitle = (TextView) findViewById(R.id.textview_title);
        mHeaderSubTitle = (TextView) findViewById(R.id.sub_description);
        mDescription = (TextView) findViewById(R.id.description);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mHeaderImageView.setTransitionName(getString(R.string.transition_image));
            mHeaderTitle.setTransitionName(getString(R.string.transition_title));
            mHeaderSubTitle.setTransitionName(getString(R.string.transition_sub_title));
        }

        loadItem();
    }

    private void loadItem() {
        // Set the title TextView to the item's name and author
        mHeaderTitle.setText(title);
        mDescription.setText(description);
        mHeaderSubTitle.setText(subDescription);

        if (imageUrl.length() > 0) loadFullSizeImage();
    }

    private void loadFullSizeImage() {
        Glide.with(mHeaderImageView.getContext())
                .load(imageUrl)
                .crossFade()
                .placeholder(R.drawable.dummy)
                .into(mHeaderImageView);
        parallax.setImageViewToParallax(mHeaderImageView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        DetailsNewsActivity.this.supportFinishAfterTransition();
    }
}