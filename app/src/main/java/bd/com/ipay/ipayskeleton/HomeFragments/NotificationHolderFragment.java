package bd.com.ipay.ipayskeleton.HomeFragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import bd.com.ipay.ipayskeleton.Aspect.ValidateAccess;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.CacheManager.ACLManager;
import bd.com.ipay.ipayskeleton.Utilities.ServiceIdConstants;

public class NotificationHolderFragment extends Fragment {

    private RadioButton generalNotificationRadioButton;
    private RadioButton deepLinkedRadioButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_holder, container, false);

        RadioGroup notificationTypeRadioGroup = (RadioGroup) view.findViewById(R.id.notification_type_radio_group);
        generalNotificationRadioButton = (RadioButton) view.findViewById(R.id.radio_button_general);
        deepLinkedRadioButton = (RadioButton) view.findViewById(R.id.radio_button_deep_linked);

        notificationTypeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            @ValidateAccess
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radio_button_general:
                        switchToPendingTransactionsFragment();
                        break;
                    case R.id.radio_button_deep_linked:
                        switchToProcessedTransactionsFragment();
                        break;
                }
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (ACLManager.hasServicesAccessibility(ServiceIdConstants.COMPLETED_TRANSACTION)) {
            deepLinkedRadioButton.setChecked(true);
        } else if (ACLManager.hasServicesAccessibility(ServiceIdConstants.PENDING_TRANSACTION)) {
            generalNotificationRadioButton.setChecked(true);
        }
    }


    private void switchToProcessedTransactionsFragment() {
        NotificationFragment notificationFragment = new NotificationFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_notification, notificationFragment).commit();
    }

    private void switchToPendingTransactionsFragment() {
        NotificationDeeplinkedFragment notificationFragment = new NotificationDeeplinkedFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_notification, notificationFragment).commit();
    }
}
