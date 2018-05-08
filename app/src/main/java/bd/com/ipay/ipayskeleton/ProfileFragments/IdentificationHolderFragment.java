package bd.com.ipay.ipayskeleton.ProfileFragments;

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
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class IdentificationHolderFragment extends Fragment {
    private ViewPager viewPager;
    private final int RECEIVED_REQUEST_TAB = 1;
    private final int SENT_REQUEST_TAB = 0;

    private TabLayout.Tab mReceivedRequestTab;
    private TabLayout.Tab mSentRequestTab;

    private View mReceivedRequestTabView;
    private View mSentRequestTabView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_identification_holder, container, false);
        (getActivity()).setTitle(R.string.profile_introducers);

        viewPager = (ViewPager) v.findViewById(R.id.viewpager);
        viewPager.setAdapter(new IdentificationFragmentAdapter(getChildFragmentManager()));

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        mReceivedRequestTab = tabLayout.getTabAt(RECEIVED_REQUEST_TAB);
        mSentRequestTab = tabLayout.getTabAt(SENT_REQUEST_TAB);
        setupCustomViewsForTabLayout();

        return v;
    }

    private void setupCustomViewsForTabLayout() {

        mSentRequestTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);
        mReceivedRequestTabView = getActivity().getLayoutInflater().inflate(R.layout.view_single_tab_background, null);
        setTabViews();
    }

    @Override
    public void onPause() {
        super.onPause();
        Utilities.hideKeyboard(getActivity());
    }

    private void setTabViews() {
        setTabIconsWithTexts();

        mSentRequestTab.setCustomView(mSentRequestTabView);
        mReceivedRequestTab.setCustomView(mReceivedRequestTabView);
    }

    private void setTabIconsWithTexts() {

        ((ImageView) mSentRequestTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_send_request);
        ((ImageView) mReceivedRequestTabView.findViewById(R.id.tab_icon)).setImageResource(R.drawable.ic_request_received);

        ((TextView) mSentRequestTabView.findViewById(R.id.tab_text)).setText(getActivity().getResources().getString(R.string.introducer_list_header));
        ((TextView) mReceivedRequestTabView.findViewById(R.id.tab_text)).setText(getActivity().getResources().getString(R.string.introduced_list_header));
    }


    public class IdentificationFragmentAdapter extends FragmentPagerAdapter {
        private final String[] tabTitles = new String[]{
                getString(R.string.introducer_list_header),
                getString(R.string.introduced_list_header),
        };

        public IdentificationFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                return new IntroducerFragment();
            } else if (position == 1) {
                return new IntroducedFragment();
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
