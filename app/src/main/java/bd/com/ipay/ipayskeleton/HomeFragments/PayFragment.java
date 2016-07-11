package bd.com.ipay.ipayskeleton.HomeFragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.MakePaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentMakingActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;

public class PayFragment extends Fragment {
    private ListView mServiceActionListView;
    private WalletActionListAdapter mServiceActionListAdapter;

    private List<ServiceAction> mServiceActionList;

    private SharedPreferences pref;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_services, container, false);
        pref = getActivity().getSharedPreferences(Constants.ApplicationTag, Activity.MODE_PRIVATE);

        mServiceActionList = new ArrayList<>();
        mServiceActionList.add(new ServiceAction(getString(R.string.topup)));
        if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE) {
            mServiceActionList.add(new ServiceAction(getString(R.string.make_payment)));
        } else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE) {
            mServiceActionList.add(new ServiceAction(getString(R.string.create_invoice)));
        }

        mServiceActionListView = (ListView) v.findViewById(R.id.list_services);
        mServiceActionListAdapter = new WalletActionListAdapter(getActivity(), R.layout.list_item_services, mServiceActionList);
        mServiceActionListView.setAdapter(mServiceActionListAdapter);

        return v;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    private class WalletActionListAdapter extends ArrayAdapter<ServiceAction> {

        private int mResource;

        public WalletActionListAdapter(Context context, int resource, List<ServiceAction> serviceActionList) {
            super(context, resource, serviceActionList);
            mResource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                ServiceAction serviceAction = getItem(position);

                v = getActivity().getLayoutInflater().inflate(mResource, null);

                TextView actionNameView = (TextView) v.findViewById(R.id.textview_action_name);
                actionNameView.setText(serviceAction.text);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (position) {
                            // Add Money
                            case 0:
                                PinChecker pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent = new Intent(getActivity(), TopUpActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                                break;
                            case 1:
                                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent;
                                        if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.PERSONAL_ACCOUNT_TYPE) {
                                            intent = new Intent(getActivity(), PaymentMakingActivity.class);
                                            startActivity(intent);
                                        } else if (pref.getInt(Constants.ACCOUNT_TYPE, Constants.PERSONAL_ACCOUNT_TYPE) == Constants.BUSINESS_ACCOUNT_TYPE) {
                                            intent = new Intent(getActivity(), MakePaymentActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                                pinChecker.execute();
                                break;
                        }
                    }
                });
            }

            return v;
        }
    }

    private class ServiceAction {
        private String text;

        public ServiceAction(String text) {
            this.text = text;
        }
    }
}
