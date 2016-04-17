package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPostAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.PinInfoResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.GetTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.RemoveTrustedDeviceRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.RemoveTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.TrustedDevice;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Common.CommonData;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AccountSettingsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPostAsyncTask mSavePINTask = null;
    private SetPinResponse mSetPinResponse;

    private HttpRequestPostAsyncTask mChangePasswordTask = null;
    private ChangePasswordResponse mChangePasswordResponse;

    private HttpRequestGetAsyncTask mGetTrustedDeviceTask = null;
    private GetTrustedDeviceResponse mGetTrustedDeviceResponse = null;

    private HttpRequestPostAsyncTask mRemoveTrustedDeviceTask = null;
    private RemoveTrustedDeviceResponse mRemoveTrustedDeviceResponse = null;

    private HttpRequestGetAsyncTask mGetPinInfoTask = null;
    private PinInfoResponse mPinInfoResponse;

    private EditText mEnterPINEditText;
    private EditText mEnterPasswordEditText;

    private View setPINHeader;
    private View changePasswordHeader;
    private View trustedDevicesHeader;

    private Button mSetPINButton;
    private ImageView setPinArrow;
    private ImageView changePassArrow;
    private ImageView trustedDevicesArrow;

    private LinearLayout mPINChangeLayout;
    private LinearLayout mPassChangeLayout;
    private LinearLayout mTrustedDevicesLayout;

    private Button mChangePasswordButton;
    private EditText mEnterCurrentPasswordEditText;
    private EditText mEnterNewPasswordEditText;
    private EditText mEnterConfirmNewPasswordEditText;

    private ListView mTrustedDevicesListView;
    private TrustedDeviceAdapter mTrustedDeviceAdapter;

    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_account_settings, container, false);
        ((HomeActivity) getActivity()).setTitle(R.string.account_settings);

        mEnterPINEditText = (EditText) v.findViewById(R.id.new_pin);
        mEnterPasswordEditText = (EditText) v.findViewById(R.id.password);

        mEnterCurrentPasswordEditText = (EditText) v.findViewById(R.id.current_password);
        mEnterNewPasswordEditText = (EditText) v.findViewById(R.id.new_password);
        mEnterConfirmNewPasswordEditText = (EditText) v.findViewById(R.id.confirm_new_password);

        mChangePasswordButton = (Button) v.findViewById(R.id.save_pass);
        mSetPINButton = (Button) v.findViewById(R.id.save_pin);

        setPINHeader = v.findViewById(R.id.set_pin_header);
        changePasswordHeader = v.findViewById(R.id.change_password);
        trustedDevicesHeader = v.findViewById(R.id.trusted_devices);

        changePassArrow = (ImageView) v.findViewById(R.id.change_pass_arrow);
        setPinArrow = (ImageView) v.findViewById(R.id.change_pin_arrow);
        trustedDevicesArrow = (ImageView) v.findViewById(R.id.trusted_device_arrow);

        mPINChangeLayout = (LinearLayout) v.findViewById(R.id.pin_change_layout);
        mPassChangeLayout = (LinearLayout) v.findViewById(R.id.pass_change_layout);
        mTrustedDevicesLayout = (LinearLayout) v.findViewById(R.id.trusted_devices_layout);

        mTrustedDevicesListView = (ListView) v.findViewById(R.id.list_trusted_devices);
        mProgressDialog = new ProgressDialog(getActivity());

        setPINHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPINChangeLayout.getVisibility() == View.VISIBLE) {
                    mPINChangeLayout.setVisibility(View.GONE);
                    setPinArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mPINChangeLayout.setVisibility(View.VISIBLE);
                    Utilities.setLayoutAnim_slideDown(mPINChangeLayout, getActivity());
                    setPinArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });

        changePasswordHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPassChangeLayout.getVisibility() == View.VISIBLE) {
                    mPassChangeLayout.setVisibility(View.GONE);
                    changePassArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mPassChangeLayout.setVisibility(View.VISIBLE);
                    Utilities.setLayoutAnim_slideDown(mPassChangeLayout, getActivity());
                    changePassArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
                }
            }
        });

        trustedDevicesHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTrustedDevicesLayout.getVisibility() == View.VISIBLE) {
                    mTrustedDevicesLayout.setVisibility(View.GONE);
                    trustedDevicesArrow.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                } else {
                    mTrustedDevicesLayout.setVisibility(View.VISIBLE);
                    Utilities.setLayoutAnim_slideDown(mTrustedDevicesLayout, getActivity());
                    trustedDevicesArrow.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);


                    // We are trying to load the trusted device list in the background when the users enters
                    // the settings page. When the user click on the down arrow, if the loading task is still
                    // running we need to inform him/her that we are still loading the list.
                    if (mGetTrustedDeviceTask != null) {
                        mProgressDialog.setMessage(getString(R.string.progress_dialog_loading_trusted_devices));
                        mProgressDialog.show();
                    }
                }
            }
        });

        mSetPINButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSavePIN();
            }
        });

        mChangePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptChangePassword();
            }
        });

        if (getArguments() != null && getArguments().getBoolean(Constants.EXPAND_PIN, false)) {
            setPINHeader.performClick();
        }

        loadTrustedDeviceList();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.action_search_contacts) != null)
            menu.findItem(R.id.action_search_contacts).setVisible(false);
    }

    private void attemptSavePIN() {
        if (mSavePINTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        if (mEnterPINEditText.getText().toString().trim().length() != 4) {
            Toast.makeText(getActivity(), R.string.error_invalid_pin, Toast.LENGTH_LONG).show();
            focusView = mEnterPINEditText;
            cancel = true;
        } else if (mEnterPasswordEditText.getText().toString().trim().length() == 0) {
            Toast.makeText(getActivity(), R.string.error_invalid_password, Toast.LENGTH_LONG).show();
            focusView = mEnterPasswordEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String pin = mEnterPINEditText.getText().toString().trim();
            String password = mEnterPasswordEditText.getText().toString().trim();

            mProgressDialog.setMessage(getString(R.string.saving_pin));
            mProgressDialog.show();
            SetPinRequest mSetPinRequest = new SetPinRequest(password, pin);
            Gson gson = new Gson();
            String json = gson.toJson(mSetPinRequest);
            mSavePINTask = new HttpRequestPostAsyncTask(Constants.COMMAND_SET_PIN,
                    Constants.BASE_URL + Constants.URL_SET_PIN, json, getActivity());
            mSavePINTask.mHttpResponseListener = this;
            mSavePINTask.execute((Void) null);
        }
    }

    private void getPinInfo() {
        if (mGetPinInfoTask != null) {
            return;
        }

        mGetPinInfoTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_PIN_INFO,
                Constants.BASE_URL + Constants.URL_GET_PIN_INFO, getActivity(), this);
        mGetPinInfoTask.execute();
    }

    private void attemptChangePassword() {
        if (mChangePasswordTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String passwordValidationMsg = Utilities.isPasswordValid(mEnterNewPasswordEditText.getText().toString().trim());

        if (mEnterCurrentPasswordEditText.getText().toString().length() < 5) {
            Toast.makeText(getActivity(), R.string.error_invalid_password, Toast.LENGTH_LONG).show();
            focusView = mEnterCurrentPasswordEditText;
            cancel = true;

        } else if (passwordValidationMsg.length() > 0) {
            Toast.makeText(getActivity(), passwordValidationMsg, Toast.LENGTH_LONG).show();
            focusView = mEnterNewPasswordEditText;
            cancel = true;

        } else if (mEnterConfirmNewPasswordEditText.getText().toString().length() < 5
                || !(mEnterConfirmNewPasswordEditText.getText().toString()
                .equals(mEnterConfirmNewPasswordEditText.getText().toString()))) {
            Toast.makeText(getActivity(), R.string.confirm_password_not_matched, Toast.LENGTH_LONG).show();
            focusView = mEnterConfirmNewPasswordEditText;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            String newPassword = mEnterNewPasswordEditText.getText().toString().trim();
            String password = mEnterCurrentPasswordEditText.getText().toString().trim();

            mProgressDialog.setMessage(getString(R.string.change_password_progress));
            mProgressDialog.show();
            ChangePasswordRequest mChangePasswordRequest = new ChangePasswordRequest(password, newPassword);
            Gson gson = new Gson();
            String json = gson.toJson(mChangePasswordRequest);
            mChangePasswordTask = new HttpRequestPostAsyncTask(Constants.COMMAND_CHANGE_PASSWORD,
                    Constants.BASE_URL + Constants.URL_CHANGE_PASSWORD, json, getActivity());
            mChangePasswordTask.mHttpResponseListener = this;
            mChangePasswordTask.execute((Void) null);
        }
    }

    private void loadTrustedDeviceList() {
        if (mGetTrustedDeviceTask != null) {
            return;
        }

        mGetTrustedDeviceTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRUSTED_DEVICES,
                Constants.BASE_URL + "/" + Constants.URL_GET_TRUSTED_DEVICES, getActivity(), this);
        mGetTrustedDeviceTask.execute();
    }

    private void showTrustedDeviceRemoveConfirmationDialog(final long id, String name) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
        dialog
                .setMessage(getString(R.string.confirmation_remove_trusted_device))
                .setPositiveButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeTrustedDevice(id);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void removeTrustedDevice(long id) {
        if (mRemoveTrustedDeviceTask != null)
            return;
        ;

        mProgressDialog.setMessage("Removing device from your trusted device list");
        mProgressDialog.show();

        RemoveTrustedDeviceRequest removeTrustedDeviceRequest = new RemoveTrustedDeviceRequest(id);
        Gson gson = new Gson();
        String json = gson.toJson(removeTrustedDeviceRequest);

        mRemoveTrustedDeviceTask = new HttpRequestPostAsyncTask(Constants.COMMAND_REMOVE_TRUSTED_DEVICE,
                Constants.BASE_URL + Constants.URL_REMOVE_TRUSTED_DEVICE, json, getActivity(), this);
        mRemoveTrustedDeviceTask.execute();
    }

    @Override
    public void httpResponseReceiver(String result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mSavePINTask = null;
            mChangePasswordTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }

        List<String> resultList = Arrays.asList(result.split(";"));
        Gson gson = new Gson();

        if (resultList.get(0).equals(Constants.COMMAND_SET_PIN)) {

            if (resultList.size() > 2) {
                try {
                    mSetPinResponse = gson.fromJson(resultList.get(2), SetPinResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                        getPinInfo();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mSavePINTask = null;

        } else if (resultList.get(0).equals(Constants.COMMAND_CHANGE_PASSWORD)) {

            if (resultList.size() > 2) {
                try {
                    mChangePasswordResponse = gson.fromJson(resultList.get(2), ChangePasswordResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mChangePasswordResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), R.string.change_pass_failed, Toast.LENGTH_LONG).show();
                }
            } else if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.change_pass_failed, Toast.LENGTH_LONG).show();

            mProgressDialog.dismiss();
            mChangePasswordTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_GET_TRUSTED_DEVICES)) {

            if (resultList.size() > 2) {
                try {
                    mGetTrustedDeviceResponse = gson.fromJson(resultList.get(2), GetTrustedDeviceResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        mTrustedDeviceAdapter = new TrustedDeviceAdapter(getActivity(), mGetTrustedDeviceResponse.getDevices());
                        mTrustedDevicesListView.setAdapter(mTrustedDeviceAdapter);
                        Utilities.setUpNonScrollableListView(mTrustedDevicesListView);
                    } else {
                        if (getActivity() != null)
                            Toast.makeText(getActivity(), mGetTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mGetTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            mProgressDialog.dismiss();
            mGetTrustedDeviceTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_REMOVE_TRUSTED_DEVICE)) {

            if (resultList.size() > 2) {
                try {
                    mRemoveTrustedDeviceResponse = gson.fromJson(resultList.get(2), RemoveTrustedDeviceResponse.class);

                    if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.success_device_removed, Toast.LENGTH_LONG).show();
                        }

                        mProgressDialog.setMessage(getString(R.string.progress_dialog_loading_trusted_devices));
                        mProgressDialog.show();

                        loadTrustedDeviceList();
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), mRemoveTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), mRemoveTrustedDeviceResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            mProgressDialog.cancel();
            mRemoveTrustedDeviceTask = null;
        } else if (resultList.get(0).equals(Constants.COMMAND_GET_PIN_INFO)) {
            if (resultList.size() > 2) {
                if (resultList.get(1) != null && resultList.get(1).equals(Constants.HTTP_RESPONSE_STATUS_OK)) {
                    try {
                        mPinInfoResponse = gson.fromJson(resultList.get(2), PinInfoResponse.class);
                        CommonData.setPinInfo(mPinInfoResponse);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public class TrustedDeviceAdapter extends ArrayAdapter<TrustedDevice> {

        private LayoutInflater inflater;

        public TrustedDeviceAdapter(Context context, List<TrustedDevice> objects) {
            super(context, 0, objects);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TrustedDevice trustedDevice = getItem(position);

            View view = convertView;
            if (view == null)
                view = inflater.inflate(R.layout.list_item_trsuted_device, null);

            TextView deviceNameView = (TextView) view.findViewById(R.id.textview_device_name);
            TextView grantTimeView = (TextView) view.findViewById(R.id.textview_time);
            Button removeButton = (Button) view.findViewById(R.id.button_remove);

            deviceNameView.setText(trustedDevice.getDeviceName());
            grantTimeView.setText(trustedDevice.getCreatedTimeString());
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTrustedDeviceRemoveConfirmationDialog(
                            trustedDevice.getId(), trustedDevice.getDeviceName());
                }
            });

            return view;
        }
    }
}