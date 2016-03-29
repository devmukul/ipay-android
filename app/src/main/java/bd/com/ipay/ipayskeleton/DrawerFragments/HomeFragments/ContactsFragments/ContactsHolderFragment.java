package bd.com.ipay.ipayskeleton.DrawerFragments.HomeFragments.ContactsFragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.flipboard.bottomsheet.BottomSheetLayout;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.GetInviteInfoRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.RecommendationAndInvite.GetInviteInfoResponse;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class ContactsHolderFragment extends Fragment implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetInviteInfoTask = null;
    public static GetInviteInfoResponse mGetInviteInfoResponse;

    private BottomSheetLayout mBottomSheetLayout;
    private IPayContactsFragment iPayContactsFragment;
    private AllContactsFragment allContactsFragment;
    private Switch mContactSwitch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_holder, container, false);
        mBottomSheetLayout = (BottomSheetLayout) v.findViewById(R.id.bottom_sheet);
        mContactSwitch = (Switch) v.findViewById(R.id.switch_contacts);

        iPayContactsFragment = new IPayContactsFragment();
        allContactsFragment = new AllContactsFragment();
        iPayContactsFragment.setBottomSheetLayout(mBottomSheetLayout);
        allContactsFragment.setBottomSheetLayout(mBottomSheetLayout);

        mContactSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    switchToiPayContacts();
                else switchToAllContacts();
            }
        });

        getInviteInfo();
        switchToAllContacts();

        return v;
    }

    private void getInviteInfo() {
        if (mGetInviteInfoTask == null) {
            mGetInviteInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_INVITE_INFO,
                    new GetInviteInfoRequestBuilder().getGeneratedUri(), getActivity(), this);
            mGetInviteInfoTask.execute();
        }
    }

    private void switchToAllContacts() {
        if (getActivity() != null)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_contacts, allContactsFragment).commit();
                }
            }, 300);
    }

    private void switchToiPayContacts() {
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