package bd.com.ipay.ipayskeleton.DrawerFragments.ProfileFragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import bd.com.ipay.ipayskeleton.Activities.EditProfileActivity;
import bd.com.ipay.ipayskeleton.Customview.AddressInputView;
import bd.com.ipay.ipayskeleton.R;

public class EditUserAddressFragment extends Fragment {

    private AddressInputView mPresentAddressView;
    private AddressInputView mPermanentAddressView;
    private AddressInputView mOfficeAddressView;

    private CheckBox mPermanentAddressCheckBox;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_edit_user_address, container, false);

        mPresentAddressView = (AddressInputView) v.findViewById(R.id.present_address);
        mPermanentAddressView = (AddressInputView) v.findViewById(R.id.permanent_address);
        mOfficeAddressView = (AddressInputView) v.findViewById(R.id.office_address);

        mPermanentAddressCheckBox = (CheckBox) v.findViewById(R.id.checkbox_permanent_address);
        mPermanentAddressCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mPermanentAddressView.setInformation(mPresentAddressView.getInformation());
                }
            }
        });

        mPresentAddressView.setInformation(ProfileFragment.mPresentAddress);
        mPermanentAddressView.setInformation(ProfileFragment.mPermanentAddress);
        mOfficeAddressView.setInformation(ProfileFragment.mOfficeAddress);

        // We just have set information in the address input views, so the edit flag is set.
        // But we are interested only in edits made by the users, so clear the flags first.
        // Flags will be set again when user changes any of the fields.
        mPresentAddressView.clearEditFlag();
        mPermanentAddressView.clearEditFlag();
        mOfficeAddressView.clearEditFlag();

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.profile, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save) {
            ((EditProfileActivity) getActivity()).attemptSaveProfile();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }

    public boolean isEdited() {
        return mPresentAddressView.isEdited() || mPermanentAddressView.isEdited() || mOfficeAddressView.isEdited();
    }

    public boolean verifyUserInputs() {
        if (mPresentAddressView.verifyUserInputs() && mPermanentAddressView.verifyUserInputs() && mOfficeAddressView.verifyUserInputs()) {
            ProfileFragment.mPresentAddress = mPresentAddressView.getInformation();
            ProfileFragment.mPermanentAddress = mPermanentAddressView.getInformation();
            ProfileFragment.mOfficeAddress = mOfficeAddressView.getInformation();

            return true;
        }
        else {
            return false;
        }
    }
}
