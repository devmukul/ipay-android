package bd.com.ipay.ipayskeleton.Customview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.AddressClass;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CountryList;
import bd.com.ipay.ipayskeleton.Utilities.Common.DistrictList;
import bd.com.ipay.ipayskeleton.Utilities.Common.ThanaList;

public class AddressInputView extends FrameLayout {
    private Context context;

    private EditText mAddressLine1Field;
    private EditText mAddressLine2Field;
    private Spinner mThanaSelection;
    private Spinner mDistrictSelection;
    private Spinner mCountrySelection;
    private EditText mPostalCodeField;

    public AddressInputView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    public AddressInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public AddressInputView(Context context) {
        super(context);
        initView(context);
    }

    private void initView(Context context) {
        this.context = context;

        View v = inflate(context, R.layout.view_address_input, null);

        mAddressLine1Field = (EditText) v.findViewById(R.id.address_line_1);
        mAddressLine2Field = (EditText) v.findViewById(R.id.address_line_2);
        mThanaSelection = (Spinner) v.findViewById(R.id.thana);
        mDistrictSelection = (Spinner) v.findViewById(R.id.district);
        mCountrySelection = (Spinner) v.findViewById(R.id.country);
        mPostalCodeField = (EditText) v.findViewById(R.id.postcode);

        ArrayAdapter<CharSequence> mAdapterBusinessCity = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_dropdown_item_1line, ThanaList.thanaNames);
        mThanaSelection.setAdapter(mAdapterBusinessCity);

        ArrayAdapter<CharSequence> mAdapterDistrict = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_dropdown_item_1line, DistrictList.districtNames);
        mDistrictSelection.setAdapter(mAdapterDistrict);

        ArrayAdapter<CharSequence> mAdapterCountry = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_dropdown_item_1line, CountryList.countryNames);
        mCountrySelection.setAdapter(mAdapterCountry);

        addView(v);
    }

    public void setInformation(AddressClass address) {
        if (address == null)
            return;;

        if (address.getAddressLine1() != null)
            mAddressLine1Field.setText(address.getAddressLine1());
        if (address.getAddressLine2() != null)
            mAddressLine2Field.setText(address.getAddressLine2());
        if (address.getPostalCode() != null)
            mPostalCodeField.setText(address.getPostalCode());

        int thanaPosition = 0;
        for (int i = 0; i < ThanaList.thanaIds.length; i++) {
            if (address.getThanaCode() == ThanaList.thanaIds[i]) {
                thanaPosition = i;
                break;
            }
        }
        mThanaSelection.setSelection(thanaPosition);

        int districtPosition = 0;
        for (int i = 0; i < DistrictList.districtIds.length; i++) {
            if (address.getDistrictCode() == DistrictList.districtIds[i]) {
                districtPosition = i;
                break;
            }
        }
        mDistrictSelection.setSelection(districtPosition);

        String countryCode = address.getCountryCode();
        if (countryCode == null)
            countryCode = "BD";
        for (int i = 0; i < CountryList.countryISOcodes.length; i++) {
            if (countryCode.equals(CountryList.countryISOcodes[i])) {
                mCountrySelection.setSelection(i);
                break;
            }
        }
    }

    public void resetInformation() {
        setInformation(new AddressClass());
    }

    public boolean verifyUserInputs() {
        boolean cancel = false;
        View focusedView = null;

        if (mAddressLine1Field.getText().toString().isEmpty()) {
            mAddressLine1Field.setError(context.getString(R.string.invalid_address_line_1));
            focusedView = mAddressLine1Field;
            cancel = true;
        }

        if (mAddressLine2Field.getText().toString().isEmpty()) {
            mAddressLine2Field.setError(context.getString(R.string.invalid_address_line_2));
            focusedView = mAddressLine2Field;
            cancel = true;
        }

        if (mPostalCodeField.getText().toString().isEmpty()) {
            mPostalCodeField.setError(context.getString(R.string.invalid_postcode));
            focusedView = mPostalCodeField;
            cancel = true;
        }

        if (cancel) {
            focusedView.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    public AddressClass getInformation() {
        String addressLine1 = mAddressLine1Field.getText().toString().trim();
        String addressLine2 = mAddressLine2Field.getText().toString().trim();
        String postalCode = mPostalCodeField.getText().toString().trim();
        String country = mCountrySelection.getSelectedItem().toString();
        String thana = mThanaSelection.getSelectedItem().toString();
        String district = mDistrictSelection.getSelectedItem().toString();

        AddressClass addressClass = new AddressClass(addressLine1, addressLine2, country,
                district, thana, postalCode);
        return addressClass;
    }
}
