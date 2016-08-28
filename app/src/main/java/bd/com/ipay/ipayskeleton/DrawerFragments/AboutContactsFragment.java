package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AboutContactsFragment extends Fragment {

    private ImageView mapView;
    private TextView mAddressView;
    private TextView mPhoneView;
    private TextView mEmailView;
    private TextView mWebView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_about_contacts, container, false);

        getActivity().setTitle(R.string.contact_us);

        mapView = (ImageView) v.findViewById(R.id.mapview);
        mAddressView = (TextView) v.findViewById(R.id.address);
        mPhoneView = (TextView) v.findViewById(R.id.phone_number);
        mEmailView = (TextView) v.findViewById(R.id.email);
        mWebView = (TextView) v.findViewById(R.id.web);

        mAddressView.setText(Constants.OFFICE_ADDRESS);
        mPhoneView.setText(": " + Constants.OFFICE_PHONE_NUMBER);
        mEmailView.setText(": " + Constants.OFFICE_EMAIL);
        mWebView.setText(": " + Constants.OFFICE_WEB);

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMap();
            }
        });

        return v;
    }

    private void openMap() {
        String strUri = "http://maps.google.com/maps?q=loc:" + Constants.OFFICE_LATITUDE + "," + Constants.OFFICE_LONGITUDE + ")";
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

        startActivity(intent);
    }
}