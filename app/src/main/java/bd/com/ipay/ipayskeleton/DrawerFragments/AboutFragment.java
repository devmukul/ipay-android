package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.AboutActivity;
import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragmentV4;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AboutFragment extends BaseFragmentV4 {

    private TextView mBuildNumberView;
    private IconifiedTextViewWithButton mContactView;
    private IconifiedTextViewWithButton mTermView;
    private IconifiedTextViewWithButton mPrivacyView;
    private TextView mCopyRightTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about, container, false);

        mBuildNumberView = (TextView) view.findViewById(R.id.text_view_build_number);
        mContactView = (IconifiedTextViewWithButton) view.findViewById(R.id.text_view_contact);
        mTermView = (IconifiedTextViewWithButton) view.findViewById(R.id.text_view_terms_of_service);
        mPrivacyView = (IconifiedTextViewWithButton) view.findViewById(R.id.text_view_privacy);
        mCopyRightTextView = (TextView) view.findViewById(R.id.text_view_copyright);

        setButtonActions();
        setAppVersionView();
        setCopyRightFooterView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_ipay_about) );
    }

    private void setButtonActions() {
        mContactView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((AboutActivity) getActivity()).switchToAboutContactsFragment();
            }
        });

        mTermView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getContext().getString(R.string.term_link)));
                startActivity(intent);
            }
        });

        mPrivacyView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(getContext().getString(R.string.privacy_link)));
                startActivity(intent);
            }
        });
    }

    private void setAppVersionView() {
        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            mBuildNumberView.setText(getString(R.string.version) + ": " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setCopyRightFooterView() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        mCopyRightTextView.setText(getString(R.string.copyright) + " " + year + " " + getString(R.string.iPay_system));
    }

}
