package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyHistoryActivity;
import bd.com.ipay.ipayskeleton.Activities.QRCodeViewerActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class MoneyRequestListHolderFragment extends Fragment {
    private ViewPager viewPager;

    private final int RECEIVED_REQUEST_TAB = 0;
    private final int SENT_REQUEST_TAB = 1;
    private final int TOTAL_PAGE_COUNT = 2;

    private TabLayout.Tab mReceivedRequestTab;
    private TabLayout.Tab mSentRequestTab;

    private View mReceivedRequestTabView;
    private View mSentRequestTabView;

    public static final String SWITCH_TO_SENT_REQUESTS = "SWITCH_TO_SENT_REQUESTS";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_money_request_list_holder, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MoneyRequestListFragmentAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mReceivedRequestTab = tabLayout.getTabAt(RECEIVED_REQUEST_TAB);
        mSentRequestTab = tabLayout.getTabAt(SENT_REQUEST_TAB);

        setupCustomViewsForTabLayout();

        if (getArguments().getBoolean(SWITCH_TO_SENT_REQUESTS, false)) {
            mSentRequestTab.select();
        }

        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    RequestMoneyActivity.switchedToReceivedRequestFragment = true;
                    RequestMoneyActivity.switchedToSentRequestFragment = false;
                } else if (tab.getPosition() == 1) {
                    RequestMoneyActivity.switchedToReceivedRequestFragment = false;
                    RequestMoneyActivity.switchedToSentRequestFragment = true;

                }
                super.onTabSelected(tab);
            }
        });
        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
/*

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.home_activity, menu);
    }
*/

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_notification) {
            Intent intent = new Intent(getActivity(), RequestMoneyHistoryActivity.class);
            if (RequestMoneyActivity.switchedToReceivedRequestFragment)
                intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_RECEIVED_REQUEST);
            else intent.putExtra(Constants.REQUEST_TYPE, Constants.REQUEST_TYPE_SENT_REQUEST);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
*/
    private void setupCustomViewsForTabLayout() {
        mReceivedRequestTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);
        mSentRequestTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);
        setTabViews();
    }

    private void setTabViews() {
        setTabIconsWithTexts();

        mReceivedRequestTab.setCustomView(mReceivedRequestTabView);
        mSentRequestTab.setCustomView(mSentRequestTabView);
    }

    private void setTabIconsWithTexts() {

        ((ImageView) mReceivedRequestTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_received_request);
        ((ImageView) mSentRequestTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_send_request);

        ((TextView) mReceivedRequestTabView.findViewById(R.id.tab_text)).setText(getActivity().getResources().getString(R.string.received_request));
        ((TextView) mSentRequestTabView.findViewById(R.id.tab_text)).setText(getActivity().getResources().getString(R.string.sent_request));
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
                return new ReceivedMoneyRequestsFragment();
            } else if (position == 1) {
                return new SentMoneyRequestsFragment();
            } else {
                return new Fragment();
            }
        }

    }


}
