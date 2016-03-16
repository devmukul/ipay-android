package bd.com.ipay.ipayskeleton.Activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments.DocumentUploadFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments.EditBasicInfoFragment;
import bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments.EditUserAddressFragment;
import bd.com.ipay.ipayskeleton.R;

public class EditProfileActivity extends AppCompatActivity {

    public static final String TARGET_TAB = "TARGET_TAB";

    public static final int TARGET_TAB_BASIC_INFO = 0;
    public static final int TARGET_TAB_USER_ADDRESS = 1;
    public static final int TARGET_TAB_UPLOAD_DOCUMENTS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new EditProfileFragmentAdapter(getSupportFragmentManager(), this));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        if (getIntent().hasExtra(TARGET_TAB)) {
            int targetTabPosition = getIntent().getIntExtra(TARGET_TAB, 0);
            TabLayout.Tab targetTab = tabLayout.getTabAt(targetTabPosition);
            if (targetTab != null)
                    targetTab.select();
        }
    }

    public class EditProfileFragmentAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private String[] tabTitles;

        public EditProfileFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            tabTitles = new String[] {
                    getString(R.string.profile_basic_info),
                    getString(R.string.profile_address),
                    getString(R.string.profile_documents)
            };
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new EditBasicInfoFragment();
            else if (position == 1)
                return new EditUserAddressFragment();
            else if (position == 2)
                return new DocumentUploadFragment();
            else
                return new Fragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }
}
