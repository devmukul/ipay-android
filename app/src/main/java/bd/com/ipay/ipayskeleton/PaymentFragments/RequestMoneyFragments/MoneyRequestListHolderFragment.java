package bd.com.ipay.ipayskeleton.PaymentFragments.RequestMoneyFragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;

public class MoneyRequestListHolderFragment extends Fragment {
    private ViewPager viewPager;
    private final int RECEIVED_REQUEST_TAB = 0;
    private final int SENT_REQUEST_TAB = 1;

    private TabLayout.Tab mReceivedRequestTab;
    private TabLayout.Tab mSentRequestTab;

    private View mReceivedRequestTabView;
    private View mSentRequestTabView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_money_request_list_holder, container, false);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new MoneyRequestListFragmentAdapter(getChildFragmentManager(), getActivity()));

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mReceivedRequestTab = tabLayout.getTabAt(RECEIVED_REQUEST_TAB);
        mSentRequestTab = tabLayout.getTabAt(SENT_REQUEST_TAB);
        setupCustomViewsForTabLayout();

        return v;
    }

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

        ((ImageView) mReceivedRequestTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_walletw);
        ((ImageView) mSentRequestTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_contact);

        ((TextView) mReceivedRequestTabView.findViewById(R.id.tab_text)).setText(getActivity().getResources().getString(R.string.received_request));
        ((TextView) mSentRequestTabView.findViewById(R.id.tab_text)).setText(getActivity().getResources().getString(R.string.sent_request));
    }


    public class MoneyRequestListFragmentAdapter extends FragmentPagerAdapter {
        private String[] tabTitles = new String[]{
                getString(R.string.request_from_other),
                getString(R.string.my_requests),
        };

        public MoneyRequestListFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new MoneyRequestsFragment();
            } else if (position == 1) {
                return new MyRequestsFragment();
            } else {
                return new Fragment();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
