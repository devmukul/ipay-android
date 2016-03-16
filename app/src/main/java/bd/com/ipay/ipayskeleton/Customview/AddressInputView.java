package bd.com.ipay.ipayskeleton.Customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;

import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CountryList;
import bd.com.ipay.ipayskeleton.Utilities.Common.DistrictList;
import bd.com.ipay.ipayskeleton.Utilities.Common.ThanaList;

public class AddressInputView extends FrameLayout {
    private Context context;

    private EditText mAddressLine1;
    private EditText mAddressLine2;
    private Spinner mCity;
    private Spinner mDistrict;
    private Spinner mCountry;
    private EditText mPostalCode;

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

        mAddressLine1 = (EditText) v.findViewById(R.id.address_line_1);
        mAddressLine2 = (EditText) v.findViewById(R.id.address_line_2);
        mCity = (Spinner) v.findViewById(R.id.city);
        mDistrict = (Spinner) v.findViewById(R.id.district);
        mCountry = (Spinner) v.findViewById(R.id.country);
        mPostalCode = (EditText) v.findViewById(R.id.postcode);

        ArrayAdapter<CharSequence> mAdapterBusinessCity = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_dropdown_item_1line, ThanaList.thanaNames);
        mCity.setAdapter(mAdapterBusinessCity);

        ArrayAdapter<CharSequence> mAdapterDistrict = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_dropdown_item_1line, DistrictList.districtNames);
        mDistrict.setAdapter(mAdapterDistrict);

        ArrayAdapter<CharSequence> mAdapterCountry = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_dropdown_item_1line, CountryList.countryNames);
        mCountry.setAdapter(mAdapterCountry);

        addView(v);
    }
}
