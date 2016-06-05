package bd.com.ipay.ipayskeleton.DrawerFragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
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

import java.util.ArrayList;
import java.util.List;

import bd.com.ipay.ipayskeleton.Activities.HomeActivity;
import bd.com.ipay.ipayskeleton.Api.HttpRequestDeleteAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestGetAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpRequestPutAsyncTask;
import bd.com.ipay.ipayskeleton.Api.HttpResponseListener;
import bd.com.ipay.ipayskeleton.Api.HttpResponseObject;
import bd.com.ipay.ipayskeleton.DatabaseHelper.DataHelper;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.ChangePasswordResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinRequest;
import bd.com.ipay.ipayskeleton.Model.MMModule.ChangeCredentials.SetPinResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.GetTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.RemoveTrustedDeviceResponse;
import bd.com.ipay.ipayskeleton.Model.MMModule.TrustedDevice.TrustedDevice;
import bd.com.ipay.ipayskeleton.R;
import bd.com.ipay.ipayskeleton.Utilities.Constants;
import bd.com.ipay.ipayskeleton.Service.GCM.PushNotificationStatusHolder;
import bd.com.ipay.ipayskeleton.Utilities.Utilities;

public class AccountSettingsFragment extends Fragment implements HttpResponseListener {

    private HttpRequestPutAsyncTask mSavePINTask = null;
    private SetPinResponse mSetPinResponse;

    private HttpRequestPutAsyncTask mChangePasswordTask = null;
    private ChangePasswordResponse mChangePasswordResponse;

    private HttpRequestGetAsyncTask mGetTrustedDeviceTask = null;
    private GetTrustedDeviceResponse mGetTrustedDeviceResponse = null;

    private HttpRequestDeleteAsyncTask mRemoveTrustedDeviceTask = null;
    private RemoveTrustedDeviceResponse mRemoveTrustedDeviceResponse = null;

    private EditText mEnterPINEditText;
    private EditText mConfirmPINEditText;
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
        ((HomeActivity) getActivity()).setTitle(R.string.security_settings);

        mEnterPINEditText = (EditText) v.findViewById(R.id.new_pin);
        mConfirmPINEditText = (EditText) v.findViewById(R.id.confirm_pin);
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

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
        if (pushNotificationStatusHolder.isUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE))
            getTrustedDeviceList();
        else {
            DataHelper dataHelper = DataHelper.getInstance(getActivity());
            String json = dataHelper.getPushEvent(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE);
            dataHelper.closeDbOpenHelper();

            if (json == null)
                getTrustedDeviceList();
            else {
                processTrustedDeviceList(json);
            }
        }
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

        //hiding keyboard after save button pressed in set pin
        Utilities.hideKeyboard(getActivity());

        if (mSavePINTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String passwordValidationMsg = Utilities.isPasswordValid(mEnterPasswordEditText.getText().toString());

        if (mEnterPINEditText.getText().toString().trim().length() != 4) {
            mEnterPINEditText.setError(getString(R.string.error_invalid_pin));
            focusView = mEnterPINEditText;
            cancel = true;
        } else if (mConfirmPINEditText.getText().toString().length() !=4
                || !(mEnterPINEditText.getText().toString().equals(mConfirmPINEditText.getText().toString()))) {
            mConfirmPINEditText.setError(getString(R.string.confirm_pin_not_matched));
            focusView = mConfirmPINEditText;
            cancel = true;
        } else if (passwordValidationMsg.length() > 0) {
            mEnterPasswordEditText.setError(passwordValidationMsg);
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
            SetPinRequest mSetPinRequest = new SetPinRequest(pin, password);
            Gson gson = new Gson();
            String json = gson.toJson(mSetPinRequest);
            mSavePINTask = new HttpRequestPutAsyncTask(Constants.COMMAND_SET_PIN,
                    Constants.BASE_URL_MM + Constants.URL_SET_PIN, json, getActivity());
            mSavePINTask.mHttpResponseListener = this;
            mSavePINTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void attemptChangePassword() {

        //hiding keyboard after save button pressed in change password
        Utilities.hideKeyboard(getActivity());

        if (mChangePasswordTask != null) {
            return;
        }

        boolean cancel = false;
        View focusView = null;

        String passwordValidationMsg = Utilities.isPasswordValid(mEnterNewPasswordEditText.getText().toString().trim());

        if (mEnterCurrentPasswordEditText.getText().toString().length() < 5) {
            mEnterCurrentPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mEnterCurrentPasswordEditText;
            cancel = true;

        } else if (passwordValidationMsg.length() > 0) {
            Toast.makeText(getActivity(), passwordValidationMsg, Toast.LENGTH_LONG).show();
            focusView = mEnterNewPasswordEditText;
            cancel = true;

        } else if (mEnterConfirmNewPasswordEditText.getText().toString().length() < 5
                || !(mEnterNewPasswordEditText.getText().toString()
                .equals(mEnterConfirmNewPasswordEditText.getText().toString()))) {
            mEnterConfirmNewPasswordEditText.setError(getString(R.string.confirm_password_not_matched));
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
            mChangePasswordTask = new HttpRequestPutAsyncTask(Constants.COMMAND_CHANGE_PASSWORD,
                    Constants.BASE_URL_MM + Constants.URL_CHANGE_PASSWORD, json, getActivity());
            mChangePasswordTask.mHttpResponseListener = this;
            mChangePasswordTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    private void getTrustedDeviceList() {
        if (mGetTrustedDeviceTask != null) {
            return;
        }

        mGetTrustedDeviceTask = new HttpRequestGetAsyncTask(Constants.COMMAND_GET_TRUSTED_DEVICES,
                Constants.BASE_URL_MM + Constants.URL_GET_TRUSTED_DEVICES, getActivity(), this);
        mGetTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

        mProgressDialog.setMessage("Removing device from your trusted device list");
        mProgressDialog.show();

        mRemoveTrustedDeviceTask = new HttpRequestDeleteAsyncTask(Constants.COMMAND_REMOVE_TRUSTED_DEVICE,
                Constants.BASE_URL_MM + Constants.URL_REMOVE_TRUSTED_DEVICE + id, getActivity(), this);
        mRemoveTrustedDeviceTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @Override
    public void httpResponseReceiver(HttpResponseObject result) {

        if (result == null) {
            mProgressDialog.dismiss();
            mSavePINTask = null;
            mChangePasswordTask = null;
            mGetTrustedDeviceTask = null;
            mRemoveTrustedDeviceTask = null;
            if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.request_failed, Toast.LENGTH_LONG).show();
            return;
        }


        Gson gson = new Gson();

        if (result.getApiCommand().equals(Constants.COMMAND_SET_PIN)) {

            try {
                mSetPinResponse = gson.fromJson(result.getJsonString(), SetPinResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    if (getActivity() != null)
                        Toast.makeText(getActivity(), mSetPinResponse.getMessage(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (getActivity() != null)
                    Toast.makeText(getActivity(), R.string.save_failed, Toast.LENGTH_LONG).show();
            }

            mProgressDialog.dismiss();
            mSavePINTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_CHANGE_PASSWORD)) {

            try {
                mChangePasswordResponse = gson.fromJson(result.getJsonString(), ChangePasswordResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
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

            mProgressDialog.dismiss();
            mChangePasswordTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_GET_TRUSTED_DEVICES)) {

            try {
                mGetTrustedDeviceResponse = gson.fromJson(result.getJsonString(), GetTrustedDeviceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    processTrustedDeviceList(result.getJsonString());

                    DataHelper dataHelper = DataHelper.getInstance(getActivity());
                    dataHelper.updatePushEvents(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE, result.getJsonString());
                    dataHelper.closeDbOpenHelper();

                    PushNotificationStatusHolder pushNotificationStatusHolder = new PushNotificationStatusHolder(getActivity());
                    pushNotificationStatusHolder.setUpdateNeeded(Constants.PUSH_NOTIFICATION_TAG_DEVICE_UPDATE, false);
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

            mProgressDialog.dismiss();
            mGetTrustedDeviceTask = null;

        } else if (result.getApiCommand().equals(Constants.COMMAND_REMOVE_TRUSTED_DEVICE)) {

            try {
                mRemoveTrustedDeviceResponse = gson.fromJson(result.getJsonString(), RemoveTrustedDeviceResponse.class);

                if (result.getStatus() == Constants.HTTP_RESPONSE_STATUS_OK) {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), R.string.success_device_removed, Toast.LENGTH_LONG).show();
                    }

                    mProgressDialog.setMessage(getString(R.string.progress_dialog_loading_trusted_devices));
                    mProgressDialog.show();

                    getTrustedDeviceList();
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

            mProgressDialog.cancel();
            mRemoveTrustedDeviceTask = null;
        }
    }

    private void processTrustedDeviceList(String json) {
        Gson gson = new Gson();
        mGetTrustedDeviceResponse = gson.fromJson(json, GetTrustedDeviceResponse.class);

        ArrayList<TrustedDevice> mTrustedDeviceList = (ArrayList<TrustedDevice>) mGetTrustedDeviceResponse.getDevices();
        mTrustedDeviceAdapter = new TrustedDeviceAdapter(getActivity(), mTrustedDeviceList);
        mTrustedDevicesListView.setAdapter(mTrustedDeviceAdapter);
        Utilities.setUpNonScrollableListView(mTrustedDevicesListView);
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
                view = inflater.inflate(R.layout.list_item_trusted_device, null);
            ImageView deviceimageView = (ImageView) view.findViewById(R.id.trusted_device_imageView);
            TextView deviceNameView = (TextView) view.findViewById(R.id.textview_device_name);
            TextView grantTimeView = (TextView) view.findViewById(R.id.textview_time);
            TextView currentDevice = (TextView) view.findViewById(R.id.current_device);
            Button removeButton = (Button) view.findViewById(R.id.button_remove);

            //Setting the correct image based on trusted device type
            int[] images = {
                    R.drawable.ic_computer_black_24dp,
                    R.drawable.ic_phone_android_black_24dp,
                    R.drawable.ic_phone_iphone_black_24dp
            };
            String DeviceID = trustedDevice.getDeviceId();
            String Android = "android";
            String IOS = "ios";
            String Computer = "browser";
            if (DeviceID.toLowerCase().contains(Android.toLowerCase())) {
                deviceimageView.setImageResource(images[1]);

            } else if (DeviceID.toLowerCase().contains(IOS.toLowerCase())) {
                deviceimageView.setImageResource(images[2]);

            } else if (DeviceID.toLowerCase().contains(Computer.toLowerCase())) {
                deviceimageView.setImageResource(images[0]);

            }

            TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
            String mDeviceID = "mobile-android-";
            mDeviceID = mDeviceID.concat(telephonyManager.getDeviceId());

            if (mDeviceID.equals(DeviceID)) {
                removeButton.setVisibility(View.INVISIBLE);
                currentDevice.setVisibility(View.VISIBLE);
                deviceNameView.setTextColor(getResources().getColor(R.color.cardview_dark_background));
            } else {
                removeButton.setVisibility(View.VISIBLE);
                currentDevice.setVisibility(View.INVISIBLE);

            }


            deviceNameView.setText(trustedDevice.getDeviceName());
            grantTimeView.setText(trustedDevice.getCreatedTimeString());
            currentDevice.setText("Current Device");
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