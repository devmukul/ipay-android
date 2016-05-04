package bd.com.ipay.ipayskeleton.Customview;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.Profile.Address.AddressClass;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.District;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.DistrictRequestBuilder;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetDistrictResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.GetThanaResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.Thana;
import bd.com.ipay.ipayskeleton.Model.MMModule.Resource.ThanaRequestBuilder;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CountryList;
import bd.com.ipay.ipayskeleton.Utilities.Constants;

public class AddressInputView extends FrameLayout implements HttpResponseListener {

    private HttpRequestGetAsyncTask mGetThanaListAsyncTask = null;
    private GetThanaResponse mGetThanaResponse;

    private HttpRequestGetAsyncTask mGetDistrictListAsyncTask = null;
    private GetDistrictResponse mGetDistrictResponse;

    private List<Thana> mThanaList;
    private List<District> mDistrictList;

    private List<String> mThanaNames;
    private List<String> mDistrictNames;

    private AddressClass mAddressClass;

    private Context context;

    private EditText mAddressLine1Field;
    private EditText mAddressLine2Field;
    private Spinner mThanaSelection;
    private Spinner mDistrictSelection;
    private Spinner mCountrySelection;
    private EditText mPostalCodeField;

    private ArrayAdapter<String> mAdapterThana;
    private ArrayAdapter<String> mAdapterDistrict;

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

        mThanaNames = new ArrayList<>();

        mAdapterThana = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, mThanaNames);
        mThanaSelection.setAdapter(mAdapterThana);

        mDistrictNames = new ArrayList<>();
        mDistrictNames.add(context.getString(R.string.loading));

        mAdapterDistrict = new ArrayAdapter<>(context,
                android.R.layout.simple_dropdown_item_1line, mDistrictNames);
        mDistrictSelection.setAdapter(mAdapterDistrict);

        mDistrictSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mDistrictSelection.getSelectedItemPosition() != 0
                        && mDistrictList != null) {
                    int districtId = mDistrictList.get(
                            mDistrictSelection.getSelectedItemPosition() - 1).getId();
                    getThanaList(districtId);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<CharSequence> mAdapterCountry = new ArrayAdapter<CharSequence>(context,
                android.R.layout.simple_dropdown_item_1line, CountryList.countryNames);
        mCountrySelection.setAdapter(mAdapterCountry);

        for (int i = 0; i < CountryList.countryISOcodes.length; i++) {
            if (CountryList.countryISOcodes[i].equals("BD")) {
                mCountrySelection.setSelection(i);
                break;
            }
        }

        addView(v);
        getDistrictList();
    }

    private void getThanaList(int districtId) {
        if (mGetThanaListAsyncTask != null) {
            return;
        }
        mThanaNames.clear();
        mThanaNames.add(context.getString(R.string.loading));
        mAdapterThana.notifyDataSetChanged();

        mGetThanaListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_THANA_LIST,
                new ThanaRequestBuilder(districtId).getGeneratedUri(), context, this);
        mGetThanaListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private void getDistrictList() {
        if (mGetDistrictListAsyncTask != null) {
            return;
        }

        mGetDistrictListAsyncTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_DISTRICT_LIST,
                new DistrictRequestBuilder().getGeneratedUri(), context, this);
        mGetDistrictListAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setInformation(AddressClass address) {
        if (address == null)
            return;

        mAddressClass = address;
        if (mAddressClass.getAddressLine1() != null)
            mAddressLine1Field.setText(mAddressClass.getAddressLine1());
        if (mAddressClass.getAddressLine2() != null)
            mAddressLine2Field.setText(mAddressClass.getAddressLine2());
        if (mAddressClass.getPostalCode() != null)
            mPostalCodeField.setText(mAddressClass.getPostalCode());

        if (mThanaList != null) {
            setThana(mAddressClass.getThanaCode());
        }

        if (mDistrictList != null) {
            setDistrict(mAddressClass.getDistrictCode());
        }

        String countryCode = mAddressClass.getCountryCode();
        if (countryCode == null)
            countryCode = "BD";
        for (int i = 0; i < CountryList.countryISOcodes.length; i++) {
            if (countryCode.equals(CountryList.countryISOcodes[i])) {
                mCountrySelection.setSelection(i);
                break;
            }
        }
    }

    private void setDistrict(int district) {
        int districtPosition = 0;
        if (mDistrictList != null) {
            for (int i = 0; i < mDistrictList.size(); i++) {
                if (district == mDistrictList.get(i).getId()) {
                    districtPosition = i + 1;
                    break;
                }
            }
        }
        mDistrictSelection.setSelection(districtPosition);
    }

    private void setThana(int thana) {
        int thanaPosition = 0;
        for (int i = 0; i < mThanaList.size(); i++) {
            if (thana == mThanaList.get(i).getId()) {
                thanaPosition = i + 1;
                break;
            }
        }
        mThanaSelection.setSelection(thanaPosition);
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

        if (mPostalCodeField.getText().toString().length() < 4) {
            mPostalCodeField.setError(context.getString(R.string.invalid_postcode));
            focusedView = mPostalCodeField;
            cancel = true;
        }

        if (mThanaSelection.getSelectedItemPosition() == 0) {
            ((TextView) mThanaSelection.getSelectedView()).setError("");
            focusedView = mThanaSelection;
            cancel = true;
        }

        if (mDistrictSelection.getSelectedItemPosition() == 0) {
            ((TextView) mDistrictSelection.getSelectedView()).setError("");
            focusedView = mDistrictSelection;
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
        String country = CountryList.countryNameToCountryCodeMap.get(
                mCountrySelection.getSelectedItem().toString());
        int thana = mThanaList.get(mThanaSelection.getSelectedItemPosition() - 1).getId();
        int district = mDistrictList.get(mDistrictSelection.getSelectedItemPosition() - 1).getId();

        AddressClass addressClass = new AddressClass(addressLine1, addressLine2, country,
                district, thana, postalCode);
        return addressClass;
    }

    @Override
    public void httpResponseReceiver(String result) {
        if (result == null) {
            mGetThanaListAsyncTask = null;
            mGetDistrictListAsyncTask = null;
            if (context != null)
                Toast.makeText(context, R.string.request_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_GET_THANA_LIST)) {
            try {
                mGetThanaResponse = gson.fromJson(resultList.get(2), GetThanaResponse.class);
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mThanaList = mGetThanaResponse.getThanas();
                    mThanaNames.clear();
                    mThanaNames.add(context.getString(R.string.select_one));

                    for (Thana thana : mThanaList) {
                        mThanaNames.add(thana.getName());
                    }

                    if (context != null) {
                        mAdapterThana.notifyDataSetChanged();
                        if (mAddressClass != null) {
                            setThana(mAddressClass.getThanaCode());
                        }
                    }
                } else {
                    if (context != null)
                        Toast.makeText(context, R.string.failed_loading_thana_list, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (context != null)
                    Toast.makeText(context, R.string.failed_loading_thana_list, Toast.LENGTH_LONG).show();
            }

            mGetThanaListAsyncTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_GET_DISTRICT_LIST)) {
            try {
                mGetDistrictResponse = gson.fromJson(resultList.get(2), GetDistrictResponse.class);

                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    mDistrictList = mGetDistrictResponse.getDistricts();
                    mDistrictNames.clear();
                    mDistrictNames.add(context.getString(R.string.select_one));

                    for (District district : mDistrictList) {
                        mDistrictNames.add(district.getName());
                    }

                    if (context != null) {
                        mAdapterDistrict.notifyDataSetChanged();
                        if (mAddressClass != null) {
                            setDistrict(mAddressClass.getDistrictCode());
                        }
                    }
                } else {
                    if (context != null)
                        Toast.makeText(context, R.string.failed_loading_district_list, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (context != null)
                    Toast.makeText(context, R.string.failed_loading_district_list, Toast.LENGTH_LONG).show();
            }

            mGetDistrictListAsyncTask = null;
        }
    }
}
