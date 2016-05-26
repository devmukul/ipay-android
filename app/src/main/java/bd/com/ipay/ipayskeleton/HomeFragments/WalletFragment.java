package bd.com.ipay.ipayskeleton.HomeFragments;

import android.content.Context;
import android.content.Intent;
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

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.AddMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SendMoneyActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.WithdrawMoneyActivity;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;

public class WalletFragment extends Fragment {

    private ListView mWalletActionListView;
    private WalletActionListAdapter walletActionListAdapter;

    private List<WalletAction> mWalletActionList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wallet, container, false);

        mWalletActionList = new ArrayList<>();
        mWalletActionList.add(new WalletAction(getString(R.string.add_money)));
        mWalletActionList.add(new WalletAction(getString(R.string.withdraw_money)));
        mWalletActionList.add(new WalletAction(getString(R.string.send_money)));
        mWalletActionList.add(new WalletAction(getString(R.string.request_money)));

        mWalletActionListView = (ListView) v.findViewById(R.id.list_wallet);
        walletActionListAdapter = new WalletActionListAdapter(getActivity(), R.layout.list_item_wallet, mWalletActionList);
        mWalletActionListView.setAdapter(walletActionListAdapter);

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

    private class WalletActionListAdapter extends ArrayAdapter<WalletAction> {

        private int mResource;

        public WalletActionListAdapter(Context context, int resource, List<WalletAction> walletActionList) {
            super(context, resource, walletActionList);
            mResource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                WalletAction walletAction = getItem(position);

                v = getActivity().getLayoutInflater().inflate(mResource, null);

                TextView actionNameView = (TextView) v.findViewById(R.id.textview_action_name);
                actionNameView.setText(walletAction.text);

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (position) {
                            // Add Money
                            case 0:
                                PinChecker addMoneyPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent = new Intent(getActivity(), AddMoneyActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                addMoneyPinChecker.execute();
                                break;

                            // Withdraw Money
                            case 1:
                                PinChecker pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent = new Intent(getActivity(), WithdrawMoneyActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                                break;

                            // Send Money
                            case 2:
                                PinChecker sendMoneyPinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent = new Intent(getActivity(), SendMoneyActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                sendMoneyPinChecker.execute();
                                break;

                            // Request Money
                            case 3:
                                Intent intent = new Intent(getActivity(), RequestMoneyActivity.class);
                                startActivity(intent);
                                break;

                        }
                    }
                });
            }

            return v;
        }
    }

    private class WalletAction {
        private String text;

        public WalletAction(String text) {
            this.text = text;
        }
    }
}
