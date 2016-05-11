package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.RecommendationAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ContactsHolderFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetInviteInfoTask = null;
    public static GetInviteInfoResponse mGetInviteInfoResponse;

    private BottomSheetLayout mBottomSheetLayout;
    private IPayContactsFragment iPayContactsFragment;
    private AllContactsFragment allContactsFragment;

    private Button mAllContactsSelector;
    private Button miPayContactsSelector;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);
        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);

        mAllContactsSelector = (Button) v.findViewById(R.id.button_contacts_all);
        miPayContactsSelector = (Button) v.findViewById(R.id.button_contacts_ipay);

        iPayContactsFragment = new IPayContactsFragment();
        allContactsFragment = new AllContactsFragment();
        iPayContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
        allContactsFragment.setBottomSheetLayout(mBottomSheetLayout);

        mAllContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToAllContacts();
            }
        });

        miPayContactsSelector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToiPayContacts();
            }
        });

        getInviteInfo();
        switchToAllContacts();

        return v;
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
                    Constants.BASE_URL + "/" + Constants.URL_GET_INVITE_INFO, getActivity(), this);
            mGetInviteInfoTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void switchToAllContacts() {
        setEnabled(mAllContactsSelector, true);
        setEnabled(miPayContactsSelector, false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (getActivity() != null)
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, allContactsFragment).commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    private void switchToiPayContacts() {
        setEnabled(miPayContactsSelector, true);
        setEnabled(mAllContactsSelector, false);

        if (getActivity() != null)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, iPayContactsFragment).commit();
                }
            }, 300);
    }

    @Override
    public void httpResponseReceiver(String result) {

        try {
            List<String> resultList = Arrays.asList(result.split(";"));
            Gson gson = new Gson();

            if (resultList.get(0).equals(Constants.COMMAND_GET_INVITE_INFO)) {
                try {
                    if (resultList.size() > 2)
                        ContactsHolderFragment.mGetInviteInfoResponse = gson.fromJson(resultList.get(2), GetInviteInfoResponse.class);

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