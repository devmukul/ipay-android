package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import bd.com.ipay.ipayskeleton.Activities.DrawerActivities.AboutActivity;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.R;

public class AboutFragment extends Fragment {

    private TextView mBuildNumberView;
    private IconifiedTextViewWithButton mContactView;
    private IconifiedTextViewWithButton mTermView;
    private IconifiedTextViewWithButton mPrivacyView;
    private TextView mCopyRightTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about, container, false);

        mBuildNumberView = (TextView) v.findViewById(R.id.text_view_build_number);
        mContactView = (IconifiedTextViewWithButton) v.findViewById(R.id.text_view_contact);
        mTermView = (IconifiedTextViewWithButton) v.findViewById(R.id.text_view_terms_of_service);
        mPrivacyView = (IconifiedTextViewWithButton) v.findViewById(R.id.text_view_privacy);
        mCopyRightTextView = (TextView) v.findViewById(R.id.text_view_copyright);

        try {
            PackageInfo pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            String version = pInfo.versionName;
            mBuildNumberView.setText(getString(R.string.version) + ": " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setCopyRightFooterView();

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

        return v;
    }

    private void setCopyRightFooterView() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);

        mCopyRightTextView.setText(getString(R.string.copyright) + " " + year + " " + getString(R.string.iPay_system));
    }

}
