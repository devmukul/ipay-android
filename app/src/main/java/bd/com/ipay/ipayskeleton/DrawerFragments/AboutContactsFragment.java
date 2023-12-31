package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import bd.com.ipay.ipayskeleton.BaseFragments.BaseFragment;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AboutContactsFragment extends BaseFragment {

    private ImageView mapView;
    private TextView mAddressView;
    private TextView mPhoneView;
    private TextView mEmailView;
    private TextView mWebView;
    private FloatingActionButton mFabFeedback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_contacts, container, false);

        getActivity().setTitle(R.string.contact_us);

        mFabFeedback = (FloatingActionButton) v.findViewById(R.id.fab_feedback);
        mapView = (ImageView) v.findViewById(R.id.mapview);
        mAddressView = (TextView) v.findViewById(R.id.address);
        mPhoneView = (TextView) v.findViewById(R.id.phone_number);
        mEmailView = (TextView) v.findViewById(R.id.email);
        mWebView = (TextView) v.findViewById(R.id.web);

        mAddressView.setText(Constants.OFFICE_ADDRESS);
        mPhoneView.setText(": " + Constants.OFFICE_HOTLINE_NUMBER + ", " + Constants.OFFICE_LAND_LINE_NUMBER_PRIMARY);
        mEmailView.setText(": " + Constants.OFFICE_EMAIL);
        mWebView.setText(": " + Constants.HOST_NAME);

        mFabFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + Constants.FEEDBACK));
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMapWithLocation();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.sendScreenTracker(mTracker, getString(R.string.screen_name_ipay_contacts));
    }

    private void openMapWithLocation() {
        try {
            Uri mapLocationUri = Uri.parse("geo:" + Constants.OFFICE_LATITUDE + "," + Constants.OFFICE_LONGITUDE);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapLocationUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getContext().getPackageManager()) == null) {
                String mapLocationWebUri = "http://maps.google.com/maps?q=loc:" + Constants.OFFICE_LATITUDE + "," + Constants.OFFICE_LONGITUDE + ")";
                mapIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(mapLocationWebUri));
            }
            startActivity(mapIntent);
        } catch (Exception e) {
            Toast.makeText(getContext(), R.string.no_browser_map_app_found_error_message, Toast.LENGTH_SHORT).show();
        }
    }
}