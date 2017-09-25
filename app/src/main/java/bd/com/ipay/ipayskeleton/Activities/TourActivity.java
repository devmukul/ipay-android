package bd.com.ipay.ipayskeleton.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.CustomView.PagerIndicator;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class TourActivity extends AppCompatActivity {

    private final int[] TOUR_BACKGROUND_LAYOUTS = {R.layout.tour_first_page, R.layout.tour_second_page,
            R.layout.tour_third_page, R.layout.tour_fourth_page};
    private PagerIndicator mPagerIndicator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        final Button buttonLogin = (Button) findViewById(R.id.button_sign_in);
        final Button buttonSignUp = (Button) findViewById(R.id.button_join_now);

        final ViewPager tourPager = (ViewPager) findViewById(R.id.tour_pager);
        final TourPagerAdapter tourPageAdapter = new TourPagerAdapter(getSupportFragmentManager(), initFragments());
        tourPager.setAdapter(tourPageAdapter);

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TourActivity.this, SignupOrLoginActivity.class);
                intent.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_IN);
                startActivity(intent);
            }
        });

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TourActivity.this, SignupOrLoginActivity.class);
                intent.putExtra(Constants.TARGET_FRAGMENT, Constants.SIGN_UP);
                startActivity(intent);
            }
        });

        initFragments();

        mPagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);
        mPagerIndicator.setup(R.drawable.pager_indicator_selected,
                R.drawable.pager_indicator_unselected,
                TOUR_BACKGROUND_LAYOUTS.length);

        tourPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                mPagerIndicator.setSelectedPosition(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onResume() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        super.onResume();
    }

    private ArrayList<Fragment> initFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int tourBackgroundLayout : TOUR_BACKGROUND_LAYOUTS) {
            fragments.add(TourFragment.getInstance(tourBackgroundLayout));
        }
        return fragments;
    }

    public static class TourFragment extends BaseFragment {

        private int layout;

        public static TourFragment getInstance(int layout) {
            TourFragment fragment = new TourFragment();
            fragment.setLayout(layout);
            return fragment;
        }

        public void setLayout(int layout) {
            this.layout = layout;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            try {
                return inflater.inflate(layout, container, false);
            } catch (Exception e) {
                if (layout == R.layout.tour_first_page) {
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage() + " on First Page");
                } else if (layout == R.layout.tour_second_page) {
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage() + " on Second Page");
                } else if (layout == R.layout.tour_third_page) {
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage() + " on Third Page");
                } else if (layout == R.layout.tour_fourth_page) {
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage() + " on Fourth Page");
                } else {
                    Utilities.sendExceptionTracker(mTracker, ProfileInfoCacheManager.getAccountId(), e.getMessage() + " for wrong layout");
                }
                return container;
            }
        }
    }

    private class TourPagerAdapter extends FragmentPagerAdapter {

        final ArrayList<Fragment> fragments;

        TourPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
