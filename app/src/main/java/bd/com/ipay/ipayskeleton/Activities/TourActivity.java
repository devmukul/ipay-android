package bd.com.ipay.ipayskeleton.Activities;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import java.util.ArrayList;

import bd.com.ipay.ipayskeleton.CustomView.PagerIndicator;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class TourActivity extends BaseActivity {

    private ViewPager tourPager;
    private TourPagerAdapter mAdapter;
    private ArrayList<Fragment> fragments;
    private Button buttonLogin;
    private Button buttonSignUp;
    private PagerIndicator mPagerIndicator;

    private final int[] tourBackgroundLayouts = {R.layout.tour_first_page, R.layout.tour_second_page,
            R.layout.tour_third_page, R.layout.tour_fourth_page};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tour);

        getSupportActionBar().hide();

        buttonLogin = (Button) findViewById(R.id.button_sign_in);
        buttonSignUp = (Button) findViewById(R.id.button_join_now);

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
        tourPager = (ViewPager) findViewById(R.id.tour_pager);
        mAdapter = new TourPagerAdapter(getSupportFragmentManager(), fragments);
        tourPager.setAdapter(mAdapter);

        mPagerIndicator = (PagerIndicator) findViewById(R.id.pager_indicator);
        mPagerIndicator.setup(R.drawable.pager_indicator_selected,
                R.drawable.pager_indicator_unselected,
                tourBackgroundLayouts.length);

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
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
        super.onResume();
    }

    private void initFragments() {
        fragments = new ArrayList<>();

        boolean isLast;
        for (int i = 0; i < tourBackgroundLayouts.length; i++) {
            isLast = i >= tourBackgroundLayouts.length - 1;
            fragments.add(TourFragment.getInstance(tourBackgroundLayouts[i],
                    isLast));
        }
    }

    public static class TourFragment extends Fragment {

        private boolean isLast = false;
        private int layout;

        private void setIsLast(boolean last) {
            this.isLast = last;
        }

        public static TourFragment getInstance(int layout, boolean isLast) {
            TourFragment fragment = new TourFragment();
            fragment.setLayout(layout);
            fragment.setIsLast(isLast);
            return fragment;
        }

        public void setLayout(int layout) {
            this.layout = layout;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(layout, null);
        }
    }

    private class TourPagerAdapter extends FragmentPagerAdapter {

        final ArrayList<Fragment> fragments;

        public TourPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
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

    @Override
    public Context setContext() {
        return TourActivity.this;
    }
}
