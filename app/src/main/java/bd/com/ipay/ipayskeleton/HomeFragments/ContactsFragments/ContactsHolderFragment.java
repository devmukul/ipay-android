package bd.com.ipay.ipayskeleton.HomeFragments.ContactsFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.IntroductionAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class ContactsHolderFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetInviteInfoTask = null;
    public static GetInviteInfoResponse mGetInviteInfoResponse;

    private BottomSheetLayout mBottomSheetLayout;
    private Button mAllContactsSelector;
    private Button miPayContactsSelector;

    private IPayContactsFragment miPayAllContactsFragment;
    private IPayContactsFragment miPayMemberContactsFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);
        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);

        mAllContactsSelector = (Button) v.findViewById(R.id.button_contacts_all);
        miPayContactsSelector = (Button) v.findViewById(R.id.button_contacts_ipay);

        mAllContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(getActivity());
                switchToAllContacts();
            }
        });

        miPayContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilities.hideKeyboard(getActivity());
                switchToiPayContacts();
            }
        });

        getInviteInfo();

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        switchToiPayContacts();
    }

    private void setEnabled(Button button, boolean isEnabled) {
        if (isEnabled) {
            button.setBackgroundResource(R.drawable.drawable_contact_selector_active);
            button.setTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
        } else {
            button.setBackgroundResource(R.drawable.drawable_contact_selector_inactive);
            button.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorLightGray));
        }
    }

    private void getInviteInfo() {
        if (mGetInviteInfoTask == null) {
            mGetInviteInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INVITE_INFO,
                    Constants.BASE_URL_MM + Constants.URL_GET_INVITE_INFO, getActivity(), this);
            mGetInviteInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void switchToAllContacts() {
        setEnabled(mAllContactsSelector, true);
        setEnabled(miPayContactsSelector, false);

        try {
            if (getActivity() != null) {
                if (miPayAllContactsFragment == null) {
                    miPayAllContactsFragment = new IPayContactsFragment();
                }
                miPayAllContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, miPayAllContactsFragment).commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void switchToiPayContacts() {
        setEnabled(miPayContactsSelector, true);
        setEnabled(mAllContactsSelector, false);

        try {
            if (miPayMemberContactsFragment == null) {
                miPayMemberContactsFragment = new IPayContactsFragment();

                Bundle bundle = new Bundle();
                bundle.putBoolean(Constants.IPAY_MEMBERS_ONLY, true);
                miPayMemberContactsFragment.setArguments(bundle);
            }
            miPayMemberContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
            getChildFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, miPayMemberContactsFragment).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        try {
            Gson gson = new Gson();

            if (result.getApiCommand().equals(Constants.COMMAND_GET_INVITE_INFO)) {
                try {
                    ContactsHolderFragment.mGetInviteInfoResponse = gson.fromJson(result.getJsonString(), GetInviteInfoResponse.class);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                mGetInviteInfoTask = null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}