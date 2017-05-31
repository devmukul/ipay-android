package bd.com.ipay.ipayskeleton.HomeFragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.PaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.RequestPaymentActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.SingleInvoiceActivity;
import bd.com.ipay.ipayskeleton.Activities.PaymentActivities.TopUpActivity;
import bd.com.ipay.ipayskeleton.CustomView.IconifiedTextViewWithButton;
import bd.com.ipay.ipayskeleton.Model.CommunicationPOJO.Pay.PayPropertyConstants;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ProfileInfoCacheManager;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.DialogUtils;
import bd.com.ipay.ipayskeleton.Utilities.PinChecker;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class PayFragment extends Fragment {
    private static final int REQUEST_CODE_PERMISSION = 1001;

    private ListView mServiceActionListView;
    private WalletActionListAdapter mServiceActionListAdapter;

    private List<ServiceAction> mServiceActionList;

    private PinChecker pinChecker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_services, container, false);

        mServiceActionList = new ArrayList<>();
        mServiceActionList.add(new ServiceAction(getString(R.string.make_payment)));
        if (ProfileInfoCacheManager.isBusinessAccount()) {
            mServiceActionList.add(new ServiceAction(getString(R.string.request_payment)));
        }
        mServiceActionList.add(new ServiceAction(getString(R.string.pay_by_QR_code)));
        mServiceActionList.add(new ServiceAction(getString(R.string.mobile_topup)));
        // mServiceActionList.add(new ServiceAction(getString(R.string.education_payment)));

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
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CODE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initiateScan();
                } else {
                    Toast.makeText(getActivity(), R.string.error_camera_permission_denied, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void initiateScan() {
        IntentIntegrator.forSupportFragment(this).initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(
                    requestCode, resultCode, data);
            if (scanResult == null) {
                return;
            }
            final String result = scanResult.getContents();
            if (result != null) {
                Handler mHandler = new Handler();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            PinChecker singleInvoicePinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                @Override
                                public void ifPinAdded() {
                                    Intent intent = new Intent(getActivity(), SingleInvoiceActivity.class);
                                    intent.putExtra(Constants.RESULT, result);
                                    startActivity(intent);
                                }
                            });
                            singleInvoicePinChecker.execute();
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), R.string.error_invalid_QR_code, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);

        if (menu.findItem(R.id.action_filter_by_service) != null)
            menu.findItem(R.id.action_filter_by_service).setVisible(false);
        if (menu.findItem(R.id.action_filter_by_date) != null)
            menu.findItem(R.id.action_filter_by_date).setVisible(false);
    }

    private class WalletActionListAdapter extends ArrayAdapter<ServiceAction> {

        private final int mResource;

        public WalletActionListAdapter(Context context, int resource, List<ServiceAction> serviceActionList) {
            super(context, resource, serviceActionList);
            mResource = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if (v == null) {
                final ServiceAction serviceAction = getItem(position);

                v = getActivity().getLayoutInflater().inflate(mResource, null);

                IconifiedTextViewWithButton actionView = (IconifiedTextViewWithButton) v.findViewById(R.id.item_services);
                View divider = v.findViewById(R.id.divider);
                if (position == mServiceActionList.size() - 1)
                    divider.setVisibility(View.INVISIBLE);

                actionView.setText(serviceAction.text);
                actionView.setDrawableLeft(getResources().getDrawable(PayPropertyConstants.PAY_PROPERTY_NAME_TO_ICON_MAP.get(serviceAction.text)));

                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (serviceAction.text) {
                            case Constants.SERVICE_ACTION_REQUEST_PAYMENT:
                                if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.REQUEST_PAYMENT)) {
                                    DialogUtils.showServiceNotAllowedDialog(getContext());
                                    return;
                                }
                                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent;
                                        intent = new Intent(getActivity(), RequestPaymentActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                                break;
                            case Constants.SERVICE_ACTION_MAKE_PAYMENT:
                                if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                                    DialogUtils.showServiceNotAllowedDialog(getContext());
                                    return;
                                }

                                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent;
                                        intent = new Intent(getActivity(), PaymentActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                                break;
                            case Constants.SERVICE_ACTION_TOP_UP:
                                if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.TOP_UP)) {
                                    DialogUtils.showServiceNotAllowedDialog(getContext());
                                    return;
                                }
                                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent = new Intent(getActivity(), TopUpActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                                break;
                            /*case Constants.SERVICE_ACTION_EDUCATION_PAYMENT:
                                pinChecker = new PinChecker(getActivity(), new PinChecker.PinCheckerListener() {
                                    @Override
                                    public void ifPinAdded() {
                                        Intent intent = new Intent(getActivity(), EducationPaymentActivity.class);
                                        startActivity(intent);
                                    }
                                });
                                pinChecker.execute();
                                break;*/
                            case Constants.SERVICE_ACTION_PAY_BY_QR_CODE:
                                if (!ACLCacheManager.hasServicesAccessibility(ServiceIdConstants.MAKE_PAYMENT)) {
                                    DialogUtils.showServiceNotAllowedDialog(getContext());
                                    return;
                                }

                                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                                            REQUEST_CODE_PERMISSION);
                                } else initiateScan();
                                break;
                        }
                    }
                });
            }

            return v;
        }
    }

    private class ServiceAction {
        private final String text;

        public ServiceAction(String text) {
            this.text = text;
        }
    }
}
