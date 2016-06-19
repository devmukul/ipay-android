package bd.com.ipay.ipayskeleton.CustomView;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.List;

import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.CustomView.Dialogs.ResourceSelectorDialog;
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

    private int mSelectedThanaId = -1;
    private int mSelectedDistrictId = -1;

    private AddressClass mAddressClass;

    private Context context;

    private IconifiedEditText mAddressLine1Field;
    private IconifiedEditText mAddressLine2Field;
    private IconifiedEditText mThanaSelection;
    private IconifiedEditText mDistrictSelection;
    private IconifiedEditText mCountrySelection;
    private IconifiedEditText mPostalCodeField;

    private ResourceSelectorDialog<District> districtSelectorDialog;
    private ResourceSelectorDialog<Thana> thanaSelectorDialog;

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

        mAddressLine1Field = (IconifiedEditText) v.findViewById(R.id.address_line_1);
        mAddressLine2Field = (IconifiedEditText) v.findViewById(R.id.address_line_2);
        mThanaSelection = (IconifiedEditText) v.findViewById(R.id.thana);
        mDistrictSelection = (IconifiedEditText) v.findViewById(R.id.district);
        mCountrySelection = (IconifiedEditText) v.findViewById(R.id.country);
        mCountrySelection.setEnabled(false);
        mPostalCodeField = (IconifiedEditText) v.findViewById(R.id.postcode);

        addView(v);
        getDistrictList();
    }

    private void getThanaList(int districtId) {
        if (mGetThanaListAsyncTask != null) {
            return;
        }

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

    private void setThanaAdapter(List<Thana> thanaList) {
        thanaSelectorDialog = new ResourceSelectorDialog<>(context, thanaList, mSelectedThanaId);
        thanaSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mThanaSelection.setText(name);
                mSelectedThanaId = id;
            }
        });

        mThanaSelection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                thanaSelectorDialog.show();
            }
        });
    }

    private void setDistrictAdapter(List<District> districtList) {
        districtSelectorDialog = new ResourceSelectorDialog<>(context, districtList, mSelectedDistrictId);
        districtSelectorDialog.setOnResourceSelectedListener(new ResourceSelectorDialog.OnResourceSelectedListener() {
            @Override
            public void onResourceSelected(int id, String name) {
                mDistrictSelection.setText(name);
                mSelectedDistrictId = id;

                mThanaList = null;
                mSelectedThanaId = -1;
                mThanaSelection.setText(context.getString(R.string.loading));

                getThanaList(mSelectedDistrictId);
            }
        });

        mDistrictSelection.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                districtSelectorDialog.show();
            }
        });
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

        mSelectedDistrictId = mAddressClass.getDistrictCode();
        if (mDistrictList != null) {
            setDistrictName(mSelectedDistrictId);
        }

        mSelectedThanaId = mAddressClass.getThanaCode();
        if (mThanaList != null) {
            setThanaName(mSelectedThanaId);
        }

        String countryCode = mAddressClass.getCountryCode();
        if (countryCode == null)
            countryCode = "BD";
        for (int i = 0; i < CountryList.countryISOcodes.length; i++) {
            if (countryCode.equals(CountryList.countryISOcodes[i])) {
                mCountrySelection.setText(CountryList.countryNames[i]);
                break;
            }
        }
    }

    private void setDistrictName(int district) {
        if (mDistrictList != null && district >= 0) {
            for (int i = 0; i < mDistrictList.size(); i++) {
                if (district == mDistrictList.get(i).getId()) {
                    mDistrictSelection.setText(mDistrictList.get(i).getName());
                    return;
                }
            }
        }
    }

    private void setThanaName(int thana) {
        if (mThanaList != null) {
            for (int i = 0; i < mThanaList.size(); i++) {
                if (thana == mThanaList.get(i).getId()) {
                    mThanaSelection.setText(mThanaList.get(i).getName());
                    return;
                }
            }

            // No selected thana, select the first thana
            if (mThanaList.size() > 0) {
                mSelectedThanaId = mThanaList.get(0).getId();
                mThanaSelection.setText(mThanaList.get(0).getName());
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
        } else if (mSelectedDistrictId < 0) {
            mDistrictSelection.setError(context.getString(R.string.invalid_district));
            focusedView = mDistrictSelection;
            cancel = true;
        } else if (mSelectedThanaId < 0) {
            mThanaSelection.setError(context.getString(R.string.invalid_thana));
            focusedView = mThanaSelection;
            cancel = true;
        } else if (mPostalCodeField.getText().toString().length() < 4) {
            mPostalCodeField.setError(context.getString(R.string.invalid_postcode));
            focusedView = mPostalCodeField;
            cancel = true;
        }

        if (cancel) {
            focusedView.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    public AddressClass getInformation() {
        String addressLine1 = mAddressLine1Field.getText().toString().trim();
        String addressLine2 = mAddressLine2Field.getText().toString().trim();
        String postalCode = mPostalCodeField.getText().toString().trim();
        String country = CountryList.countryNameToCountryCodeMap.get(
                mCountrySelection.getText().toString());
        int thana = mSelectedThanaId;
        int district = mSelectedDistrictId;

        AddressClass addressClass = new AddressClass(addressLine1, addressLine2, country,
                district, thana, postalCode);
        return addressClass;
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {
        if (result == null || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_INTERNAL_ERROR
                    || result.getStatus() == Constants.HTTP_RESPONSE_STATUS_NOT_FOUND) {
            mGetThanaListAsyncTask = null;
            mGetDistrictListAsyncTask = null;
            if (context != null)
                Toast.makeText(context, R.string.service_not_available, Toast.LENGTH_SHORT).show();
            return;
        }

        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_GET_DISTRICT_LIST)) {
            try {
                mGetDistrictResponse = gson.fromJson(result.getJsonString(), GetDistrictResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mDistrictList = mGetDistrictResponse.getDistricts();
                    setDistrictAdapter(mDistrictList);
                    setDistrictName(mSelectedDistrictId);
                    if (mSelectedDistrictId >= 0) {
                        getThanaList(mSelectedDistrictId);
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
        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_THANA_LIST)) {
            try {
                mGetThanaResponse = gson.fromJson(result.getJsonString(), GetThanaResponse.class);
                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    mThanaList = mGetThanaResponse.getThanas();
                    setThanaAdapter(mThanaList);
                    setThanaName(mSelectedThanaId);

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
        }
    }
}
