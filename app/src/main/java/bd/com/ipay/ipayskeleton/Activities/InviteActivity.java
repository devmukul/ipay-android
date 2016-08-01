package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class InviteActivity extends BaseActivity {
    
    private ViewPager viewPager;

    private final int INVITE_TAB = 0;
    private final int INVITED_TAB = 1;
    private final int TOTAL_PAGE_COUNT = 2;

    private TabLayout.Tab mInviteTab;
    private TabLayout.Tab mInvitedTab;

    private View mInviteTabView;
    private View mInvitedTabView;

    private FloatingActionButton mSendInviteButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        mSendInviteButton = (FloatingActionButton) findViewById(R.id.fab_invite);

        mSendInviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new MoneyRequestListFragmentAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mInviteTab = tabLayout.getTabAt(INVITE_TAB);
        mInvitedTab = tabLayout.getTabAt(INVITED_TAB);

        setupCustomViewsForTabLayout();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Utilities.hideKeyboard(this);
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void setupCustomViewsForTabLayout() {
        mInviteTabView = getLayoutInflater().inflate(R.layout.view_single_tab_background, null);
        mInvitedTabView = getLayoutInflater().inflate(R.layout.view_single_tab_background, null);
        setTabViews();
    }

    private void setTabViews() {
        setTabIconsWithTexts();

        mInviteTab.setCustomView(mInviteTabView);
        mInvitedTab.setCustomView(mInvitedTabView);
    }

    private void setTabIconsWithTexts() {

        ((ImageView) mInviteTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_request_sent);
        ((ImageView) mInvitedTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_received_request);

        ((TextView) mInviteTabView.findViewById(R.id.tab_text)).setText(getResources().getString(R.string.invite));
        ((TextView) mInvitedTabView.findViewById(R.id.tab_text)).setText(getResources().getString(R.string.invited));
    }

    public class MoneyRequestListFragmentAdapter extends FragmentPagerAdapter {
        public MoneyRequestListFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return TOTAL_PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new Fragment();
            } else if (position == 1) {
                return new Fragment();
            } else {
                return new Fragment();
            }
        }

    }

    @Override
    protected Context setContext() {
        return InviteActivity.this;
    }
}
